/*
 * see license.txt 
 */
package seventh.client.sfx;

import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

import paulscode.sound.ListenerData;
import paulscode.sound.SoundSystem;
import paulscode.sound.SoundSystemConfig;
import paulscode.sound.SoundSystemException;
import paulscode.sound.SoundSystemLogger;
import paulscode.sound.codecs.CodecWav;
import paulscode.sound.libraries.LibraryLWJGLOpenAL;
import seventh.client.ClientSeventhConfig;
import seventh.client.ClientTeam;
import seventh.game.SoundType;
import seventh.game.net.NetSound;
import seventh.math.Vector2f;
import seventh.shared.Command;
import seventh.shared.Cons;
import seventh.shared.Console;

/**
 * @author Tony
 *
 */
public class Sounds {

//	private static ExecutorService service = Executors.newCachedThreadPool();
	public static final long uiChannel = 2004 >> 2;
							
	public static final int[] die = {0,1,2};
	public static final int[] hit = {3,4,5};
			
//	public static final int[] normalWalk = {6,7,8,9};
	public static final int[] weaponDrop = {6,7};
	public static final int[] uiHover = {8};
	public static final int[] uiSelect = {9};
	public static final int[] gib = {10,11};
	
	public static final int[] bombTick = {12};
	public static final int[] bombPlant = {13};	
	public static final int[] bombDisarm = {14};
	
	public static final int[] logAlert = {15};
	public static final int[] weaponPickupSnd = {16};
	
	public static final int[] explodeSnd = {17,18,19};
	public static final int[] emptyFireSnd = {20};
	public static final int[] ammoPickupSnd = {21};
	public static final int[] respawnSnd = {22};	
	public static final int[] weaponSwitch = {23};

	public static final int[] ruffle = {24,25,26};

	public static final int[] thompsonFire = {27};
	public static final int[] thompsonReload = {28};
	

	public static final int[] shotgunFire = {29};
	public static final int[] shotgunPump = {30};
	public static final int[] shotgunReload = {31};
	
	public static final int[] springfieldFire = {32};
	public static final int[] springfieldReload = {33};
	public static final int[] springfieldRechamber = {34};
	
	public static final int[] m1Fire = {35};
	public static final int[] m1FireLast = {36};
	public static final int[] m1Reload = {37};
	
	public static final int[] grenadePinPulled = {38};
	public static final int[] grenadeThrow = {39};
	
	public static final int[] rocketFire = {40};
	
	// TODO:
	public static final int[] kar98Fire = {41};
	public static final int[] kar98Reload = {42};
	public static final int[] kar98Rechamber = {43};
	
	public static final int[] mp44Fire = {44};
	public static final int[] mp44Reload = {45};
	
	public static final int[] mp40Fire = {46};
	public static final int[] mp40Reload = {47};
	
	public static final int[] meleeSwing = {48,49};
	public static final int[] meleeHit = {50,51};
	
	public static final int[] normalWalk = {52, 53, 54, 55};
	public static final int[] dirtWalk = {56,57,58,59};
	public static final int[] grassWalk = {60,61,62,63};
	public static final int[] metalWalk = {64,65,66,67};
	public static final int[] waterWalk = {68,69,70,71};
	public static final int[] woodWalk = {72,73,74,75};
	
	public static final int[] uiNavigate = {76,77};
	public static final int[] uiKeyType = {78, 79, 80};
	
	public static final int[] pistolFire = {81};
	public static final int[] pistolReload = {82};
	
	
	public static final int[] impactMetal = {83,84,85,86,87};
	public static final int[] impactWood = {88,89,90,91,92};
	public static final int[] impactFoliage = {93,94,95,96,97};
	public static final int[] impactDefault = {98,99,100,101,102};
	
	public static final int[] riskerFire = {103};
	public static final int[] riskerReload = {104};
	public static final int[] riskerRechamber = {105};
	
	public static final int[] mechForwardFootstep = {106};
	public static final int[] mechRetractFootstep = {107};
	public static final int[] mechTorsoMove = {108};
	
	public static final int[] breadthLite = {109,110,111};
	public static final int[] breadthHeavy = {112,113,114};
	
	public static final int[] bulletZing = {115,116,117};
	
	//public static final int[] tankStart = {118};
	//public static final int[] tankMove = {119};
	public static final int[] healthPackPickup = {120};

