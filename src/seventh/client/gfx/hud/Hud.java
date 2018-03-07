/*
 * The Seventh
 * see license.txt 
 */
package seventh.client.gfx.hud;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

import harenet.api.Client;
import seventh.client.ClientGame;
import seventh.client.ClientPlayer;
import seventh.client.ClientPlayers;
import seventh.client.ClientSeventhConfig;
import seventh.client.ClientTeam;
import seventh.client.SeventhGame;
import seventh.client.entities.ClientBombTarget;
import seventh.client.entities.ClientPlayerEntity;
import seventh.client.gfx.Art;
import seventh.client.gfx.Camera;
import seventh.client.gfx.Canvas;
import seventh.client.gfx.Cursor;
import seventh.client.gfx.MiniMap;
import seventh.client.gfx.RenderFont;
import seventh.client.gfx.Renderable;
import seventh.client.inputs.KeyMap;
import seventh.client.sfx.Sounds;
import seventh.client.weapon.ClientWeapon;
import seventh.game.entities.Entity.Type;
import seventh.game.entities.PlayerEntity.Keys;
import seventh.math.Rectangle;
import seventh.math.Vector2f;
import seventh.shared.TimeStep;
import seventh.shared.Timer;
import seventh.ui.ProgressBar;
import seventh.ui.view.ProgressBarView;

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
    private Cursor cursor;
    private ClientSeventhConfig config;
    private KillLog killLog;
    private MessageLog messageLog, centerLog, objectiveLog;
    private Scoreboard scoreboard;
    private EffectsLog awardsLog;

    private MiniMap miniMap;
    
    private ClientPlayer localPlayer;
    private Date gameClockDate;
    
    private ProgressBar bombProgressBar;
    private ProgressBarView bombProgressBarView;
    private long bombTime, completionTime;
    private boolean isAtBomb, useButtonReleased;
    private boolean isHoveringOverBomb;
    
    private Timer weaponHoverTimer;
    private boolean isHoveringOverWeapon;
    
    /**
     * 
     */
    public Hud(ClientGame game) {
        this.game = game;
        this.app = game.getApp();
        this.config = app.getConfig();
        
        this.localPlayer = game.getLocalPlayer();                
        this.cursor = app.getUiManager().getCursor();
        int screenWidth = app.getScreenWidth();
        
        this.killLog = new KillLog(screenWidth-260, 30, 5000);
        this.messageLog = new MessageLog(10, 60, 15000, 6);        
        this.objectiveLog = new SlowDisplayMessageLog(10, 120, 8000, 3);
        this.objectiveLog.setFont(app.getTheme().getSecondaryFontName());
        this.objectiveLog.setFontSize(24);
        
        this.awardsLog = new EffectsLog(-190, 100, 15);
        
        this.centerLog = new MessageLog(screenWidth/2, 50, 3000, 2) {
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
        
        this.weaponHoverTimer = new Timer(false, 500);
        this.weaponHoverTimer.stop();
        this.isHoveringOverWeapon = false;
        
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
     * @return the objectiveLog
     */
    public MessageLog getObjectiveLog() {
        return objectiveLog;
    }
    
    /**
     * @return the awardsLog
     */
    public EffectsLog getAwardsLog() {
        return awardsLog;
    }
    
    /**
     * @return the centerLog
     */
    public MessageLog getCenterLog() {
        return centerLog;
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
        if(message!=null) {
            messageLog.log(message);

            Sounds.playGlobalSound(Sounds.logAlert);
        }
    }
    
    
    /**
     * Checks the players actions and determines if it impacts the {@link Hud}
     * 
     * @param keys
     */
    public void applyPlayerInput(float mx, float my, int keys) {
        /* Check to see if the user is planting or disarming
         * a bomb
         */
        setAtBomb(false);
        
        boolean hasLocalPlayer = this.localPlayer != null && this.localPlayer.isAlive(); 
        
        if(Keys.USE.isDown(keys)) {
            if(hasLocalPlayer) {
                Rectangle bounds = this.localPlayer.getEntity().getBounds();
                List<ClientBombTarget> bombTargets = this.game.getBombTargets();
                for(int i = 0; i < bombTargets.size(); i++) {
                    ClientBombTarget target = bombTargets.get(i);                   
                    if(target.isAlive()) {
                        if(bounds.intersects(target.getBounds())) {
                            
                            /* if we are disarming it takes longer
                             * than planting
                             */
                            if(target.isBombPlanted()) {
                                setBombCompletionTime(5_000);
                            }
                            else {
                                setBombCompletionTime(3_000);
                            }
                            
                            setAtBomb(true);
                            break;
                        }
                    }
                }
            }
        }
        
        if(hasLocalPlayer) {
            ClientPlayerEntity ent = this.localPlayer.getEntity();
            ClientWeapon weapon = ent.getWeapon();
            if(weapon!=null && weapon.isReloading()) {
                cursor.setAccuracy(0);
                cursor.setColor(0x6fffffff);
            }
            else {
                if(game.isHoveringOverEnemy(mx, my)) {
                    cursor.setColor(0xafff0000);
                }
                else {
                    cursor.setColor(0xafffff00);
                }
            }            
        }
        else {
            cursor.setColor(0xafffff00);
        }
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
        objectiveLog.update(timeStep);
     
        awardsLog.update(timeStep);
        
        miniMap.update(timeStep);
        
        // if the player is under the mini-map, hide it
        int miniMapAlpha = scoreboard.isVisible() ? 0x3f : 0x8f;
        ClientPlayer player = game.getLocalPlayer();
        if(player.isAlive()) {
            Vector2f cameraPos = game.getCamera().getPosition();
            Vector2f playerPos = player.getEntity().getCenterPos();
            float x = playerPos.x - cameraPos.x;
            float y = playerPos.y - cameraPos.y;
            
            if(miniMap.intersectsMiniMap(x, y)) {
                miniMapAlpha = 0x0a;
            }
        }
        
        miniMap.setMapAlpha(miniMapAlpha);
        
        updateProgressBar(timeStep);
        
        isHoveringOverBomb = game.isHoveringOverBomb();
        isHoveringOverWeapon = game.isNearDroppedItem(this.localPlayer.getEntity());
        if(isHoveringOverWeapon) {
            weaponHoverTimer.start();
            weaponHoverTimer.update(timeStep);
        }
        else {
            weaponHoverTimer.stop();
        }
    }
    
    /* (non-Javadoc)
     * @see leola.live.gfx.Renderable#render(leola.live.gfx.Canvas, leola.live.gfx.Camera, long)
     */
    @Override
    public void render(Canvas canvas, Camera camera, float alpha) {    
        if(this.config.showFps()) {
            canvas.setFont("Consola", 12);
            canvas.drawString("FPS: " + app.getFps(), canvas.getWidth() - 60, 10, 0xff10f0ef);
        }
        
        miniMap.render(canvas, camera, alpha);
        
        killLog.render(canvas, camera, 0);
        messageLog.render(canvas, camera, 0);
        centerLog.render(canvas, camera, 0);
        objectiveLog.render(canvas, camera, 0);
        
        awardsLog.render(canvas, camera, 0);
        
        boolean isPlayerAlive = localPlayer.isAlive();
        if(isPlayerAlive) {
            ClientPlayerEntity ent = localPlayer.getEntity();
            ClientWeapon weapon = ent.getWeapon();
            
            if(!ent.isOperatingVehicle()) {
                drawWeaponIcons(canvas, camera, weapon);            
                drawGrenadeIcons(canvas, ent.getNumberOfGrenades());
            }
            
            drawHealth(canvas, ent.getHealth());
            drawStamina(canvas, ent.getStamina());
        }
        else {
            drawHealth(canvas, 0);
            drawStamina(canvas, 0);
        }
    

        
        drawScore(canvas);
        
        drawPlayersRemaining(canvas);
        
        drawClock(canvas);
        
        drawSpectating(canvas);
        
        if(game.getGameType()==seventh.game.type.GameType.Type.OBJ && isPlayerAlive) {
            ClientPlayerEntity ent = localPlayer.getEntity();
            if(!ent.isOperatingVehicle()) {
                ClientTeam attackingTeam = game.getAttackingTeam();
                boolean isAttacker = false; 
                
                if(attackingTeam!=null) {
                    isAttacker = this.localPlayer.getTeam().equals(attackingTeam);
                }
                
                if(isHoveringOverBomb) {                
                    if(isAttacker) {
                        drawPlantBombNotification(canvas);        
                    }
                    else {
                        drawDefuseBombNotification(canvas);
                    }
                }
            
                //drawObjectiveStance(canvas, isAttacker);           
                drawBombProgressBar(canvas, camera);
            }
        }
        
        drawPickupWeaponNotication(canvas);
        drawEnterVehicleNotication(canvas);
            
        if(this.config.showDebugInfo()) {
            drawMemoryUsage(canvas);
            drawNetworkUsage(canvas);
        }
        
        if(this.scoreboard.isVisible()) {
            this.scoreboard.drawScoreboard(canvas);
        }
    }
    
    private void drawWeaponIcons(Canvas canvas, Camera camera, ClientWeapon weapon) {
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
            
            // do recoil (shaking the camera)
            // if this client has it enabled.
            if(this.config.getWeaponRecoilEnabled()) {
                weapon.cameraKick(camera);
            }
        }
    }
    
    private void drawGrenadeIcons(Canvas canvas, int numberOfGrenades) {
        Sprite sprite = this.localPlayer.getEntity().isSmokeGrenades() ? Art.smokeGrenadeIcon : Art.fragGrenadeIcon;
        
        int imageWidth = sprite.getRegionWidth();

        canvas.drawImage(sprite, 10, canvas.getHeight() - sprite.getRegionHeight() - 40, 0xffffff00);
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
    
    private void drawPlayersRemaining(Canvas canvas) {
        seventh.game.type.GameType.Type type = game.getGameType();
        if(type!=null && type.equals(seventh.game.type.GameType.Type.OBJ)) {
            int numberOfAxisAlive = 0;
            int numberOfAlliedAlive = 0;
            
            ClientPlayers players = game.getPlayers();
            for(int i = 0; i < players.getMaxNumberOfPlayers(); i++) {
                ClientPlayer player = players.getPlayer(i);
                if(player!=null) {
                    if(player.isAlive()) {
                        if(player.getTeam()==ClientTeam.ALLIES) {
                            numberOfAlliedAlive++;
                        }
                        else {
                            numberOfAxisAlive++;
                        }
                    }
                }
            }

            int x = 200;
            int y = canvas.getHeight() - 40;
            
            canvas.fillCircle(14, x + 2, y + 2, 0xff000000);
            canvas.fillCircle(13, x + 3, y + 3, 0xffffffff);
            canvas.drawImage(Art.alliedIcon, x, y, null);
            RenderFont.drawShadedString(canvas, Integer.toString(numberOfAlliedAlive), x + 40, y + 20, ClientTeam.ALLIES.getColor());
            
            x += 70;
            
            canvas.fillCircle(13, x + 3, y + 3, 0xffffffff);
            canvas.drawImage(Art.axisIcon, x, y, null);
            RenderFont.drawShadedString(canvas, Integer.toString(numberOfAxisAlive), x + 40, y + 20, ClientTeam.AXIS.getColor());
        }
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
            ClientPlayer spectated = game.getPlayers().getPlayer(localPlayer.getSpectatingPlayerId());
            if(spectated!=null) {
                message += ": " + spectated.getName();
            }
            
            int width = canvas.getWidth(message);
            RenderFont.drawShadedString(canvas, message, canvas.getWidth()/2 - (width/2), canvas.getHeight() - canvas.getHeight("W") - 25, 0xffffffff);            
        }
    }
    
    private void drawHealth(Canvas canvas, int health) {
        int x = canvas.getWidth() - 125;
        int y = canvas.getHeight() - 25;
        
        canvas.fillRect( x, y, 100, 15, 0x7fFF0000 );
        if (health > 0) {
            canvas.fillRect( x, y, (100 * health/100), 15, 0xff9aFF1a );
        }
        
        //canvas.drawLine(x, y, x, y + 10, 0xff0000ff, 0x0000000a, 440);
        
        //canvas.drawRect(x, y, 100, 15, 0xbf000000);
        //canvas.drawRect(x, y+2, 100, 13, 0x9f000000);
        
        
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
        
        
        /*
      //  canvas.drawLine( x, y+0, x+100, y+0, 0xff000000, 2 );
        canvas.drawLine( x, y+1, x+100, y+1, 0x8f000000, 4 );
        canvas.drawLine( x, y+2, x+100, y+2, 0x5f000000, 4 );
        canvas.drawLine( x, y+3, x+100, y+3, 0x2f000000, 4 );
        canvas.drawLine( x, y+4, x+100, y+4, 0x0f000000, 4 );
        canvas.drawLine( x, y+5, x+100, y+5, 0x0b000000, 4 );
        canvas.drawLine( x, y+6, x+100, y+6, 0x0a000000, 4 );
        
        //canvas.drawLine( x, y+7, x+100, y+7, 0x5f000000, 2 );
        canvas.drawLine( x, y+8, x+100, y+8, 0xf000000, 2 );
        canvas.drawLine( x, y+9, x+100, y+9, 0x0b000000, 2 );
        canvas.drawLine( x, y+10, x+100, y+10, 0x0b000000, 2 );
        canvas.drawLine( x, y+11, x+100, y+11, 0x0a000000, 2 );
        canvas.drawLine( x, y+12, x+100, y+12, 0x0a000000, 2 );//
        
        y = y+15;
        canvas.drawLine( x, y-6, x+100, y-6, 0x0a000000, 4 );
        canvas.drawLine( x, y-5, x+100, y-5, 0x0b000000, 4 );
        canvas.drawLine( x, y-4, x+100, y-4, 0x0f000000, 4 );
        canvas.drawLine( x, y-3, x+100, y-3, 0x2f000000, 4 );
        canvas.drawLine( x, y-2, x+100, y-2, 0x5f000000, 4 );
        canvas.drawLine( x, y-1, x+100, y-1, 0x8f000000, 4 );        
       // canvas.drawLine( x, y-0, x+100, y-0, 0xff000000, 2 );
        canvas.drawRect( x, y-15, 100, 15, 0xff000000 );
        */

        canvas.drawRect( x, y-15, 100, 15, 0xff000000 );
//        Art.healthIcon.setSize(12, 12);
        canvas.drawSprite(Art.healthIcon, x - 20, y - 16, null);
    }
    
    
    private void drawStamina(Canvas canvas, int stamina) {
        int x = 15;
        int y = canvas.getHeight() - 25;
        
        canvas.fillRect( x, y, 100, 15, 0x8f4a5f8f );
        if (stamina > 0) {
            canvas.fillRect( x, y, (100 * stamina/100), 15, 0xff3a9aFF );
        }
        
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
        
        canvas.drawRect( x, y-15, 101, 15, 0xff000000 );
        canvas.drawSprite(Art.staminaIcon, x + 108, y - 14, null);
    }
    
    private void drawBombProgressBar(Canvas canvas, Camera camera) {
        this.bombProgressBarView.render(canvas, camera, 0);
    }
    
    
    private void drawMemoryUsage(Canvas canvas) {
        Runtime runtime = Runtime.getRuntime();
        long maxMemory = runtime.totalMemory() / (1024*1024);
        long usedMemory = maxMemory - runtime.freeMemory() / (1024*1024);
        
        
        int x = 12;
        int y = canvas.getHeight() - 125;
        
        canvas.fillRect( x, y, 100, 15, 0x9f696969 );
        if (usedMemory > 0 && maxMemory > 0) {
            double percentage = ((double)usedMemory / (double)maxMemory);
            canvas.fillRect( x, y, (int)(100 * percentage), 15, 0xaf2f2f2f );
        }
        
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
        
        canvas.drawRect( x, y-15, 100, 15, 0xff000000 );
        canvas.setFont("Consola", 14);
        canvas.drawString(usedMemory + " / " + maxMemory + " MiB", x, y - 20, 0x6f00CC00);
    }
    

    /**
     * Network usage
     * 
     * @param canvas
     */
    private void drawNetworkUsage(Canvas canvas) {
        int x = 12;
        int y = canvas.getHeight() - 150;
        
        int color = 0x6f00CC00;
        
        canvas.setFont("Consola", 14);
        Client client = app.getClientConnection().getClient();
        canvas.drawString("Avg. R: " + client.getAvgBitsPerSecRecv() + " B/S", x, y - 20, color);
        canvas.drawString("Avg. S: " + client.getAvgBitsPerSecSent() + " B/S", x, y - 35, color);
        canvas.drawString("Drop  : " + client.getNumberOfDroppedPackets(), x, y - 50, color);
        canvas.drawString("Tot. R: " + client.getNumberOfBytesReceived()/1024 + " KiB", x, y - 70, color);
        canvas.drawString("Tot. S: " + client.getNumberOfBytesSent()/1024 + " KiB", x, y - 85, color);
        
        String compressedBytes = client.getNumberOfBytesCompressed()>1024?
                                    client.getNumberOfBytesCompressed()/1024 + " KiB" : 
                                    client.getNumberOfBytesCompressed() + " B";
        canvas.drawString("Comp B: " + compressedBytes, x, y - 100, color);
    }
    
    
    private void drawDefuseBombNotification(Canvas canvas) {
        drawBombNotification(canvas, false);
    }
    
    private void drawPlantBombNotification(Canvas canvas) {
        drawBombNotification(canvas, true);
    }
    
    private void drawBombNotification(Canvas canvas, boolean isAttacking) {
        KeyMap keyMap = app.getKeyMap();
        String action = isAttacking ? "plant" : "defuse";
        String text = "Hold the '" + keyMap.keyString(keyMap.getUseKey()) +"' key to " + action + " the bomb.";
        drawMessage(canvas, text);
    }
    
    private void drawEnterVehicleNotication(Canvas canvas) {

        if(game.isNearVehicle(this.localPlayer.getEntity())) {
            KeyMap keyMap = app.getKeyMap();            
            String text = "Hold the '" + keyMap.keyString(keyMap.getUseKey()) +"' key to enter the vehicle";
            drawMessage(canvas, text);    
        }
    }
    
    private void drawPickupWeaponNotication(Canvas canvas) {

        if(isHoveringOverWeapon && weaponHoverTimer.isExpired()) {
            KeyMap keyMap = app.getKeyMap();            
            String text = "Press the '" + keyMap.keyString(keyMap.getDropWeaponKey()) +"' key to pick up or swap weapons";
            drawMessage(canvas, text);    
        }
    }
    
    private void drawMessage(Canvas canvas, String text) {
        canvas.setFont("Consola", 18);
        int width = canvas.getWidth(text);
        RenderFont.drawShadedString(canvas, text, canvas.getWidth()/2 - width/2, 140, 0xffffff00);
    }
    
//    private void drawObjectiveStance(Canvas canvas, boolean isAttacking) {
//        int defaultColor = 0xffffffff;
//        ClientTeam localTeam = game.getLocalPlayer().getTeam();
//        int teamColor = (localTeam!=null) ? localTeam.getColor() : defaultColor;
//        
//        //isAttacking = false;
//        TextureRegion tex = isAttacking ? Art.attackerIcon : Art.defenderIcon;
//        canvas.drawScaledImage(tex, 380 - (tex.getRegionWidth()/2), canvas.getHeight() - tex.getRegionHeight() + 15, 45, 45, teamColor);
//        
//    }
}
