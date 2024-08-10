package com.github.koxsosen.velocity;

import com.github.koxsosen.common.PunishmentPlayerType;
import com.github.koxsosen.common.abstraction.Constants;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.connection.PluginMessageEvent;
import com.velocitypowered.api.proxy.ServerConnection;
import space.arim.omnibus.util.concurrent.ReactionStage;

import static com.github.koxsosen.velocity.VelocityPluginLoader.*;

public class MessageReceiver {

    @SuppressWarnings("unused")
    @Subscribe
    public void onPluginMessageFromBackend(PluginMessageEvent event) {

        PunishmentPlayerType punishmentPlayerType = getMessageSender().handlePluginMessage(event.getSource(), event.getIdentifier().toString(), getPlatform(), event.getData());

        if (punishmentPlayerType != null) {
            ServerConnection playerConnection = (ServerConnection) event.getSource();

            ReactionStage<Integer> isMuted = VelocityPluginLoader.getLibertyBansApiHelper().checkMuted(VelocityPluginLoader.getApi(), punishmentPlayerType);
            isMuted.thenAcceptAsync(mutedState -> getMessageSender().sendPluginMessage(playerConnection.getPlayer().getUniqueId(), getPlatform(), new PunishmentPlayerType(punishmentPlayerType.getUuid(), punishmentPlayerType.getInetAddress(), mutedState)))
                    .exceptionally(ex -> {
                        getLogger().info(Constants.getErrPluginMessage() + ex);
                        return null;
                    });
        } else {
            getLogger().info(Constants.getErrSpoofingAttempt() + event.getSource().toString());
        }

    }

}
