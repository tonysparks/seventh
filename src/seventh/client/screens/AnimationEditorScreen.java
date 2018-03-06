/*
 * see license.txt 
 */
package seventh.client.screens;

import java.math.BigInteger;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

import seventh.client.SeventhGame;
import seventh.client.gfx.AnimatedImage;
import seventh.client.gfx.Art;
import seventh.client.gfx.Canvas;
import seventh.client.gfx.RenderFont;
import seventh.client.gfx.TextureUtil;
import seventh.client.gfx.Theme;
import seventh.client.inputs.Inputs;
import seventh.client.sfx.Sounds;
import seventh.math.Rectangle;
import seventh.math.Vector2f;
import seventh.shared.TimeStep;
import seventh.ui.Button;
import seventh.ui.Label.TextAlignment;
import seventh.ui.Panel;
import seventh.ui.TextBox;
import seventh.ui.UserInterfaceManager;
import seventh.ui.events.ButtonEvent;
import seventh.ui.events.HoverEvent;
import seventh.ui.events.OnButtonClickedListener;
import seventh.ui.events.OnHoverListener;
import seventh.ui.events.TextBoxActionEvent;
import seventh.ui.events.TextBoxActionListener;
import seventh.ui.view.ButtonView;
import seventh.ui.view.PanelView;
import seventh.ui.view.TextBoxView;

/**
 * Tool for tweaking Animations
 * 
 * @author Tony
 *
 */
public class AnimationEditorScreen implements Screen {
    
    
    private SeventhGame app;
    
    private UserInterfaceManager uiManager;
    
    private Theme theme;
        
    private Panel optionsPanel, animationPanel, steppingPanel;    
    private PanelView panelView;
    private Button loadAnimation;
    
    private int backgroundColor;
    
    private AnimatedImage animation;
    private boolean stepping;
    private int frameNumber;
    private long keyDelay;
    
