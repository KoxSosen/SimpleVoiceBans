package com.github.koxsosen.common.abstraction;

import java.util.UUID;

public interface AbstractPlatform {

    // TODO: Add param descriptions.
    /**
     * Gets the respective platform's server implementation's object.
     * @return The respective platform's server object.
     */
    Object getAbstractServer();

    /**
     * Gets the player on their respective platforms by their uuid, and returns the player object.
     * @param uuid
     * @return The player object.
     */
    Object getAbstractPlayerByUUID(UUID uuid);

    /**
     * Gets the player on their respective platforms by their name, and returns the player object.
     * @param name
     * @return The player object.
     */
    Object getAbstractPlayerByName(String name);

    /**
     * Implements plugin messaging on all respective platforms. Additionally, this method's
     * implementation ensures the player is connected to the server, the connection is live, and the server exists.
     * @param player
     * @param identifier
     * @param data
     */
    void getAbstractPluginMessaging(UUID player, String identifier, byte[] data);

    /**
     * Returns the server object of the server the player is currently connected to.
     * @param player
     * @return The server object the player is connected to.
     */
    default Object getAbstractConnection(UUID player) {
        return null;
    }

    /**
     * Sends the specified data to the platform's logger implementation using the info logging level.
     * @param data
     */
    // TODO: Figure out if a common logging level parameter can be implemented.
    void sendToAbstractLogger(String data);

    /**
     * Without players on backend servers, or proxies it's impossible to send plugin messages.
     * For this reason, we'll need at least one player online. For proxies, this is a global limit, for backends
     * this is a single-server limit. For this reason this method only checks the current server this player is connected to.
     * @param player
     * @return The amount of players connected to the same server as the player is connected to.
     */
    int getConnectedPlayers(UUID player);

}
