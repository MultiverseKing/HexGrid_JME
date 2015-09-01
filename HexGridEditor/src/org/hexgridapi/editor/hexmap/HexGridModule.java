package org.hexgridapi.editor.hexmap;

import com.jme3.app.Application;
import com.jme3.app.SimpleApplication;
import com.jme3.input.MouseInput;
import com.jme3.input.controls.MouseButtonTrigger;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.scene.Node;
import org.hexgridapi.editor.core.HexGridEditorMain;
import org.hexgridapi.editor.utility.gui.Base3DModuleTab;
import org.hexgridapi.editor.utility.gui.JPanelTab;
import org.hexgridapi.editor.utility.gui.JPanelTabController;
import org.hexgridapi.editor.utility.gui.JPanelTabListener;
import org.hexgridapi.editor.hexmap.gui.JHexEditorMenu;
import java.awt.BorderLayout;
import java.awt.Canvas;
import java.util.ArrayList;
import java.util.concurrent.Callable;
import javax.swing.JMenuBar;
import javax.swing.JOptionPane;
import org.hexgridapi.core.mousepicking.GridMouseControlAppState;
import org.hexgridapi.core.data.MapData;
import org.hexgridapi.core.MapParam;
import org.hexgridapi.core.camera.RTSCamera;
import org.hexgridapi.core.camera.RTSCamera0;
import org.hexgridapi.core.coordinate.HexCoordinate;
import org.hexgridapi.core.data.HexTile;
import org.hexgridapi.core.geometry.HexSetting;
import org.hexgridapi.editor.hexmap.gui.HexGridPropertiesPan;
import org.hexgridapi.utility.Vector2Int;
import org.slf4j.LoggerFactory;

/**
 *
 * @author roah
 */
public final class HexGridModule extends Base3DModuleTab implements JPanelTabListener {

    private final JPanelTabController panelController = new JPanelTabController("HexMapPanelControl");
    private final GridMouseControlAppState mouseSystem = new GridMouseControlAppState();
    private final JHexEditorMenu editorMenu;
    private final ArrayList<HexGridPropertiesPan> propertiesPans = new ArrayList<>(5);
    private final HexGridAppState hexGridState;
    private SimpleApplication app;
    private RTSCamera rtsCam;
    private Vector3f camPos;

    public HexGridModule(Application app, JMenuBar menu) {
        super(app.getAssetManager().loadTexture("org/hexgridapi/assets/Textures/Icons/Buttons/hexIconBW.png").getImage(), "HexGrid Module");

        editorMenu = new JHexEditorMenu(this);
        editorMenu.setAction(JHexEditorMenu.HexMenuAction.New);
        editorMenu.setAction(JHexEditorMenu.HexMenuAction.Load);
        editorMenu.setAction(JHexEditorMenu.HexMenuAction.Save); //@todo add when a map is loaded
        menu.add(editorMenu);


        MapData mapData = new MapData(app.getAssetManager(), new String[]{"EARTH", "ICE", "NATURE", "VOLT"});
        rtsCam = new RTSCamera(RTSCamera.KeyMapping.col);
        camPos = rtsCam.getCenter();
        hexGridState = new HexGridAppState(mapData, rtsCam, "org/hexgridapi/assets/Textures/HexField/");

        setLayout(new BorderLayout());
        RootProperties hexPanel = new RootProperties((HexGridEditorMain) app, hexGridState, mouseSystem);
        addPropertiesTab(hexPanel);

        //----------------------------
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


        app.getFlyByCamera().setEnabled(false);
        rtsCam.setCenter(camPos);
        app.getStateManager().attachAll(rtsCam, hexGridState, mouseSystem);
        for (HexGridPropertiesPan pan : propertiesPans) {
            pan.onMapLoaded();
        }
        revalidate();
    }

    @Override
    public void onContextLostFocus() {
        app.getInputManager().deleteMapping("Confirm");
        app.getInputManager().deleteMapping("Cancel");

        app.getStateManager().detach(mouseSystem);
        app.getStateManager().detach(hexGridState);
        app.getStateManager().detach(rtsCam);
        camPos = rtsCam.getCenter();
        app.getFlyByCamera().setEnabled(true);
    }

    public void addPropertiesTab(HexGridPropertiesPan tab) {
        propertiesPans.add(tab);
        panelController.add(tab);
    }

    @Override
    public void onPanelChange(JPanelTab tab) {
        LoggerFactory.getLogger(HexGridModule.class).info("Panel Switch To {}", tab.getName());
    }

    @Override
    public Node getModuleNode() {
        return hexGridState.getGridNode();
    }

    public GridMouseControlAppState getMouseSystem() {
        return mouseSystem;
    }

    public HexGridAppState getHexGridSystem() {
        return hexGridState;
    }

    public void generateNewMap(final MapParam param) {
        app.enqueue(new Callable<Void>() {
            @Override
            public Void call() throws Exception {
                hexGridState.setParam(param);
//                rtsCam.setHeightProvider(hexGridState.getMapData());
                rtsCam.setHeightProvider(new RTSCamera.HeightProvider() {
                    @Override
                    public float getHeight(Vector2f coord) {
                        HexTile t = hexGridState.getMapData().getTile(
                                new HexCoordinate(HexCoordinate.Coordinate.OFFSET, 
                                new Vector2Int(coord)));
                        return (t != null ? t.getHeight() + 10 : 10) 
                                * HexSetting.FLOOR_OFFSET;
                    }
                });
                for (HexGridPropertiesPan pan : propertiesPans) {
                    pan.onMapReset();
                }
                return null;
            }
        });
    }

    public void LoadMap(String loadName) {
        JOptionPane.showMessageDialog(getTopLevelAncestor(), "TODO... Load " + loadName);
    }

    public void saveMap(String saveName) {
        JOptionPane.showMessageDialog(getTopLevelAncestor(), "TODO... Save " + saveName);
    }

    public void collapseProperties(boolean collapse) {
        panelController.collapse(collapse);
    }
}
