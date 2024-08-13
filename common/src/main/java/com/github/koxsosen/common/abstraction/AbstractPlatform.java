package com.github.koxsosen.common.abstraction;

import com.github.koxsosen.common.PunishmentPlayerType;
import space.arim.omnibus.Omnibus;

import java.net.InetAddress;
import java.util.UUID;

public interface AbstractPlatform<T, E, V> {

    // TODO: Add param descriptions.
    /**
     * Gets the respective platform's server implementation's object.
     *
     * @return The respective platform's server object.
     */
    T getAbstractServer();

    /**
     * Gets the player on their respective platforms by their uuid, and returns the player object.
     *
     * @param uuid
     * @return The player object.
     */
    E getAbstractPlayerByUUID(UUID uuid);

    /**
     * Gets the player on their respective platforms by their name, and returns the player object.
     * @param name
     * @return The player object.
     */
    E getAbstractPlayerByName(String name);

    /**
     * Gets the player's UUID on their respective platform.
     * @param player
     * @return The supplied player object's UUID.
     */
    UUID getAbstractPlayerUUID(E player);

    /**
     * Gets the player's InetAddress on their respective platform.
     * @param player
     * @return The supplied player object's InetAddress.
     */
    InetAddress getAbstractPlayerInetAddress(E player);

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
     default V getAbstractConnection(UUID player) {
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

    /**
     * The Omnibus instance for the respective platform.
     * @return The omnibus instance for the platform.
     */
    Omnibus getAbstractOmnibus();

    /**
     * This enum represents the
     * @return The omnibus instance for the platform.
     * @see ServerType
     */
    ServerType getAbstractServerType();

    /**
     * This method is only available when dealing with {@link ServerType#BACKEND} servers.
     * So on every other platform this should basically not to anything.
     *
     * @see ServerType
     */
    default void addToAbstractServerCache(PunishmentPlayerType type, Boolean value) {
        sendToAbstractLogger("Cache access without proper cache implementation.");
    }

}
