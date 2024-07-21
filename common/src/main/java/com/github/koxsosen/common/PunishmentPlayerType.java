package com.github.koxsosen.common;

import java.io.Serializable;
import java.net.InetAddress;
import java.util.Objects;
import java.util.UUID;

public final class PunishmentPlayerType implements Serializable {

    private final UUID uuid;

    private final InetAddress inetAddress;

    /**
     * This represents the muted response state of the player.
     * There are 4 states:
     * 0 - Not muted.
     * 1 - Muted.
     * 2 - Invalid - This is used to invalidate players from the cache manually.
     * It is sent when a new mute event is fired for the player, and is used to invalidate the player from the cache.
     * 3 - Unknown - This state is originally set when constructing a player without knowing it's state. This is usually sent
     * when the plugin message for the proxy is being constructed.
     * @return the muted response state of the player.
     */
    public int getState() {
        return state;
    }

    private final int state;

    public UUID getUuid() {
        return uuid;
    }

    public InetAddress getInetAddress() {
        return inetAddress;
    }

    public PunishmentPlayerType(UUID uuid, InetAddress inetAddress, int state) {
        this.uuid = uuid;
        this.inetAddress = inetAddress;
        this.state = state;
    }

    public PunishmentPlayerType(UUID uuid, InetAddress inetAddress) {
        this.uuid = uuid;
        this.inetAddress = inetAddress;
        this.state = 3;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PunishmentPlayerType that = (PunishmentPlayerType) o;
        return getState() == that.getState() && Objects.equals(getUuid(), that.getUuid()) && Objects.equals(getInetAddress(), that.getInetAddress());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getUuid(), getInetAddress(), getState());
    }

    @Override
    public String toString() {
        return "PunishmentPlayerType{" +
                "uuid=" + uuid +
                ", inetAddress=" + inetAddress +
                ", state=" + state +
                '}';
    }
}
