package com.github.koxsosen.bukkit;

import com.github.koxsosen.common.PunishmentPlayerType;
import org.bukkit.Bukkit;
import org.bukkit.Server;
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
                    Server server = Bukkit.getServer();
                    if (event.getTarget().isPresent()) {
                        if (server.getPlayer(event.getTarget().get()) != null) {
                            PunishmentPlayerType type = new PunishmentPlayerType(server.getPlayer(event.getTarget().get()).getUniqueId(), server.getPlayer(event.getTarget().get()).getAddress().getAddress());
                            SimpleVoiceBans.getMuteCache().put(type, false);
                        }
                    }
                }
            }
        };
        BukkitPluginLoader.getOmnibus().getEventBus().registerListener(PostPunishEvent.class, ListenerPriorities.NORMAL, listener);
    }

    public void listenToPostPardonEvent() {
        EventConsumer<PostPardonEvent> listener = event -> {
            if (event.getPunishment().getType().equals(PunishmentType.MUTE)) {
                if (event.getTarget().isPresent()) {
                    Server server = Bukkit.getServer();
                    if (event.getTarget().isPresent()) {
                        if (server.getPlayer(event.getTarget().get()) != null) {
                            PunishmentPlayerType type = new PunishmentPlayerType(server.getPlayer(event.getTarget().get()).getUniqueId(), server.getPlayer(event.getTarget().get()).getAddress().getAddress());
                            SimpleVoiceBans.getMuteCache().put(type, false);
                        }
                    }
                }
            }
        };
        BukkitPluginLoader.getOmnibus().getEventBus().registerListener(PostPardonEvent.class, ListenerPriorities.NORMAL, listener);
    }
}