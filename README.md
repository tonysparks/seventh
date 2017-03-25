seventh
=======

A top down 2D Shooter game I've been working on in my spare time.  This repository only includes the source.  

Download
===
You can download the BETA version of the game [here](https://www.dropbox.com/s/srxcko8c3qvc3m8/seventh.zip?dl=0)


Videos/Screenshots
===
[![Seventh Gameplay Video](http://img.youtube.com/vi/JEKWlPJX8V0/0.jpg)](http://youtube.com/watch?v=JEKWlPJX8V0)
![alt text](http://i.imgur.com/Y8bV3jM.png "Title Screen")
![alt text](http://i.imgur.com/PgQNj1W.png "In Game")
![alt text](http://i.imgur.com/BW2Txym.png "In Game #2")


Features
===
* Three game modes (Team Death Match, Objective Based, Capture the Flag)
* 9 different weapons
* Melee
* Grenades
* Bots
* Tanks
* Multiplayer and Single player (Single player vs. Bots)

TODO
===
* Update all graphics to WWII
* Replace temporary sounds
* Create more maps
* New game types (custom)
* Flamethrower
* More Vehicles
* Destructable terrain
* Update HUD
* Tweak game design
* Add more intelligence to Bots

Technical Stuff
===
* Custom built reliable messaging protocol over UDP
  - Handles out of order packets 
  - Can flag messages as 'reliable' which will guarantee delivery
  - Very fast, was able to play with 8 players with the server hosted in NYC and players from Seattle, Milwaukee, Pittsburg and San Diego all with <100ms ping
* Client/Server model.  The client is fairly dumb, it just sends player input to the server and does some minor interpolation.  The server is authorative and handles all of the game logic.
* Can host a dedicated server (either a command line only option, or GUI option)
* Can issue remote commands from client to server to administer server (similar to Quake rcon)
* Libgdx is used for client rendering and input handling
* SoundSystem is used for 3d sound
* Tiled is used for the map editor

Build from Source
===
* Download the source code from github:
 - git clone https://github.com/tonysparks/seventh
* Download the game assets from [here](https://dl.dropboxusercontent.com/u/11954191/seventh.zip) 
* Open the seventh.zip file and copy the *seventh/assets* folder into your project folder
  - folder structure should look like this:
  ```
  seventh/
    assets/
    lib/
    src/
  ```
* Compile the project by (TODO: convert to Maven project to make this easier):
 - Eclipse:
    - Convert to Java project
    - Add jars in *lib* folder to classpath


Run from Source
===
* Run the game by:
 - Windows:
 ```
 java -cp ./lib/*;./lib/libgdx/*;./bin/ -Djava.library.path="./lib/natives" -Xmx1g seventh.ClientMain
 ```
 - Mac/Linux/Unix:
 ```
 java -cp ./lib/*:./lib/libgdx/*:./bin/ -Djava.library.path="./lib/natives" -Xmx1g seventh.ClientMain
 ```

More to come!
