package com.github.koxsosen.velocity;

import com.github.koxsosen.common.PunishmentPlayerType;
import com.velocitypowered.api.proxy.ProxyServer;
import space.arim.libertybans.api.PunishmentType;
import space.arim.libertybans.api.event.PostPardonEvent;
import space.arim.libertybans.api.event.PostPunishEvent;
import space.arim.omnibus.events.EventConsumer;
import space.arim.omnibus.events.ListenerPriorities;

public class PunishmentListener {
    public void listenToPostPunishEvent() {
        // After a punishment re-check if someone is muted.
        EventConsumer<PostPunishEvent> listener = event -> {
            if (event.getPunishment().getType().equals(PunishmentType.MUTE)) {
                if (event.getTarget().isPresent()) {
                    ProxyServer proxyServer = VelocityPluginLoader.getServer();
                    if (proxyServer.getPlayer(event.getTarget().get()).isPresent()) {
                        PunishmentPlayerType type = new PunishmentPlayerType(proxyServer.getPlayer(event.getTarget().get()).get().getUniqueId(), proxyServer.getPlayer(event.getTarget().get()).get().getRemoteAddress().getAddress());
                        boolean isMuted = VelocityPluginLoader.getLibertyBansApiHelper().isMuted(VelocityPluginLoader.getApi(), type);
                        MessageReceiver.sendPluginMessageToBackend(proxyServer.getPlayer(event.getTarget().get()).get(), new PunishmentPlayerType(type.getUuid(), type.getInetAddress(), isMuted));
                    }
                }
            }
        };
        VelocityPluginLoader.getOmnibus().getEventBus().registerListener(PostPunishEvent.class, ListenerPriorities.NORMAL, listener);
    }

    public void listenToPostPardonEvent() {
        EventConsumer<PostPardonEvent> listener = event -> {
            if (event.getPunishment().getType().equals(PunishmentType.MUTE)) {
                if (event.getTarget().isPresent()) {
                    ProxyServer proxyServer = VelocityPluginLoader.getServer();
                    if (proxyServer.getPlayer(event.getTarget().get()).isPresent()) {
                        PunishmentPlayerType type = new PunishmentPlayerType(proxyServer.getPlayer(event.getTarget().get()).get().getUniqueId(), proxyServer.getPlayer(event.getTarget().get()).get().getRemoteAddress().getAddress());
                        boolean isMuted = false;
                        MessageReceiver.sendPluginMessageToBackend(proxyServer.getPlayer(event.getTarget().get()).get(), new PunishmentPlayerType(type.getUuid(), type.getInetAddress(), isMuted));
                    }
                }
            }
        };
        VelocityPluginLoader.getOmnibus().getEventBus().registerListener(PostPardonEvent.class, ListenerPriorities.NORMAL, listener);
    }
}
