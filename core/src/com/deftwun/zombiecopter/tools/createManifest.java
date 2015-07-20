package com.deftwun.zombiecopter.tools;

import java.io.File;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Json;
import com.deftwun.zombiecopter.AssetManifest;

//When run as a java application, this class will scan the assets folder and create an 
// assetmanifest file that is used when loading assets. (need this when you want assets bundled with jar file)

public class createManifest {

	public static void main(String[] arg){
		
		AssetManifest manifest = new AssetManifest();
		
		String relativePath = "../desktop/assets/",
			   assetPath = "data/",
			   entityPath = assetPath + "entities/",
			   imagePath = assetPath + "images/",
			   //texturePath = assetPath + "textures/",
			   levelPath = assetPath + "levels/",
			   particlePath = assetPath + "particles/",
			   weaponPath = assetPath + "weapons/";
			   
		File entityDir = new File(relativePath + entityPath),
			 imageDir = new File(relativePath + imagePath),
			 //textureDir = new File(relativePath + texturePath),
			 levelDir = new File(relativePath + levelPath),
			 particleDir = new File(relativePath + particlePath),
			 weaponDir = new File(relativePath + weaponPath);
		
		for (String f : entityDir.list()){
			manifest.entities.add(entityPath + f);
		}
		for (String f : imageDir.list()){
			manifest.images.add(imagePath + f);
		}
		/* not using texture atlases so this gives a null pointer
		for (String f : textureDir.list()){
			manifest.textures.add(texturePath + f);
		}
		*/
		for (String f : levelDir.list()){
			manifest.levels.add(levelPath + f);
		}
		for (String f : particleDir.list()){
			if (f.endsWith(".p"))
				manifest.particles.add(particlePath + f);
		}
		for (String f : weaponDir.list()){
			manifest.weapons.add(weaponPath + f);
		}
		
		Json json = new Json();
		FileHandle file = new FileHandle("../desktop/assets/data/assetManifest");
		file.writeString(json.prettyPrint(manifest), false);
	}
}
