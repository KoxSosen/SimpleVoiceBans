package com.github.koxsosen.bungee;

import com.github.koxsosen.common.PunishmentPlayerType;
import net.md_5.bungee.api.ProxyServer;
import space.arim.libertybans.api.PunishmentType;
import space.arim.libertybans.api.event.PostPardonEvent;
import space.arim.libertybans.api.event.PostPunishEvent;
import space.arim.omnibus.events.EventConsumer;
import space.arim.omnibus.events.ListenerPriorities;

public class PunishmentListener {

    public void listenToPostPunishEvent() {
        EventConsumer<PostPunishEvent> listener = event -> {
            if (event.getPunishment().getType().equals(PunishmentType.MUTE)) {
                if (event.getTarget().isPresent()) {
                    ProxyServer proxyServer = ProxyServer.getInstance();
                    if (proxyServer.getPlayer(event.getTarget().get()).getUniqueId() != null) {
                        PunishmentPlayerType type = new PunishmentPlayerType(proxyServer.getPlayer(event.getTarget().get()).getUniqueId(), proxyServer.getPlayer(event.getTarget().get()).getAddress().getAddress());
                        MessageReceiver.sendCustomDataWithResponse(proxyServer.getPlayer(event.getTarget().get()), new PunishmentPlayerType(type.getUuid(), type.getInetAddress(), 1));
                    }
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
                    if (proxyServer.getPlayer(event.getTarget().get()).getUniqueId() != null) {
                        PunishmentPlayerType type = new PunishmentPlayerType(proxyServer.getPlayer(event.getTarget().get()).getUniqueId(), proxyServer.getPlayer(event.getTarget().get()).getAddress().getAddress());
                        MessageReceiver.sendCustomDataWithResponse(proxyServer.getPlayer(event.getTarget().get()), new PunishmentPlayerType(type.getUuid(), type.getInetAddress(), 0));
                    }
                }
            }
        };
        BungeePluginLoader.getOmnibus().getEventBus().registerListener(PostPardonEvent.class, ListenerPriorities.NORMAL, listener);
    }

}
