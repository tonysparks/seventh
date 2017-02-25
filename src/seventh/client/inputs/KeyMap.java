/*
 * see license.txt 
 */
package seventh.client.inputs;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

import com.badlogic.gdx.Input.Buttons;
import com.badlogic.gdx.Input.Keys;

import leola.vm.Leola;
import leola.vm.types.LeoMap;
import leola.vm.types.LeoObject;
import seventh.client.inputs.ControllerInput.ControllerButtons;

/**
 * Handles Player key bindings
 * 
 * @author Tony
 *
 */
public class KeyMap {

    private LeoMap config;
    private LeoMap joystick;
    
    /**
     * Keyboard settings
     */
    private int reloadKey,
                walkKey,
                crouchKey,
                sprintKey,
                upKey,
                downKey,
                leftKey,
                rightKey,
                fireKey,
                throwGrenadeKey,
                useKey,
                dropWeaponKey,
                meleeAttackKey,
                
                sayKey,
                teamSayKey;
    
    private boolean invertMouse;
    
    /**
     * Joystick settings
     */
    private ControllerInput.ControllerButtons reloadBtn,
                walkBtn,
                crouchBtn,
                sprintBtn,
                upBtn,
                downBtn,
                leftBtn,
                rightBtn,
                fireBtn,
                throwGrenadeBtn,
                useBtn,
                dropWeaponBtn,
                meleeAttackBtn;
    
    private boolean invertJoystick;
    private boolean isSouthPaw;
    
    private static final int NumberOfKeyboardKeys = 256; /* this includes mouse buttons and keyboard keys */
    private static final int NumberOfJoystickButtons = 20;
    
