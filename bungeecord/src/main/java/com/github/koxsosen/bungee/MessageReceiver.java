package com.github.koxsosen.bungee;

import com.github.koxsosen.common.PunishmentPlayerType;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.PluginMessageEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

import java.io.*;
import java.util.Collection;

public class MessageReceiver implements Listener {

    @EventHandler
    @SuppressWarnings("unused")
    public void onPluginMessageReceived(PluginMessageEvent event) {
        if (!event.getTag().equalsIgnoreCase("simplevoicebans:custom")) {
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

        boolean isMuted = BungeePluginLoader.getLibertyBansApiHelper().isMuted(BungeePluginLoader.getApi(), punishmentPlayerType);
        sendCustomDataWithResponse(player, new PunishmentPlayerType(punishmentPlayerType.getUuid(), punishmentPlayerType.getInetAddress(), isMuted));

    }

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

        player.getServer().getInfo().sendData("simplevoicebans:custom", byao.toByteArray());
        try {
            byao.close();
        } catch (IOException e) {
            ProxyServer.getInstance().getLogger().info("Unable to close stream: " + e);
        }
    }

}
