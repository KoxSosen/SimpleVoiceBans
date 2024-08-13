package com.github.koxsosen.common.abstraction;

import com.github.koxsosen.common.PunishmentPlayerType;

import java.io.*;
import java.util.UUID;

public class MessageSender {

    public <T, E, V> void sendPluginMessage(UUID player, AbstractPlatform<T, E, V> platform, PunishmentPlayerType punishmentPlayerType) {
        if (platform.getConnectedPlayers(player) > 0) {
            if (platform.getAbstractPlayerByUUID(player) != null) {
                if (platform.getAbstractConnection(player) != null) {
                    try (ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream()) {
                        try (ObjectOutputStream stream = new ObjectOutputStream(byteArrayOutputStream)) {
                            stream.writeObject(punishmentPlayerType);
                            stream.flush();
                        } catch (IOException e) {
                            platform.sendToAbstractLogger(Constants.getErrSerialize() + e);
                        }
                        platform.getAbstractPluginMessaging(player, Constants.getChannelIdentifier(), byteArrayOutputStream.toByteArray());
                    } catch (IOException e) {
                        platform.sendToAbstractLogger(Constants.getErrStream() + e);
                    }
                }
            }
        }
    }

    public <T, E, V> PunishmentPlayerType handlePluginMessage(AbstractPlatform<T, E, V> platform, byte[] data) {
        PunishmentPlayerType punishmentPlayerType = null;

        try (ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(data)) {
            try (ObjectInputStream stream = new ObjectInputStream(byteArrayInputStream)) {
                punishmentPlayerType = (PunishmentPlayerType) stream.readObject();
            } catch (IOException | ClassNotFoundException e) {
                platform.sendToAbstractLogger(Constants.getErrDeserialize() + e);
            }
        } catch (IOException e) {
            platform.sendToAbstractLogger(Constants.getErrStream() + e);
        }
        System.out.println(punishmentPlayerType.getUuid() + " " + punishmentPlayerType.getInetAddress());
        return punishmentPlayerType;
    }


}