    private static final String[] stringKey = new String[NumberOfKeyboardKeys + NumberOfJoystickButtons];        
    private static final Map<String, Integer> keyMap = new HashMap<String, Integer>();
    static {
        keyMap.put("any_key", Keys.ANY_KEY); 
        keyMap.put("num_0", Keys.NUM_0); 
        keyMap.put("num_1", Keys.NUM_1); 
        keyMap.put("num_2", Keys.NUM_2); 
        keyMap.put("num_3", Keys.NUM_3); 
        keyMap.put("num_4", Keys.NUM_4); 
        keyMap.put("num_5", Keys.NUM_5); 
        keyMap.put("num_6", Keys.NUM_6); 
        keyMap.put("num_7", Keys.NUM_7); 
        keyMap.put("num_8", Keys.NUM_8); 
        keyMap.put("num_9", Keys.NUM_9); 
        keyMap.put("a", Keys.A); 
        keyMap.put("alt_left", Keys.ALT_LEFT); 
        keyMap.put("alt_right", Keys.ALT_RIGHT); 
        keyMap.put("apostrophe", Keys.APOSTROPHE); 
        keyMap.put("at", Keys.AT); 
        keyMap.put("b", Keys.B); 
        keyMap.put("back", Keys.BACK); 
        keyMap.put("backslash", Keys.BACKSLASH); 
        keyMap.put("c", Keys.C); 
        keyMap.put("call", Keys.CALL); 
        keyMap.put("camera", Keys.CAMERA); 
        keyMap.put("clear", Keys.CLEAR); 
        keyMap.put("comma", Keys.COMMA); 
        keyMap.put("d", Keys.D); 
        keyMap.put("del", Keys.DEL); 
        keyMap.put("backspace", Keys.BACKSPACE); 
        keyMap.put("forward_del", Keys.FORWARD_DEL); 
        keyMap.put("dpad_center", Keys.DPAD_CENTER); 
        keyMap.put("dpad_down", Keys.DPAD_DOWN); 
        keyMap.put("dpad_left", Keys.DPAD_LEFT); 
        keyMap.put("dpad_right", Keys.DPAD_RIGHT); 
        keyMap.put("dpad_up", Keys.DPAD_UP); 
        keyMap.put("center", Keys.CENTER); 
        keyMap.put("down", Keys.DOWN); 
        keyMap.put("left", Keys.LEFT); 
        keyMap.put("right", Keys.RIGHT); 
        keyMap.put("up", Keys.UP); 
        keyMap.put("e", Keys.E); 
        keyMap.put("endcall", Keys.ENDCALL); 
        keyMap.put("enter", Keys.ENTER); 
        keyMap.put("envelope", Keys.ENVELOPE); 
        keyMap.put("equals", Keys.EQUALS); 
        keyMap.put("explorer", Keys.EXPLORER); 
        keyMap.put("f", Keys.F); 
        keyMap.put("focus", Keys.FOCUS); 
        keyMap.put("g", Keys.G); 
        keyMap.put("grave", Keys.GRAVE); 
        keyMap.put("h", Keys.H); 
        keyMap.put("headsethook", Keys.HEADSETHOOK); 
        keyMap.put("home", Keys.HOME); 
        keyMap.put("i", Keys.I); 
        keyMap.put("j", Keys.J); 
        keyMap.put("k", Keys.K); 
        keyMap.put("l", Keys.L); 
        keyMap.put("left_bracket", Keys.LEFT_BRACKET); 
        keyMap.put("m", Keys.M); 
        keyMap.put("media_fast_forward", Keys.MEDIA_FAST_FORWARD); 
        keyMap.put("media_next", Keys.MEDIA_NEXT); 
        keyMap.put("media_play_pause", Keys.MEDIA_PLAY_PAUSE); 
        keyMap.put("media_previous", Keys.MEDIA_PREVIOUS); 
        keyMap.put("media_rewind", Keys.MEDIA_REWIND); 
        keyMap.put("media_stop", Keys.MEDIA_STOP); 
        keyMap.put("menu", Keys.MENU); 
        keyMap.put("minus", Keys.MINUS); 
        keyMap.put("mute", Keys.MUTE); 
        keyMap.put("n", Keys.N); 
        keyMap.put("notification", Keys.NOTIFICATION); 
        keyMap.put("num", Keys.NUM); 
        keyMap.put("o", Keys.O); 
        keyMap.put("p", Keys.P); 
        keyMap.put("period", Keys.PERIOD); 
        keyMap.put("plus", Keys.PLUS); 
        keyMap.put("pound", Keys.POUND); 
        keyMap.put("power", Keys.POWER); 
        keyMap.put("q", Keys.Q); 
        keyMap.put("r", Keys.R); 
        keyMap.put("right_bracket", Keys.RIGHT_BRACKET); 
        keyMap.put("s", Keys.S); 
        keyMap.put("search", Keys.SEARCH); 
        keyMap.put("semicolon", Keys.SEMICOLON); 
        keyMap.put("shift_left", Keys.SHIFT_LEFT); 
        keyMap.put("shift_right", Keys.SHIFT_RIGHT); 
        keyMap.put("slash", Keys.SLASH); 
        keyMap.put("soft_left", Keys.SOFT_LEFT); 
        keyMap.put("soft_right", Keys.SOFT_RIGHT); 
        keyMap.put("space", Keys.SPACE); 
        keyMap.put("star", Keys.STAR); 
        keyMap.put("sym", Keys.SYM); 
        keyMap.put("t", Keys.T); 
        keyMap.put("tab", Keys.TAB); 
        keyMap.put("u", Keys.U); 
        keyMap.put("unknown", Keys.UNKNOWN); 
        keyMap.put("v", Keys.V); 
        keyMap.put("volume_down", Keys.VOLUME_DOWN); 
        keyMap.put("volume_up", Keys.VOLUME_UP); 
        keyMap.put("w", Keys.W); 
        keyMap.put("x", Keys.X); 
        keyMap.put("y", Keys.Y); 
        keyMap.put("z", Keys.Z); 
        keyMap.put("meta_alt_left_on", Keys.META_ALT_LEFT_ON); 
        keyMap.put("meta_alt_on", Keys.META_ALT_ON); 
        keyMap.put("meta_alt_right_on", Keys.META_ALT_RIGHT_ON); 
        keyMap.put("meta_shift_left_on", Keys.META_SHIFT_LEFT_ON); 
        keyMap.put("meta_shift_on", Keys.META_SHIFT_ON); 
        keyMap.put("meta_shift_right_on", Keys.META_SHIFT_RIGHT_ON); 
        keyMap.put("meta_sym_on", Keys.META_SYM_ON); 
        keyMap.put("control_left", Keys.CONTROL_LEFT); 
        keyMap.put("control_right", Keys.CONTROL_RIGHT); 
        keyMap.put("escape", Keys.ESCAPE); 
        keyMap.put("end", Keys.END); 
        keyMap.put("insert", Keys.INSERT); 
        keyMap.put("page_up", Keys.PAGE_UP); 
        keyMap.put("page_down", Keys.PAGE_DOWN); 
        keyMap.put("pictsymbols", Keys.PICTSYMBOLS); 
        keyMap.put("switch_charset", Keys.SWITCH_CHARSET); 
        keyMap.put("button_circle", Keys.BUTTON_CIRCLE); 
        keyMap.put("button_a", Keys.BUTTON_A); 
        keyMap.put("button_b", Keys.BUTTON_B); 
        keyMap.put("button_c", Keys.BUTTON_C); 
        keyMap.put("button_x", Keys.BUTTON_X); 
        keyMap.put("button_y", Keys.BUTTON_Y); 
        keyMap.put("button_z", Keys.BUTTON_Z); 
        keyMap.put("button_l1", Keys.BUTTON_L1); 
        keyMap.put("button_r1", Keys.BUTTON_R1); 
        keyMap.put("button_l2", Keys.BUTTON_L2); 
        keyMap.put("button_r2", Keys.BUTTON_R2); 
        keyMap.put("button_thumbl", Keys.BUTTON_THUMBL); 
        keyMap.put("button_thumbr", Keys.BUTTON_THUMBR); 
        keyMap.put("button_start", Keys.BUTTON_START); 
        keyMap.put("button_select", Keys.BUTTON_SELECT); 
        keyMap.put("button_mode", Keys.BUTTON_MODE); 
        keyMap.put("numpad_0", Keys.NUMPAD_0); 
        keyMap.put("numpad_1", Keys.NUMPAD_1); 
        keyMap.put("numpad_2", Keys.NUMPAD_2); 
        keyMap.put("numpad_3", Keys.NUMPAD_3); 
        keyMap.put("numpad_4", Keys.NUMPAD_4); 
        keyMap.put("numpad_5", Keys.NUMPAD_5); 
        keyMap.put("numpad_6", Keys.NUMPAD_6); 
        keyMap.put("numpad_7", Keys.NUMPAD_7); 
        keyMap.put("numpad_8", Keys.NUMPAD_8); 
        keyMap.put("numpad_9", Keys.NUMPAD_9); 
        keyMap.put("colon", Keys.COLON); 
        keyMap.put("f1", Keys.F1); 
        keyMap.put("f2", Keys.F2); 
        keyMap.put("f3", Keys.F3); 
        keyMap.put("f4", Keys.F4); 
        keyMap.put("f5", Keys.F5); 
        keyMap.put("f6", Keys.F6); 
        keyMap.put("f7", Keys.F7); 
        keyMap.put("f8", Keys.F8); 
        keyMap.put("f9", Keys.F9); 
        keyMap.put("f10", Keys.F10); 
        keyMap.put("f11", Keys.F11); 
        keyMap.put("f12", Keys.F12); 
        
        keyMap.put("mouse_button_left", Buttons.LEFT);
        keyMap.put("mouse_button_middle", Buttons.MIDDLE);
        keyMap.put("mouse_button_right", Buttons.RIGHT);
        
        keyMap.put("js_left_trigger_btn", ControllerButtons.LEFT_TRIGGER_BTN.getKey());
        keyMap.put("js_right_trigger_btn", ControllerButtons.RIGHT_TRIGGER_BTN.getKey());
        
        keyMap.put("js_left_joystick_btn", ControllerButtons.LEFT_JOYSTICK_BTN.getKey());
        keyMap.put("js_right_joystick_btn", ControllerButtons.RIGHT_JOYSTICK_BTN.getKey());
        
        keyMap.put("js_y_btn", ControllerButtons.Y_BTN.getKey());
        keyMap.put("js_x_btn", ControllerButtons.X_BTN.getKey());
        keyMap.put("js_a_btn", ControllerButtons.A_BTN.getKey());
        keyMap.put("js_b_btn", ControllerButtons.B_BTN.getKey());
        
        keyMap.put("js_start_btn", ControllerButtons.START_BTN.getKey());
        keyMap.put("js_select_btn", ControllerButtons.SELECT_BTN.getKey());
        keyMap.put("js_left_bumper_btn", ControllerButtons.LEFT_BUMPER_BTN.getKey());
        keyMap.put("js_right_bumper_btn", ControllerButtons.RIGHT_BUMPER_BTN.getKey());
        
        keyMap.put("js_north_dpad_btn", ControllerButtons.NORTH_DPAD_BTN.getKey());
        keyMap.put("js_ne_dpad_btn", ControllerButtons.NE_DPAD_BTN.getKey());
        keyMap.put("js_east_dpad_btn", ControllerButtons.EAST_DPAD_BTN.getKey());
        keyMap.put("js_se_dpad_btn", ControllerButtons.SE_DPAD_BTN.getKey());
        keyMap.put("js_south_dpad_btn", ControllerButtons.SOUTH_DPAD_BTN.getKey());
        keyMap.put("js_sw_dpad_btn", ControllerButtons.SW_DPAD_BTN.getKey());
        keyMap.put("js_west_dpad_btn", ControllerButtons.WEST_DPAD_BTN.getKey());
        keyMap.put("js_nw_dpad_btn", ControllerButtons.NW_DPAD_BTN.getKey());        
        
        stringKey[Keys.NUM_0] = "num_0"; 
        stringKey[Keys.NUM_1] = "num_1"; 
        stringKey[Keys.NUM_2] = "num_2"; 
        stringKey[Keys.NUM_3] = "num_3"; 
        stringKey[Keys.NUM_4] = "num_4"; 
        stringKey[Keys.NUM_5] = "num_5"; 
        stringKey[Keys.NUM_6] = "num_6"; 
        stringKey[Keys.NUM_7] = "num_7"; 
        stringKey[Keys.NUM_8] = "num_8"; 
        stringKey[Keys.NUM_9] = "num_9"; 
        stringKey[Keys.A] = "a"; 
        stringKey[Keys.ALT_LEFT] = "alt_left"; 
        stringKey[Keys.ALT_RIGHT] = "alt_right"; 
        stringKey[Keys.APOSTROPHE] = "apostrophe"; 
        stringKey[Keys.AT] = "at"; 
        stringKey[Keys.B] = "b"; 
        stringKey[Keys.BACK] = "back"; 
        stringKey[Keys.BACKSLASH] = "backslash"; 
        stringKey[Keys.C] = "c"; 
        stringKey[Keys.CALL] = "call"; 
        stringKey[Keys.CAMERA] = "camera"; 
        stringKey[Keys.CLEAR] = "clear"; 
        stringKey[Keys.COMMA] = "comma"; 
        stringKey[Keys.D] = "d"; 
        stringKey[Keys.DEL] = "del"; 
        stringKey[Keys.BACKSPACE] = "backspace"; 
        stringKey[Keys.FORWARD_DEL] = "forward_del"; 
        stringKey[Keys.DPAD_CENTER] = "dpad_center"; 
        stringKey[Keys.DPAD_DOWN] = "dpad_down"; 
        stringKey[Keys.DPAD_LEFT] = "dpad_left"; 
        stringKey[Keys.DPAD_RIGHT] = "dpad_right"; 
        stringKey[Keys.DPAD_UP] = "dpad_up"; 
        stringKey[Keys.CENTER] = "center"; 
        stringKey[Keys.DOWN] = "down"; 
        stringKey[Keys.LEFT] = "left"; 
        stringKey[Keys.RIGHT] = "right"; 
        stringKey[Keys.UP] = "up"; 
        stringKey[Keys.E] = "e"; 
        stringKey[Keys.ENDCALL] = "endcall"; 
        stringKey[Keys.ENTER] = "enter"; 
        stringKey[Keys.ENVELOPE] = "envelope"; 
        stringKey[Keys.EQUALS] = "equals"; 
        stringKey[Keys.EXPLORER] = "explorer"; 
        stringKey[Keys.F] = "f"; 
        stringKey[Keys.FOCUS] = "focus"; 
        stringKey[Keys.G] = "g"; 
        stringKey[Keys.GRAVE] = "grave"; 
        stringKey[Keys.H] = "h"; 
        stringKey[Keys.HEADSETHOOK] = "headsethook"; 
        stringKey[Keys.HOME] = "home"; 
        stringKey[Keys.I] = "i"; 
        stringKey[Keys.J] = "j"; 
        stringKey[Keys.K] = "k"; 
        stringKey[Keys.L] = "l"; 
        stringKey[Keys.LEFT_BRACKET] = "left_bracket"; 
        stringKey[Keys.M] = "m"; 
        stringKey[Keys.MEDIA_FAST_FORWARD] = "media_fast_forward"; 
        stringKey[Keys.MEDIA_NEXT] = "media_next"; 
        stringKey[Keys.MEDIA_PLAY_PAUSE] = "media_play_pause"; 
        stringKey[Keys.MEDIA_PREVIOUS] = "media_previous"; 
        stringKey[Keys.MEDIA_REWIND] = "media_rewind"; 
        stringKey[Keys.MEDIA_STOP] = "media_stop"; 
        stringKey[Keys.MENU] = "menu"; 
        stringKey[Keys.MINUS] = "minus"; 
        stringKey[Keys.MUTE] = "mute"; 
        stringKey[Keys.N] = "n"; 
        stringKey[Keys.NOTIFICATION] = "notification"; 
        stringKey[Keys.NUM] = "num"; 
        stringKey[Keys.O] = "o"; 
        stringKey[Keys.P] = "p"; 
        stringKey[Keys.PERIOD] = "period"; 
        stringKey[Keys.PLUS] = "plus"; 
        stringKey[Keys.POUND] = "pound"; 
        stringKey[Keys.POWER] = "power"; 
        stringKey[Keys.Q] = "q"; 
        stringKey[Keys.R] = "r"; 
        stringKey[Keys.RIGHT_BRACKET] = "right_bracket"; 
        stringKey[Keys.S] = "s"; 
        stringKey[Keys.SEARCH] = "search"; 
        stringKey[Keys.SEMICOLON] = "semicolon"; 
        stringKey[Keys.SHIFT_LEFT] = "shift_left"; 
        stringKey[Keys.SHIFT_RIGHT] = "shift_right"; 
        stringKey[Keys.SLASH] = "slash"; 
//        stringKey[Keys.SOFT_LEFT] = "soft_left"; 
//        stringKey[Keys.SOFT_RIGHT] = "soft_right"; 
        stringKey[Keys.SPACE] = "space"; 
        stringKey[Keys.STAR] = "star"; 
        stringKey[Keys.SYM] = "sym"; 
        stringKey[Keys.T] = "t"; 
        stringKey[Keys.TAB] = "tab"; 
        stringKey[Keys.U] = "u"; 
//        stringKey[Keys.UNKNOWN] = "unknown"; 
        stringKey[Keys.V] = "v"; 
        //stringKey[Keys.VOLUME_DOWN] = "volume_down"; 
        //stringKey[Keys.VOLUME_UP] = "volume_up"; 
        stringKey[Keys.W] = "w"; 
        stringKey[Keys.X] = "x"; 
        stringKey[Keys.Y] = "y"; 
        stringKey[Keys.Z] = "z"; 
        /*stringKey[Keys.META_ALT_LEFT_ON] = "meta_alt_left_on"; 
        stringKey[Keys.META_ALT_ON] = "meta_alt_on"; 
        stringKey[Keys.META_ALT_RIGHT_ON] = "meta_alt_right_on"; 
        stringKey[Keys.META_SHIFT_LEFT_ON] = "meta_shift_left_on"; 
        stringKey[Keys.META_SHIFT_ON] = "meta_shift_on"; 
        stringKey[Keys.META_SHIFT_RIGHT_ON] = "meta_shift_right_on"; 
        stringKey[Keys.META_SYM_ON] = "meta_sym_on"; */
        stringKey[Keys.CONTROL_LEFT] = "control_left"; 
        stringKey[Keys.CONTROL_RIGHT] = "control_right"; 
        stringKey[Keys.ESCAPE] = "escape"; 
        stringKey[Keys.END] = "end"; 
        stringKey[Keys.INSERT] = "insert"; 
        stringKey[Keys.PAGE_UP] = "page_up"; 
        stringKey[Keys.PAGE_DOWN] = "page_down"; 
    //    stringKey[Keys.PICTSYMBOLS] = "pictsymbols"; 
    //    stringKey[Keys.SWITCH_CHARSET] = "switch_charset"; 
        stringKey[Keys.BUTTON_CIRCLE] = "button_circle"; 
        stringKey[Keys.BUTTON_A] = "button_a"; 
        stringKey[Keys.BUTTON_B] = "button_b"; 
        stringKey[Keys.BUTTON_C] = "button_c"; 
        stringKey[Keys.BUTTON_X] = "button_x"; 
        stringKey[Keys.BUTTON_Y] = "button_y"; 
        stringKey[Keys.BUTTON_Z] = "button_z"; 
        stringKey[Keys.BUTTON_L1] = "button_l1"; 
        stringKey[Keys.BUTTON_R1] = "button_r1"; 
        stringKey[Keys.BUTTON_L2] = "button_l2"; 
        stringKey[Keys.BUTTON_R2] = "button_r2"; 
        stringKey[Keys.BUTTON_THUMBL] = "button_thumbl"; 
        stringKey[Keys.BUTTON_THUMBR] = "button_thumbr"; 
        stringKey[Keys.BUTTON_START] = "button_start"; 
        stringKey[Keys.BUTTON_SELECT] = "button_select"; 
        stringKey[Keys.BUTTON_MODE] = "button_mode"; 
        stringKey[Keys.NUMPAD_0] = "numpad_0"; 
        stringKey[Keys.NUMPAD_1] = "numpad_1"; 
        stringKey[Keys.NUMPAD_2] = "numpad_2"; 
        stringKey[Keys.NUMPAD_3] = "numpad_3"; 
        stringKey[Keys.NUMPAD_4] = "numpad_4"; 
        stringKey[Keys.NUMPAD_5] = "numpad_5"; 
        stringKey[Keys.NUMPAD_6] = "numpad_6"; 
        stringKey[Keys.NUMPAD_7] = "numpad_7"; 
        stringKey[Keys.NUMPAD_8] = "numpad_8"; 
        stringKey[Keys.NUMPAD_9] = "numpad_9"; 
        stringKey[Keys.COLON] = "colon"; 
        stringKey[Keys.F1] = "f1"; 
        stringKey[Keys.F2] = "f2"; 
        stringKey[Keys.F3] = "f3"; 
        stringKey[Keys.F4] = "f4"; 
        stringKey[Keys.F5] = "f5"; 
        stringKey[Keys.F6] = "f6"; 
        stringKey[Keys.F7] = "f7"; 
        stringKey[Keys.F8] = "f8"; 
        stringKey[Keys.F9] = "f9"; 
        stringKey[Keys.F10] = "f10"; 
        stringKey[Keys.F11] = "f11"; 
        stringKey[Keys.F12] = "f12"; 

        // 0,1,2
        stringKey[Buttons.LEFT] = "mouse_button_left";
        stringKey[Buttons.RIGHT] = "mouse_button_right";
        stringKey[Buttons.MIDDLE] = "mouse_button_middle";        
        
        stringKey[ControllerButtons.LEFT_TRIGGER_BTN.getKey()] = "js_left_trigger_btn";
        stringKey[ControllerButtons.RIGHT_TRIGGER_BTN.getKey()] = "js_right_trigger_btn";
        
        stringKey[ControllerButtons.LEFT_JOYSTICK_BTN.getKey()] = "js_left_joystick_btn";
        stringKey[ControllerButtons.RIGHT_JOYSTICK_BTN.getKey()] = "js_right_joystick_btn";
        
        stringKey[ControllerButtons.Y_BTN.getKey()] = "js_y_btn";
        stringKey[ControllerButtons.X_BTN.getKey()] = "js_x_btn";
        stringKey[ControllerButtons.A_BTN.getKey()] = "js_a_btn";
        stringKey[ControllerButtons.B_BTN.getKey()] = "js_b_btn";
        
        stringKey[ControllerButtons.START_BTN.getKey()] = "js_start_btn";
        stringKey[ControllerButtons.SELECT_BTN.getKey()] = "js_select_btn";
        
        stringKey[ControllerButtons.LEFT_BUMPER_BTN.getKey()] = "js_left_bumper_btn";
        stringKey[ControllerButtons.RIGHT_BUMPER_BTN.getKey()] = "js_right_bumper_btn";
        
        stringKey[ControllerButtons.NORTH_DPAD_BTN.getKey()] = "js_north_dpad_btn";
        stringKey[ControllerButtons.NE_DPAD_BTN.getKey()] = "js_ne_dpad_btn";
        stringKey[ControllerButtons.EAST_DPAD_BTN.getKey()] = "js_east_dpad_btn";
        stringKey[ControllerButtons.SE_DPAD_BTN.getKey()] = "js_se_dpad_btn";
        stringKey[ControllerButtons.SOUTH_DPAD_BTN.getKey()] = "js_south_dpad_btn";
        stringKey[ControllerButtons.SW_DPAD_BTN.getKey()] = "js_sw_dpad_btn";
        stringKey[ControllerButtons.WEST_DPAD_BTN.getKey()] = "js_west_dpad_btn";
        stringKey[ControllerButtons.NW_DPAD_BTN.getKey()] = "js_nw_dpad_btn";
    }
    
