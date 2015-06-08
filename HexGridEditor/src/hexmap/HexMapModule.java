package hexmap;

import com.jme3.app.Application;
import com.jme3.app.SimpleApplication;
import com.jme3.input.MouseInput;
import com.jme3.input.controls.MouseButtonTrigger;
import com.jme3.scene.Node;
import gui.Base3DModuleTab;
import gui.JPanelTab;
import gui.JPanelTabController;
import gui.JPanelTabListener;
import hexmap.gui.JCursorPositionPanel;
import hexmap.gui.JHexEditorMenu;
import java.awt.BorderLayout;
import java.awt.Canvas;
import javax.swing.JMenuBar;
import org.hexgridapi.core.appstate.MapDataAppState;
import org.hexgridapi.core.appstate.MouseControlSystem;
import org.hexgridapi.core.data.MapData;
import org.hexgridapi.core.geometry.builder.GridParam;
import org.hexgridapi.core.geometry.builder.coordinate.SquareCoordinate;

/**
 *
 * @author roah
 */
public final class HexMapModule extends Base3DModuleTab implements JPanelTabListener {

    private static JPanelTabController panelController = new JPanelTabController("HexMapPanelControl");
    private final MouseControlSystem mouseSystem = new MouseControlSystem();
    private MapDataAppState mapDataState;
    private HexMapSystem hexMapSystem;
    private SimpleApplication app;
    private boolean isStart = true;

    public HexMapModule(Application app, JMenuBar menu) {
        super(app.getAssetManager().loadTexture("Textures/Icons/Buttons/hexIconBW.png").getImage(), "HexGrid Module");
        
        JHexEditorMenu editorMenu = new JHexEditorMenu(this);
        editorMenu.setAction(JHexEditorMenu.HexMenuAction.New);
        editorMenu.setAction(JHexEditorMenu.HexMenuAction.Load);
        editorMenu.setAction(JHexEditorMenu.HexMenuAction.Save);
        menu.add(editorMenu);
        
        MapData mapData = new MapData(app.getAssetManager(), 
                new GridParam(new String[]{"EARTH", "ICE", "NATURE", "VOLT"}, 
                SquareCoordinate.class, true, true, false, true));
        mapDataState = new MapDataAppState(mapData);
        hexMapSystem = new HexMapSystem(mapData);
        
        setLayout(new BorderLayout());
        
        panelController.add(new HexMapPropertiesPanel(getIcon(), new JCursorPositionPanel(mouseSystem)));
        panelController.registerTabChangeListener(this);
        validate();
    }

    @Override
    public void onContextGainFocus(SimpleApplication app, Canvas canvas) {
        add(canvas, BorderLayout.CENTER);
        add(panelController, BorderLayout.EAST);
        this.app = app;
        app.getInputManager().addMapping("Confirm", new MouseButtonTrigger(MouseInput.BUTTON_LEFT));
        app.getInputManager().addMapping("Cancel", new MouseButtonTrigger(MouseInput.BUTTON_RIGHT));
        
        app.getStateManager().attachAll(mapDataState, hexMapSystem, mouseSystem);
        revalidate();
        isStart = true;
    }

    @Override
    public void onContextLostFocus() {
        app.getInputManager().deleteMapping("Confirm");
        app.getInputManager().deleteMapping("Cancel");

        app.getStateManager().detach(mouseSystem);
        app.getStateManager().detach(mapDataState);
        app.getStateManager().detach(hexMapSystem);
        isStart = false;
    }
    public void addPropertiesTab(JPanelTab tab){
        panelController.add(tab);
    }

    @Override
    public void onPanelChange(JPanelTab tab) {
//        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public void generateNewMap() {
//        mapDataState.getMapData().generateNewSeed();
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public boolean isStart() {
        return isStart;
    }

    @Override
    public Node getModuleNode() {
        return hexMapSystem.getGridNode();
    }

    public MouseControlSystem getMouseSystem() {
        return mouseSystem;
    }

    public MapData getMapDataState() {
        return mapDataState.getMapData();
    }

    public HexMapSystem getHexMapSystem() {
        return hexMapSystem;
    }

    public String getMapName() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public void saveMap() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
