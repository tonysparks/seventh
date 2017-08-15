/*
 * see license.txt 
 */
package seventh.client.screens;

import seventh.client.SeventhGame;
import seventh.client.gfx.Camera;
import seventh.client.gfx.Canvas;
import seventh.client.gfx.Renderable;
import seventh.client.inputs.Inputs;
import seventh.math.Rectangle;
import seventh.shared.TimeStep;
import seventh.ui.Button;
import seventh.ui.Panel;
import seventh.ui.events.ButtonEvent;
import seventh.ui.events.OnButtonClickedListener;
import seventh.ui.view.ButtonView;
import seventh.ui.view.PanelView;

/**
 * @author Tony
 *
 */
public class ServerFullScreen implements Screen {

    private SeventhGame app;
    private Panel serverFullDialog;
    private PanelView dialogView;
    
    private Screen background;
    
    /**
     * 
     */
    public ServerFullScreen(final SeventhGame app) {
        this.app = app;
        
        serverFullDialog = new Panel();
        serverFullDialog.setBounds(new Rectangle(350, 200));
        serverFullDialog.setTheme(app.getTheme());
        serverFullDialog.getBounds().centerAround(app.getScreenWidth()/2, app.getScreenHeight()/2);
        
        Button btn = new Button();
        btn.setForegroundColor(0xffffffff);
        btn.setForegroundAlpha(256);
        btn.setBackgroundAlpha(0);
        btn.setBackgroundColor(0x00000000);
        btn.setText("Server is full");
        btn.setBounds(new Rectangle(300, 150));
        btn.addOnButtonClickedListener(new OnButtonClickedListener() {
            
            @Override
            public void onButtonClicked(ButtonEvent event) {
                app.goToMenuScreen();
            }
        });
        serverFullDialog.addWidget(btn);
        
        this.dialogView = new PanelView();
        this.dialogView.addElement(new PanelView().addElement(new Renderable() {            
            @Override
            public void update(TimeStep timeStep) {                
            }
            
            @Override
            public void render(Canvas canvas, Camera camera, float alpha) {                
                Rectangle bounds = serverFullDialog.getBounds();
                canvas.fillRect(bounds.x, bounds.y, bounds.width, bounds.height, 0xdf1c1c1c);
                canvas.drawRect(bounds.x, bounds.y, bounds.width, bounds.height, 0xff000000);
            }
        }).addElement(new ButtonView(btn)));
    }

    
    
    @Override
    public void enter() {
        background = app.getMenuScreen();
        background.enter();
        
        serverFullDialog.show();
    }

    @Override
    public void exit() {
        background.exit();
        
        serverFullDialog.hide();
        serverFullDialog.destroy();
    }

    @Override
    public void destroy() {
        serverFullDialog.destroy();
    }
    
    @Override
    public void update(TimeStep timeStep) {
        this.background.update(timeStep);

    }


    @Override
    public void render(Canvas canvas, float alpha) {
        this.background.render(canvas, alpha);
        this.dialogView.render(canvas, null, alpha);
    }

    @Override
    public Inputs getInputs() {
        return this.app.getUiManager();
    }

}
