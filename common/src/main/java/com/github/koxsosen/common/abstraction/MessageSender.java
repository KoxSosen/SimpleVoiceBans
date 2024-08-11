package com.github.koxsosen.common.abstraction;

import com.github.koxsosen.common.PunishmentPlayerType;

import java.io.*;
import java.util.UUID;

public class MessageSender {

    public <T, E, V, V1> void sendPluginMessage(UUID player, AbstractPlatform<T, E, V, V1> platform, PunishmentPlayerType punishmentPlayerType) {
        if (platform.getConnectedPlayers(player) > 1) {
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

    public <T, E, V, V1> PunishmentPlayerType handlePluginMessage(V1 source, String identifier, AbstractPlatform<T, E, V, V1> platform, byte[] data) {
        if (!platform.verifyAbstractSource(source) || identifier.equalsIgnoreCase(Constants.getChannelIdentifier())) {
            return null;
        }

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
        return punishmentPlayerType;
    }


}
