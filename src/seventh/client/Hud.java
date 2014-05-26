/*
 * The Seventh
 * see license.txt 
 */
package seventh.client;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import seventh.client.gfx.Art;
import seventh.client.gfx.Camera;
import seventh.client.gfx.Canvas;
import seventh.client.gfx.MiniMap;
import seventh.client.gfx.RenderFont;
import seventh.client.gfx.Renderable;
import seventh.client.sfx.Sounds;
import seventh.client.weapon.ClientWeapon;
import seventh.game.Entity.Type;
import seventh.math.Rectangle;
import seventh.shared.TimeStep;
import seventh.ui.ProgressBar;
import seventh.ui.view.ProgressBarView;

import com.badlogic.gdx.graphics.g2d.TextureRegion;

/**
 * The players Heads Up Display
 * 
 * @author Tony
 *
 */
public class Hud implements Renderable {

	private static final DateFormat TIME_FORMAT = new SimpleDateFormat("HH:mm:ss");
	static {
		TIME_FORMAT.setTimeZone(TimeZone.getTimeZone("GMT"));
	}
	
	private ClientGame game;
	private SeventhGame app;
	private KillLog killLog;
	private MessageLog messageLog, centerLog;
	private Scoreboard scoreboard;
	
	private MiniMap miniMap;
	
	private ClientPlayer localPlayer;
	private Date gameClockDate;
	
	private ProgressBar bombProgressBar;
	private ProgressBarView bombProgressBarView;
	private long bombTime, completionTime;
	private boolean isAtBomb, useButtonReleased;
	/**
	 * 
	 */
	public Hud(ClientGame game) {
		this.game = game;
		this.app = game.getApp();
		
		this.localPlayer = game.getLocalPlayer();				
		int screenWidth = game.getApp().getScreenWidth();
		
		this.killLog = new KillLog(screenWidth-260, 30, 5000);
		this.messageLog = new MessageLog(10, 60, 15000, 6);
		this.centerLog = new MessageLog(screenWidth/2, 90, 3000, 2) {
			@Override
			protected void onRenderMesage(Canvas canvas, Camera camera, String message, int x, int y) {
				int width = canvas.getWidth(message);				
				canvas.drawString(message, x - (width/2), y, 0xffffffff);
			}
		};
		this.centerLog.setFontSize(24);
		
		this.scoreboard = game.getScoreboard();		
		this.gameClockDate = new Date();		
		this.bombProgressBar = new ProgressBar();
		this.bombProgressBar.setTheme(app.getTheme());
		this.bombProgressBar.setBounds(new Rectangle(300, 15));
		this.bombProgressBar.getBounds().centerAround(screenWidth/2, app.getScreenHeight() - 80);
		this.bombProgressBarView = new ProgressBarView(bombProgressBar);
		
		this.miniMap = new MiniMap(game);
	}

	/**
	 * @return the killLog
	 */
	public KillLog getKillLog() {
		return killLog;
	}
	
	/**
	 * @return the messageLog
	 */
	public MessageLog getMessageLog() {
		return messageLog;
	}

	/**
	 * Posts a death message 
	 * @param killed
	 * @param killer
	 * @param meansOfDeath
	 */
	public void postDeathMessage(ClientPlayer killed, ClientPlayer killer, Type meansOfDeath) {
		killLog.logDeath(killed, killer, meansOfDeath);
		
		if(killer!=null) {
			if ( killer.getId() == this.game.getLocalPlayer().getId()) {
				if(killer.getId() == killed.getId()) {
					this.centerLog.log("You killed yourself");
				}
				else {
					this.centerLog.log("You killed " + killed.getName());
				}
			}
		}
	}
	
	/**
	 * Posts a message to the notifications 
	 * @param message
	 */
	public void postMessage(String message) {
		messageLog.log(message);

		Sounds.playGlobalSound(Sounds.logAlert);
	}
	
	/**
	 * @param isAtBomb the isAtBomb to set
	 */
	public void setAtBomb(boolean isAtBomb) {
		this.isAtBomb = isAtBomb;
	}
	
	/**
	 * @return the isAtBomb
	 */
	public boolean isAtBomb() {
		return isAtBomb;
	}
	
	/**
	 * @param completionTime the completionTime to set
	 */
	public void setBombCompletionTime(long completionTime) {
		this.completionTime = completionTime;
	}
	
