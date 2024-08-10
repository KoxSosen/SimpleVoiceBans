package com.github.koxsosen.common;

import com.github.koxsosen.common.abstraction.AbstractPlatform;
import space.arim.libertybans.api.LibertyBans;
import space.arim.libertybans.api.NetworkAddress;
import space.arim.libertybans.api.PunishmentType;
import space.arim.libertybans.api.event.PostPardonEvent;
import space.arim.libertybans.api.event.PostPunishEvent;
import space.arim.libertybans.api.punish.Punishment;
import space.arim.omnibus.events.EventConsumer;
import space.arim.omnibus.events.ListenerPriorities;
import space.arim.omnibus.util.concurrent.ReactionStage;

import java.util.Optional;

public class LibertyBansApiHelper {

    public ReactionStage<Integer> checkMuted(LibertyBans api, PunishmentPlayerType punishmentPlayerType) {
        ReactionStage<Optional<Punishment>> mutes = api.getSelector().getCachedMute(punishmentPlayerType.getUuid(), NetworkAddress.of(punishmentPlayerType.getInetAddress()));
        return mutes.thenApplyAsync((Optional::isPresent)).thenApply((value) -> value ? 1 : 0).exceptionally((ex) -> {
            System.out.println("Finding out the mutes has completed exceptionally: " + ex.getMessage());
            return 3;
        });
    }

    public void listenToPostPardonEvent(AbstractPlatform platform) {
        EventConsumer<PostPardonEvent> listener = event -> {
            if (event.getPunishment().getType().equals(PunishmentType.MUTE)) {
                if (event.getTarget().isPresent()) {
                    String playerName = event.getTarget().get();
                    if (platform.getAbstractServer() != null) {
                        if (platform.getAbstractPlayerByName(playerName) != null) {
                            PunishmentPlayerType type = new PunishmentPlayerType(platform.getAbstractPlayerUUID(playerName), platform.getAbstractPlayerInetAddress(playerName));
                            switch (platform.getAbstractServerType()) {
                                case BACKEND -> platform.addToAbstractServerCache(type, false);
                                case PROXY -> platform.getAbstractPluginMessaging();
                            }
                            SimpleVoiceBans.getMuteCache().put(type, false);
                        }
                    }

                }
            }
        };
        platform.getAbstractOmnibus().getEventBus().registerListener(PostPardonEvent.class, ListenerPriorities.NORMAL, listener);
    }

    public void listenToPostPunishEvent(AbstractPlatform platform) {
        EventConsumer<PostPunishEvent> listener = event -> {
            if (event.getPunishment().getType().equals(PunishmentType.MUTE)) {
                if (event.getTarget().isPresent()) {
                    Server server = Bukkit.getServer();
                    if (event.getTarget().isPresent()) {
                        if (server.getPlayer(event.getTarget().get()) != null) {
                            PunishmentPlayerType type = new PunishmentPlayerType(server.getPlayer(event.getTarget().get()).getUniqueId(), server.getPlayer(event.getTarget().get()).getAddress().getAddress());
                            SimpleVoiceBans.getMuteCache().put(type, true);
                        }
                    }
                }
            }
        };
        platform.getAbstractOmnibus().getEventBus().registerListener(PostPunishEvent.class, ListenerPriorities.NORMAL, listener);
    }

}
