package com.github.koxsosen.bungee;

import com.github.koxsosen.common.PunishmentPlayerType;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.PluginMessageEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import space.arim.omnibus.util.concurrent.ReactionStage;

import java.io.*;
import java.util.Collection;

public class MessageReceiver implements Listener {

    @EventHandler
    @SuppressWarnings("unused")
    public void onPluginMessageReceived(PluginMessageEvent event) {
        if (!event.getTag().equalsIgnoreCase("simplevbans:custom")) {
            return;
        }

        if (!(event.getReceiver() instanceof ProxiedPlayer player)) {
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
            ProxyServer.getInstance().getLogger().info("Unable to deserialize: " + e);
            return;
        }

        try {
            inputStream.close();
        } catch (IOException e) {
            ProxyServer.getInstance().getLogger().info("Unable to close stream: " + e);
            return;
        }

        ReactionStage<Integer> isMuted = BungeePluginLoader.getLibertyBansApiHelper().checkMuted(BungeePluginLoader.getApi(), punishmentPlayerType);
        isMuted.thenAcceptAsync(mutedState -> sendCustomDataWithResponse(player, new PunishmentPlayerType(punishmentPlayerType.getUuid(), punishmentPlayerType.getInetAddress(), mutedState)))
                .exceptionally(ex -> {
                    ProxyServer.getInstance().getLogger().info("Unable to sent plugin message: " + ex);
                    return null;
                });

    }


    // TODO: Why is the custom response implemented in the object, instead of the method?
    // We should instead send the message independently in the stream, and don't bake it into the player response object
    public static void sendCustomDataWithResponse(ProxiedPlayer player, PunishmentPlayerType punishmentPlayerType) {
        Collection<ProxiedPlayer> networkPlayers = ProxyServer.getInstance().getPlayers();
        // perform a check to see if globally are no players
        if (networkPlayers == null || networkPlayers.isEmpty()) {
            return;
        }

        ByteArrayOutputStream byao = new ByteArrayOutputStream();
        ObjectOutputStream outputStream;
        try {
            outputStream = new ObjectOutputStream(byao);
            outputStream.writeObject(punishmentPlayerType);
            outputStream.flush();
            outputStream.close();
        } catch (IOException e) {
            ProxyServer.getInstance().getLogger().info("Unable to serialize: " + e);
            return;
        }

        player.getServer().getInfo().sendData("simplevbans:custom", byao.toByteArray());
        try {
            byao.close();
        } catch (IOException e) {
            ProxyServer.getInstance().getLogger().info("Unable to close stream: " + e);
        }
    }

}