	private void updateProgressBar(TimeStep timeStep) {
		if(this.isAtBomb) {
			
			/* this forces the user to release the USE key
			 * which therefore doesn't spaz out the progress bar
			 * with continuous repeated progress
			 */
			if(useButtonReleased) {
				bombTime += timeStep.getDeltaTime();
				float percentageCompleted = (float) ((float)bombTime / this.completionTime);			
				bombProgressBar.setProgress( (int)(percentageCompleted * 100));
				bombProgressBar.show();
				
				if(bombProgressBar.getProgress() >= 99) {
					bombTime = 0;
					bombProgressBar.hide();
					useButtonReleased = false;
				}
			}						
		}
		else {
			bombProgressBar.hide();
			bombProgressBar.setProgress(0);
			bombTime = 0;
			useButtonReleased = true;
		}
		bombProgressBarView.update(timeStep);
	}
	
	/*
	 * (non-Javadoc)
	 * @see leola.live.gfx.Renderable#update(leola.live.TimeStep)
	 */
	@Override
	public void update(TimeStep timeStep) {
		killLog.update(timeStep);
		messageLog.update(timeStep);		
		centerLog.update(timeStep);
		miniMap.update(timeStep);		
		miniMap.setMapAlpha( scoreboard.isVisible() ? 0x3f : 0x8f);
		
		updateProgressBar(timeStep);
	}
	
	/* (non-Javadoc)
	 * @see leola.live.gfx.Renderable#render(leola.live.gfx.Canvas, leola.live.gfx.Camera, long)
	 */
	@Override
	public void render(Canvas canvas, Camera camera, long alpha) {	
		canvas.setFont("Consola", 12);
		canvas.drawString("FPS: " + app.getFps(), canvas.getWidth() - 60, 10, 0xff10f0ef);
		
		killLog.render(canvas, camera, 0);
		messageLog.render(canvas, camera, 0);
		centerLog.render(canvas, camera, 0);
		miniMap.render(canvas, camera, alpha);
		
		if(localPlayer.isAlive()) {
			ClientPlayerEntity ent = localPlayer.getEntity();
			ClientWeapon weapon = ent.getWeapon();
			
			if(!ent.isMech()) {
				drawWeaponIcons(canvas, weapon);			
				drawGrenadeIcons(canvas, ent.getNumberOfGrenades());
			}
			
			drawHealth(canvas, ent.getHealth());
			drawStamina(canvas, ent.getStamina());
		}
		else {
			drawHealth(canvas, 0);
			drawStamina(canvas, 0);
		}
	
		if(this.scoreboard.isVisible()) {
			this.scoreboard.drawScoreboard(canvas);
		}
		
		drawScore(canvas);
		
		drawClock(canvas);
		
		drawSpectating(canvas);
		
		drawBombProgressBar(canvas, camera);
	}
	
	private void drawWeaponIcons(Canvas canvas, ClientWeapon weapon) {
		if(weapon!=null) {		
			canvas.setFont("Consola", 14);
			canvas.boldFont();
			RenderFont.drawShadedString(canvas, weapon.getAmmoInClip() + " | " + weapon.getTotalAmmo()
									  , canvas.getWidth() - 200
									  , canvas.getHeight() - 50, 0xffffff00);
			
			TextureRegion icon = weapon.getWeaponIcon();
			if(icon!=null) {													
				canvas.drawImage(icon, canvas.getWidth() - icon.getRegionWidth() - 10, canvas.getHeight() - icon.getRegionHeight() - 30, null);
			}
			
//			weapon.cameraKick(camera);
		}
	}
	
	private void drawGrenadeIcons(Canvas canvas, int numberOfGrenades) {
		int imageWidth = Art.grenadeIcon.getRegionWidth();
//		for(int i = 0; i< ent.getNumberOfGrenades();i++) {
//			canvas.drawImage(Art.grenadeIcon, 20 + (i*imageWidth+10), canvas.getHeight() - 40, 0xffffff00);
//		}
		
		canvas.drawImage(Art.grenadeIcon, 10, canvas.getHeight() - Art.grenadeIcon.getRegionHeight() - 40, 0xffffff00);
		canvas.setFont("Consola", 14);
		canvas.boldFont();				
		RenderFont.drawShadedString(canvas, "x " + numberOfGrenades, imageWidth+20, canvas.getHeight() - 50, 0xffffff00);
		
	}
	