	public static final int[] alliedSpeechAttack = {121};
	public static final int[] alliedSpeechCoverMe = {122};
	public static final int[] alliedSpeechFollowMe = {123};
	public static final int[] alliedSpeechGetOutOfMyWay = {124};
	public static final int[] alliedSpeechGetUsKilled = {125};
	public static final int[] alliedSpeechHoldPosition = {126};
	public static final int[] alliedSpeechIllCoverYou = {127};
	public static final int[] alliedSpeechTakingFire = {128};
	public static final int[] alliedSpeechYouTakeLead = {129};

	public static final int[] axisSpeechAttack = {130};
	public static final int[] axisSpeechCoverMe = {131};
	public static final int[] axisSpeechFollowMe = {132};
	public static final int[] axisSpeechGetOutOfMyWay = {133};
	public static final int[] axisSpeechGetUsKilled = {134};
	public static final int[] axisSpeechHoldPosition = {135};
	public static final int[] axisSpeechIllCoverYou = {136};
	public static final int[] axisSpeechTakingFire = {137};
	public static final int[] axisSpeechYouTakeLead = {138};

	public static final int[] tankOn = {139};
	public static final int[] tankOff = {140};
	public static final int[] tankIdle = {141};
	public static final int[] tankShift = {142,143,144};
	public static final int[] tankRevUp = {145};
	public static final int[] tankRevDown = {146};
	public static final int[] tankTurret = {147};
	public static final int[] tankMove = {148};
	
	public static final int[][] alliedSpeeches = {
			alliedSpeechAttack,
			alliedSpeechCoverMe,
			alliedSpeechFollowMe,
			alliedSpeechGetOutOfMyWay,
			alliedSpeechGetUsKilled,
			alliedSpeechHoldPosition,
			alliedSpeechIllCoverYou,
			alliedSpeechTakingFire,
			alliedSpeechYouTakeLead,		
	};
	
	public static final int[][] axisSpeeches = {
			axisSpeechAttack,
			axisSpeechCoverMe,
			axisSpeechFollowMe,
			axisSpeechGetOutOfMyWay,
			axisSpeechGetUsKilled,
			axisSpeechHoldPosition,
			axisSpeechIllCoverYou,
			axisSpeechTakingFire,
			axisSpeechYouTakeLead,		
	};
	
	private static Map<String, Sound> loadedSounds = new ConcurrentHashMap<>();
	private static Sound[][] channels = new Sound[32][];
	private static float volume = 0.1f;
	private static ClientSeventhConfig config;
	