    public static final void main(String [] args) throws Exception {
        Field[] fields = Keys.class.getFields();
        for(Field f : fields) {
            System.out.printf("keyMap.put(\"%s\", Keys.%s); \n", f.getName().toLowerCase(), f.getName());
        }
        
        for(Field f : fields) {
            System.out.printf("stringKey[Keys.%s] = \"%s\"; \n", f.getName(), f.getName().toLowerCase());
        }
    }
    
    /**
     * @param config
     */
    public KeyMap(LeoMap config) {
        this.config = config;
        
        if(!config.hasObject("joystick")) {
            config.putByString("joystick", new LeoMap());
        }
        
        this.joystick = config.getByString("joystick").as();
        
        refresh();
    }
    
    private void refresh() {
        this.reloadKey = getKey(config, "reload", Keys.R);
        this.walkKey = getKey(config, "walk", Keys.SHIFT_LEFT);
        this.crouchKey = getKey(config, "crouch", Keys.CONTROL_LEFT);
        this.sprintKey = getKey(config, "sprint", Keys.SPACE);
        this.upKey = getKey(config, "up", Keys.W);
        this.downKey = getKey(config, "down", Keys.S);
        this.leftKey = getKey(config, "left", Keys.A);
        this.rightKey = getKey(config, "right", Keys.D);
        this.fireKey = getKey(config, "fire", Buttons.LEFT);
        this.throwGrenadeKey = getKey(config, "throw_grenade", Buttons.RIGHT);
        this.useKey  = getKey(config, "use", Keys.E);
        this.dropWeaponKey  = getKey(config, "drop_weapon", Keys.F);
        this.meleeAttackKey = getKey(config, "melee_attack", Keys.Q);
        this.invertMouse = LeoObject.isTrue(config.getByString("inverted"));
        
        this.sayKey = getKey(config, "say", Keys.Y);
        this.teamSayKey = getKey(config, "team_say", Keys.T);
        
        this.reloadBtn = getKey(joystick, "reload", ControllerButtons.X_BTN);
        this.walkBtn = getKey(joystick, "walk", ControllerButtons.LEFT_TRIGGER_BTN);
        this.crouchBtn = getKey(joystick, "crouch", ControllerButtons.LEFT_BUMPER_BTN);
        this.sprintBtn = getKey(joystick, "sprint", ControllerButtons.LEFT_JOYSTICK_BTN);
        this.upBtn = getKey(joystick, "up", ControllerButtons.NORTH_DPAD_BTN);
        this.downBtn = getKey(joystick, "down", ControllerButtons.SOUTH_DPAD_BTN);
        this.leftBtn = getKey(joystick, "left", ControllerButtons.WEST_DPAD_BTN);
        this.rightBtn = getKey(joystick, "right", ControllerButtons.EAST_DPAD_BTN);
        this.fireBtn = getKey(joystick, "fire", ControllerButtons.RIGHT_TRIGGER_BTN);
        this.throwGrenadeBtn = getKey(joystick, "throw_grenade", ControllerButtons.B_BTN);
        this.useBtn  = getKey(joystick, "use", ControllerButtons.A_BTN);
        this.dropWeaponBtn = getKey(joystick, "drop_weapon", ControllerButtons.SELECT_BTN);
        this.meleeAttackBtn = getKey(joystick, "melee_attack", ControllerButtons.RIGHT_JOYSTICK_BTN);
        this.invertJoystick = LeoObject.isTrue(joystick.getByString("inverted"));
        this.isSouthPaw = LeoObject.isTrue(joystick.getByString("south_paw"));
    }

