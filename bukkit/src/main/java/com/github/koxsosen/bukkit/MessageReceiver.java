package com.github.koxsosen.bukkit;

import com.github.koxsosen.common.PunishmentPlayerType;
import com.github.koxsosen.common.abstraction.Constants;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.messaging.PluginMessageListener;
import org.checkerframework.checker.nullness.qual.NonNull;

import static com.github.koxsosen.bukkit.BukkitPluginLoader.*;


public class MessageReceiver implements PluginMessageListener {
    @Override
    public void onPluginMessageReceived(@NonNull String channel, @NonNull Player player, byte @NonNull [] bytes) {
        if (!channel.equalsIgnoreCase(Constants.getChannelIdentifier())) {
            return;
        }

        if (!Bukkit.getOnlinePlayers().contains(player)) {
            return;
        }

        PunishmentPlayerType punishmentPlayerType = getMessageSender().handlePluginMessage(getPlatform(), bytes);

        if (punishmentPlayerType != null) {
            SimpleVoiceBans.checkResponse(new PunishmentPlayerType(punishmentPlayerType.getUuid(), punishmentPlayerType.getInetAddress()), punishmentPlayerType.getState());
            SimpleVoiceBans.getUuidSet().remove(punishmentPlayerType.getUuid());
        }

    }

}

