/*
 * see license.txt 
 */
package seventh.client.gfx.hud;

import java.util.ArrayList;
import java.util.List;

import seventh.client.ClientPlayer;
import seventh.client.gfx.Art;
import seventh.client.gfx.Camera;
import seventh.client.gfx.Canvas;
import seventh.client.gfx.RenderFont;
import seventh.client.gfx.Renderable;
import seventh.game.entities.Entity.Type;
import seventh.shared.TimeStep;
import seventh.shared.Timer;

/**
 * Logs deaths
 * 
 * @author Tony
 *
 */
public class KillLog implements Renderable {

    static class Entry {
        ClientPlayer killed, killer;
        Type mod;
        /**
         * @param killed
         * @param killer
         * @param mod
         */
        public Entry(ClientPlayer killed, ClientPlayer killer, Type mod) {
            super();
            this.killed = killed;
            this.killer = killer;
            this.mod = mod;
        }
                
    }
    
    private List<Entry> logs;
    private Timer timeToShow;
    private final int startY;
    /**
     * 
     */
    public KillLog(int x, int y, int bleedOffTime) {
//        this.startX = x;
        this.startY = y;
        
        this.timeToShow = new Timer(true, bleedOffTime);
        this.logs = new ArrayList<KillLog.Entry>();
    }
    
    /**
     * Log a death
     * @param killed
     * @param killer
     * @param meansOfDeath
     */
    public void logDeath(ClientPlayer killed, ClientPlayer killer, Type meansOfDeath) {
        if(logs.isEmpty()) {
            timeToShow.reset();
        }
        else if (logs.size() > 6) {
            logs.remove(0);
        }
        
        this.logs.add(new Entry(killed, killer, meansOfDeath));
        
//        if(killer != null) {
//            Cons.println(killer.getName() + " killed " + killed.getName());
//        }
//        else {
//            Cons.println(killed.getName() + " died. ");
//        }
    }

    /* (non-Javadoc)
     * @see leola.live.gfx.Renderable#update(leola.live.TimeStep)
     */
    @Override
    public void update(TimeStep timeStep) {
        timeToShow.update(timeStep);
        
        if(timeToShow.isTime()) {
            if(!logs.isEmpty()) {
                logs.remove(0);                
            }
        }
        
    }
    
