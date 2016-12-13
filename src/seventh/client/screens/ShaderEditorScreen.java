/*
 * see license.txt 
 */
package seventh.client.screens;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.GL30;

import seventh.client.SeventhGame;
import seventh.client.gfx.Camera;
import seventh.client.gfx.Camera2d;
import seventh.client.gfx.Canvas;
import seventh.client.gfx.Cursor;
import seventh.client.gfx.Renderable;
import seventh.client.gfx.effects.FireEmitter;
import seventh.client.gfx.effects.ShaderTest;
import seventh.client.gfx.particle.Emitter;
import seventh.client.gfx.particle_system.Emitters;
import seventh.client.inputs.Inputs;
import seventh.math.Rectangle;
import seventh.math.Vector2f;
import seventh.shared.TimeStep;
import seventh.shared.Timer;

/**
 * Tool for tweaking Animations
 * 
 * @author Tony
 *
 */
public class ShaderEditorScreen implements Screen {
	
	static class FlameEmitter implements Renderable {
		
		static class FireBall implements Renderable {
			Emitter base,center;
		
			private Vector2f origin;
			private Vector2f pos, vel;
			private float speed;
			private boolean isStarted;
			
			FireBall(Vector2f pos, Vector2f vel, int timeToLive) {
				this.origin = pos;
				this.pos = pos.createClone();
				this.vel = vel.createClone();
				this.speed = 950f;
				
				this.base = new FireEmitter(pos.createClone(), timeToLive,0xEB502Fff, 0.99f, 12.2f);
				this.base.stop();
				this.center = new FireEmitter(pos.createClone(), timeToLive,0xFFFF09ff,0.954f, 11.5f);//0xE6F109ff, 1.4f);
				this.center.stop();
			}
			
			public void reset() {
				base.reset();
				center.reset();
				isStarted = false;
								
				this.base.getPos().set(origin);
				this.center.getPos().set(origin);
				speed = 950f;
			}
			
			public void start() {
				base.start();
				center.start();
				isStarted = true;
			}
			
			@Override
			public void update(TimeStep timeStep) {
				this.base.update(timeStep);
				this.center.update(timeStep);
				
				if(this.isStarted) {
					Vector2f.Vector2fMA(pos, vel, speed * (float)timeStep.asFraction(), pos);
					this.base.getPos().set(pos);
					this.center.getPos().set(pos);
					speed *= 0.9f;
				}
			}
			
			@Override
			public void render(Canvas canvas, Camera camera, float alpha) {
				//canvas.enableBlending();
				int src = canvas.getSrcBlendFunction();
				int dst = canvas.getDstBlendFunction();
				//canvas.setBlendFunction(GL20.GL_SRC_ALPHA, GL20.GL_FUNC_ADD);
				canvas.setBlendFunction(GL20.GL_SRC_ALPHA, GL20.GL_ONE);
				Gdx.gl20.glBlendEquation(GL20.GL_FUNC_ADD);
				this.base.render(canvas, camera, alpha);
				this.center.render(canvas, camera, alpha);
				canvas.setBlendFunction(src, dst);
				//canvas.disableBlending();
			}
		}
		
		private List<FireBall> fireBalls;
		private Timer rate;
		private int index;
		/**
		 * 
		 */
		public FlameEmitter(Vector2f origin, Vector2f dir) {
			this.fireBalls = new ArrayList<>();
			Random rand = new Random();
			for(int i = 0; i < 8; i++) {
				
				double adjustedAngle = Math.toRadians(rand.nextInt(5)) * (rand.nextBoolean() ? -1 : 1);
				Vector2f vel = new Vector2f(dir);
				Vector2f.Vector2fRotate(vel, adjustedAngle, vel);
				
				this.fireBalls.add(new FireBall(origin, vel, 900));
			}
			this.rate = new Timer(true, 80);			
		}
		
		public void reset() {
			this.rate.reset();
			for(FireBall ball : this.fireBalls) {
				ball.base.reset();
				ball.center.reset();
			}
			index=1;
		}
		
		public void start() {
			this.fireBalls.get(0).start();
			this.rate.start();
//			for(FireBall ball : this.fireBalls) {
//				ball.base.start();
//				ball.center.start();
//			}
		}
		
		public void destroy() {
			for(FireBall ball : this.fireBalls) {
				ball.base.destroy();
				ball.center.destroy();
			}
		}
		
