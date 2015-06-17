package org.hexgridapi.editor.core;

import com.jme3.renderer.RenderManager;
import com.jme3.system.AppSettings;
import com.jme3.system.JmeCanvasContext;
import org.hexgridapi.editor.hexmap.HexMapModule;
import java.awt.Dimension;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.logging.Level;
import javax.swing.JFrame;
import javax.swing.JMenuBar;
import org.hexgridapi.core.appstate.HexGridDefaultApplication;

/**
 *
 * @author normenhansen, roah
 */
public class HexGridEditorMain extends HexGridDefaultApplication {

    public static void main(String[] args) {
        java.awt.EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                HexGridEditorMain editorMain = new HexGridEditorMain("Hex Grid Editor");
            }
        });
    }
    protected final ModuleControlTab moduleControl;
    private static JFrame rootFrame;
    private HexMapModule hexMapModule;
    private boolean isStart = false;

    public HexGridEditorMain(String windowName) {
        AppSettings initSettings = new AppSettings(true);
        Dimension dim = new Dimension(1024, 768);
        initSettings.setWidth(dim.width);
        initSettings.setHeight(dim.height);

        java.util.logging.Logger.getLogger("").setLevel(Level.WARNING);

        rootFrame = new JFrame(windowName);
        rootFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        rootFrame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosed(WindowEvent e) {
                stop();
            }
        });
        setSettings(initSettings);

        createCanvas(); // create canvas!
        JmeCanvasContext ctx = (JmeCanvasContext) getContext();
        ctx.getCanvas().setSize(dim);

        //-------------
        rootFrame.setJMenuBar(new JMenuBar());

        moduleControl = new ModuleControlTab(this);
        rootFrame.getContentPane().add(moduleControl.getContent());

        //-------------

        rootFrame.setMinimumSize(dim);
        rootFrame.pack();
        rootFrame.setLocationRelativeTo(null);
        rootFrame.setVisible(true);

        startCanvas();
        ctx.setSystemListener(this);
    }

    public JFrame getRootFrame() {
        return rootFrame;
    }

    public ModuleControlTab getModuleControl() {
        return moduleControl;
    }

    public HexMapModule getHexMapModule() {
        return hexMapModule;
    }

    @Override
    public final void initApp() {
        hexMapModule = new HexMapModule(this, rootFrame.getJMenuBar());
        moduleControl.addRootTab(hexMapModule);
        initApplication();
    }

    @Override
    public void simpleUpdate(float tpf) {
        //TODO: add update code
    }

    @Override
    public void simpleRender(RenderManager rm) {
        //TODO: add render code
    }

    public void initApplication() {
    }
}