	private static Sound[] createChannel() {
		return new Sound[] {					
			loadSound("./seventh/sfx/player/die1.wav") ,   // 0
			loadSound("./seventh/sfx/player/die2.wav") ,   // 1
			loadSound("./seventh/sfx/player/die3.wav") ,   // 2
			loadSound("./seventh/sfx/player/hit1.wav") ,   // 3
			loadSound("./seventh/sfx/player/hit2.wav") ,   // 4
			loadSound("./seventh/sfx/player/hit3.wav") ,   // 5												

			loadSound("./seventh/sfx/weapon_drop01.wav") ,   // 6,
			loadSound("./seventh/sfx/weapon_drop02.wav") ,   // 7,			
			loadSound("./seventh/sfx/ui/element_hover.wav") ,   // 8,
			loadSound("./seventh/sfx/ui/element_select.wav") ,   // 9,
			loadSound("./seventh/sfx/player/body_explode1.wav"), // 10
			loadSound("./seventh/sfx/player/body_explode2.wav"), // 11						
			loadSound("./seventh/sfx/bomb_tick.wav"), // 12
			loadSound("./seventh/sfx/bomb_plant.wav"), // 13
			loadSound("./seventh/sfx/bomb_disarm.wav"), // 14
			
			// UI stuff
			loadSound("./seventh/sfx/log_alert.wav"), // 15
			loadSound("./seventh/sfx/player/weapon_pickup.wav"), //16
															
			// misc.
			loadSound("./seventh/sfx/explosion1.wav") ,   // 17
			loadSound("./seventh/sfx/explosion2.wav") ,   // 18		
			loadSound("./seventh/sfx/explosion3.wav") ,   // 19		
			loadSound("./seventh/sfx/empty_fire.wav") ,   // 20
			loadSound("./seventh/sfx/player/ammo_pickup.wav") ,   // 21									
			loadSound("./seventh/sfx/respawn.wav") ,   // 22
			loadSound("./seventh/sfx/weapon_switch.wav") ,   // 23
			loadSound("./seventh/sfx/player/ruffle1.wav"), // 24
			loadSound("./seventh/sfx/player/ruffle2.wav"), // 25
			loadSound("./seventh/sfx/player/ruffle3.wav"), // 26
			
			loadSound("./seventh/sfx/thompson/thompson_fire.wav") ,   // 27
			loadSound("./seventh/sfx/thompson/thompson_reload.wav") ,   // 28
				
			loadSound("./seventh/sfx/shotgun/shotgun_fire.wav") ,		// 29
			loadSound("./seventh/sfx/shotgun/shotgun_pump.wav") ,   // 30
			loadSound("./seventh/sfx/shotgun/shotgun_reload.wav") ,   // 31
			
			loadSound("./seventh/sfx/springfield/springfield_fire.wav") ,   // 32
			loadSound("./seventh/sfx/springfield/springfield_reload.wav") ,   // 33
			loadSound("./seventh/sfx/springfield/springfield_rechamber.wav") ,   // 34
		
			loadSound("./seventh/sfx/m1garand/m1garand_fire.wav") ,   // 35
			loadSound("./seventh/sfx/m1garand/m1garand_fire_last.wav") ,   // 36
			loadSound("./seventh/sfx/m1garand/m1garand_reload.wav") ,   // 37
			

			loadSound("./seventh/sfx/grenade/grenade_pinpull.wav") ,   // 38
			loadSound("./seventh/sfx/grenade/grenade_throw.wav") ,   // 39
			
			loadSound("./seventh/sfx/rocket/rocket_fire.wav") ,   // 40
			
			loadSound("./seventh/sfx/kar98/kar98_fire.wav") ,   // 41
			loadSound("./seventh/sfx/kar98/kar98_reload.wav") ,   // 42
			loadSound("./seventh/sfx/kar98/kar98_rechamber.wav") ,   // 43
			
			loadSound("./seventh/sfx/mp44/mp44_fire.wav") ,   // 44
			loadSound("./seventh/sfx/mp44/mp44_reload.wav") ,   // 45
			
			loadSound("./seventh/sfx/mp40/mp40_fire.wav") ,   // 46
			loadSound("./seventh/sfx/mp40/mp40_reload.wav") ,   // 47
			
			loadSound("./seventh/sfx/melee/melee_swing01.wav") ,   // 48
			loadSound("./seventh/sfx/melee/melee_swing02.wav") ,   // 49
			loadSound("./seventh/sfx/melee/melee_hit01.wav") ,   // 50
			loadSound("./seventh/sfx/melee/melee_hit02.wav") ,   // 51
			
			// footsteps
			loadSound("./seventh/sfx/player/footsteps/foot_normal01.wav") ,   // 52
			loadSound("./seventh/sfx/player/footsteps/foot_normal02.wav") ,   // 53
			loadSound("./seventh/sfx/player/footsteps/foot_normal03.wav") ,   // 54
			loadSound("./seventh/sfx/player/footsteps/foot_normal04.wav") ,   // 55
			
			loadSound("./seventh/sfx/player/footsteps/foot_dirt01.wav") ,   // 56
			loadSound("./seventh/sfx/player/footsteps/foot_dirt02.wav") ,   // 57
			loadSound("./seventh/sfx/player/footsteps/foot_dirt03.wav") ,   // 58
			loadSound("./seventh/sfx/player/footsteps/foot_dirt04.wav") ,   // 59
			
			loadSound("./seventh/sfx/player/footsteps/foot_grass01.wav") ,   // 60
			loadSound("./seventh/sfx/player/footsteps/foot_grass02.wav") ,   // 61
			loadSound("./seventh/sfx/player/footsteps/foot_grass03.wav") ,   // 62
			loadSound("./seventh/sfx/player/footsteps/foot_grass04.wav") ,   // 63
			
			loadSound("./seventh/sfx/player/footsteps/foot_metal01.wav") ,   // 64
			loadSound("./seventh/sfx/player/footsteps/foot_metal02.wav") ,   // 65
			loadSound("./seventh/sfx/player/footsteps/foot_metal03.wav") ,   // 66
			loadSound("./seventh/sfx/player/footsteps/foot_metal04.wav") ,   // 67
			
			loadSound("./seventh/sfx/player/footsteps/foot_water01.wav") ,   // 68
			loadSound("./seventh/sfx/player/footsteps/foot_water02.wav") ,   // 69
			loadSound("./seventh/sfx/player/footsteps/foot_water03.wav") ,   // 70
			loadSound("./seventh/sfx/player/footsteps/foot_water04.wav") ,   // 71
			
			loadSound("./seventh/sfx/player/footsteps/foot_wood01.wav") ,   // 72
			loadSound("./seventh/sfx/player/footsteps/foot_wood02.wav") ,   // 73
			loadSound("./seventh/sfx/player/footsteps/foot_wood03.wav") ,   // 74
			loadSound("./seventh/sfx/player/footsteps/foot_wood04.wav") ,   // 75
			
			loadSound("./seventh/sfx/ui/navigate01.wav") ,   // 76			
			loadSound("./seventh/sfx/ui/navigate02.wav") ,   // 77
			
			loadSound("./seventh/sfx/ui/key_type01.wav") ,   // 78
			loadSound("./seventh/sfx/ui/key_type02.wav") ,   // 79
			loadSound("./seventh/sfx/ui/key_type03.wav") ,   // 80
			
			loadSound("./seventh/sfx/pistol/pistol_fire.wav") ,   // 81
			loadSound("./seventh/sfx/pistol/pistol_reload.wav") ,   // 82
			
			loadSound("./seventh/sfx/impact/impact_metal01.wav") ,   // 83
			loadSound("./seventh/sfx/impact/impact_metal02.wav") ,   // 84
			loadSound("./seventh/sfx/impact/impact_metal03.wav") ,   // 85
			loadSound("./seventh/sfx/impact/impact_metal04.wav") ,   // 86
			loadSound("./seventh/sfx/impact/impact_metal05.wav") ,   // 87
			
			loadSound("./seventh/sfx/impact/impact_wood01.wav") ,   // 88
			loadSound("./seventh/sfx/impact/impact_wood02.wav") ,   // 89
			loadSound("./seventh/sfx/impact/impact_wood03.wav") ,   // 90
			loadSound("./seventh/sfx/impact/impact_wood04.wav") ,   // 91
			loadSound("./seventh/sfx/impact/impact_wood05.wav") ,   // 92
			
			loadSound("./seventh/sfx/impact/impact_foliage01.wav") ,   // 93
			loadSound("./seventh/sfx/impact/impact_foliage02.wav") ,   // 94
			loadSound("./seventh/sfx/impact/impact_foliage03.wav") ,   // 95
			loadSound("./seventh/sfx/impact/impact_foliage04.wav") ,   // 96
			loadSound("./seventh/sfx/impact/impact_foliage05.wav") ,   // 97
			
			loadSound("./seventh/sfx/impact/impact_default01.wav") ,   // 98
			loadSound("./seventh/sfx/impact/impact_default02.wav") ,   // 99
			loadSound("./seventh/sfx/impact/impact_default03.wav") ,   // 100
			loadSound("./seventh/sfx/impact/impact_default04.wav") ,   // 101
			loadSound("./seventh/sfx/impact/impact_default05.wav") ,   // 102
			
			loadSound("./seventh/sfx/risker/risker_fire.wav") ,   // 103
			loadSound("./seventh/sfx/risker/risker_reload.wav") ,   // 104
			loadSound("./seventh/sfx/risker/risker_rechamber.wav") ,   // 105
			
			loadSound("./seventh/sfx/player/footsteps/mech_footstep01.wav") ,   // 106
			loadSound("./seventh/sfx/player/footsteps/mech_footstep02.wav") ,   // 107
			
			loadSound("./seventh/sfx/player/mech_torso_move.wav") ,   // 108
			
			loadSound("./seventh/sfx/player/breathing_lite01.wav") ,   // 109
			loadSound("./seventh/sfx/player/breathing_lite02.wav") ,   // 110
			loadSound("./seventh/sfx/player/breathing_lite03.wav") ,   // 111
			
			loadSound("./seventh/sfx/player/breathing_heavy01.wav") ,   // 112
			loadSound("./seventh/sfx/player/breathing_heavy02.wav") ,   // 113
			loadSound("./seventh/sfx/player/breathing_heavy03.wav") ,   // 114
			
			loadSound("./seventh/sfx/bullet_zing01.wav") ,   // 115
            loadSound("./seventh/sfx/bullet_zing02.wav") ,   // 116
            loadSound("./seventh/sfx/bullet_zing03.wav") ,   // 117
            
//            loadSound("./seventh/sfx/tank/movement_start.wav") ,   // 118
//            loadSound("./seventh/sfx/tank/movement_loop.wav") ,   // 119
            null,null,
            loadSound("./seventh/sfx/player/healthpack_pickup.wav") ,   // 120
            
            loadSound("./seventh/sfx/player/speech/allied/attack.wav") ,   // 121
            loadSound("./seventh/sfx/player/speech/allied/cover_me.wav") ,   // 122
            loadSound("./seventh/sfx/player/speech/allied/follow_me.wav") ,   // 123
            loadSound("./seventh/sfx/player/speech/allied/get_out_of_my_way.wav") ,   // 124
            loadSound("./seventh/sfx/player/speech/allied/get_us_killed.wav") ,   // 125
            loadSound("./seventh/sfx/player/speech/allied/hold_position.wav") ,   // 126
            loadSound("./seventh/sfx/player/speech/allied/ill_cover_you.wav") ,   // 127
            loadSound("./seventh/sfx/player/speech/allied/taking_fire_help.wav") ,   // 128
            loadSound("./seventh/sfx/player/speech/allied/you_take_lead.wav") ,   // 129
            
            
            loadSound("./seventh/sfx/player/speech/axis/attack.wav") ,   // 130
            loadSound("./seventh/sfx/player/speech/axis/cover_me.wav") ,   // 131
            loadSound("./seventh/sfx/player/speech/axis/follow_me.wav") ,   // 132
            loadSound("./seventh/sfx/player/speech/axis/get_out_of_my_way.wav") ,   // 133
            loadSound("./seventh/sfx/player/speech/axis/get_us_killed.wav") ,   // 134
            loadSound("./seventh/sfx/player/speech/axis/hold_position.wav") ,   // 135
            loadSound("./seventh/sfx/player/speech/axis/ill_cover_you.wav") ,   // 136
            loadSound("./seventh/sfx/player/speech/axis/taking_fire_help.wav") ,   // 137
            loadSound("./seventh/sfx/player/speech/axis/you_take_lead.wav") ,   // 138
            
            loadSound("./seventh/sfx/tank/tank_on.wav") ,   // 139
            loadSound("./seventh/sfx/tank/tank_off.wav") ,   // 140
            loadSound("./seventh/sfx/tank/tank_idle.wav") ,   // 141
            loadSound("./seventh/sfx/tank/tank_shift1.wav") ,   // 142
            loadSound("./seventh/sfx/tank/tank_shift2.wav") ,   // 143
            loadSound("./seventh/sfx/tank/tank_shift3.wav") ,   // 144
            loadSound("./seventh/sfx/tank/tank_revup.wav") ,   // 145
            loadSound("./seventh/sfx/tank/tank_revdown.wav") ,   // 146
            loadSound("./seventh/sfx/tank/tank_turret2.wav") ,   // 147
            loadSound("./seventh/sfx/tank/tank_move.wav") ,   // 148
		};
	};

