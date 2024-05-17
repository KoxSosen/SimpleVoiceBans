package com.github.koxsosen.velocity;

import com.github.koxsosen.common.PunishmentPlayerType;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.connection.PluginMessageEvent;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ServerConnection;
import com.velocitypowered.api.proxy.server.RegisteredServer;

import java.io.*;
import java.util.Optional;

import static com.github.koxsosen.velocity.VelocityPluginLoader.IDENTIFIER;

public class MessageReceiver {

    @Subscribe
    @SuppressWarnings("unused")
    public void onPluginMessageFromBackend(PluginMessageEvent event) {

        if (!(event.getSource() instanceof Player player)) {
            return;
        }

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
        sendPluginMessageToBackend(player, new PunishmentPlayerType(punishmentPlayerType.getUuid(), punishmentPlayerType.getInetAddress(), isMuted));

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


            player.getCurrentServer()
                    .map(ServerConnection::getServer)
                    .ifPresent((RegisteredServer server) -> {
                        server. sendPluginMessage(IDENTIFIER, byao.toByteArray());
                    });

            try {
                byao.close();
            } catch (IOException e) {
                VelocityPluginLoader.getLogger().info("Unable to close stream: " + e);
            }
        }
    }

}
