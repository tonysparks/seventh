/*
 * see license.txt
 */
package seventh.client.gfx.hud;

import seventh.client.gfx.Camera;
import seventh.client.gfx.Canvas;
import seventh.client.gfx.RenderFont;
import seventh.client.sfx.Sounds;
import seventh.shared.TimeStep;
import seventh.shared.Timer;

/**
 * Slowly prints out a message to the log
 * 
 * @author Tony
 *
 */
public class SlowDisplayMessageLog extends MessageLog {

    private static class Entry {
        Timer nextCharTimer;
        String currentMessage;
        String builtMessage;
        boolean isFinished;
        int currentIndex;
        
        Entry next;
        
        public Entry(String message) {
            this.nextCharTimer = new Timer(true, 500);
            this.nextCharTimer.reset();
            this.nextCharTimer.start();
            this.isFinished = false;
            this.currentMessage = message;
            this.builtMessage = "";
            this.currentIndex = 0;    
        }
        
        void add(Entry entry) {
            if(this.next == null) {
                this.next = entry;
            }
            else {
                next.add(entry);
            }
        }
        
        public void update(TimeStep timeStep) {
            if(!this.isFinished && this.currentMessage != null) {
                this.nextCharTimer.update(timeStep);
                if(this.nextCharTimer.isTime()) {
                    if(this.currentIndex < this.currentMessage.length()) {
                        if(this.currentMessage.charAt(currentIndex)==' ') {
                            this.nextCharTimer.setEndTime(230);
                        }
                        else this.nextCharTimer.setEndTime(50);
                        this.builtMessage += this.currentMessage.charAt(currentIndex++);
                        Sounds.playGlobalSound(Sounds.uiKeyType);
                        
                    }
                    else {
                        this.isFinished = true;
                    }
                }
            }
            else {
                if(this.next != null) {
                    this.next.update(timeStep);
                }
            }
        }
     
        void onRenderMesage(Canvas canvas, Camera camera, String message, int x, int y) {
            if(this.currentMessage == message) {
                RenderFont.drawShadedString(canvas, this.builtMessage, x, y, 0xffffff00);
            }
            
            if(this.next != null) {
                this.next.onRenderMesage(canvas, camera, message, x, y);
            }
        }
    }
    
    private Entry entries;
    
    /**
     * @param x
     * @param y
     * @param bleedOffTime
     * @param maxLogEntries
     */
    public SlowDisplayMessageLog(int x, int y, int bleedOffTime, int maxLogEntries) {
        super(x, y, bleedOffTime, maxLogEntries);
        this.entries = null;
    }
    
    /* (non-Javadoc)
     * @see seventh.client.MessageLog#log(java.lang.String)
     */
    @Override
    public void log(String message) {
        super.log(message);
        if(this.entries != null) {
            this.entries.add( new Entry(message));
        }
        else {
            this.entries = new Entry(message);
        }
    }
    
    /* (non-Javadoc)
     * @see seventh.client.MessageLog#expireEntry()
     */
    @Override
    protected void expireEntry() {
        super.expireEntry();
        if(this.entries != null) {
            this.entries = null;
            Sounds.playGlobalSound(Sounds.uiSelect);
        }
    }
    
    /* (non-Javadoc)
     * @see seventh.client.MessageLog#update(seventh.shared.TimeStep)
     */
    @Override
    public void update(TimeStep timeStep) {
        super.update(timeStep);
        
        if(this.entries != null) {
            this.entries.update(timeStep);
        }
        
    }

    /* (non-Javadoc)
     * @see seventh.client.MessageLog#onRenderMesage(seventh.client.gfx.Canvas, seventh.client.gfx.Camera, java.lang.String, int, int)
     */
    @Override
    protected void onRenderMesage(Canvas canvas, Camera camera, String message, int x, int y) {        
        if(entries != null) {
            entries.onRenderMesage(canvas, camera, message, x, y);
        }
    }
}