	private static SoundSystem soundSystem;
	
	public static void init(ClientSeventhConfig cfg) {
		try {
			Cons.println("Initializing the sound subsystem...");
			Cons.getImpl().addCommand(getVolumeCommand());
			
			config = cfg;
			volume = config.getVolume();
			
			SoundSystemConfig.setMasterGain(volume);					
			SoundSystemConfig.setLogger(new SoundSystemLogger() {
				@Override
				public void errorMessage(String message, String error, int code) {
					Cons.println("*** Error in the sound system: " + message + " " + error + " :: " + code);
				}
				@Override
				public void message(String message, int code) {
					Cons.println("*** Sound system: " + message + " :: " + code);
				}
				
				@Override
				public void importantMessage(String message, int code) {
					Cons.println("*** (I) Sound system: " + message + " :: " + code);
				}
			});
			SoundSystemConfig.addLibrary(LibraryLWJGLOpenAL.class);
			SoundSystemConfig.setCodec( "wav", CodecWav.class );
			
			soundSystem = new SoundSystem(LibraryLWJGLOpenAL.class);
			setVolume(volume);		
			
			for(int i = 0; i < channels.length; i++) {
				channels[i] = createChannel();
			}
			
			Cons.println("Sound system online!");
		}
		catch(SoundSystemException e) {
			Cons.println("Unable to initialize the sound plugins.");
		}
	}
	