    /**
     * 
     */
    public AnimationEditorScreen(SeventhGame app) {
        this.app = app;
        this.theme = app.getTheme();            
        this.uiManager = app.getUiManager();            
        this.backgroundColor = 0xff000000;
        
        createUI();
    }
    
    
    private void createUI() {
        this.panelView = new PanelView();
        this.optionsPanel = new Panel();
        this.animationPanel = new Panel();
        this.steppingPanel = new Panel();
        this.loadAnimation = new Button();                
        
        Vector2f uiPos = new Vector2f();

        uiPos.x = app.getScreenWidth()/2 + 50;
        uiPos.y = app.getScreenHeight() - 20;
        Button cancelBtn = setupButton(uiPos, "Exit", true);
        cancelBtn.addOnButtonClickedListener(new OnButtonClickedListener() {
            
            @Override
            public void onButtonClicked(ButtonEvent event) {
                app.popScreen();
                Sounds.playGlobalSound(Sounds.uiNavigate);
            }
        });
        
        int startX = app.getScreenWidth()/2;
        int startY = 60;//app.getScreenHeight()/6;
        uiPos.x = startX;
        uiPos.y = startY;
        
        int xSpacing = 180;
        int ySpacing = 30;
        
        final TextBox pathBox = setupTextBox(uiPos, 500, "Image File: ", "[PASTE FILE PATH TO IMAGE]");
        uiPos.x = pathBox.getBounds().x;
        uiPos.y += ySpacing;
        final TextBox rowBox = setupTextBox(uiPos, 25, "# of Rows: ", "1");
        uiPos.x += xSpacing;        
        final TextBox colBox = setupTextBox(uiPos, 25, "# of Cols: ", "1");
        uiPos.x += xSpacing;        
        final TextBox fpsBox = setupTextBox(uiPos, 40, "FrameTime: ", "800");
        uiPos.x += xSpacing;        
        final TextBox backgroundColorBox = setupTextBox(uiPos, 70, "BG Color: ", Integer.toHexString(backgroundColor));
        
        
        uiPos.x = pathBox.getBounds().x;
        uiPos.y += ySpacing;
            
        final TextBox subImageX = setupTextBox(uiPos, 25, "X-Offset: ", "");
        uiPos.x += xSpacing;        
        final TextBox subImageY = setupTextBox(uiPos, 25, "Y-Offset: ", "");
        uiPos.x += xSpacing;        
        final TextBox subImageWidth = setupTextBox(uiPos, 40, "Width-Offset: ", "");
        uiPos.x += xSpacing;        
        final TextBox subImageHeight = setupTextBox(uiPos, 40, "Height-Offset: ", "");
        
        uiPos.x = pathBox.getBounds().x + pathBox.getBounds().width + 80;
        uiPos.y = pathBox.getBounds().y + 15;
        
        loadAnimation = setupButton(uiPos, "Load", true);        
        loadAnimation.getTextLabel().setFont(theme.getPrimaryFontName());
        loadAnimation.addOnButtonClickedListener(new OnButtonClickedListener() {
            
            @Override
            public void onButtonClicked(ButtonEvent event) {    
                String rowTxt = rowBox.getText();
                String colTxt = colBox.getText();
                String fpsTxt = fpsBox.getText();
                
                String subXTxt = subImageX.getText();
                String subYTxt = subImageY.getText();
                String subWidthTxt = subImageWidth.getText();
                String subHeightTxt = subImageHeight.getText();
                
                String backgroundColorTxt = backgroundColorBox.getText();
                try {
                    backgroundColor = new BigInteger(backgroundColorTxt, 16).intValue();
                    
                    int rows = Integer.parseInt(rowTxt);
                    int cols = Integer.parseInt(colTxt);
                    int frameTime = Integer.parseInt(fpsTxt);
                    
                    int[] frames = new int[rows*cols];                    
                    for(int i = 0; i < frames.length; i++) {
                        frames[i] = frameTime;
                    }
                    
                    TextureRegion tex = Art.loadImage(pathBox.getText());
                    
                    int subX = 0;
                    int subY = 0;
                    int subWidth = tex.getRegionWidth();
                    int subHeight = tex.getRegionHeight();
                    
                    if(notEmpty(subXTxt)) {
                        subX = Integer.parseInt(subXTxt);
                    }
                    if(notEmpty(subYTxt)) {
                        subY = Integer.parseInt(subYTxt);
                    }
                    if(notEmpty(subWidthTxt)) {
                        subWidth = Integer.parseInt(subWidthTxt);
                    }
                    if(notEmpty(subHeightTxt)) {
                        subHeight = Integer.parseInt(subHeightTxt);
                    }
                    
                    
                    tex = TextureUtil.subImage(tex, subX, subY, subWidth, subHeight);
                    animation = Art.newAnimatedSplitImage(frames, tex, rows, cols);
                    for(TextureRegion r : animation.getImages()) {
                        r.flip(false, true);
                    }
                
                }
                catch(Exception e) {
                    /* Ignore any errors */
                }
            }
        });
        
        
        /*====================================*/
        /*
         * Lower control panel
         */
        /*====================================*/
                
        
        uiPos.x = app.getScreenWidth()/3;
        uiPos.y = app.getScreenHeight() - 50;
        
        /*====================================*/
        /* Animation Panel */
        /*====================================*/
        
        
        Button stopBtn = setupButton(uiPos, "Stop");
        animationPanel.addWidget(stopBtn);
        stopBtn.addOnButtonClickedListener(new OnButtonClickedListener() {
            
            @Override
            public void onButtonClicked(ButtonEvent event) {
                stepping = true;                
            }
        });
        
        uiPos.x += xSpacing;        
        
        Button slowBtn = setupButton(uiPos, "Slower");
        animationPanel.addWidget(slowBtn);
        slowBtn.addOnButtonClickedListener(new OnButtonClickedListener() {
            
            @Override
            public void onButtonClicked(ButtonEvent event) {
                String fpsTxt = fpsBox.getText();
                try {
                    int fps = Integer.parseInt(fpsTxt);
                    fpsBox.setText((fps + 10) + "");
                    loadAnimation.click();    
                }
                catch(Exception e) {
                    /* ignore */
                }
            }
        });
        
        uiPos.x += xSpacing;
        
        Button fasterBtn = setupButton(uiPos, "Faster");
        animationPanel.addWidget(fasterBtn);
        fasterBtn.addOnButtonClickedListener(new OnButtonClickedListener() {
            
            @Override
            public void onButtonClicked(ButtonEvent event) {
                String fpsTxt = fpsBox.getText();
                try {
                    int fps = Integer.parseInt(fpsTxt);
                    if(fps > 0) {
                        fpsBox.setText((fps - 10) + "");
                        loadAnimation.click();
                    }
                }
                catch(Exception e) {
                    /* ignore */
                }
                
            }
        });
            
        
        /*====================================*/
        /* Stepping Panel */
        /*====================================*/
        
        uiPos.x = app.getScreenWidth()/3;
        uiPos.y = app.getScreenHeight() - 50;

        Button playBtn = setupButton(uiPos, "Play");
        steppingPanel.addWidget(playBtn);        
        playBtn.addOnButtonClickedListener(new OnButtonClickedListener() {
            
            @Override
            public void onButtonClicked(ButtonEvent event) {
                stepping = false;
            }
        });
        
        uiPos.x += xSpacing;

        Button nextFrameBtn = setupButton(uiPos, "Next Frame");
        steppingPanel.addWidget(nextFrameBtn);        
        nextFrameBtn.addOnButtonClickedListener(new OnButtonClickedListener() {
            
            @Override
            public void onButtonClicked(ButtonEvent event) {
                nextFrame();
            }
        });
        
        uiPos.x += xSpacing;

        Button prevFrameBtn = setupButton(uiPos, "Prev Frame");
        steppingPanel.addWidget(prevFrameBtn);                
        prevFrameBtn.getTextLabel().setFont(theme.getPrimaryFontName());
        prevFrameBtn.addOnButtonClickedListener(new OnButtonClickedListener() {
            
            @Override
            public void onButtonClicked(ButtonEvent event) {
                prevFrame();
            }
        });
        
    }

