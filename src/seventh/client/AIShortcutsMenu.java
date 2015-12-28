/*
 * see license.txt
 */
package seventh.client;

import seventh.client.gfx.Camera;
import seventh.client.gfx.Canvas;
import seventh.client.gfx.RenderFont;
import seventh.client.gfx.Renderable;
import seventh.shared.TimeStep;

/**
 * @author Tony
 *
 */
public class AIShortcutsMenu implements Renderable {

    private AIShortcuts shortcuts;
    private boolean show;
    
    private String[] texts;
    
    /**
     * 
     */
    public AIShortcutsMenu(KeyMap keyMap, AIShortcuts shortcuts) {
        this.shortcuts = shortcuts;
        
        this.texts = new String[this.shortcuts.getCommands().size() + 1];
        
        this.texts[0] = "Dispatch command:";
        int i = 1;
        for(AIShortcut s : this.shortcuts.getCommands()) {
            this.texts[i++] = "   PRESS '" + keyMap.keyString(s.getShortcutKey()) + "' : " + s.getDescription();
        }
    }
    
    public boolean isShowing() {
        return this.show;
    }
    
    public void show() {
        this.show = true;
    }

    public void hide() {
        this.show = false;
    }
    public void toggle() {
        this.show = !this.show;
    }
    
    /* (non-Javadoc)
     * @see seventh.client.gfx.Renderable#update(seventh.shared.TimeStep)
     */
    @Override
    public void update(TimeStep timeStep) {
    }

    /* (non-Javadoc)
     * @see seventh.client.gfx.Renderable#render(seventh.client.gfx.Canvas, seventh.client.gfx.Camera, long)
     */
    @Override
    public void render(Canvas canvas, Camera camera, float alpha) {
        if(isShowing()) {
            int x = 20;
            int y = 200;
            
            int color = 0xffffff00;
            canvas.setFont("Consola", 14);
            canvas.boldFont();
            for(int i = 0; i < this.texts.length; i++) {
                RenderFont.drawShadedString(canvas, this.texts[i], x, y, color);
                y+=30;
            }
        }
    }

}