	/**
	 * @param volume the volume to set
	 */
	public static void setVolume(float volume) {
		Sounds.volume = volume;		
		soundSystem.setMasterVolume(volume);
		if(config!=null) {			
			config.setVolume(volume);
		}
	}
	
	/**
	 * @return the volume
	 */
	public static float getVolume() {
		return volume;
	}
	
	
	private static Command getVolumeCommand() {
		return new Command("volume") {
			
			@Override
			public void execute(Console console, String... args) {
				if(args == null || args.length < 1) {
					console.println(volume);
				}
				else {
					try {
						float v = Float.parseFloat(args[0]);
						setVolume(v);
					}
					catch(Exception e) {
						console.println("*** Must be a number between 0 and 1");
					}
				}
			}
		};
	}
	
	public static void setPosition(Vector2f pos) {
		if(soundSystem!=null) {
			soundSystem.setListenerPosition(pos.x, pos.y, 0);
		}
	}
	
	public static synchronized void destroy() {
		if(soundSystem!=null) {
			for(Sound sound : loadedSounds.values()) {
				sound.destroy();
			}
			loadedSounds.clear();
			
			soundSystem.removeTemporarySources();
			try {
				Thread.sleep(500);
			} catch (InterruptedException e) {
			}
			try {soundSystem.cleanup(); } catch(Exception e) {}
			
		}
	}
	
	
	
