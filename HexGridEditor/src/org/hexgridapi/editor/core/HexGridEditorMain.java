package org.hexgridapi.editor.core;

import com.jme3.renderer.RenderManager;
import com.jme3.system.AppSettings;
import com.jme3.system.JmeCanvasContext;
import org.hexgridapi.editor.hexmap.HexGridModule;
import java.awt.Dimension;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.logging.Level;
import javax.swing.JFrame;
import javax.swing.JMenuBar;
import org.hexgridapi.core.AbstractHexGridApplication;

/**
 *
 * @author normenhansen, roah
 */
public class HexGridEditorMain extends AbstractHexGridApplication {

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
                HexGridEditorMain editorMain = new HexGridEditorMain("Hex Grid Editor");
            }
        });
    }
    protected final ModuleControlTab moduleControl;
    private static JFrame rootFrame;
    private HexGridModule hexMapModule;

    public HexGridEditorMain(String windowName) {
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

    public HexGridModule getHexMapModule() {
        return hexMapModule;
    }

    @Override
    public final void initApp() {
        hexMapModule = new HexGridModule(this, rootFrame.getJMenuBar());
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
