package com.github.koxsosen.common;

import java.io.Serializable;
import java.net.InetAddress;
import java.util.Objects;
import java.util.UUID;

public final class PunishmentPlayerType implements Serializable {

    private UUID uuid;

    private InetAddress inetAddress;

    public UUID getUuid() {
        return uuid;
    }

    public void setUuid(UUID uuid) {
        this.uuid = uuid;
    }

    public InetAddress getInetAddress() {
        return inetAddress;
    }

    public void setInetAddress(InetAddress inetAddress) {
        this.inetAddress = inetAddress;
    }

    public PunishmentPlayerType(UUID uuid, InetAddress inetAddress) {
        this.uuid = uuid;
        this.inetAddress = inetAddress;
    }

    @Override
    public String toString() {
        return "PunishmentPlayerType{" +
                "uuid=" + uuid +
                ", inetAddress=" + inetAddress +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PunishmentPlayerType that = (PunishmentPlayerType) o;
        return Objects.equals(getUuid(), that.getUuid()) && Objects.equals(getInetAddress(), that.getInetAddress());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getUuid(), getInetAddress());
    }

}
