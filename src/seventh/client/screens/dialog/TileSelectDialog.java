/*
 * see license.txt 
 */
package seventh.client.screens.dialog;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.badlogic.gdx.graphics.g2d.TextureRegion;

import seventh.client.ClientPlayer;
import seventh.client.gfx.Camera;
import seventh.client.gfx.Canvas;
import seventh.client.gfx.Renderable;
import seventh.client.gfx.Theme;
import seventh.map.DefaultMapObjectFactory;
import seventh.map.DefaultMapObjectFactory.TileDefinition;
import seventh.map.TilesetAtlas;
import seventh.math.Rectangle;
import seventh.math.Vector2f;
import seventh.shared.Console;
import seventh.shared.EventDispatcher;
import seventh.shared.TimeStep;
import seventh.ui.Button;
import seventh.ui.ScrollBar.Orientation;
import seventh.ui.ScrollBar;
import seventh.ui.Scrollable;
import seventh.ui.Widget;
import seventh.ui.events.ButtonEvent;
import seventh.ui.events.HoverEvent;
import seventh.ui.events.OnButtonClickedListener;
import seventh.ui.events.OnHoverListener;
import seventh.ui.events.OnScrollBarListener;
import seventh.ui.events.ScrollBarEvent;
import seventh.ui.view.ButtonView;
import seventh.ui.view.ImageButtonView;
import seventh.ui.view.PanelView;
import seventh.ui.view.ScrollBarView;

/**
 * A dialog box to pick the weapon class.
 * 
 * @author Tony
 *
 */
public class TileSelectDialog extends Widget implements Scrollable {

    public static interface OnHideListener {
        void onShow();
        void onHide();
    }
    
    
    private Theme theme;
    
    private Button closeBtn;
    private List<Button> tileBtns;
    
    private OnHideListener onHide;
    
    private Map<Integer, TileDefinition> tiles;
    
    private PanelView panelView;
    private TilePanelView tilePanelView;
    
    private TilesetAtlas atlas;
    
    private ScrollBar scrollBar;
    private int totalHeight;
    
    private ClientPlayer player;
    private Console console;
    
    private int scrollIndex;
    private int maxIndex;
    private int tilesPerRow;
    
    private int xMargin, yMargin;
    private int buttonWidth, buttonHeight;
    
    /**
     */
    public TileSelectDialog(Console console, ClientPlayer player, 
            TilesetAtlas atlas, DefaultMapObjectFactory factory, Theme theme) {
        
        super(new EventDispatcher());
        this.console = console;
        this.theme = theme;
        this.atlas = atlas;
        this.player = player;
        
        this.tiles = factory.getTileDefinitions();
        this.tileBtns = new ArrayList<>();
        
        this.xMargin = 8;
        this.yMargin = 8;
        
        this.buttonWidth = 32;
        this.buttonHeight = 32;
        
        createUI();
    }
    
    /**
     * @param onHide the onHide to set
     */
    public void setOnHide(OnHideListener onHide) {
        this.onHide = onHide;
    }
    
    /* (non-Javadoc)
     * @see seventh.ui.Widget#destroy()
     */
    @Override
    public void destroy() {    
        super.destroy();
    }
    
    /* (non-Javadoc)
     * @see seventh.ui.Widget#isDisabled()
     */
    @Override
    public boolean isDisabled() {    
        return super.isDisabled();
    }
    
    /**
     * @return true if this dialog (or sub dialogs) are open
     */
    public boolean isOpen() {
        return !super.isDisabled();
    }
    
    /* (non-Javadoc)
     * @see seventh.ui.Widget#hide()
     */
    @Override
    public void hide() {     
        super.hide();
    }
    
    /* (non-Javadoc)
     * @see seventh.ui.Widget#show()
     */
    @Override
    public void show() {
        super.show();
        if(this.onHide!=null) {
            this.onHide.onShow();
        }
    }
    
    /**
     * Closes this dialog window
     */
    public void close() {
        hide();
        
        if(this.onHide!=null) {
            this.onHide.onHide();
        }
    }
    
    @Override
    public void setBounds(Rectangle bounds) {    
        super.setBounds(bounds);
                        
        createUI();
    }
        
    /**
     * @return the panelView
     */
    public PanelView getPanelView() {
        return panelView;
    }
    
