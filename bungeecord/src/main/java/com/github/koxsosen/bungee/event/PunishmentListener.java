package com.github.koxsosen.bungee.event;

import com.github.koxsosen.bungee.BungeePluginLoader;
import com.github.koxsosen.common.PunishmentPlayerType;
import com.github.koxsosen.common.PunishmentPlayerTypeWithResponse;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import space.arim.libertybans.api.PunishmentType;
import space.arim.libertybans.api.event.PostPardonEvent;
import space.arim.libertybans.api.event.PostPunishEvent;
import space.arim.omnibus.events.EventConsumer;
import space.arim.omnibus.events.ListenerPriorities;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.util.Collection;

public class PunishmentListener {

    public void listenToPostPunishEvent() {
        // After a punishment re-check if someone is muted.
        EventConsumer<PostPunishEvent> listener = event -> {
            if (event.getPunishment().getType().equals(PunishmentType.MUTE)) {
                if (event.getTarget().isPresent()) {
                    ProxyServer proxyServer = ProxyServer.getInstance();
                    PunishmentPlayerType type = new PunishmentPlayerType(proxyServer.getPlayer(event.getTarget().get()).getUniqueId(), proxyServer.getPlayer(event.getTarget().get()).getAddress().getAddress());
                    boolean isMuted = BungeePluginLoader.getLibertyBansApiHelper().isMuted(BungeePluginLoader.getApi(), type);
                    sendCustomDataWithResponse(proxyServer.getPlayer(event.getTarget().get()), new PunishmentPlayerTypeWithResponse(type.getUuid(), type.getInetAddress(), isMuted));
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
                    sendCustomDataWithResponse(proxyServer.getPlayer(event.getTarget().get()), new PunishmentPlayerTypeWithResponse(type.getUuid(), type.getInetAddress(), isMuted));
                }
            }
        };
        BungeePluginLoader.getOmnibus().getEventBus().registerListener(PostPardonEvent.class, ListenerPriorities.NORMAL, listener);
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
