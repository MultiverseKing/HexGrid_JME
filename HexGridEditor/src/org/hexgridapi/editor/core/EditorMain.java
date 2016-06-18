package org.hexgridapi.editor.core;

import com.jme3.app.SimpleApplication;
import com.jme3.renderer.RenderManager;
import com.jme3.system.AppSettings;
import com.jme3.system.JmeCanvasContext;
import java.awt.Dimension;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.logging.Level;
import javax.swing.JFrame;
import javax.swing.JMenuBar;
import org.hexgridapi.editor.hexgrid.HexGridModule;

/**
 *
 * @author normenhansen, roah
 */
public class EditorMain extends SimpleApplication {

    public static void main(String[] args) {
        java.util.logging.Logger.getLogger("com.jme3").setLevel(Level.WARNING);
//        final org.slf4j.Logger logger =
//                org.slf4j.LoggerFactory.getLogger("org.hexgridapi");
//        ch.qos.logback.classic.Logger logbackLogger =
//                (ch.qos.logback.classic.Logger) logger;
//        logbackLogger.setLevel(ch.qos.logback.classic.Level.ALL);
        
        java.awt.EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                EditorMain editorMain = new EditorMain("Hex Grid Editor");
            }
        });
    }
    private static JFrame rootFrame;
    private DefaultSetting defaultSetting;
    private HexGridModule hexGridModule;
    protected final ModuleControl moduleControl;

    public EditorMain(String windowName) {
        AppSettings initSettings = new AppSettings(true);
        Dimension dim = new Dimension(1024, 768);
        initSettings.setWidth(dim.width);
        initSettings.setHeight(dim.height);

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

        moduleControl = new ModuleControl(this);
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

    public ModuleControl getModuleControl() {
        return moduleControl;
    }

    public HexGridModule getHexGridModule() {
        return hexGridModule;
    }
    
    @Override
    public final void simpleInitApp() {
        super.inputManager.clearMappings();
        setPauseOnLostFocus(false);
        flyCam.setEnabled(false);
        
        hexGridModule = new HexGridModule(this, rootFrame.getJMenuBar());
        moduleControl.addRootTab(hexGridModule);
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
