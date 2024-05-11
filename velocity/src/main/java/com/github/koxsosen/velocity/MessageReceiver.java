package com.github.koxsosen.velocity;

import com.github.koxsosen.common.PunishmentPlayerType;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.connection.PluginMessageEvent;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ServerConnection;

import java.io.*;
import java.util.Optional;

import static com.github.koxsosen.velocity.VelocityPluginLoader.IDENTIFIER;

public class MessageReceiver {

    @Subscribe
    public void onPluginMessageFromBackend(PluginMessageEvent event) {

        if (!(event.getSource() instanceof ServerConnection backend)) {
            return;
        }

        // Ensure the identifier is what you expect before trying to handle the data
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

        boolean isMuted = VelocityPluginLoader.getLibertyBansApiHelper().isMuted(VelocityPluginLoader.getApi(), punishmentPlayerType);
        sendPluginMessageToBackend(backend.getPlayer(), new PunishmentPlayerType(punishmentPlayerType.getUuid(), punishmentPlayerType.getInetAddress(), isMuted));

    }

    public static void sendPluginMessageToBackend(Player player, PunishmentPlayerType punishmentPlayerType) {
        Optional<ServerConnection> connection = player.getCurrentServer();

        if (connection.isPresent()) {
            ByteArrayOutputStream byao = new ByteArrayOutputStream();
            ObjectOutputStream outputStream;
            try {
                outputStream = new ObjectOutputStream(byao);
                outputStream.writeObject(punishmentPlayerType);
                outputStream.flush();
                outputStream.close();
            } catch (IOException e) {
                VelocityPluginLoader.getLogger().info("Unable to serialize: " + e);
                return;
            }

            player.sendPluginMessage(IDENTIFIER, byao.toByteArray());

            try {
                byao.close();
            } catch (IOException e) {
                VelocityPluginLoader.getLogger().info("Unable to close stream: " + e);
            }
        }
    }

}