    private boolean notEmpty(String s) {
        return s != null && !s.isEmpty();
    }    
    
    private TextBox setupTextBox(Vector2f pos, int width, final String label, final String defaultTxt) {
        TextBox pathBox = new TextBox();
        pathBox.setFocus(false);
        pathBox.setLabelText(label);
        pathBox.setBounds(new Rectangle(width, 25));
        pathBox.getBounds().centerAround(pos);
        pathBox.setTheme(theme);
        pathBox.setFont("Courier New");        
        pathBox.setMaxSize(128);
        pathBox.setText(defaultTxt);
        pathBox.addTextBoxActionListener(new TextBoxActionListener() {
            
            @Override
            public void onEnterPressed(TextBoxActionEvent event) {
                loadAnimation.click();
            }
        });
        this.optionsPanel.addWidget(pathBox);
        this.panelView.addElement(new TextBoxView(pathBox));
        return pathBox;
    }
    
    private Button setupButton(Vector2f pos, final String text) {
        return setupButton(pos, text, false);
    }
    
    private Button setupButton(Vector2f pos, final String text, boolean isBig) {
        Button btn = new Button();
        btn.setTheme(theme);
        btn.setText(text);
        btn.setBounds(isBig ? new Rectangle(100, 40) : new Rectangle(100, 30));
        btn.getBounds().centerAround(pos);
        btn.setForegroundColor(theme.getForegroundColor());
        btn.setEnableGradiant(false);
        btn.setTextSize(isBig ? 18 : 14);
        btn.setHoverTextSize(isBig ? 22 : 18);        
        btn.getTextLabel().setFont(theme.getPrimaryFontName());        
        btn.getTextLabel().setHorizontalTextAlignment(TextAlignment.LEFT);
        btn.getTextLabel().setForegroundColor(theme.getForegroundColor());        
        btn.addOnHoverListener(new OnHoverListener() {
            
            @Override
            public void onHover(HoverEvent event) {
                uiManager.getCursor().touchAccuracy();
            }
        });
        this.optionsPanel.addWidget(btn);
        this.panelView.addElement(new ButtonView(btn));
        
        return btn;
    }
    
    /* (non-Javadoc)
     * @see seventh.shared.State#enter()
     */
    @Override
    public void enter() {
        this.optionsPanel.show();
    }

    /* (non-Javadoc)
     * @see seventh.shared.State#exit()
     */
    @Override
    public void exit() {
        this.optionsPanel.hide();
    }
    
    /* (non-Javadoc)
     * @see seventh.client.Screen#destroy()
     */
    @Override
    public void destroy() {
    }
    
        
    /* (non-Javadoc)
     * @see seventh.shared.State#update(seventh.shared.TimeStep)
     */
    @Override
    public void update(TimeStep timeStep) {
        this.panelView.update(timeStep);
                
        uiManager.update(timeStep);
        uiManager.checkIfCursorIsHovering();
        
        if(uiManager.isKeyDown(Keys.ESCAPE)) {
            app.popScreen();
        }

        if(stepping) { 
            if(Gdx.input.isKeyPressed(Keys.LEFT) && keyDelay <= 0) {
                prevFrame();
                keyDelay = 300;
            }
            
            if(Gdx.input.isKeyPressed(Keys.RIGHT) && keyDelay <= 0) {
                nextFrame();
                keyDelay = 300;
            }
        }
        
        if(keyDelay > 0) {
            keyDelay -= timeStep.getDeltaTime();
        }
        
        if(animation != null) {
            if(stepping) {
                
                steppingPanel.show();
                animationPanel.hide();
                animation.getAnimation().setCurrentFrame(frameNumber);
            }
            else {
                animationPanel.show();
                steppingPanel.hide();
                
                animation.update(timeStep);
            }
        }
        else {
            animationPanel.show();
            steppingPanel.hide();
        }
        
    }




