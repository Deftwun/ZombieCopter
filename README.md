# ZombieCopter

A physics based game written in java with the help of [libgdx](http://libgdx.badlogicgames.com/) that doesn't really have much direction anymore. Think I might refocus my efforts on something with a little better end game and smaller scope. I plan on using a lot of what I've learned here for things in the future. Hopefully it can be of use to others as well :)

### Story
Your a guy with an arrow firing gun that drives helicopters and jeeps while shooting zombies.

![ScreenShot](/ScreenShots/screenShot1.png?raw=true)
![ScreenShot](/ScreenShots/screenShot3.png?raw=true)

### How To Get It
[Download the jar file](https://www.dropbox.com/s/o6pd9t3qw3brnss/ZombieCopter.jar?dl=0) and run it. You'll nee to have Java installed.

You should also be able clone or fork this repo and import the project files into eclipse if you wan't to build it yourself. You'll need the eclipse gradle plugin though. All the interesting game code is located in [/core/src/com/deftwun/zombiecopter](https://github.com/Deftwun/ZombieCopter/tree/master/core/src/com/deftwun/zombiecopter)

### Controls

Player Controls|Action
---------|----------
W,A,S,D   |move       
Left Mouse|fire weapon
Q|Slow motion
E|Enter/Exit vehicle

Debugging controls|Action
------------------|------
Esc|restart
P|pause
# 1-6|Change zoom level
Tab|Debug console.
Left Cntrl|Spawn Entity


#### Note
*Bring up the debug console and type in an entity name. Now you can spawn that entity using control key. 
Here are some entities you can try :*

*jeep, helicopter, friend, friendCopter, enemy, enemyCopter, zombie, civilian*
[full list](/desktop/assets/data/entities/)


### Bugs and stuff
<ul>
<li>Desktop version is the only one that works currently. There was an android project that worked but I haven't been optimizing for that so curretly there isn't one. (controls not really ironed out though) </li>
<li>Helicopters lose thrust during slow motion</li>
<li>gaps/lines appear between tiles during rendering because they have no padding</li>
<li>Lots of planned features aren't yet implemented. Can't change weapons in game for example. (hint: play with the entity config files.)</li>
<li>Art is NOT good and there is no sound or music but you can find all the assets in the desktop folder or inside the jar</li>

</ul>


### Awesome 3rd Party libs & tools used
<ul>
<li><a href = "http://libgdx.badlogicgames.com/">libgdx</a></li>
<li><a href = "https://github.com/libgdx/ashley">Ashley Entity framework</li>
<li><a href = "http://box2d.org">Box2d: 2d Physics Engine</a></li>
<li><a href = "http://www.mapeditor.org/">Tiled map editor</a></li>
<li><a href = "https://pyxeledit.com/">Pyxel Edit (amazing pixel art tool)</a></li>
</ul>

