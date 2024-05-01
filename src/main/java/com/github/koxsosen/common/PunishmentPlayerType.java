package com.github.koxsosen.common;

import java.net.InetAddress;
import java.util.UUID;

public class PunishmentPlayerType {

    public PunishmentPlayerType(UUID uuid, InetAddress inetAddress) {
        this.uuid = uuid;
        this.inetAddress = inetAddress;
    }

    private UUID uuid;

    public InetAddress getInetAddress() {
        return inetAddress;
    }

    public void setInetAddress(InetAddress inetAddress) {
        this.inetAddress = inetAddress;
    }

    public UUID getUuid() {
        return uuid;
    }

    public void setUuid(UUID uuid) {
        this.uuid = uuid;
    }

    private InetAddress inetAddress;


    @Override
    public String toString() {
        return "PunishmentPlayerType{" +
                "uuid=" + uuid +
                ", inetAddress=" + inetAddress +
                '}';
    }
}