	/**
	 * Attempts to load a {@link Sound}
	 * 
	 * @param soundFile
	 * @return the {@link Sound} if loaded successfully
	 */
	public static synchronized Sound loadSound(String soundFile) {
		try {			
			if(loadedSounds.containsKey(soundFile)) {
				return loadedSounds.get(soundFile);
			}
			Sound sound = new Sound(soundFile, soundSystem);
			loadedSounds.put(soundFile, sound);
			
			return sound;
		}
		catch(Exception e) {
			Cons.println("*** Error loading sound: " + soundFile + " - " + e);
		}
		
		return null;
	}
	
	private static final Random random = new Random();
	public static Sound findFreeSound(int soundIndex) {
		
		for(int i = 0; i < channels.length; i++) {
			Sound[] sounds = channels[i];		
			Sound sound = sounds[soundIndex];
			if(!sound.isPlaying()) return sound;
		}
		
		return null;
	}
	
	public static Sound startPlaySound(int[] soundBank, long channelId, Vector2f pos) {
		return startPlaySound(soundBank, channelId, pos.x, pos.y);
	}
	
	public static Sound startPlaySound(int[] soundBank, long channelId, float x, float y) {
		int index = random.nextInt(soundBank.length);
		int soundIndex = soundBank[index];
		
		Sound[] sounds = channels[ (int)channelId % channels.length];		
		Sound sound = sounds[soundIndex];
		
//		Sound sound = findFreeSound(soundIndex);
//		if(sound!=null) 
		{
	//		sound.reset();
			sound.stop();
			sound.setVolume(volume); // TODO global config
			sound.play(x,y);	
		}
		
		return sound;
	}
	
	/**
	 * Plays the sound right next to the sound listener so it is
	 * always audible.
	 * @param soundBank
	 * @return the {@link Sound}
	 */
	public static Sound playGlobalSound(int[] soundBank) {
		float x = 0;
		float y = 0;
		if(soundSystem != null) {
			ListenerData data = soundSystem.getListenerData();
			x = data.position.x;
			y = data.position.y;
		}
		return playSound(soundBank, uiChannel, x, y);
	}
	
	/**
	 * Plays a sound at a particular position.
	 * 
	 * @param soundBank
	 * @param channelId
	 * @param pos
	 * @return
	 */
	public static Sound playSound(int[] soundBank, long channelId, Vector2f pos) {
		return playSound(soundBank, channelId, pos.x, pos.y);
	}
	
	
	/**
	 * Plays a sound at a particular position.
	 * 
	 * @param soundBank
	 * @param channelId
	 * @param x
	 * @param y
	 * @return
	 */
	public static Sound playSound(int[] soundBank, long channelId, float x, float y) {
		int index = random.nextInt(soundBank.length);
		int soundIndex = soundBank[index];
		
		Sound[] sounds = channels[ (int)channelId % channels.length];		
		Sound sound = sounds[soundIndex];
		if(!sound.isPlaying()) {
			sound.reset();
			sound.setVolume(volume); // TODO global config
			sound.play(x,y);
		}
		
		return sound;
	}

