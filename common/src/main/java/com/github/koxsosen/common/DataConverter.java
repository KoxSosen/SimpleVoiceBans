package com.github.koxsosen.common;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.UUID;

public class DataConverter {

    public PunishmentPlayerType convertToPunishmentPlayerType(String data1, String data2) {
        UUID uuid = UUID.fromString(data1);
        InetAddress inetAddress = null;
        try {
            inetAddress = InetAddress.getByName(data2);
        } catch (UnknownHostException ignored) {
            // We only supply raw IPs so there is no need for anything else.
        }
        return new PunishmentPlayerType(uuid, inetAddress);
    }



}
