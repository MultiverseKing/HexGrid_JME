package org.hexgridapi.editor.hexmap;

import com.jme3.app.Application;
import com.jme3.app.SimpleApplication;
import com.jme3.input.MouseInput;
import com.jme3.input.controls.MouseButtonTrigger;
import com.jme3.scene.Node;
import org.hexgridapi.editor.core.HexGridEditorMain;
import org.hexgridapi.editor.utility.gui.Base3DModuleTab;
import org.hexgridapi.editor.utility.gui.JPanelTab;
import org.hexgridapi.editor.utility.gui.JPanelTabController;
import org.hexgridapi.editor.utility.gui.JPanelTabListener;
import org.hexgridapi.editor.hexmap.gui.JHexEditorMenu;
import java.awt.BorderLayout;
import java.awt.Canvas;
import javax.swing.JMenuBar;
import javax.swing.JOptionPane;
import org.hexgridapi.core.appstate.MapDataAppState;
import org.hexgridapi.core.appstate.GridMouseControlAppState;
import org.hexgridapi.core.data.MapData;
import org.hexgridapi.core.geometry.builder.GridParam;
import org.hexgridapi.core.geometry.builder.coordinate.SquareCoordinate;

/**
 *
 * @author roah
 */
public final class HexMapModule extends Base3DModuleTab implements JPanelTabListener {

    private static JPanelTabController panelController = new JPanelTabController("HexMapPanelControl");
    private final GridMouseControlAppState mouseSystem = new GridMouseControlAppState();
    private MapDataAppState mapDataState;
    private HexMapAppState hexMapSystem;
    private SimpleApplication app;
    private boolean isStart = true;
    private final HexMapPanelTab hexPanel;

    public HexMapModule(Application app, JMenuBar menu) {
        super(app.getAssetManager().loadTexture("org/hexgridapi/assets/Textures/Icons/Buttons/hexIconBW.png").getImage(), "HexGrid Module");
        
        JHexEditorMenu editorMenu = new JHexEditorMenu(this);
        editorMenu.setAction(JHexEditorMenu.HexMenuAction.New);
        editorMenu.setAction(JHexEditorMenu.HexMenuAction.Load);
        editorMenu.setAction(JHexEditorMenu.HexMenuAction.Save); //@todo add when a map is loaded
        menu.add(editorMenu);
        
        GridParam param = new GridParam("org/hexgridapi/assets/Textures/HexField/", new String[]{"EARTH", "ICE", "NATURE", "VOLT"}, 
                          SquareCoordinate.class, true, true, false, true);
//                SquareCoordinate.class, true, false, false, true)); //useWater
        MapData mapData = new MapData(app.getAssetManager(), param);
        mapDataState = new MapDataAppState(mapData);
        hexMapSystem = new HexMapAppState(mapData);
        
        setLayout(new BorderLayout());
        
//        panelController.add(new HexMapPropertiesPanel(getIcon(), new JCursorPositionPanel(mouseSystem)));
        hexPanel = new HexMapPanelTab((HexGridEditorMain) app, mapDataState, hexMapSystem, mouseSystem);
        panelController.add(hexPanel);
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

    public boolean isStart() {
        return isStart;
    }

    @Override
    public Node getModuleNode() {
        return hexMapSystem.getGridNode();
    }

    public GridMouseControlAppState getMouseSystem() {
        return mouseSystem;
    }

    public MapData getMapDataState() {
        return mapDataState.getMapData();
    }

    public HexMapAppState getHexMapSystem() {
        return hexMapSystem;
    }

    public String getMapName() {
        return hexPanel.getMapName();
    }

    public void generateNewMap(GridParam param) {
        JOptionPane.showMessageDialog(getTopLevelAncestor(), "TODO...");
    }

    public void LoadMap(String loadName) {
        JOptionPane.showMessageDialog(getTopLevelAncestor(), "TODO... Load " + loadName);
    }

    public void saveMap(String saveName) {
        JOptionPane.showMessageDialog(getTopLevelAncestor(), "TODO... Save " + saveName);
    }
}