    private int getKey(LeoMap config, String name, int defaultKey) {
        Integer key = defaultKey;
        LeoObject value = config.getByString(name);
        if(value != null) {
            if(value.isNumber()) {
                key = value.asInt();
            }
            else {
                key = keyMap.get(value.toString().toLowerCase());
                if(key == null) {
                    key = defaultKey;
                }
            }
        }
        
        return key;
    }
    
    private ControllerButtons getKey(LeoMap config, String name, ControllerButtons defaultKey) {
        ControllerButtons key = defaultKey;
        LeoObject value = config.getByString(name);
        if(value != null) {                
            key = ControllerButtons.fromString(value.toString());
            if(key == null) {
                key = defaultKey;
            }                
        }
        
        return key;
    }
    
    public String keyString(int key) {
        String strKey = keyToString(key);
        return strKey.replace("_", " ");
    }
    
    private String keyToString(int key) {
        if(key < 0 || key > stringKey.length) {
            return "Unknown";
        }
        return stringKey[key];
    }
    
    
    
    /**
     * @return true if the mouse is inverted
     */
    public boolean isMouseInverted() {
        return invertMouse;
    }
    
    /**
     * @return true if the joystick is inverted
     */
    public boolean isJoystickInverted() {
        return invertJoystick;
    }

