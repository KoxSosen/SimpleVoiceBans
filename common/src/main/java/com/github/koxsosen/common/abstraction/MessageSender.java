package com.github.koxsosen.common.abstraction;

import com.github.koxsosen.common.PunishmentPlayerType;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.UUID;

public class MessageSender {

    public void sendPluginMessage(UUID player, AbstractPlatform platform, PunishmentPlayerType punishmentPlayerType) {
        if (platform.getConnectedPlayers(player) > 1) {
            if (platform.getAbstractPlayerByUUID(player) != null) {
                if (platform.getAbstractConnection(player) != null) {
                    try (ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream()) {
                        try (ObjectOutputStream stream = new ObjectOutputStream(byteArrayOutputStream)) {
                            stream.writeObject(punishmentPlayerType);
                            stream.flush();
                        } catch (IOException e) {
                            platform.sendToAbstractLogger("Unable to serialize: " + e);
                        }
                    } catch (IOException e) {
                        platform.sendToAbstractLogger("Unable to close stream: " + e);
                    }
                }
            }
        }
    }
}
