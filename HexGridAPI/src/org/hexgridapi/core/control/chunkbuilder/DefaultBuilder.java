package org.hexgridapi.core.control.chunkbuilder;

import com.jme3.app.Application;
import com.jme3.asset.AssetManager;
import com.jme3.asset.TextureKey;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.renderer.queue.RenderQueue;
import com.jme3.scene.Geometry;
import com.jme3.scene.Mesh;
import com.jme3.scene.Node;
import com.jme3.texture.Image;
import com.jme3.texture.Texture;
import com.jme3.texture.TextureArray;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.hexgridapi.core.HexSetting;
import org.hexgridapi.core.data.MapData;
import org.hexgridapi.core.mesh.GreddyMesher;
import org.hexgridapi.utility.Vector2Int;

/**
 * Used to generate a chunk Geometry.
 *
 * @todo texture array
 * @author roah
 */
public class DefaultBuilder {

    protected final Material hexMaterial;
    protected final GreddyMesher greddyMesher;
    protected final MapData.GhostMode mode;
    protected final AssetManager assetManager;

    /**
     * Return the proper builder to use for generating chunk.
     *
     * @param app
     * @param mapData
     * @return
     */
    public static DefaultBuilder getBuilder(Application app, MapData mapData) {
        GreddyMesher mesher;
        Material mat;
        if (true) {//!app.getRenderer().getCaps().contains(Caps.TextureArray)) {
            Logger.getLogger(DefaultBuilder.class.getName()).log(Level.WARNING,
                    "The hardware does not support TextureArray");
            mesher = new GreddyMesher(mapData, false);
            mat = app.getAssetManager().loadMaterial("Materials/hexMat.j3m");
            mat.setName("hexMaterial");
        } else {
            mesher = new GreddyMesher(mapData, true);
            mat = new Material(app.getAssetManager(), "MatDefs/UnshadedArray.j3md");
            mat.setName("arrayTextureMaterial");
            List<Image> images = new ArrayList<Image>();
            for (int i = 0; i < mapData.getTextureKeys().size(); i++) {
                images.add(app.getAssetManager().loadTexture(HexSetting.TEXTURE_PATH + mapData.getTextureKeys().get(i) + ".png").getImage());
            }
            TextureArray arrayTexture = new TextureArray(images);
            mat.setTexture("ColorMap", arrayTexture);
            mat.getAdditionalRenderState().setDepthTest(true);
//            mat.getAdditionalRenderState().setColorWrite(true);
            mat.getAdditionalRenderState().setDepthWrite(true);
        }

        return new DefaultBuilder(mesher, mapData.getMode(), mat, app.getAssetManager());
    }

    private DefaultBuilder(GreddyMesher greddyMesher, MapData.GhostMode mode, Material hexMaterial, AssetManager assetManager) {
        this.greddyMesher = greddyMesher;
        this.mode = mode;
        this.hexMaterial = hexMaterial;
        this.assetManager = assetManager;
    }

    protected DefaultBuilder(DefaultBuilder builder) {
        this.greddyMesher = builder.greddyMesher;
        this.mode = builder.mode;
        this.hexMaterial = builder.hexMaterial;
        this.assetManager = builder.assetManager;
    }

    /**
     * Generate a chunk and attach it to the specifiate Node.
     *
     * @param parent node to attach the geometry.
     * @param onlyGround generate tile side face ?
     * @param chunkPosition on the map.
     */
    public void getTiles(Node parent, boolean onlyGround, Vector2Int chunkPosition) {
        HashMap<String, Mesh> mesh = greddyMesher.getMesh(onlyGround, chunkPosition);
        if (!hexMaterial.getName().equals("arrayTextureMaterial")) {
            for (String value : mesh.keySet()) {
                Material mat = hexMaterial.clone();
                Geometry tile = new Geometry(value != null ? value : "debug", mesh.get(value));
                Texture text;
                /**
                 * Debuging purpose
                 * if (value == null && (mode.equals(MapData.GhostMode.GHOST)
                 * || mode.equals(MapData.GhostMode.GHOST_PROCEDURAL))) {
                 * TextureKey k = new TextureKey(HexSetting.TEXTURE_PATH +
                 * "EMPTY_TEXTURE_KEY.png", false);
                 * k.setGenerateMips(true);
                 * text = assetManager.loadTexture(k);
                 * mat.setColor("Color", ColorRGBA.Red);
                 * } else
                 */
                if (value != null && value.equals("NO_TILE") && (mode.equals(MapData.GhostMode.GHOST)
                        || mode.equals(MapData.GhostMode.GHOST_PROCEDURAL))) {
                    TextureKey k = new TextureKey(HexSetting.TEXTURE_PATH + "EMPTY_TEXTURE_KEY.png", false);
                    k.setGenerateMips(true);
                    text = assetManager.loadTexture(k);
                    mat.setColor("Color", ColorRGBA.Blue);
                } else {
                    TextureKey k = new TextureKey(HexSetting.TEXTURE_PATH + value + ".png", false);
                    k.setGenerateMips(true);
                    text = assetManager.loadTexture(k);
                }
                text.setWrap(Texture.WrapMode.Repeat);

                mat.setTexture("ColorMap", text);
//            mat.setTexture("DiffuseMap", text);
//            mat.getAdditionalRenderState().setWireframe(true);
//            tile.getMesh().setMode(Mesh.Mode.Points);
                tile.setMaterial(mat);
                if (HexSetting.CHUNK_SHAPE_TYPE.equals(GreddyMesher.ShapeType.HEXAGON)) {
                    tile.setLocalTranslation(new Vector3f(
                            -(HexSetting.HEX_WIDTH * HexSetting.CHUNK_SIZE
                            - ((HexSetting.CHUNK_SIZE & 1) == 0 ? 0 : -HexSetting.HEX_WIDTH / 2)), 0,
                            -(HexSetting.HEX_RADIUS * 1.5f * HexSetting.CHUNK_SIZE)));
                }
                parent.attachChild(tile);
            }
        } else {
            Geometry tile = new Geometry("Geometry.ArrayTexture.TILES.0|0", mesh.get("mesh"));
            tile.setMaterial(hexMaterial);
            tile.setShadowMode(RenderQueue.ShadowMode.Inherit);
            parent.attachChild(tile);
        }
    }
}
