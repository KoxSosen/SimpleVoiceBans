package com.github.koxsosen.bukkit.messaging;

import com.github.koxsosen.bukkit.SimpleVoiceBans;
import com.github.koxsosen.common.DataConverter;
import com.github.koxsosen.common.PunishmentPlayerType;
import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteStreams;
import org.bukkit.entity.Player;
import org.bukkit.plugin.messaging.PluginMessageListener;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.net.InetAddress;
import java.util.Map;
import java.util.UUID;

public class MessageReceiver implements PluginMessageListener {
    @Override
    public void onPluginMessageReceived(String channel, @NonNull Player player, byte[] bytes) {
        if (!channel.equalsIgnoreCase("simplevoicebans:custom")) {
            return;
        }

        System.out.println(player.getClass());

        ByteArrayDataInput in = ByteStreams.newDataInput(bytes);
        DataConverter dataConverter = new DataConverter();
        PunishmentPlayerType punishmentPlayerType = dataConverter.convertToPunishmentPlayerType(in.readUTF(), in.readUTF());

        if (punishmentPlayerType != null) {
            SimpleVoiceBans.getMuteCache().put(Map.of(punishmentPlayerType.getUuid(), punishmentPlayerType.getInetAddress()), in.readBoolean());
            SimpleVoiceBans.getMuteCache().forEach((uuidInetAddressMap, aBoolean) -> {
                for (Map.Entry<UUID, InetAddress> entry : uuidInetAddressMap.entrySet()) {
                    System.out.println(entry.getKey() + " " + entry.getValue() + " " + aBoolean);
                }
            });
        }

    }
}