    /**
     * @return true if the joystick's are reversed, i.e., configured for the lefties, movement controlled by the right joystick,
     * and aiming is controlled by the left joystick
     */
    public boolean isSouthPaw() {
        return isSouthPaw;
    }

    /**
     * @return the reloadBtn
     */
    public ControllerInput.ControllerButtons getReloadBtn() {
        return reloadBtn;
    }

    /**
     * @return the walkBtn
     */
    public ControllerInput.ControllerButtons getWalkBtn() {
        return walkBtn;
    }

    /**
     * @return the crouchBtn
     */
    public ControllerInput.ControllerButtons getCrouchBtn() {
        return crouchBtn;
    }

    /**
     * @return the sprintBtn
     */
    public ControllerInput.ControllerButtons getSprintBtn() {
        return sprintBtn;
    }

    /**
     * @return the upBtn
     */
    public ControllerInput.ControllerButtons getUpBtn() {
        return upBtn;
    }

    /**
     * @return the downBtn
     */
    public ControllerInput.ControllerButtons getDownBtn() {
        return downBtn;
    }

    /**
     * @return the leftBtn
     */
    public ControllerInput.ControllerButtons getLeftBtn() {
        return leftBtn;
    }

    /**
     * @return the rightBtn
     */
    public ControllerInput.ControllerButtons getRightBtn() {
        return rightBtn;
    }

