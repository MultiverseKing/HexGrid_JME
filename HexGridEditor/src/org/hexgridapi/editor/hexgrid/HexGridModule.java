package org.hexgridapi.editor.hexgrid;

import com.jme3.app.Application;
import com.jme3.app.SimpleApplication;
import com.jme3.input.MouseInput;
import com.jme3.input.controls.MouseButtonTrigger;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import java.awt.BorderLayout;
import java.awt.Canvas;
import java.util.ArrayList;
import java.util.concurrent.Callable;
import javax.swing.JMenuBar;
import javax.swing.JOptionPane;
import org.hexgridapi.core.MapParam;
import org.hexgridapi.core.camera.RTSCamera;
import org.hexgridapi.core.coordinate.HexCoordinate;
import org.hexgridapi.core.data.HexTile;
import org.hexgridapi.core.data.MapData;
import org.hexgridapi.core.geometry.HexSetting;
import org.hexgridapi.core.mousepicking.GridMouseControlAppState;
import org.hexgridapi.editor.core.EditorMain;
import org.hexgridapi.editor.hexgrid.gui.HexGridPropertiesPan;
import org.hexgridapi.editor.hexgrid.gui.JHexEditorMenu;
import org.hexgridapi.editor.utility.gui.Base3DModuleTab;
import org.hexgridapi.editor.utility.gui.JPanelTab;
import org.hexgridapi.editor.utility.gui.JPanelTabController;
import org.hexgridapi.editor.utility.gui.JPanelTabListener;
import org.hexgridapi.events.MouseInputEvent;
import org.hexgridapi.utility.Vector2Int;
import org.slf4j.LoggerFactory;

/**
 *
 * @author roah
 */
public final class HexGridModule extends Base3DModuleTab implements JPanelTabListener {

    private final JPanelTabController panelController = new JPanelTabController("SubModuleControl");
    private final GridMouseControlAppState mouseSystem = new GridMouseControlAppState();
    private final JHexEditorMenu editorMenu;
    private final ArrayList<HexGridPropertiesPan> propertiesPans = new ArrayList<>(5);
    private final HexGridAppState hexGridState;
    private final RTSCamera rtsCam;
    private SimpleApplication app;
    private Vector3f camPos;
    private boolean init = false;

    public HexGridModule(Application app, JMenuBar menu) {
        super(app.getAssetManager().loadTexture("org/hexgridapi/assets/Textures/"
                + "Icons/Buttons/hexIconBW.png").getImage(), 
                "HexGrid Module", null, true);

        editorMenu = new JHexEditorMenu(this);
        editorMenu.setAction(JHexEditorMenu.HexMenuAction.New);
        editorMenu.setAction(JHexEditorMenu.HexMenuAction.Load);
        editorMenu.setAction(JHexEditorMenu.HexMenuAction.Save); //@todo add when a map is loaded
        menu.add(editorMenu);


        MapData mapData = new MapData(app.getAssetManager(), new String[]{"EARTH", "ICE", "NATURE", "VOLT"});
        rtsCam = new RTSCamera(RTSCamera.KeyMapping.col);
        camPos = rtsCam.getCenter();
        hexGridState = new HexGridAppState(mapData, rtsCam, "org/hexgridapi/assets/Textures/HexField/");
        moduleNode = hexGridState.getGridNode();

        setLayout(new BorderLayout());
        PropertiesTab hexPanel = new PropertiesTab((EditorMain) app, hexGridState, mouseSystem);
        addPropertiesTab(hexPanel);

        //----------------------------
        panelController.registerTabChangeListener(this);
        add(panelController, BorderLayout.EAST);
        validate();
    }

    @Override
    public void onContextGainFocus(SimpleApplication app, Canvas canvas) {
        add(canvas, BorderLayout.CENTER);
        this.app = app;
        app.getInputManager().addMapping(MouseInputEvent.MouseInputEventType.LMB.toString(), 
                new MouseButtonTrigger(MouseInput.BUTTON_LEFT));
        app.getInputManager().addMapping(MouseInputEvent.MouseInputEventType.RMB.toString(),
                new MouseButtonTrigger(MouseInput.BUTTON_RIGHT));
//        app.getInputManager().addMapping("Confirm", new MouseButtonTrigger(MouseInput.BUTTON_LEFT));
//        app.getInputManager().addMapping("Cancel", new MouseButtonTrigger(MouseInput.BUTTON_RIGHT));


//        app.getFlyByCamera().setEnabled(false);
        rtsCam.setCenter(camPos);
        app.getStateManager().attachAll(rtsCam, hexGridState, mouseSystem);
        if(!init) {
            for (HexGridPropertiesPan pan : propertiesPans) {
                pan.onMapLoaded();
            }
            init = true;
        }
        revalidate();
    }

    @Override
    public void onContextLostFocus() {
        app.getInputManager().deleteMapping(MouseInputEvent.MouseInputEventType.LMB.toString());
        app.getInputManager().deleteMapping(MouseInputEvent.MouseInputEventType.RMB.toString());
//        app.getInputManager().deleteMapping("Confirm");
//        app.getInputManager().deleteMapping("Cancel");

        app.getStateManager().detach(mouseSystem);
        app.getStateManager().detach(hexGridState);
        app.getStateManager().detach(rtsCam);
        camPos = rtsCam.getCenter();
//        app.getFlyByCamera().setEnabled(true);
    }

    public void addPropertiesTab(HexGridPropertiesPan tab) {
        propertiesPans.add(tab);
        panelController.add(tab);
    }

    @Override
    public void onPanelChange(JPanelTab tab) {
        LoggerFactory.getLogger(HexGridModule.class).info("Panel Switch To {}", tab.getName());
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
