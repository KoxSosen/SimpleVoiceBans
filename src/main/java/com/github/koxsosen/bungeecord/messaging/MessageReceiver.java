package com.github.koxsosen.bungeecord.messaging;

import com.github.koxsosen.bungeecord.BungeePluginLoader;
import com.github.koxsosen.common.DataConverter;
import com.github.koxsosen.common.PunishmentPlayerType;
import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.PluginMessageEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

import java.util.Collection;


public class MessageReceiver implements Listener {

    @EventHandler
    public void on(PluginMessageEvent event) {
        if (!event.getTag().equalsIgnoreCase("simplevoicechat:custom")) {
            return;
        }

        ByteArrayDataInput in = ByteStreams.newDataInput(event.getData());
        DataConverter dataConverter = new DataConverter();
        PunishmentPlayerType punishmentPlayerType = dataConverter.convertToPunishmentPlayerType(in.readUTF(), in.readUTF());

        if (event.getReceiver() instanceof ProxiedPlayer receiver) {
            Boolean isMuted = BungeePluginLoader.getLibertyBansApiHelper().isMuted(BungeePluginLoader.getApi(), punishmentPlayerType);
            sendCustomDataWithResponse(receiver, punishmentPlayerType, isMuted);
        }


    }


    public void sendCustomDataWithResponse(ProxiedPlayer player, PunishmentPlayerType punishmentPlayerType, Boolean isMuted) {
        Collection<ProxiedPlayer> networkPlayers = ProxyServer.getInstance().getPlayers();
        // perform a check to see if globally are no players
        if (networkPlayers == null || networkPlayers.isEmpty()) {
            return;
        }

        ByteArrayDataOutput out = ByteStreams.newDataOutput();
        out.writeUTF(punishmentPlayerType.getUuid().toString());
        out.writeUTF(punishmentPlayerType.getInetAddress().toString());
        out.writeBoolean(isMuted);

        player.getServer().getInfo().sendData("simplevoicechat:channel", out.toByteArray());
    }

}
