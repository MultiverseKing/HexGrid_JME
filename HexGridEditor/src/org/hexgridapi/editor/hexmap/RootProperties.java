package org.hexgridapi.editor.hexmap;

import org.hexgridapi.editor.utility.gui.ComboBoxRenderer;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.Callable;
import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.plaf.basic.BasicArrowButton;
import org.hexgridapi.core.mousepicking.GridMouseControlAppState;
import org.hexgridapi.core.coordinate.HexCoordinate;
import org.hexgridapi.editor.core.HexGridEditorMain;
import org.hexgridapi.editor.hexmap.gui.HexGridPropertiesPan;
import org.hexgridapi.editor.hexmap.gui.JCursorPositionPanel;
import org.hexgridapi.events.TileChangeEvent;
import org.hexgridapi.events.MapDataListener;
import org.hexgridapi.events.TileSelectionListener;

/**
 * 
 * @author roah
 */
public final class RootProperties extends HexGridPropertiesPan {

    private final HexGridEditorMain editorMain;
    private final HexGridAppState hexSystem;
    private final GridMouseControlAppState mouseSystem;
    private boolean update = true;
    private boolean ghostIsVisible = true;
    private boolean currentIsGroup = false;
    private Boolean currentIsGhost;
    private JPanel tileProperties;
    private HashMap<String, JComponent> comps = new HashMap<>();

    public RootProperties(HexGridEditorMain editorMain, HexGridAppState hexMapSystem, GridMouseControlAppState mouseSystem) {
        super(editorMain.getAssetManager().loadTexture(
                "org/hexgridapi/assets/Textures/Icons/Buttons/configKey.png").getImage(), "HexMapConfig");
        this.editorMain = editorMain;
        this.mouseSystem = mouseSystem;
        hexSystem = hexMapSystem;
    }
    
