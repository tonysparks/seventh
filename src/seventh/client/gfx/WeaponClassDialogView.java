/*
 * see license.txt 
 */
package seventh.client.gfx;


import com.badlogic.gdx.graphics.g2d.TextureRegion;

import seventh.math.Rectangle;
import seventh.math.Vector2f;
import seventh.shared.TimeStep;
import seventh.ui.Button;
import seventh.ui.Label;
import seventh.ui.Widget;
import seventh.ui.view.ButtonView;
import seventh.ui.view.ImageButtonView;
import seventh.ui.view.LabelView;
import seventh.ui.view.PanelView;

/**
 * @author Tony
 *
 */
public class WeaponClassDialogView implements Renderable {

    static class SelectionAreaView implements Renderable {
        private Button btn;
        private Widget parent;
        
        public SelectionAreaView(Button btn, Widget parent) {
            this.btn = btn;
            this.parent = parent;
        }
        
        @Override
        public void update(TimeStep timeStep) {
        }
        
        @Override
        public void render(Canvas canvas, Camera camera, float alpha) {
            Rectangle bounds = this.btn.getScreenBounds();
            
            int x = this.parent.getBounds().x;
            int width = this.parent.getBounds().width;
            int height = bounds.height + 9;
            if(this.btn.isHovering()) {
                //0xff8c955c very lite
                //0xff5a6136
                //0xff202503 very dark
                canvas.fillRect(x, bounds.y, width, height, 0xaf202503);
                canvas.drawRect(x, bounds.y, width, height, 0xff5a6136);
            }
            else {
                canvas.fillRect(x, bounds.y, width, height, 0xaf363d0f);
                canvas.drawRect(x, bounds.y, width, height, 0xaf202503);
            }
            
            
        }
    }
    
    private WeaponClassDialog dialog;
    private PanelView panelView;
    private Button[] weaponBtns;
    private Label[] weaponDescriptions;
    
    private Vector2f origin;
    private int originalButtonWidth;
    
    
    /**
     * 
     */
    public WeaponClassDialogView(WeaponClassDialog dialog) {
        this.dialog = dialog;
        
        this.panelView = new PanelView();
        this.panelView.addElement(new LabelView(dialog.getTitle()));
        this.panelView.addElement(new ButtonView(dialog.getCancelBtn()));
        
        this.origin = new Vector2f();
        
        Button[] btns = dialog.getWeaponClasses();                
        this.weaponBtns = btns;
        
        if(btns.length != 6) {
            throw new IllegalArgumentException("Don't have all the weapons defined!");
        }
        

        for(Button btn : this.weaponBtns) {
            this.panelView.addElement(new SelectionAreaView(btn, dialog));
        }
        
        this.origin.set(this.weaponBtns[0].getBounds().x, this.weaponBtns[0].getBounds().y);
        this.originalButtonWidth = this.weaponBtns[0].getBounds().width;
        
        switch(dialog.getTeam()) {
            case ALLIES:
                this.panelView.addElement(new ImageButtonView(btns[0], scale(Art.thompsonIcon)));                
                this.panelView.addElement(new ImageButtonView(btns[1], Art.m1GarandIcon));
                this.panelView.addElement(new ImageButtonView(btns[2], Art.springfieldIcon));
                
                break;
            case AXIS:
                this.panelView.addElement(new ImageButtonView(btns[0], Art.mp40Icon));
                this.panelView.addElement(new ImageButtonView(btns[1], Art.mp44Icon));
                this.panelView.addElement(new ImageButtonView(btns[2], Art.kar98Icon));
                break;        
            default:
                break;            
        }
        
        this.panelView.addElement(new ImageButtonView(btns[3], Art.riskerIcon));
        this.panelView.addElement(new ImageButtonView(btns[4], Art.shotgunIcon));
        this.panelView.addElement(new ImageButtonView(btns[5], Art.rocketIcon));
        
        this.weaponDescriptions = dialog.getWeaponClassDescriptions();
        for(Label lbl : this.weaponDescriptions) {
            this.panelView.addElement(new LabelView(lbl));
        }
                
    }
               
    private TextureRegion scale(TextureRegion tex) {
//        Sprite sprite = new Sprite(tex);
//        sprite.setSize(220, 80);
        TextureRegion sprite = new TextureRegion(tex);
        sprite.setRegionWidth(350);
        return sprite;
    }
    
    public void clear() {
        this.panelView.clear();
    }

    /* (non-Javadoc)
     * @see seventh.client.gfx.Renderable#update(seventh.shared.TimeStep)
     */
    @Override
    public void update(TimeStep timeStep) {
        this.panelView.update(timeStep);
        
        final int openSpeed = 15;
        final int closeSpeed = 9;
        final int maxMoveBy = 100;
        
        for(int i = 0; i < this.weaponBtns.length; i++) {
            Button btn = this.weaponBtns[i];
            if(btn.isHovering()) {
                
                if( btn.getBounds().x > this.origin.x - maxMoveBy ) {
                    btn.getBounds().x -= openSpeed;
                    btn.getBounds().width = this.originalButtonWidth + ((int)this.origin.x - btn.getBounds().x);
                }
                else {
                    btn.getBounds().x = (int)this.origin.x - maxMoveBy;
                    btn.getBounds().width = this.originalButtonWidth + ((int)this.origin.x - btn.getBounds().x);
                    this.weaponDescriptions[i].show();
                }
            }
            else {
                if( btn.getBounds().x < this.origin.x  ) {
                    btn.getBounds().x += closeSpeed;
                    btn.getBounds().width = this.originalButtonWidth + ((int)this.origin.x - btn.getBounds().x);
                    this.weaponDescriptions[i].hide();
                }
                else {
                    btn.getBounds().x = (int)this.origin.x;
                    btn.getBounds().width = this.originalButtonWidth + ((int)this.origin.x - btn.getBounds().x);
                }
            }
        }
    }

    /* (non-Javadoc)
     * @see seventh.client.gfx.Renderable#render(seventh.client.gfx.Canvas, seventh.client.gfx.Camera, long)
     */
    @Override
    public void render(Canvas canvas, Camera camera, float alpha) {
        if(dialog.isVisible()) {
            Rectangle bounds = dialog.getBounds();
            canvas.fillRect(bounds.x, bounds.y, bounds.width, bounds.height, 0xaf383e18);
            canvas.drawRect(bounds.x, bounds.y, bounds.width, bounds.height, 0xff000000);
            
            this.panelView.render(canvas, camera, alpha);
        }
    }

}