    /* (non-Javadoc)
     * @see seventh.client.Screen#render(seventh.client.gfx.Canvas)
     */
    @Override
    public void render(Canvas canvas, float alpha) {
        canvas.fillRect(0, 0, canvas.getWidth() + 100,  canvas.getHeight() + 100, theme.getBackgroundColor());
        
        this.panelView.render(canvas, null, 0);
        
        canvas.begin();
        canvas.setFont(theme.getPrimaryFontName(), 34);
        canvas.boldFont();
        
        int fontColor = theme.getForegroundColor();
        String message = "Animation Editor";
        RenderFont.drawShadedString(canvas, message
                , canvas.getWidth()/2 - canvas.getWidth(message)/2, 30, fontColor);
                

        canvas.end();
        
        canvas.fillRect(0, canvas.getHeight()/3, canvas.getWidth(), canvas.getHeight()/2, backgroundColor);
        canvas.drawRect(0, canvas.getHeight()/3, canvas.getWidth(), canvas.getHeight()/2, fontColor);

        renderAnimation(canvas, animation);

        this.uiManager.render(canvas);
    }
    
    private void renderAnimation(Canvas canvas, AnimatedImage animation) {
        
        if(animation != null) {
            int fontColor = theme.getForegroundColor();    
            TextureRegion frame = animation.getCurrentImage();
            
            int x = canvas.getWidth()/2 - frame.getRegionWidth()/2;
            int y = canvas.getHeight()/2 - frame.getRegionWidth();
        //    canvas.drawRect(x,y, frame.getRegionWidth(), frame.getRegionHeight(), 0xff00ff00);
            canvas.drawImage(frame, x, y, null);
            
            canvas.setFont("Courier New", 14);
            String message = frame.getRegionWidth() + " px";
            canvas.drawString(message, x + (frame.getRegionWidth()/2) - canvas.getWidth(message)/2, y + frame.getRegionHeight() + canvas.getHeight("W"), fontColor);
            
            message = frame.getRegionHeight() + " px";
            canvas.drawString(message, x + frame.getRegionWidth()+5, y + (frame.getRegionHeight()/2) + canvas.getHeight("W")/2, fontColor);
            
            renderAnimationWheel(canvas, frame);
        }
    }
    

    private void renderAnimationWheel(Canvas canvas, TextureRegion activeFrame) {
        /* render the animation frames */
//        int center = canvas.getWidth()/2 - activeFrame.getRegionWidth()/2; 
        
        int x = 0;//center - activeFrame.getRegionWidth() * animation.getAnimation().getCurrentFrame();
        int y = ((canvas.getHeight()/3) + canvas.getHeight()/2) - activeFrame.getRegionHeight();
        TextureRegion[] frames = animation.getImages();
        
        for(TextureRegion frame : frames) {
            int frameColor = 0xff00ff00;
            if(activeFrame==frame) {
                frameColor = 0xffff00ff;
            }
            
            canvas.drawImage(frame, x, y, null);
            canvas.drawRect(x,y, frame.getRegionWidth(), frame.getRegionHeight(), frameColor);
            x+=frame.getRegionWidth();
        }
    }
    
    private void prevFrame() {
        if(animation != null) {
            int numberOfFrames = animation.getAnimation().getNumberOfFrames();
            if(numberOfFrames > 0) {
                stepping = true;
                frameNumber = (animation.getAnimation().getCurrentFrame() - 1) % numberOfFrames;
                if(frameNumber<0) {
                    frameNumber = numberOfFrames-1;
                }
            }
        }        
    }
    
    private void nextFrame() {
        if(animation != null) {
            int numberOfFrames = animation.getAnimation().getNumberOfFrames();
            if(numberOfFrames > 0) {
                stepping = true;
                frameNumber = (animation.getAnimation().getCurrentFrame() + 1) % numberOfFrames;
            }
        }        
    }
    
    /* (non-Javadoc)
     * @see seventh.client.Screen#getInputs()
     */
    @Override
    public Inputs getInputs() {
        return this.uiManager;
    }

}
