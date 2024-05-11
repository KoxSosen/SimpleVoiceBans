package com.github.koxsosen.common;

import java.io.Serializable;
import java.net.InetAddress;
import java.util.Objects;
import java.util.UUID;

public class PunishmentPlayerTypeWithResponse implements Serializable {

    private UUID uuid;

    private InetAddress inetAddress;

    private boolean response;

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

    public boolean getResponse() { return response; }

    public void setResponse(boolean response) { this.response = response; }

    public PunishmentPlayerTypeWithResponse(UUID uuid, InetAddress inetAddress, boolean response) {
        this.uuid = uuid;
        this.inetAddress = inetAddress;
        this.response = response;
    }

    @Override
    public String toString() {
        return "PunishmentPlayerType{" +
                "uuid=" + uuid +
                ", inetAddress=" + inetAddress +
                ", response=" + response +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PunishmentPlayerTypeWithResponse that = (PunishmentPlayerTypeWithResponse) o;
        return Objects.equals(getUuid(), that.getUuid()) && Objects.equals(getInetAddress(), that.getInetAddress()) && Objects.equals(getResponse(), that.getResponse());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getUuid(), getInetAddress(), getResponse());
    }
}
