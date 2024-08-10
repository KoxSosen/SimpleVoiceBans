package com.github.koxsosen.bukkit;

import com.github.koxsosen.common.PunishmentPlayerType;
import com.github.koxsosen.common.abstraction.Constants;
import org.bukkit.entity.Player;
import org.bukkit.plugin.messaging.PluginMessageListener;
import org.checkerframework.checker.nullness.qual.NonNull;

import static com.github.koxsosen.bukkit.BukkitPluginLoader.*;


public class MessageReceiver implements PluginMessageListener {
    @Override
    public void onPluginMessageReceived(@NonNull String channel, @NonNull Player player, byte @NonNull [] bytes) {

        PunishmentPlayerType punishmentPlayerType = getMessageSender().handlePluginMessage(player, channel, getPlatform(), bytes);

        if (punishmentPlayerType != null) {
            SimpleVoiceBans.checkResponse(new PunishmentPlayerType(punishmentPlayerType.getUuid(), punishmentPlayerType.getInetAddress()), punishmentPlayerType.getState());
            SimpleVoiceBans.getUuidSet().remove(punishmentPlayerType.getUuid());
        } else {
            getPluginLogger().info(Constants.getErrSpoofingAttempt() + player);
        }

    }

}

