package com.github.koxsosen.bungee;

import com.github.koxsosen.common.PunishmentPlayerType;
import com.github.koxsosen.common.abstraction.Constants;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.PluginMessageEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import space.arim.omnibus.util.concurrent.ReactionStage;

import static com.github.koxsosen.bungee.BungeePluginLoader.*;


public class MessageReceiver implements Listener {

    @EventHandler
    @SuppressWarnings("unused")
    public void onPluginMessageReceived(PluginMessageEvent event) {
        if (!event.getTag().equalsIgnoreCase(Constants.getChannelIdentifier())) {
            return;
        }

        if (!(event.getReceiver() instanceof ProxiedPlayer)) {
            return;
        }

        PunishmentPlayerType punishmentPlayerType = getMessageSender().handlePluginMessage(getPlatform(), event.getData());

        if (punishmentPlayerType != null) {
            ProxiedPlayer player = (ProxiedPlayer) event.getReceiver();

            ReactionStage<Integer> isMuted = getLibertyBansApiHelper().checkMuted(BungeePluginLoader.getApi(), punishmentPlayerType);
            isMuted.thenAcceptAsync(mutedState -> getMessageSender().sendPluginMessage(player.getUniqueId(), BungeePluginLoader.getPlatform(), new PunishmentPlayerType(punishmentPlayerType.getUuid(), punishmentPlayerType.getInetAddress(), mutedState)))
                    .exceptionally(ex -> {
                        getPluginLogger().info(Constants.getErrPluginMessage() + ex);
                        return null;
                    });

        }

    }

}