    /**
     * @return the fireBtn
     */
    public ControllerInput.ControllerButtons getFireBtn() {
        return fireBtn;
    }

    /**
     * @return the throwGrenadeBtn
     */
    public ControllerInput.ControllerButtons getThrowGrenadeBtn() {
        return throwGrenadeBtn;
    }

    /**
     * @return the useBtn
     */
    public ControllerInput.ControllerButtons getUseBtn() {
        return useBtn;
    }

    /**
     * @return the dropWeaponBtn
     */
    public ControllerInput.ControllerButtons getDropWeaponBtn() {
        return dropWeaponBtn;
    }

    /**
     * @return the meleeAttackBtn
     */
    public ControllerInput.ControllerButtons getMeleeAttackBtn() {
        return meleeAttackBtn;
    }



    /**
     * @return the meleeAttack
     */
    public int getMeleeAttackKey() {
        return meleeAttackKey;
    }
    
    
    /**
     * @return the dropWeaponKey
     */
    public int getDropWeaponKey() {
        return dropWeaponKey;
    }

    /**
     * @return the reloadKey
     */
    public int getReloadKey() {
        return reloadKey;
    }


    /**
     * @return the walkKey
     */
    public int getWalkKey() {
        return walkKey;
    }


    /**
     * @return the crouchKey
     */
    public int getCrouchKey() {
        return crouchKey;
    }


