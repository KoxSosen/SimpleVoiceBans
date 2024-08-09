package com.github.koxsosen.velocity;

import com.github.koxsosen.common.PunishmentPlayerType;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.connection.PluginMessageEvent;
import com.velocitypowered.api.proxy.ServerConnection;
import space.arim.omnibus.util.concurrent.ReactionStage;

import java.io.*;

import static com.github.koxsosen.velocity.VelocityPluginLoader.*;

public class MessageReceiver {

    @SuppressWarnings("unused")
    @Subscribe
    public void onPluginMessageFromBackend(PluginMessageEvent event) {
        if (!(event.getSource() instanceof ServerConnection)) {
            return;
        }
        ServerConnection backend = (ServerConnection) event.getSource();
        if (event.getIdentifier() != IDENTIFIER) {
            return;
        }

        PunishmentPlayerType punishmentPlayerType;

        ByteArrayInputStream inputStream = new ByteArrayInputStream(event.getData());
        ObjectInputStream objectInputStream;
        try {
            objectInputStream = new ObjectInputStream(inputStream);
            punishmentPlayerType = (PunishmentPlayerType) objectInputStream.readObject();
            objectInputStream.close();
        } catch (IOException | ClassNotFoundException e) {
            VelocityPluginLoader.getLogger().info("Unable to deserialize: " + e);
            return;
        }

        try {
            inputStream.close();
        } catch (IOException e) {
            VelocityPluginLoader.getLogger().info("Unable to close stream: " + e);
            return;
        }

        ReactionStage<Integer> isMuted = VelocityPluginLoader.getLibertyBansApiHelper().checkMuted(VelocityPluginLoader.getApi(), punishmentPlayerType);
        isMuted.thenAcceptAsync(mutedState -> getMessageSender().sendPluginMessage(backend.getPlayer().getUniqueId(), getPlatform(), new PunishmentPlayerType(punishmentPlayerType.getUuid(), punishmentPlayerType.getInetAddress(), mutedState)))
                .exceptionally(ex -> {
                    VelocityPluginLoader.getLogger().info("Unable to sent plugin message: " + ex);
                    return null;
                });

    }

}
