package com.github.koxsosen.bungee.messaging;

import com.github.koxsosen.bungee.BungeePluginLoader;
import com.github.koxsosen.common.PunishmentPlayerType;
import com.github.koxsosen.common.PunishmentPlayerTypeWithResponse;
import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.PluginMessageEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

import java.io.*;
import java.util.Collection;

public class MessageReceiver implements Listener {

    @EventHandler
    public void on(PluginMessageEvent event) {
        if (!event.getTag().equalsIgnoreCase("simplevoicebans:custom")) {
            return;
        }

        PunishmentPlayerType punishmentPlayerType = null;

        ByteArrayDataInput in = ByteStreams.newDataInput(event.getData());
        try (ObjectInputStream objectInputStream = new ObjectInputStream((InputStream) in)) {
            punishmentPlayerType = (PunishmentPlayerType) objectInputStream.readObject();
        } catch (IOException | ClassNotFoundException e) {
            ProxyServer.getInstance().getLogger().info("Failed to deserialize: " + e);
        }

        if (event.getReceiver() instanceof ProxiedPlayer receiver) {
            boolean isMuted = BungeePluginLoader.getLibertyBansApiHelper().isMuted(BungeePluginLoader.getApi(), punishmentPlayerType);
            sendCustomDataWithResponse(receiver, new PunishmentPlayerTypeWithResponse(punishmentPlayerType.getUuid(), punishmentPlayerType.getInetAddress(), isMuted));
        }
    }

    public void sendCustomDataWithResponse(ProxiedPlayer player, PunishmentPlayerTypeWithResponse punishmentPlayerTypeWithResponse) {
        Collection<ProxiedPlayer> networkPlayers = ProxyServer.getInstance().getPlayers();
        // perform a check to see if globally are no players
        if (networkPlayers == null || networkPlayers.isEmpty()) {
            return;
        }

        ByteArrayDataOutput out = ByteStreams.newDataOutput();

        try (ObjectOutputStream objectOutputStream = new ObjectOutputStream((OutputStream) out)) {
            objectOutputStream.writeObject(punishmentPlayerTypeWithResponse);
        } catch (IOException e) {
            ProxyServer.getInstance().getLogger().info("Failed to seralize object:" + e);
        }

        player.getServer().getInfo().sendData("simplevoicebans:custom", out.toByteArray());
    }

}