	public static Sound playFreeSound(int[] soundBank, Vector2f pos) {
		return playFreeSound(soundBank, pos.x, pos.y);
	}
	
	public static Sound playFreeSound(int[] soundBank, float x, float y) {
		int index = random.nextInt(soundBank.length);
		int soundIndex = soundBank[index];
		Sound snd = findFreeSound(soundIndex);
		if(snd!=null) {
			snd.reset();
			snd.setVolume(volume); // TODO global config
			snd.play(x,y);
		}
		return snd;
	}
	
	public static Sound playSound(byte soundId, float x, float y) {
		SoundType type = SoundType.fromNet(soundId);
		return playSound(type, x, y);
	}
	
	public static Sound playSound(NetSound sound, float x, float y) {
		SoundType type = SoundType.fromNet(sound.type);
		return playSound(type, x, y);
	}
	
	public static Sound playSpeechSound(int teamId, byte speech, float x, float y) {
		Sound sound = null;
		if(speech > -1 && speech < axisSpeeches.length) {
			
			if(teamId == ClientTeam.AXIS.getId()) {
				sound = playFreeSound(axisSpeeches[speech], x, y);
			}
			else if(teamId == ClientTeam.ALLIES.getId()) {
				sound = playFreeSound(alliedSpeeches[speech], x, y);
			}
		}
		
		return (sound);
	}
	