		@Override
		public void update(TimeStep timeStep) {
			this.rate.update(timeStep);
			
			if(this.rate.isTime() && index < this.fireBalls.size()) {
				//index = (index+1) % this.fireBalls.size();
				this.fireBalls.get(index).start();
				index++;
			}
			
			for(FireBall ball : this.fireBalls) {				
				ball.update(timeStep);
			}
		}
		
		@Override
		public void render(Canvas canvas, Camera camera, float alpha) {
			for(FireBall ball : this.fireBalls) {
				ball.render(canvas, camera, alpha);
			}
		}
	}
	
	private SeventhGame app;
	private ShaderTest test;
	private Emitter emitter,emitter2;
	private Camera camera;
	private Cursor cursor;
	private FlameEmitter flame;
	private boolean attached=false;	
	private List<seventh.client.gfx.particle_system.Emitter> emitters;
	
	private Inputs inputs = new Inputs() {

	    @Override
	    public boolean keyUp(int key) {
	        if(key==Keys.ESCAPE) {
	            app.goToMenuScreen();
	            return true;
	        }
	        return super.keyUp(key);
	    }
	    
		@Override
		public boolean keyTyped(char key) {
			if(key=='r') {
				test = new ShaderTest();
//				emitter.reset();
//				emitter.start();
//				
//				emitter2.reset();
//				emitter2.start();
//				
//				flame.reset();
//				flame.start();
				flame = new FlameEmitter(new Vector2f(333,333), new Vector2f(1,0));
				return true;
			}
			if(key=='t') {
				attached = !attached;
			}
			
			
			return super.keyTyped(key);
		}
		
		@Override
		public boolean touchUp(int x, int y, int pointer, int button) {
			if(button == 1) {
				//emitters.add(Emitters.newFireEmitter(new Vector2f(x,y)));
				emitters.add(Emitters.newBloodEmitter(new Vector2f(x,y)));
			}
			return true;
		}
		
		@Override
		public boolean mouseMoved(int x, int y) {
			cursor.moveTo(x, y);
			return super.mouseMoved(x, y);
		}
	};
	
	/**
	 * 
	 */
	public ShaderEditorScreen(SeventhGame app) {
		this.app = app;
		this.cursor = app.getUiManager().getCursor();
		this.emitter = new FireEmitter(new Vector2f(333,333), 1_000_000_000,0xEB502Fff, 0.2f, 2.2f);
		this.emitter2 = new FireEmitter(new Vector2f(333,333), 1_000_000_000,0xFFFF09ff,0.053f, 1.5f);//0xE6F109ff, 1.4f);
		
		this.flame = new FlameEmitter(new Vector2f(333,333), new Vector2f(1,0));
		
		camera = new Camera2d();		
		camera.setWorldBounds(new Vector2f(app.getScreenWidth(), app.getScreenHeight()));		
		camera.setViewPort(new Rectangle(this.app.getScreenWidth(), this.app.getScreenHeight()));
		camera.setMovementSpeed(new Vector2f(130, 130));
		
		this.emitters = new ArrayList<>();
	}
	
	
	@Override
	public void enter() {
		test = new ShaderTest();
		emitter.reset();
		emitter2.reset();
	}

	@Override
	public void exit() {
		if(test!=null) {
			test.destroy();
		}
		emitter.destroy();
		emitter2.destroy();
	}
	
	@Override
	public void destroy() {
		if(test!=null) {
			test.destroy();
		}
	}
	
	@Override
	public void update(TimeStep timeStep) {
		this.test.update(timeStep);
		
//		if(attached) {
//			this.emitter.getPos().set(inputs.getMousePosition());
//			this.emitter2.getPos().set(inputs.getMousePosition());
//		}
//		else {
//			this.emitter.getPos().set(333,333);
//			this.emitter2.getPos().set(333,333);
//		}
//		this.emitter.update(timeStep);
//		this.emitter2.update(timeStep);
		
		//this.flame.update(timeStep);
		
		for(seventh.client.gfx.particle_system.Emitter e : this.emitters) {
			e.update(timeStep);
		}
		
	}


	@Override
	public void render(Canvas canvas, float alpha) {
		//this.test.render(canvas, null, 0);
		canvas.fillRect(0, 0, canvas.getWidth(), canvas.getHeight(), 0xff000000);
		//this.emitter.render(canvas, camera, alpha);
		//this.emitter2.render(canvas, camera, alpha);
		
		//this.flame.render(canvas, camera, alpha);
		
		for(seventh.client.gfx.particle_system.Emitter e : this.emitters) {
			e.render(canvas, camera, alpha);
		}
		
		this.cursor.render(canvas);
	}
	
	@Override
	public Inputs getInputs() {
		return inputs;
	}

}
