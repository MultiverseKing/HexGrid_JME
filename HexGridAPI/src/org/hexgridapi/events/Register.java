package org.hexgridapi.events;

/**
 * utility to create listeners easyer.
 *
 * @author roah
 */
public interface Register<T> {

    /**
     * Register a listener to respond to event(s).
     *
     * @param listener to register.
     */
    void register(T listener);

    /**
     * Remove listener from responding event(s).
     *
     * @param listener
     */
    void unregister(T listener);
}
