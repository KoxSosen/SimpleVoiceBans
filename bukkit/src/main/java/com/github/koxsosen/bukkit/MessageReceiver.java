package com.github.koxsosen.bukkit;

import com.github.koxsosen.common.PunishmentPlayerType;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.messaging.PluginMessageListener;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.io.*;
import java.util.Map;

public class MessageReceiver implements PluginMessageListener {
    @Override
    public void onPluginMessageReceived(String channel, @NonNull Player player, byte[] bytes) {
        if (!channel.equalsIgnoreCase("simplevoicebans:custom")) {
            return;
        }

        PunishmentPlayerType punishmentPlayerTypeWithResponse = null;

        ByteArrayInputStream inputStream = new ByteArrayInputStream(bytes);
        ObjectInputStream objectInputStream = null;
        try {
            objectInputStream = new ObjectInputStream(inputStream);
            punishmentPlayerTypeWithResponse = (PunishmentPlayerType) objectInputStream.readObject();
            objectInputStream.close();
        } catch (IOException | ClassNotFoundException e) {
            Bukkit.getServer().getLogger().info("Unable to deserialize: " + e);
            return;
        }

        try {
            inputStream.close();
        } catch (IOException e) {
            Bukkit.getServer().getLogger().info("Unable to close stream: " + e);
            return;
        }

        if (punishmentPlayerTypeWithResponse != null) {
            SimpleVoiceBans.getMuteCache().put(Map.of(punishmentPlayerTypeWithResponse.getUuid(), punishmentPlayerTypeWithResponse.getInetAddress()), punishmentPlayerTypeWithResponse.getResponse());
        }
    }

}