    /**
     * @return the sprintKey
     */
    public int getSprintKey() {
        return sprintKey;
    }


    /**
     * @return the upKey
     */
    public int getUpKey() {
        return upKey;
    }


    /**
     * @return the downKey
     */
    public int getDownKey() {
        return downKey;
    }


    /**
     * @return the leftKey
     */
    public int getLeftKey() {
        return leftKey;
    }


    /**
     * @return the rightKey
     */
    public int getRightKey() {
        return rightKey;
    }


    /**
     * @return the fireKey
     */
    public int getFireKey() {
        return fireKey;
    }


    /**
     * @return the throwGrenadeKey
     */
    public int getThrowGrenadeKey() {
        return throwGrenadeKey;
    }


    /**
     * @return the useKey
     */
    public int getUseKey() {
        return useKey;
    }
    
    /**
     * @return the sayKey
     */
    public int getSayKey() {
        return sayKey;
    }
    
    /**
     * @return the teamSayKey
     */
    public int getTeamSayKey() {
        return teamSayKey;
    }

    /**
     * @param reloadKey the reloadKey to set
     */
    public void setReloadKey(int reloadKey) {
        this.reloadKey = reloadKey;
    }

    /**
     * @param walkKey the walkKey to set
     */
    public void setWalkKey(int walkKey) {
        this.walkKey = walkKey;
    }