	private void drawScore(Canvas canvas) {				
		canvas.setFont("Consola", 18);
		
		int textLen = canvas.getWidth("WWWWW");
		int x = canvas.getWidth() / 2 - textLen;
		int y = canvas.getHeight() - 10;
		
		int width = 50;
		int height = 20;
		
		canvas.fillRect(x, y-15, width, height, 0x8fffffff & ClientTeam.ALLIES.getColor());
		canvas.drawRect(x, y-15, width, height, 0xff000000);
		
		String txt = scoreboard.getAlliedScore() + "";
		RenderFont.drawShadedString(canvas, txt, x + (width - canvas.getWidth(txt)) - 1, y, 0xffffffff);
		
		x += textLen + 2;
		canvas.fillRect(x-2, y-15, 2, height, 0xff000000);
		
		canvas.fillRect(x, y-15, width, height, 0x8fffffff & ClientTeam.AXIS.getColor());
		canvas.drawRect(x, y-15, width, height, 0xff000000);
				
		txt = scoreboard.getAxisScore() + "";
		RenderFont.drawShadedString(canvas, txt, x + 4, y, 0xffffffff);
	}
	
	private void drawClock(Canvas canvas) {

		this.gameClockDate.setTime(game.getGameClock());
		String time =  TIME_FORMAT.format(gameClockDate);
		int timeX = canvas.getWidth()/2-40;
		int timeY = 25;

		canvas.setFont("Consola", 24);
		RenderFont.drawShadedString(canvas, time, timeX, timeY, 0xffffff00);
		
	}
	
	private void drawSpectating(Canvas canvas) {
		if(localPlayer.isSpectating()) {
			
			String message = "Spectating";
			ClientPlayer spectated = game.getPlayers().get(localPlayer.getSpectatingPlayerId());
			if(spectated!=null) {
				message += ": " + spectated.getName();
			}
			
			int width = canvas.getWidth(message);
			RenderFont.drawShadedString(canvas, message, canvas.getWidth()/2 - (width/2), canvas.getHeight() - canvas.getHeight("W") - 10, 0xffffffff);			
		}
	}
	
	private void drawHealth(Canvas canvas, int health) {
		int x = canvas.getWidth() - 125;
		int y = canvas.getHeight() - 25;
		
		canvas.fillRect( x, y, 100, 15, 0x7fFF0000 );
		if (health > 0) {
			canvas.fillRect( x, y, (100 * health/100), 15, 0xff9aFF1a );
		}
		canvas.drawRect( x-1, y, 101, 15, 0xff000000 );
		
		// add a shadow effect
		canvas.drawLine( x, y+1, x+100, y+1, 0x8f000000 );
		canvas.drawLine( x, y+2, x+100, y+2, 0x5f000000 );
		canvas.drawLine( x, y+3, x+100, y+3, 0x2f000000 );
		canvas.drawLine( x, y+4, x+100, y+4, 0x0f000000 );
		canvas.drawLine( x, y+5, x+100, y+5, 0x0b000000 );
		
		y = y+15;
		canvas.drawLine( x, y-5, x+100, y-5, 0x0b000000 );
		canvas.drawLine( x, y-4, x+100, y-4, 0x0f000000 );
		canvas.drawLine( x, y-3, x+100, y-3, 0x2f000000 );
		canvas.drawLine( x, y-2, x+100, y-2, 0x5f000000 );
		canvas.drawLine( x, y-1, x+100, y-1, 0x8f000000 );		
		
//		Art.healthIcon.setSize(12, 12);
		canvas.drawSprite(Art.healthIcon, x - 20, y - 16, null);
	}
	
	
	private void drawStamina(Canvas canvas, int stamina) {
		int x = 15;
		int y = canvas.getHeight() - 25;
		
		canvas.fillRect( x, y, 100, 15, 0x8f4a5f8f );
		if (stamina > 0) {
			canvas.fillRect( x, y, (100 * stamina/100), 15, 0xff3a9aFF );
		}
		canvas.drawRect( x-1, y, 101, 15, 0xff000000 );
		
		// add a shadow effect
		canvas.drawLine( x, y+1, x+100, y+1, 0x8f000000 );
		canvas.drawLine( x, y+2, x+100, y+2, 0x5f000000 );
		canvas.drawLine( x, y+3, x+100, y+3, 0x2f000000 );
		canvas.drawLine( x, y+4, x+100, y+4, 0x0f000000 );
		canvas.drawLine( x, y+5, x+100, y+5, 0x0b000000 );
		
		y = y+15;
		canvas.drawLine( x, y-5, x+100, y-5, 0x0b000000 );
		canvas.drawLine( x, y-4, x+100, y-4, 0x0f000000 );
		canvas.drawLine( x, y-3, x+100, y-3, 0x2f000000 );
		canvas.drawLine( x, y-2, x+100, y-2, 0x5f000000 );
		canvas.drawLine( x, y-1, x+100, y-1, 0x8f000000 );		
		
		canvas.drawSprite(Art.staminaIcon, x + 108, y - 14, null);
	}
	
	private void drawBombProgressBar(Canvas canvas, Camera camera) {
		this.bombProgressBarView.render(canvas, camera, 0);
	}
	
}
