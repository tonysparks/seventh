/*
 * see license.txt 
 */
package seventh.client.screens;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.Input.Keys;

import seventh.client.SeventhGame;
import seventh.client.gfx.Camera;
import seventh.client.gfx.Camera2d;
import seventh.client.gfx.Canvas;
import seventh.client.gfx.Cursor;
import seventh.client.gfx.effects.ShaderTest;
import seventh.client.gfx.effects.particle_system.Emitters;
import seventh.client.inputs.Inputs;
import seventh.math.Rectangle;
import seventh.math.Vector2f;
import seventh.shared.TimeStep;

/**
 * Tool for tweaking Animations
 * 
 * @author Tony
 *
 */
public class ShaderEditorScreen implements Screen {
    
    
    
    private SeventhGame app;
    private ShaderTest test;
    
    private Camera camera;
    private Cursor cursor;
    
    private boolean attached=false;    
    private List<seventh.client.gfx.effects.particle_system.Emitter> emitters;
    
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
                //emitters.add(Emitters.newBloodEmitter(new Vector2f(x,y)));
                //emitters.add(Emitters.newBulletImpactEmitter(new Vector2f(x,y), new Vector2f(1,0)));
                emitters.add(Emitters.newSpawnEmitter(new Vector2f(x,y), 10_000));
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
        camera = new Camera2d();        
        camera.setWorldBounds(new Vector2f(app.getScreenWidth(), app.getScreenHeight()));        
        camera.setViewPort(new Rectangle(this.app.getScreenWidth(), this.app.getScreenHeight()));
        camera.setMovementSpeed(new Vector2f(130, 130));
        
        this.emitters = new ArrayList<>();
    }
    
    
    @Override
    public void enter() {
        test = new ShaderTest();        
    }

    @Override
    public void exit() {
        if(test!=null) {
            test.destroy();
        }
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
        
//        if(attached) {
//            this.emitter.getPos().set(inputs.getMousePosition());
//            this.emitter2.getPos().set(inputs.getMousePosition());
//        }
//        else {
//            this.emitter.getPos().set(333,333);
//            this.emitter2.getPos().set(333,333);
//        }
//        this.emitter.update(timeStep);
//        this.emitter2.update(timeStep);
        
        //this.flame.update(timeStep);
        
        for(seventh.client.gfx.effects.particle_system.Emitter e : this.emitters) {
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
        
        for(seventh.client.gfx.effects.particle_system.Emitter e : this.emitters) {
            e.render(canvas, camera, alpha);
        }
        
        this.cursor.render(canvas);
    }
    
    @Override
    public Inputs getInputs() {
        return inputs;
    }

}
