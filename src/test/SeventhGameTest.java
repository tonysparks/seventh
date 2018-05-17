package test;

import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.nio.IntBuffer;
import java.util.Date;
import java.util.Stack;

import org.lwjgl.BufferUtils;
import org.lwjgl.LWJGLException;
import org.lwjgl.input.Cursor;
import org.lwjgl.input.Mouse;

import com.badlogic.gdx.Application.ApplicationType;
import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.controllers.Controller;
import com.badlogic.gdx.controllers.Controllers;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.utils.TimeUtils;

import leola.vm.types.LeoObject;
import seventh.ClientMain;
import seventh.client.gfx.Art;
import seventh.client.gfx.Canvas;
import seventh.client.gfx.GdxCanvas;
import seventh.client.gfx.Terminal;
import seventh.client.gfx.Theme;
import seventh.client.gfx.effects.BlurEffectShader;
import seventh.client.gfx.effects.FireEffectShader;
import seventh.client.gfx.effects.LightEffectShader;
import seventh.client.gfx.effects.RippleEffectShader;
import seventh.client.inputs.Inputs;
import seventh.client.inputs.KeyMap;
import seventh.client.network.ClientConnection;
import seventh.client.screens.AnimationEditorScreen;
import seventh.client.screens.LoadingScreen;
import seventh.client.screens.MenuScreen;
import seventh.client.screens.Screen;
import seventh.client.screens.ShaderEditorScreen;
import seventh.client.sfx.Sounds;
import seventh.network.messages.PlayerNameChangeMessage;
import seventh.shared.Command;
import seventh.shared.CommonCommands;
import seventh.shared.Cons;
import seventh.shared.Console;
import seventh.shared.StateMachine;
import seventh.shared.TimeStep;
import seventh.ui.UserInterfaceManager;
import seventh.client.SeventhGame;

public class SeventhGameTest {

	@Test
	public void renderTest() {
		
		 Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
	        
	        double newTime = TimeUtils.millis() / 1000.0;
	        double frameTime = Math.min(newTime - currentTime, 0.25);
	        
	        currentTime = newTime;
	        accumulator += frameTime;
	        
	        while(accumulator >= step) {
	            timeStep.setDeltaTime(DELTA_TIME);
	            timeStep.setGameClock(gameClock);
	                        
	            updateScreen(timeStep);
	            
	            accumulator -= step;
	            gameClock += DELTA_TIME;
	        }
	        
	        //avoid divided by zero ('step')
	        assert(step!=0);
	        
	        float alpha = (float)(accumulator / step);  
	}

}
