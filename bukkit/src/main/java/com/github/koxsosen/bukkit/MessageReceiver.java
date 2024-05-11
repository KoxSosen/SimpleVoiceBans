package com.github.koxsosen.bukkit;

import com.github.koxsosen.common.PunishmentPlayerType;
import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteStreams;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.messaging.PluginMessageListener;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.io.*;
import java.net.InetAddress;
import java.util.Map;
import java.util.UUID;

public class MessageReceiver implements PluginMessageListener {
    @Override
    public void onPluginMessageReceived(String channel, @NonNull Player player, byte[] bytes) {
        if (!channel.equalsIgnoreCase("simplevoicebans:custom")) {
            return;
        }

        PunishmentPlayerType punishmentPlayerTypeWithResponse = null;

        ByteArrayDataInput in = ByteStreams.newDataInput(bytes);
        try (ObjectInputStream objectInputStream = new ObjectInputStream((InputStream) in)) {
            punishmentPlayerTypeWithResponse = (PunishmentPlayerType) objectInputStream.readObject();
        } catch (IOException | ClassNotFoundException e) {
            Bukkit.getServer().getLogger().info("Failed to deserialize: " + e);
        }

        if (punishmentPlayerTypeWithResponse != null) {
            SimpleVoiceBans.getMuteCache().put(Map.of(punishmentPlayerTypeWithResponse.getUuid(), punishmentPlayerTypeWithResponse.getInetAddress()), punishmentPlayerTypeWithResponse.getResponse());
            SimpleVoiceBans.getMuteCache().forEach((uuidInetAddressMap, aBoolean) -> {
                for (Map.Entry<UUID, InetAddress> entry : uuidInetAddressMap.entrySet()) {
                    System.out.println(entry.getKey() + " " + entry.getValue() + " " + aBoolean);
                }
            });
        }

    }
}
