package org.hexgridapi.editor.hexmap;

import org.hexgridapi.editor.utility.gui.ComboBoxRenderer;
import org.hexgridapi.editor.utility.gui.JPanelTab;
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
import org.hexgridapi.core.appstate.MapDataAppState;
import org.hexgridapi.core.appstate.GridMouseControlAppState;
import org.hexgridapi.core.geometry.builder.coordinate.HexCoordinate;
import org.hexgridapi.editor.core.HexGridEditorMain;
import org.hexgridapi.editor.hexmap.gui.JCursorPositionPanel;
import org.hexgridapi.events.TileChangeEvent;
import org.hexgridapi.events.TileChangeListener;
import org.hexgridapi.events.TileSelectionListener;

/**
 * @todo in short : Add a dropBox to chose the kind of replacement to set when
 * ghost tile isn't visible.
 * @todo extended : When using showGhost all ghost tile got removed but this
 * lead to ugly visual since there is no replacement for it.
 * @author roah
 */
public final class HexMapPanelTab extends JPanelTab {

    private final HexGridEditorMain editorMain;
    private HexMapAppState hexSystem;
    private GridMouseControlAppState mouseSystem;
    private Boolean currentIsGhost;
    private boolean currentIsGroup = false;
    private JPanel tileProperties;
    private HashMap<String, JComponent> comps = new HashMap<>();
    private boolean update = true;
    private boolean ghostIsVisible = true;

    public HexMapPanelTab(HexGridEditorMain editorMain, MapDataAppState mapDataState, HexMapAppState hexMapSystem, GridMouseControlAppState mouseSystem) {
        super(editorMain.getAssetManager().loadTexture(
                "org/hexgridapi/assets/Textures/Icons/Buttons/configKey.png").getImage(), "HexMapConfig");
        this.editorMain = editorMain;
        this.mouseSystem = mouseSystem;
        hexSystem = hexMapSystem;

        this.mouseSystem.getSelectionControl().registerTileListener(selectionListener);
        mapDataState.getMapData().registerTileChangeListener(tileListener);

        buildMenu(mapDataState.getMapData().getGridParameters().isBuildVoidTile());
    }
    
    private void buildMenu(boolean useVoidTile) {
        setBorder(BorderFactory.createTitledBorder("Map Property"));
        setPreferredSize(new Dimension(170, 300));
        JSeparator separator = new JSeparator(SwingConstants.HORIZONTAL);
        separator.setMaximumSize(new Dimension(Integer.MAX_VALUE, 2));
        addComp(separator);

        add(new JCursorPositionPanel(mouseSystem));

        if(useVoidTile){
            JCheckBox box = new JCheckBox(new AbstractAction("Show ghost") {
                @Override
                public void actionPerformed(ActionEvent e) {
                    onAction(e);
                }
            });
            box.setSelected(ghostIsVisible);
            box.setMaximumSize(new Dimension(Integer.MAX_VALUE, 20));
            box.setAlignmentX(0);
            addComp(box);
        }

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

        if (hexSystem.isUsingProcedural()) {
            JLabel currentSeed = new JLabel("Seed : " + String.valueOf(hexSystem.getSeed()));
            comps.put("currentSeed", currentSeed);
            seedPan.add(currentSeed);
            seedPan.add(Box.createRigidArea(new Dimension(5, 0)));
            add(seedPan);
        }

        /*-------*/
        separator = new JSeparator(SwingConstants.HORIZONTAL);
        separator.setMaximumSize(
                new Dimension(Integer.MAX_VALUE, 2));
        add(separator);

//        /* Test */
//        JButton generate = new JButton(new AbstractAction("Add Chunk") {
//            @Override
//            public void actionPerformed(ActionEvent e) {
//                onAction(e);
//            }
//        });
//        add(generate);
//        comps.put("generate", generate);

    }

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
            case "Show ghost":
                editorMain.enqueue(new Callable<Void>() {
                    @Override
                    public Void call() throws Exception {
                        hexSystem.hideGhost(ghostIsVisible);
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
    private TileChangeListener tileListener = new TileChangeListener() {
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
        
        @Override
        public void onGridReload(){
            
        }
    };

    private void buildTileMenu() {

        currentIsGhost = !hexSystem.tileExist();
        if (tileProperties == null) {
            tileProperties = new JPanel();
            tileProperties.setLayout(new BoxLayout(tileProperties, BoxLayout.PAGE_AXIS));
            tileProperties.setAlignmentX(0);
            tileProperties.setBorder(BorderFactory.createTitledBorder("Tile Property"));
            addComp(tileProperties);
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
    
    // <editor-fold defaultstate="collapsed" desc="Getters">

    public String getMapName(){
        return ((JTextField)comps.get("mapName")).getText();
    }
    
    // </editor-fold>
}