	public static Sound playSound(SoundType type, float x, float y) {	
		Sound sound = null;
		switch(type) {
		case EMPTY_FIRE: 
			sound = playFreeSound(emptyFireSnd, x, y);
			break;
			
		case THOMPSON_FIRE: 
			sound = playFreeSound(thompsonFire, x, y);
			break;
		case THOMPSON_RELOAD: 
			sound = playFreeSound(thompsonReload, x, y);
			break;
		
		
		case EXPLOSION:
			sound = playFreeSound(explodeSnd, x, y);
			break;		
		case RPG_FIRE:
			sound = playFreeSound(rocketFire, x, y);
			break;
			
		case GRENADE_PINPULLED:
			sound = playFreeSound(grenadePinPulled, x, y);
			break;			
		case GRENADE_THROW:
			sound = playFreeSound(grenadeThrow, x, y);
			break;
			
		case SHOTGUN_FIRE:
			sound = playFreeSound(shotgunFire, x, y);
			break;
		case SHOTGUN_RELOAD: 
			sound = playFreeSound(shotgunReload, x, y);
			break;
		case SHOTGUN_PUMP:
			sound = playFreeSound(shotgunPump, x, y);
			break;			
			
		case SPRINGFIELD_FIRE:
			sound = playFreeSound(springfieldFire, x, y);			
			break;
		case SPRINGFIELD_RECHAMBER:
			sound = playFreeSound(springfieldRechamber, x, y);
			break;
		case SPRINGFIELD_RELOAD: 
			sound = playFreeSound(springfieldReload, x, y);
			break;
		
		case RISKER_FIRE:
			sound = playFreeSound(riskerFire, x, y);			
			break;
		case RISKER_RECHAMBER:
			sound = playFreeSound(riskerRechamber, x, y);
			break;
		case RISKER_RELOAD: 
			sound = playFreeSound(riskerReload, x, y);
			break;
			
		case M1_GARAND_FIRE: 
			sound = playFreeSound(m1Fire, x, y);
			break;
		case M1_GARAND_LAST_FIRE:
			sound = playFreeSound(m1FireLast, x, y);
			break;
		case M1_GARAND_RELOAD:
			sound = playFreeSound(m1Reload, x, y);
			break;
			
		case KAR98_FIRE:
			sound = playFreeSound(kar98Fire, x, y);
			break;
		case KAR98_RECHAMBER:
			sound = playFreeSound(kar98Rechamber, x, y);
			break;
		case KAR98_RELOAD:
			sound = playFreeSound(kar98Reload, x, y);
			break;
		
		case MP44_FIRE:
			sound = playFreeSound(mp44Fire, x, y);
			break;
		case MP44_RELOAD:
			sound = playFreeSound(mp44Reload, x, y);
			break;
			
		case MP40_FIRE:
			sound = playFreeSound(mp40Fire, x, y);
			break;
		case MP40_RELOAD:
			sound = playFreeSound(mp40Reload, x, y);
			break;			
			
		case SURFACE_GRASS:			
			sound = playFreeSound(grassWalk, x, y);			
			break;
		case SURFACE_METAL:
			sound = playFreeSound(metalWalk, x, y);
			break;
		case SURFACE_NORMAL:
			sound = playFreeSound(normalWalk, x, y);
			break;
		case SURFACE_WATER:
			sound = playFreeSound(waterWalk, x, y);
			break;
		case SURFACE_WOOD:
			sound = playFreeSound(woodWalk, x, y);
			break;
		case SURFACE_DIRT: 
			sound = playFreeSound(dirtWalk, x, y);
			break;
		case SURFACE_SAND: 
			sound = playFreeSound(dirtWalk, x, y);
			break;
		case WEAPON_SWITCH:
			sound = playFreeSound(weaponSwitch, x, y);
			break;
		case RUFFLE:
			sound = playFreeSound(ruffle, x, y);
			break;
		case BOMB_TICK:
			sound = playFreeSound(bombTick, x, y);
			break;
		case BOMB_PLANT:
			sound = playFreeSound(bombPlant, x, y);
			break;
		case BOMB_DISARM:
			sound = playFreeSound(bombDisarm, x, y);
			break;			
		case WEAPON_DROPPED:
			sound = playFreeSound(weaponDrop, x, y);
			break;
		case WEAPON_PICKUP:		
			sound = playFreeSound(weaponPickupSnd, x, y);
			break;
		case AMMO_PICKUP:
			sound = playFreeSound(ammoPickupSnd, x, y);
			break;
		case MELEE_SWING:
			sound = playFreeSound(meleeSwing, x, y);
			break;
		case MELEE_HIT:
			sound = playFreeSound(meleeHit, x, y);
			break;
		case PISTOL_FIRE:
			sound = playFreeSound(pistolFire, x, y);
			break;
		case PISTOL_RELOAD:
			sound = playFreeSound(pistolReload, x, y);
			break;
		case UI_ELEMENT_HOVER:
			sound = playFreeSound(uiHover, x, y);
			break;
		case UI_ELEMENT_SELECT:
			sound = playFreeSound(uiSelect, x, y);
			break;			
		case UI_NAVIGATE:
			sound = playFreeSound(uiNavigate, x, y);
			break;				
		case UI_KEY_TYPE:
			sound = playFreeSound(uiKeyType, x, y);
			break;
		case IMPACT_METAL:
			sound = playFreeSound(impactMetal, x, y);
			break;
		case IMPACT_DEFAULT:
			sound = playFreeSound(impactDefault, x, y);
			break;
		case IMPACT_FOLIAGE:			
			sound = playFreeSound(impactFoliage, x, y);
			break;
		case IMPACT_WOOD:
			sound = playFreeSound(impactWood, x, y);
			break;
		case TANK_ON: 
			sound = playFreeSound(tankOn, x, y);
			break;
		case TANK_OFF: 
			sound = playFreeSound(tankOff, x, y);
			break;
		case TANK_REV_UP: 
			sound = playFreeSound(tankRevUp, x, y);
			break;			
		case TANK_REV_DOWN: 
			sound = playFreeSound(tankRevDown, x, y);
			break;			
		case TANK_IDLE: 
			sound = playFreeSound(tankIdle, x, y);
			break;			
		case TANK_SHIFT: 
			sound = playFreeSound(tankShift, x, y);
			break;			
		case TANK_TURRET_MOVE: 
			sound = playFreeSound(tankTurret, x, y);
			break;					
		case TANK_MOVE: 
			sound = playFreeSound(tankMove, x, y);
			break;					
		case BREATH_HEAVY: 
			sound = playFreeSound(breadthHeavy, x, y);
			break;
		case BREATH_LITE:
			sound = playFreeSound(breadthLite, x, y);
			break;
		case HEALTH_PACK_PICKUP:
			sound = playFreeSound(healthPackPickup, x, y);
		    break;
		case MUTE:
			
		default:
			break;
		}
		return sound;
	}

}
