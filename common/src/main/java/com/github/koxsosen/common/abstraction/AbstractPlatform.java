package com.github.koxsosen.common.abstraction;

import java.util.UUID;

public interface AbstractPlatform {

    Object getAbstractServer();

    Object getAbstractPlayerByUUID(UUID uuid);

    Object getAbstractPlayerByName(String name);

    void getAbstractPluginMessaging(UUID player, String identifier, byte[] data);

    default Object getAbstractConnection(UUID player) {
        return null;
    }

    default Object getAbstractCurrentServer(UUID player) {
        return null;
    }

    void sendToAbstractLogger(String data);

}