    private void createUI() {
        destroyChildren();
        

        final int startX = 5;
        final int startY = 5;
        
        Rectangle bounds = getBounds();
        
        
        scrollBar = new ScrollBar(this, this.getEventDispatcher());        
        addWidget(scrollBar);
        
        scrollBar.getBounds()
                    .set(bounds.width - 15, 0, 15, bounds.height);
        scrollBar.setTheme(theme);
        scrollBar.setOrientation(Orientation.Vertical);
        scrollBar.setScrollIncrement(32);
        
        int xWidth = this.buttonWidth + xMargin;
        if(xWidth == 0) {
            xWidth = 1;
        }
        
        this.tilesPerRow = (bounds.width - (scrollBar.getBounds().width + startX)) / xWidth;
        if(this.tilesPerRow > 0) {
            this.maxIndex = this.tiles.size() / this.tilesPerRow;
        }
                
        scrollBar.addScrollBarListener(new OnScrollBarListener() {
            
            @Override
            public void onScrollBar(ScrollBarEvent event) {
                int delta = event.getMovementDelta();
                if(delta < 0) {
                    while(delta < 0) {
                        scrollIndex--;
                        if(scrollIndex < 0) {
                            scrollIndex = 0;
                        }
                        
                        delta++;
                    }
                }
                else if(delta > 0) {
                    while(delta > 0) {
                        scrollIndex++;
                        if(scrollIndex > maxIndex) {
                            scrollIndex = maxIndex;
                        }
                        
                        delta--;
                    }
                }
            }
        });
        
        this.panelView = new PanelView();
        this.tilePanelView = new TilePanelView();
        this.panelView.addElement(this.tilePanelView);
        this.panelView.addElement(new ScrollBarView(scrollBar));
        
        int xInc = this.buttonWidth  + this.xMargin;
        int yInc = this.buttonHeight + this.yMargin;        
        Vector2f pos = new Vector2f(startX, startY);
    
        
        for(Map.Entry<Integer, TileDefinition> entry : this.tiles.entrySet()) {
            setupButton(pos, entry.getValue());
            
            pos.x += xInc;
            if(pos.x > (bounds.width - xInc - scrollBar.getBounds().width)) {
                pos.x = startX;
                pos.y += yInc;
            }
        }
        
        this.totalHeight = startY + (int)pos.y + this.buttonHeight;
        
        scrollBar.calculateHandlePosition();
    }
    
    private Button setupButton(Vector2f pos, final TileDefinition tile) {
        final Button btn = new Button();        
        btn.setBounds(new Rectangle(this.buttonWidth, this.buttonHeight));
        btn.getBounds().setLocation(pos);
        btn.setEnableGradiant(false);
        btn.setTheme(theme.newTheme().setForegroundColor(theme.getHoverColor()).setHoverColor(theme.getForegroundColor()));
        btn.setForegroundColor(0xffffffff);
        btn.setBorder(false);
        //btn.setText(tile.type + "");
        btn.addOnButtonClickedListener(new OnButtonClickedListener() {
            
            @Override
            public void onButtonClicked(ButtonEvent event) {
                console.execute("change_tile " + tile.type);
                hide();
            }
        });
        
        btn.addOnHoverListener(new OnHoverListener() {
            
            @Override
            public void onHover(HoverEvent event) {
                final int offset = 2;
                if(event.isHovering()) {
                    btn.getBounds().addSize(offset, offset);
                    btn.getBounds().add(-offset, -offset);
                }
                else {
                    btn.getBounds().addSize(-offset, -offset);
                    btn.getBounds().add(offset, offset);
                }
                
            }
        });
                
        addWidget(btn);
        
        this.tilePanelView.buttonViews.add(new ImageButtonView(btn, resize(this.atlas.getTile(tile.tileId))));
        
        return btn;
    }  
    
    private TextureRegion resize(TextureRegion tex) {
        //TextureRegion result = new TextureRegion(tex, 1, 1, 31, 31);        
        //return result;
        return tex;
    }

    @Override
    public Rectangle getViewport() {
        return getBounds();
    }

    @Override
    public int getTotalHeight() {
        return this.totalHeight;
    }

    @Override
    public int getTotalWidth() {
        return getBounds().width - this.scrollBar.getBounds().width;
    }
    
 
    private class TilePanelView implements Renderable {
        List<ImageButtonView> buttonViews = new ArrayList<>();
        
        
        @Override
        public void update(TimeStep timeStep) {
        }

        @Override
        public void render(Canvas canvas, Camera camera, float alpha) {            
            Rectangle bounds = getBounds();
            
            final int startX = 5;
            final int startY = 5;
            
            int x = startX; 
            int y = startY;
            
            final int xInc = buttonWidth  + xMargin;
            final int yInc = buttonHeight + yMargin;
            
            int size = buttonViews.size();
            
            int index = Math.max((scrollIndex * tilesPerRow) - 1, 0);
            
            for(int i = 0; i < size; i++) {
                ButtonView view = this.buttonViews.get(i);
                Button btn = view.getButton();
                btn.setDisabled(true);
                
                if(index > 0 && i <= index) {
                    continue;
                }
                
                Rectangle btnBnds = btn.getBounds();
                btnBnds.y = y;
                
                Rectangle rect = btn.getScreenBounds();                        
                if(bounds.contains(rect)) {
                    btn.setDisabled(false);
                    if(btn.isHovering()) {
//                        renderer.fillRect(rect.x - 10, rect.y - 5, bounds.width - 10, rect.height, 0x0fffffff);
                    }
                    view.render(canvas, camera, alpha);
                    
                    //canvas.setDefaultFont();
                    //canvas.drawString(btn.getText(), rect.x, rect.y, 0xffffffff);
                }
                
                x += xInc;
                
                if(x > (bounds.width - xInc - scrollBar.getBounds().width)) {
                    x = startX;                    
                    y += yInc;
                }
                
            }
        }

    }
}