    private void buildMenu(boolean useVoidTile) {
        setBorder(BorderFactory.createTitledBorder("Map Property"));
        setPreferredSize(new Dimension(170, 300));
        JSeparator separator = new JSeparator(SwingConstants.HORIZONTAL);
        separator.setMaximumSize(new Dimension(Integer.MAX_VALUE, 2));
        addWithSpace(separator);
        add(new JCursorPositionPanel(mouseSystem));
        
        /*-------       Show/Hide Void Tile       ------*/
        JCheckBox hideVoidTile = new JCheckBox(new AbstractAction("Hide Void Tile") {
                      @Override
                      public void actionPerformed(ActionEvent e) {
                          onAction(e);
                      }
                  });
        hideVoidTile.setSelected(ghostIsVisible);
        hideVoidTile.setMaximumSize(new Dimension(Integer.MAX_VALUE, 20));
        hideVoidTile.setAlignmentX(0);
        addWithSpace(hideVoidTile);
        hideVoidTile.setEnabled(useVoidTile);
        comps.put("voidBtn", hideVoidTile);

        /*-------       Map Name       ------*/
        JLabel mapNameLabel = new JLabel("Map Name : ");
        mapNameLabel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 18));
        add(mapNameLabel);

        JTextField mapName = new JTextField(hexSystem.getMapName());
        mapName.addFocusListener(new FocusListener() {
            @Override
            public void focusGained(FocusEvent e) {
            }

            @Override
            public void focusLost(FocusEvent e) {
                editorMain.enqueue(new Callable<Void>() {
                    @Override
                    public Void call() throws Exception {
                        hexSystem.setMapName(((JTextField) comps.get("mapName")).getText());
                        return null;
                    }
                });
            }
        });
        comps.put("mapName", mapName);
        mapName.setMaximumSize(new Dimension(Integer.MAX_VALUE, 25));
        mapName.setAlignmentX(0);
        add(mapName);

        /*-------       Map Generator       ------*/
        JLabel mapGenerator = new JLabel("Random Generator : ");
        mapGenerator.setMaximumSize(new Dimension(Integer.MAX_VALUE, 18));
        add(mapGenerator);

        JPanel seedPan = new JPanel();
        seedPan.setLayout(new BoxLayout(seedPan, BoxLayout.LINE_AXIS));
        seedPan.setMaximumSize(new Dimension(Integer.MAX_VALUE, 25));
        seedPan.setAlignmentX(0);

        JLabel currentSeed = new JLabel();
        comps.put("currentSeed", currentSeed);
        seedPan.add(currentSeed);
        seedPan.add(Box.createRigidArea(new Dimension(5, 0)));
        add(seedPan);
        if (hexSystem.useProceduralGen()) {
            currentSeed.setText("Seed : " + String.valueOf(hexSystem.getSeed()));
        } else {
            currentSeed.setText("Seed : Undefined");
        }

        /*-------*/
        separator = new JSeparator(SwingConstants.HORIZONTAL);
        separator.setMaximumSize(
                new Dimension(Integer.MAX_VALUE, 2));
        add(separator);
    }

    private void buildTileMenu() {

        currentIsGhost = !hexSystem.tileExist();
        if (tileProperties == null) {
            tileProperties = new JPanel();
            tileProperties.setLayout(new BoxLayout(tileProperties, BoxLayout.PAGE_AXIS));
            tileProperties.setAlignmentX(0);
            tileProperties.setBorder(BorderFactory.createTitledBorder("Tile Property"));
            addWithSpace(tileProperties);
        } else {
            tileProperties.removeAll();
        }
    }

    private void buildSingleTileMenu() {
        buildTileMenu();
        // Component Value
        int compCount = 1;
        currentIsGroup = false;
        if (currentIsGhost) {
            compCount += addGenerateBtn();
        } else {
            compCount += addTextureList();
            compCount += addHeightBtn(false);
            compCount += addDestroyBtn();
            tileProperties.add(Box.createRigidArea(new Dimension(0, 5)));
        }
        tileProperties.setMaximumSize(new Dimension(getMaximumSize().width, compCount * 23 + 10));
        revalidate();
    }

    // @todo does not work properly
    private void buildMultiTileMenu(ArrayList<HexCoordinate> selectedList) {
        buildTileMenu();
        currentIsGroup = !selectedList.isEmpty();
        int compCount = 1;
        compCount += addTextureList();
        compCount += addHeightBtn(true);
        compCount += addGenerateBtn();
        compCount += addDestroyBtn();
        tileProperties.setMaximumSize(new Dimension(getMaximumSize().width, compCount * 23 + 10));

        revalidate();
    }

    // <editor-fold defaultstate="collapsed" desc="Add Component Method">
    private int addGenerateBtn() {
        if (!comps.containsKey("generate")) {
            JButton generate = new JButton(new AbstractAction("Generate/Reset") {
                @Override
                public void actionPerformed(ActionEvent e) {
                    onAction(e);
                }
            });
            addComp(tileProperties, generate);
            comps.put("generate", generate);
        } else {
            addComp(tileProperties, comps.get("generate"));
        }
        return 1;
    }

    private int addHeightBtn(boolean isMulti) {
        if (!comps.containsKey("heightPanel")) {
            JPanel heightPanel = new JPanel();
            heightPanel.setAlignmentX(0);
            heightPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE));
            heightPanel.setLayout(new BorderLayout());
            BasicArrowButton btn = new BasicArrowButton(BasicArrowButton.NORTH);
            btn.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    onAction(new ActionEvent(e.getSource(), e.getID(), "btnUp"));
                }
            });
            heightPanel.add(btn, BorderLayout.NORTH);
            btn = new BasicArrowButton(BasicArrowButton.SOUTH);
            btn.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    onAction(new ActionEvent(e.getSource(), e.getID(), "btnDown"));
                }
            });
            heightPanel.add(btn, BorderLayout.SOUTH);
            JLabel height;
            if (!isMulti) {
                height = new JLabel("height : " + hexSystem.getTileHeight());
            } else {
                height = new JLabel("height : undefined");
            }
            heightPanel.add(height, BorderLayout.CENTER);
            comps.put("height", height);
            addComp(tileProperties, heightPanel);
            heightPanel.validate();
            comps.put("heightPanel", heightPanel);
        } else {
            addComp(tileProperties, comps.get("heightPanel"));
            if (!isMulti) {
                ((JLabel) comps.get("height")).setText("height : " + hexSystem.getTileHeight());
            } else {
                ((JLabel) comps.get("height")).setText("height : undefined");
            }
        }
        return 3;
    }

    private int addTextureList() {
        if (!comps.containsKey("textureList")) {
            ComboBoxRenderer combo = new ComboBoxRenderer(
                    editorMain.getAssetManager(), hexSystem.getTextureKeys());
            JComboBox textureList = new JComboBox(combo.getArray());
            textureList.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    onAction(new ActionEvent(e.getSource(), e.getID(), e.getActionCommand() + "." + ((JComboBox) comps.get("textureList")).getSelectedIndex()));
                }
            });
            textureList.setRenderer(combo);
            textureList.setMaximumRowCount(4);
            textureList.setAlignmentX(0);
            textureList.setMaximumSize(new Dimension(Integer.MAX_VALUE, 20));
            addComp(tileProperties, textureList);
            comps.put("textureList", textureList);
        } else {
            addComp(tileProperties, comps.get("textureList"));
            update = false;
            int var = hexSystem.getTextureKeys().indexOf(hexSystem.getTileTextureKey());
            ((JComboBox) comps.get("textureList")).setSelectedIndex(var);
        }
        return 1;
    }

    private int addDestroyBtn() {
        if (!comps.containsKey("destroy")) {
            JButton destroy = new JButton(new AbstractAction("Destroy") {
                @Override
                public void actionPerformed(ActionEvent e) {
                    onAction(e);
                }
            });
            destroy.setAlignmentX(0);
            destroy.setMaximumSize(new Dimension(Integer.MAX_VALUE, 20));
            addComp(tileProperties, destroy);
            comps.put("destroy", destroy);
        } else {
            addComp(tileProperties, comps.get("destroy"));
        }
        return 1;
    }

    // </editor-fold>

    private void onAction(ActionEvent e) {
        if (e.getActionCommand().contains("comboBox") && update) {
            final int value = Integer.valueOf(e.getActionCommand().split("\\.")[1]);
            editorMain.enqueue(new Callable<Void>() {
                @Override
                public Void call() throws Exception {
                    hexSystem.setTilePropertiesTexTure(hexSystem.getTextureValueFromKey(value));
                    return null;
                }
            });
            return;
        } else if (e.getActionCommand().contains("comboBox")) {
            update = true;
            return;
        }
        switch (e.getActionCommand()) {
            case "Hide Void Tile":
                editorMain.enqueue(new Callable<Void>() {
                    @Override
                    public Void call() throws Exception {
                        hexSystem.hideVoidTile(ghostIsVisible);
                        ghostIsVisible = !ghostIsVisible;
                        return null;
                    }
                });
                break;
            case "Destroy":
                editorMain.enqueue(new Callable<Void>() {
                    @Override
                    public Void call() throws Exception {
                        hexSystem.removeTile();
                        return null;
                    }
                });
                break;
            case "btnUp":
                editorMain.enqueue(new Callable<Void>() {
                    @Override
                    public Void call() throws Exception {
                        hexSystem.setTilePropertiesUp();
                        return null;
                    }
                });
                break;
            case "btnDown":
                editorMain.enqueue(new Callable<Void>() {
                    @Override
                    public Void call() throws Exception {
                        hexSystem.setTilePropertiesDown();
                        return null;
                    }
                });
                break;
            case "Generate/Reset":
                editorMain.enqueue(new Callable<Void>() {
                    @Override
                    public Void call() throws Exception {
//                        hexSystem.setNewTile();
                        hexSystem.setTilePropertiesUp();
                        return null;
                    }
                });
                break;
            default:
                System.err.println("No associated action for : " + e.getActionCommand());
        }
    }
    private TileSelectionListener selectionListener = new TileSelectionListener() {
        @Override
        public void onTileSelectionUpdate(HexCoordinate currentSelection, ArrayList<HexCoordinate> selectedList) {
            if (currentIsGhost == null || !selectedList.isEmpty() != currentIsGroup
                    || !hexSystem.tileExist() != currentIsGhost) {
                if (!selectedList.isEmpty()) {
                    buildMultiTileMenu(selectedList);
                } else {
                    buildSingleTileMenu();
                }
            } else {
                if (!selectedList.isEmpty()) {
//                    updateMultiTileMenu(selectedList);
                } else {
                    updateSingleTileMenu();
                }
            }
        }
    };
    private MapDataListener dataListener = new MapDataListener() {
        @Override
        public void onTileChange(TileChangeEvent... events) {
            if (currentIsGhost != null) {
                if (!currentIsGroup && events.length == 1
                        && events[0].getTilePos().equals(mouseSystem.getSelectionControl().getSelectedPos())) {
                    if (!hexSystem.tileExist() != currentIsGhost) {
                        buildSingleTileMenu();
                    } else {
                        updateSingleTileMenu();
                    }
                }
            }
        }
    };
    
    private void updateSingleTileMenu() {
        currentIsGhost = !hexSystem.tileExist();
        if (!currentIsGhost) {
            ((JLabel) comps.get("height")).setText("height : " + hexSystem.getTileHeight());
            update = false;
            editorMain.enqueue(new Callable<Void>() {
                @Override
                public Void call() throws Exception {
                    int var = hexSystem.getTextureKeys().indexOf(hexSystem.getTileTextureKey());
                    ((JComboBox) comps.get("textureList")).setSelectedIndex(var);
                    return null;
                }
            });
        }
        revalidate();
    }

    private void updateMultiTileMenu(ArrayList<HexCoordinate> selectedList) {
//        currentIsGroup = !selectedList.isEmpty();
        System.err.println("update group");
//        hexMapPanel.validate();
    }

    @Override
    public void isShow() {
    }

    @Override
    public void isHidden() {
    }
    
    @Override
    public void onMapLoaded() {
        mouseSystem.getSelectionControl().register(selectionListener);
        hexSystem.getMapData().register(dataListener);
        
        buildMenu(hexSystem.buildVoidTile());
    }

    @Override
    public void onMapReset() {
        comps.get("voidBtn").setEnabled(hexSystem.buildVoidTile());
        ((JCheckBox)comps.get("voidBtn")).setSelected(hexSystem.showVoidTile());
        if (hexSystem.useProceduralGen()) {
            ((JLabel)comps.get("currentSeed")).setText("Seed : " + String.valueOf(hexSystem.getSeed()));
        } else {
            ((JLabel)comps.get("currentSeed")).setText("Seed : Undefined");
        }
    }

    @Override
    public void onMapRemoved() {
        hexSystem.getMapData().unregister(dataListener);
        mouseSystem.getSelectionControl().unregister(selectionListener);
//        clearMenu();
    }

    public String getMapName(){
        return ((JTextField)comps.get("mapName")).getText();
    }
}
