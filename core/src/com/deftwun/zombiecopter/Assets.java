package com.deftwun.zombiecopter;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.Logger;
import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.utils.SerializationException;

//This class is a mess

public class Assets {
	
	private AssetManifest manifest;
	
	private final int LOG_LEVEL = Logger.DEBUG;
	private final Logger logger = new Logger("Assets",LOG_LEVEL);
	private final AssetManager assetManager = new AssetManager();
	private final ObjectMap<String,EntityConfig> entityConfigs = new ObjectMap<String,EntityConfig>();
	private final ObjectMap<String,WeaponConfig> weaponConfigs = new ObjectMap<String,WeaponConfig>();
	private ShapeRenderer renderer = new ShapeRenderer();
	

	public Assets(){
		logger.debug("Initializing");
		loadManifest();
		loadAllAssets();
	}
	
	private void loadManifest(){
		Json json = new Json();
		FileHandle file = Gdx.files.internal("data/assetManifest");
		manifest = json.fromJson(AssetManifest.class, file);
	}

	public ParticleEffect getEffect(String name){
		ParticleEffect eff = null;
		String[] possibles = {name,"data/"+name,"data/particles/"+name,};
		for (String s : possibles){
			if (assetManager.isLoaded(s)) 
				eff = assetManager.get(s); 
		}
		if (eff == null) logger.error("ParticleEffect not found: " + name);
		return eff;
	}
	
	
	public Texture getTexture(String name){
		Texture tex = null;
		String[] possibles = {name,"data/"+name,"data/images/"+name, "data/textures/"+name};
		for (String s : possibles){
			if (assetManager.isLoaded(s)) 
				tex = assetManager.get(s); 
		}
		if (tex == null) logger.error("Texture not found: " + name);
		return tex;
	}
	
	public EntityConfig getEntityConfig(String name){
		if (entityConfigs.containsKey(name))
			return entityConfigs.get(name);
		else if (entityConfigs.containsKey("entities/"+name))
			return entityConfigs.get("entities/"+name);
		logger.error("Entity config not found: " + name);
		return null;
	}
	
	public WeaponConfig getWeaponConfig(String name){
		if (weaponConfigs.containsKey(name))
			return weaponConfigs.get(name);
		else if (weaponConfigs.containsKey("weapons/"+name))
			return weaponConfigs.get("weapons/"+name);
			
		logger.error("Weapon config not found: " + name);
		return null;
	}
	
	
	//TODO: CHange to 'write*'
	//Writes template config files for weapons/entities
	public void createTemplateEntity(){
		Json json = new Json();
		json.setUsePrototypes(false);
		FileHandle file = Gdx.files.local("templateEntity.json");
		file.writeString(json.prettyPrint(new EntityConfig()), false);
	}
	
	public void createTemplateWeapon(){
		Json json = new Json();
		json.setUsePrototypes(false);
		FileHandle file = Gdx.files.local("templateWeapon.json");
		file.writeString(json.prettyPrint(new WeaponConfig()),false);
	}
	

	//This is a hack function that handles the need to access the assets folder 
	// via ./bin/data instead of just /data when running the desktop project in eclipse
	private FileHandle getAssetsPath(String folderName){
		boolean isJarFile = true;
		/*
		if (Gdx.app.getType() == ApplicationType.Desktop && !isJarFile)
			return Gdx.files.internal("./bin/" + folderName + "/");
			//return Gdx.files.internal(folderName + "/");
		else 
			return Gdx.files.internal(folderName + "/");
		*/
		return Gdx.files.internal(folderName);
	}
	
	private void loadAllAssetsInPath(String folder,Class type, String suffix){
		logger.debug("Loading '" + folder + "'");
		for (FileHandle f : getAssetsPath(folder).list()){
			String msg = "  Found: " + f.name();
			if (suffix == null){
				assetManager.load(folder + "/" + f.name(),type);
			}
			else if (f.name().toLowerCase().endsWith(suffix))
				assetManager.load(folder + "/" + f.name(),type);
			else msg+= " ...ignoring";
			logger.debug(msg);
		}
	}
	
	private void loadEntities(){
		logger.debug("Loading Entities");
		
		Json json = new Json();
		for (String s : manifest.entities){
			try {
				EntityConfig config = json.fromJson(EntityConfig.class, Gdx.files.internal(s));
				entityConfigs.put(config.name,config);
			}
			catch (SerializationException ex){
				logger.error("Failed to load entity file: " + s);
				logger.error(ex.getMessage());
			}
		}
	}
	
	private void loadWeapons(){
		logger.debug("Loading Weapons");
		Json json = new Json();;
		for (String s : manifest.weapons){
			try {
				WeaponConfig config = json.fromJson(WeaponConfig.class, Gdx.files.internal(s));
				weaponConfigs.put(config.name,config);
			}
			catch (SerializationException ex){
				logger.error("Failed to load weapons file: " + s);
				logger.error(ex.getMessage());
			}
		}
	}
	
	private void loadAllAssets(){
		logger.debug("Loading All Assets");
		
		loadEntities();
		loadWeapons();
		for (String s : manifest.images){
			logger.debug("Image: " + s);
			assetManager.load(s, Texture.class);
		}
		for (String s : manifest.textures){
			assetManager.load(s,TextureAtlas.class);
		}
		for (String s : manifest.particles){
			assetManager.load(s,ParticleEffect.class);
		}
		
		logger.debug("AssetManager begin loading...");
		try{
			while (assetManager.update() == false){
				drawProgress();
				//logger.debug("  " + (int)(assetManager.getProgress()*100) + "% complete");
			}	
		}
		catch(Exception e){
			logger.error("Some assets failed to load: " + e.getMessage());
		}
	}
	
	private void drawProgress(){
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		renderer.setColor(Color.ORANGE);
		renderer.begin(ShapeType.Filled);
		renderer.rect(0,0,800,600);
		renderer.end();
	}
	
}