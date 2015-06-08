package org.hexgridapi.events;

/**
 *
 * @author Eike Foede
 */
public interface TileChangeListener {

    /**
     * Called on one or multiple tile change.
     * @param events 
     */
    void onTileChange(TileChangeEvent[] events);

    /**
     * Called when all data get clean.
     */
    void onGridReload();
}
