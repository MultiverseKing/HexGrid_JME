package org.hexgridapi.editor.hexmap.gui;

import org.hexgridapi.editor.utility.gui.ExtendedJDialog;
import java.awt.Frame;
import org.hexgridapi.core.MapParam;
import org.hexgridapi.core.coordinate.SquareCoordinate;

/**
 *
 * @author roah
 */
public class JNewMapDialog extends ExtendedJDialog {

    private MapParam param;
    private NewMapPan innerPan;

    JNewMapDialog(Frame frame) {
        super(frame, "Generate new Map", true);
    }

    @Override
    protected Object[] getMessage() {
        innerPan = new NewMapPan();
        return new Object[]{innerPan};
    }

    @Override
    protected boolean userValidated() {
        param = new MapParam(SquareCoordinate.class, innerPan.getMapSize(),
                innerPan.getChunkSize(), innerPan.getBufferRadius(), innerPan.getBuildVoid(),
                innerPan.getUseOnlyGround(), innerPan.getUseProcedural(), null);
        return true;
    }

    @Override
    protected void userCancelled() {
    }

    @Override
    protected void dialogClose() {
    }

    /**
     * Returns null if the typed string was invalid;
     * otherwise, returns the string as the user entered it.
     */
    public MapParam getValidatedParam() {
        return param;
    }
}
