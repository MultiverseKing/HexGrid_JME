package org.hexgridapi.editor.hexmap.gui;

import org.hexgridapi.editor.utility.gui.ExtendedJDialog;
import java.awt.Frame;
import org.hexgridapi.core.geometry.builder.GridParam;
import org.hexgridapi.core.geometry.builder.coordinate.SquareCoordinate;

/**
 *
 * @author roah
 */
public class JNewMapDialog extends ExtendedJDialog {

    private GridParam param;
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
        param = new GridParam("org/hexgridapi/assets/Textures/HexField/",
                new String[]{"EARTH", "ICE", "NATURE", "VOLT"},
                SquareCoordinate.class, innerPan.getUseBuffer(), innerPan.getBuildVoid(),
                innerPan.getUseOnlyGround(), innerPan.getUseProcedural());
//        param = new GridParam("org/hexgridapi/assets/Textures/HexField/",
//                new String[]{"EARTH", "ICE", "NATURE", "VOLT"},
//                SquareCoordinate.class, true, true, false, true);
//                SquareCoordinate.class, true, false, false, true)); //useWater
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
    public GridParam getValidatedParam() {
        return param;
    }
}