    /**
     * @param crouchKey the crouchKey to set
     */
    public void setCrouchKey(int crouchKey) {
        this.crouchKey = crouchKey;
    }

    /**
     * @param sprintKey the sprintKey to set
     */
    public void setSprintKey(int sprintKey) {
        this.sprintKey = sprintKey;
    }

    /**
     * @param upKey the upKey to set
     */
    public void setUpKey(int upKey) {
        this.upKey = upKey;
    }

    /**
     * @param downKey the downKey to set
     */
    public void setDownKey(int downKey) {
        this.downKey = downKey;
    }

    /**
     * @param leftKey the leftKey to set
     */
    public void setLeftKey(int leftKey) {
        this.leftKey = leftKey;
    }

    /**
     * @param rightKey the rightKey to set
     */
    public void setRightKey(int rightKey) {
        this.rightKey = rightKey;
    }

    /**
     * @param fireKey the fireKey to set
     */
    public void setFireKey(int fireKey) {
        this.fireKey = fireKey;
    }

    /**
     * @param throwGrenadeKey the throwGrenadeKey to set
     */
    public void setThrowGrenadeKey(int throwGrenadeKey) {
        this.throwGrenadeKey = throwGrenadeKey;
    }

    /**
     * @param useKey the useKey to set
     */
    public void setUseKey(int useKey) {
        this.useKey = useKey;
    }

    /**
     * @param dropWeaponKey the dropWeaponKey to set
     */
    public void setDropWeaponKey(int dropWeaponKey) {
        this.dropWeaponKey = dropWeaponKey;
    }

    /**
     * @param meleeAttack the meleeAttack to set
     */
    public void setMeleeAttack(int meleeAttack) {
        this.meleeAttackKey = meleeAttack;
    }

    /**
     * @param sayKey the sayKey to set
     */
    public void setSayKey(int sayKey) {
        this.sayKey = sayKey;
    }
    
    /**
     * @param teamSayKey the teamSayKey to set
     */
    public void setTeamSayKey(int teamSayKey) {
        this.teamSayKey = teamSayKey;
    }
    
    /**
     * Sets the key map binding
     * @param keymap
     * @param key
     */
    public void setKey(String keymap, int key) {
        if(key>NumberOfKeyboardKeys) {
            joystick.setObject(keymap, Leola.toLeoObject(keyToString(key)));
        }
        else {
            config.setObject(keymap, Leola.toLeoObject(keyToString(key)));
        }
        
        refresh();
    }
}
