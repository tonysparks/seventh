/*
 * The Seventh
 * see license.txt 
 */
package seventh.client;

import java.util.ArrayList;
import java.util.List;

import seventh.client.gfx.Camera;
import seventh.client.gfx.Canvas;
import seventh.client.gfx.RenderFont;
import seventh.client.gfx.Renderable;
import seventh.shared.TimeStep;
import seventh.shared.Timer;

/**
 * @author Tony
 *
 */
public class MessageLog  implements Renderable {
	
	private List<String> logs;
	private Timer timeToShow;
	
	private final int maxLogEntries;
	private final int startX, startY;
	private int fontSize;
	private String font;
	
	/**
	 * @param x
	 * @param y
	 * @param bleedOffTime
	 * @param maxLogEntries
	 */
	public MessageLog(int x, int y, int bleedOffTime, int maxLogEntries) {
		this.startX = x;
		this.startY = y;
		
		this.maxLogEntries = maxLogEntries;
		
		this.fontSize = 12;
		this.font = "Consola";
		
		this.timeToShow = new Timer(true, bleedOffTime);
		this.logs = new ArrayList<String>();
	}
	
	/**
	 * Enables or disables the bleeding off of messages (removing them after
	 * X amount of seconds)
	 * 
	 * @param bleedOffEnabled
	 */
	public void enableBleedOff(boolean bleedOffEnabled) {
		if(!bleedOffEnabled) {
			this.timeToShow.stop();
		}
		else {
			this.timeToShow.start();
		}
	}
	
	/**
	 * @param fontSize the fontSize to set
	 */
	public void setFontSize(int fontSize) {
		this.fontSize = fontSize;
	}
	
	/**
     * @param font the font to set
     */
    public void setFont(String font) {
        this.font = font;
    }
	
	/**
	 * @param message
	 * @param color
	 */
	public void log(String message) {
		if(logs.isEmpty()) {
			timeToShow.reset();
		}
		else if (logs.size() > maxLogEntries) {
		    expireEntry();
		}
		
		this.logs.add(message);		
	}

	public void clearLogs() {
		logs.clear();
	}
	
	protected void expireEntry() {
	    logs.remove(0);
	}
	
	
	/* (non-Javadoc)
	 * @see leola.live.gfx.Renderable#update(leola.live.TimeStep)
	 */
	@Override
	public void update(TimeStep timeStep) {
		timeToShow.update(timeStep);
		
		if(timeToShow.isTime()) {
			if(!logs.isEmpty()) {
				expireEntry();			
			}
		}
		
	}
	
	/**
	 * Renders the message
	 * 
	 * @param canvas
	 * @param camera
	 * @param message
	 * @param x
	 * @param y
	 */
	protected void onRenderMesage(Canvas canvas, Camera camera, String message, int x, int y) {
		RenderFont.drawShadedString(canvas, message, x, y, 0xFFffffff);
	}
	
	/* (non-Javadoc)
	 * @see leola.live.gfx.Renderable#render(leola.live.gfx.Canvas, leola.live.gfx.Camera, long)
	 */
	@Override
	public void render(Canvas canvas, Camera camera, float alpha) {
		//canvas.resizeFont(this.fontSize);
		canvas.setFont(font, fontSize);
		canvas.boldFont();
		
		int height = canvas.getHeight("W") + 5;
		
		int y = startY;
		int size = logs.size();
		for(int i = 0; i < size; i++) {
			String message = logs.get(i);
//			int width = canvas.getWidth(message);
//			if(startX + width > canvas.getWidth()) {
//				TODO wrap the text	
//			}
		
			onRenderMesage(canvas, camera, message, startX, y);
			
			y+=height;
		}
		
	}
}
