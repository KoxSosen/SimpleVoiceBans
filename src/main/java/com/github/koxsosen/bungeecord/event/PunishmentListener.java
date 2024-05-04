package com.github.koxsosen.bungeecord.event;

import com.github.koxsosen.bungeecord.BungeePluginLoader;
import com.github.koxsosen.common.PunishmentPlayerType;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import space.arim.libertybans.api.PunishmentType;
import space.arim.libertybans.api.event.PostPardonEvent;
import space.arim.libertybans.api.event.PostPunishEvent;
import space.arim.omnibus.events.EventConsumer;
import space.arim.omnibus.events.ListenerPriorities;

import java.util.Collection;
import java.util.concurrent.atomic.AtomicBoolean;

public class PunishmentListener {

    public void listenToPostPunishEvent() {
        // After a punishment re-check if someone is muted.
        EventConsumer<PostPunishEvent> listener = event -> {
            if (event.getPunishment().getType().equals(PunishmentType.MUTE)) {
                if (event.getTarget().isPresent()) {
                    ProxyServer proxyServer = ProxyServer.getInstance();
                    PunishmentPlayerType type = new PunishmentPlayerType(proxyServer.getPlayer(event.getTarget().get()).getUniqueId(), proxyServer.getPlayer(event.getTarget().get()).getAddress().getAddress());
                    boolean isMuted = BungeePluginLoader.getLibertyBansApiHelper().isMuted(BungeePluginLoader.getApi(), type);
                    sendCustomDataWithResponse(proxyServer.getPlayer(event.getTarget().get()), type, isMuted);
                }
            }
        };
        BungeePluginLoader.getOmnibus().getEventBus().registerListener(PostPunishEvent.class, ListenerPriorities.NORMAL, listener);
    }

    public void listenToPostPardonEvent() {
        EventConsumer<PostPardonEvent> listener = event -> {
            if (event.getPunishment().getType().equals(PunishmentType.MUTE)) {
                if (event.getTarget().isPresent()) {
                    ProxyServer proxyServer = ProxyServer.getInstance();
                    PunishmentPlayerType type = new PunishmentPlayerType(proxyServer.getPlayer(event.getTarget().get()).getUniqueId(), proxyServer.getPlayer(event.getTarget().get()).getAddress().getAddress());
                    boolean isMuted = false;
                    sendCustomDataWithResponse(proxyServer.getPlayer(event.getTarget().get()), type, isMuted);
                }
            }
        };
        BungeePluginLoader.getOmnibus().getEventBus().registerListener(PostPardonEvent.class, ListenerPriorities.NORMAL, listener);
    }

    public void sendCustomDataWithResponse(ProxiedPlayer player, PunishmentPlayerType punishmentPlayerType, boolean isMuted) {
        Collection<ProxiedPlayer> networkPlayers = ProxyServer.getInstance().getPlayers();
        // perform a check to see if globally are no players
        if (networkPlayers == null || networkPlayers.isEmpty()) {
            return;
        }

        ByteArrayDataOutput out = ByteStreams.newDataOutput();
        out.writeUTF(punishmentPlayerType.getUuid().toString());
        out.writeUTF(punishmentPlayerType.getInetAddress().getHostAddress());
        out.writeBoolean(isMuted);

        player.getServer().getInfo().sendData("simplevoicebans:custom", out.toByteArray());
    }

}