    /* (non-Javadoc)
     * @see leola.live.gfx.Renderable#render(leola.live.gfx.Canvas, leola.live.gfx.Camera, long)
     */
    @Override
    public void render(Canvas canvas, Camera camera, float alpha) {
//        canvas.resizeFont(14);
        canvas.setFont("Consola", 14);
        canvas.boldFont();
        
        int height = canvas.getHeight("W") + 5;
        
        int startX = canvas.getWidth() - 10;
        
        int y = startY;
        int size = logs.size();
        for(int i = 0; i < size; i++) {
            Entry m = logs.get(i);
            
            int killedColor = m.killed.getTeam().getColor();
            int killerColor = killedColor;
            
            String message = m.killed.getName() + " died";
            if(m.killer != null) {
                killerColor = m.killer.getTeam().getColor();
                if(m.killed.getId() == m.killer.getId()) {
                    message = m.killed.getName() + " took their own life";
                    int length = canvas.getWidth(message);
                    RenderFont.drawShadedString(canvas, message, startX-length, y, killerColor);
                }
                else {                                        
                    int yOffset = (int)Art.smallAssaultRifleIcon.getHeight() / 2;
                    
                    int killedLength = canvas.getWidth(m.killed.getName()) + 10;
                    int killerLength = canvas.getWidth(m.killer.getName()) + 10;
                    int iconLength = (int)Art.smallAssaultRifleIcon.getWidth() + 10;
                                        
                    switch(m.mod) {
                        case EXPLOSION: {
                            RenderFont.drawShadedString(canvas, m.killer.getName(), startX-killedLength-killerLength-iconLength, y, killerColor);
                            canvas.drawImage(Art.fragGrenadeImage, startX-killedLength-iconLength+10, y-16, null);
                            RenderFont.drawShadedString(canvas, m.killed.getName(), startX-killedLength, y, killedColor);
                            break;
                        }
                        case NAPALM_GRENADE:
                        case GRENADE: {
                            RenderFont.drawShadedString(canvas, m.killer.getName(), startX-killedLength-killerLength-iconLength, y, killerColor);
                            canvas.drawImage(Art.fragGrenadeImage, startX-killedLength-iconLength, y-10, null);
                            RenderFont.drawShadedString(canvas, m.killed.getName(), startX-killedLength, y, killedColor);
                            break;
                        }
                        case THOMPSON: {
                            RenderFont.drawShadedString(canvas, m.killer.getName(), startX-killedLength-killerLength-iconLength, y, killerColor);
                            canvas.drawSprite(Art.smallAssaultRifleIcon, startX-killedLength-iconLength, y-yOffset, null);
                            RenderFont.drawShadedString(canvas, m.killed.getName(), startX-killedLength, y, killedColor);
                            break;
                        }
                        case ROCKET: {
                            RenderFont.drawShadedString(canvas, m.killer.getName(), startX-killedLength-killerLength-iconLength, y, killerColor);
                            canvas.drawSprite(Art.smallRocketIcon, startX-killedLength-iconLength, y-yOffset-5, null);
                            RenderFont.drawShadedString(canvas, m.killed.getName(), startX-killedLength, y, killedColor);
                            break;
                        }
                        case SHOTGUN: {
                            RenderFont.drawShadedString(canvas, m.killer.getName(), startX-killedLength-killerLength-iconLength, y, killerColor);
                            canvas.drawSprite(Art.smallShotgunIcon, startX-killedLength-iconLength, y-yOffset, null);
                            RenderFont.drawShadedString(canvas, m.killed.getName(), startX-killedLength, y, killedColor);
                            break;
                        }
                        case ROCKET_LAUNCHER: {
                            RenderFont.drawShadedString(canvas, m.killer.getName(), startX-killedLength-killerLength-iconLength, y, killerColor);
                            canvas.drawSprite(Art.smallRocketIcon, startX-killedLength-iconLength, y-yOffset, null);
                            RenderFont.drawShadedString(canvas, m.killed.getName(), startX-killedLength, y, killedColor);
                            break;
                        }
                        case SPRINGFIELD: {
                            RenderFont.drawShadedString(canvas, m.killer.getName(), startX-killedLength-killerLength-iconLength, y, killerColor);
                            canvas.drawSprite(Art.smallSniperRifleIcon, startX-killedLength-iconLength, y-yOffset, null);
                            RenderFont.drawShadedString(canvas, m.killed.getName(), startX-killedLength, y, killedColor);
                            break;
                        }
                        case M1_GARAND: {
                            RenderFont.drawShadedString(canvas, m.killer.getName(), startX-killedLength-killerLength-iconLength, y, killerColor);
                            canvas.drawSprite(Art.smallM1GarandIcon, startX-killedLength-iconLength, y-yOffset, null);
                            RenderFont.drawShadedString(canvas, m.killed.getName(), startX-killedLength, y, killedColor);
                            break;
                        }
                        case KAR98: {
                            RenderFont.drawShadedString(canvas, m.killer.getName(), startX-killedLength-killerLength-iconLength, y, killerColor);
                            canvas.drawSprite(Art.smallkar98Icon, startX-killedLength-iconLength, y-yOffset, null);
                            RenderFont.drawShadedString(canvas, m.killed.getName(), startX-killedLength, y, killedColor);
                            break;
                        }
                        case MP44: {
                            RenderFont.drawShadedString(canvas, m.killer.getName(), startX-killedLength-killerLength-iconLength, y, killerColor);
                            canvas.drawSprite(Art.smallmp44Icon, startX-killedLength-iconLength, y-yOffset, null);
                            RenderFont.drawShadedString(canvas, m.killed.getName(), startX-killedLength, y, killedColor);
                            break;
                        }
                        case MP40: {
                            RenderFont.drawShadedString(canvas, m.killer.getName(), startX-killedLength-killerLength-iconLength, y, killerColor);
                            canvas.drawSprite(Art.smallmp40Icon, startX-killedLength-iconLength, y-yOffset, null);
                            RenderFont.drawShadedString(canvas, m.killed.getName(), startX-killedLength, y, killedColor);
                            break;
                        }
                        case PISTOL: {
                            RenderFont.drawShadedString(canvas, m.killer.getName(), startX-killedLength-killerLength-iconLength, y, killerColor);
                            canvas.drawSprite(Art.smallPistolIcon, startX-killedLength-iconLength, y-yOffset, null);
                            RenderFont.drawShadedString(canvas, m.killed.getName(), startX-killedLength, y, killedColor);
                            break;
                        }
                        case RISKER: {
                            RenderFont.drawShadedString(canvas, m.killer.getName(), startX-killedLength-killerLength-iconLength, y, killerColor);
                            canvas.drawSprite(Art.smallRiskerIcon, startX-killedLength-iconLength, y-yOffset, null);
                            RenderFont.drawShadedString(canvas, m.killed.getName(), startX-killedLength, y, killedColor);
                            break;
                        }
                        default: {
                            message = m.killer.getName() + " killed " + m.killed.getName();
                            int messageLength = canvas.getWidth(message) + 10;
                            RenderFont.drawShadedString(canvas, message, startX-messageLength, y, killerColor);
                        }
                    }
                    
                    
                }
            }
            else {
                int messageLength = canvas.getWidth(message) + 10;
                RenderFont.drawShadedString(canvas, message, startX-messageLength, y, killedColor);
            }
            
            
            
            y+=height;
        }
        
    }
}
