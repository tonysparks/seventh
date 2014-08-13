seventh
=======

A top down 2D Shooter game I've been working on in my spare time.  This repository only includes the source.  You can download the BETA version of the game here: http://162.243.250.245:8080/seventh/

##Features##
* Two game modes (Team Death Match, Objective Based)
* 9 different weapons
* Melee
* Grenades
* Multiplayer and Single player (Single player vs. Bots)

##TODO##
* Update graphics to WWII
* Create more maps
* New game types (CTF, custom)
* Flamethrower
* Tanks/Vehicles
* Destructable terrain
* Update HUD
* Tweak game design


##Technical Stuff##
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

More to come!
