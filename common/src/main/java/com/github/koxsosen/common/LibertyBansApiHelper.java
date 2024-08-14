package com.github.koxsosen.common;

import com.github.koxsosen.common.abstraction.AbstractPlatform;
import com.github.koxsosen.common.abstraction.Constants;
import com.github.koxsosen.common.abstraction.MessageSender;
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
            System.out.println(Constants.getErrMute() + ex.getMessage());
            return 3;
        });
    }

    public <T, E, V> void listenToPunishmentEvents(AbstractPlatform<T, E, V> platform, MessageSender messageSender) {
        EventConsumer<PostPardonEvent> postPardonEventEventConsumer = event -> {
            if (event.getPunishment().getType().equals(PunishmentType.MUTE)) {
                if (event.getTarget().isPresent()) {
                    String playerName = event.getTarget().get();
                    if (platform.getAbstractServer() != null) {
                        if (platform.getAbstractPlayerByName(playerName) != null) {
                            PunishmentPlayerType type = new PunishmentPlayerType(platform.getAbstractPlayerUUID(platform.getAbstractPlayerByName(playerName)), platform.getAbstractPlayerInetAddress(platform.getAbstractPlayerByName(playerName)), 0);
                            switch (platform.getAbstractServerType()) {
                                case BACKEND -> platform.addToAbstractServerCache(type, false);
                                case PROXY -> messageSender.sendPluginMessage(platform.getAbstractPlayerUUID(platform.getAbstractPlayerByName(playerName)), platform, type);
                            }
                        }
                    }

                }
            }
        };
        platform.getAbstractOmnibus().getEventBus().registerListener(PostPardonEvent.class, ListenerPriorities.NORMAL, postPardonEventEventConsumer);

        EventConsumer<PostPunishEvent> postPunishEventEventConsumer = event -> {
            if (event.getPunishment().getType().equals(PunishmentType.MUTE)) {
                if (event.getTarget().isPresent()) {
                    String playerName = event.getTarget().get();
                    if (platform.getAbstractServer() != null) {
                        if (platform.getAbstractPlayerByName(playerName) != null) {
                            PunishmentPlayerType type = new PunishmentPlayerType(platform.getAbstractPlayerUUID(platform.getAbstractPlayerByName(playerName)), platform.getAbstractPlayerInetAddress(platform.getAbstractPlayerByName(playerName)), 1);
                            switch (platform.getAbstractServerType()) {
                                case BACKEND -> platform.addToAbstractServerCache(type, true);
                                case PROXY -> messageSender.sendPluginMessage(platform.getAbstractPlayerUUID(platform.getAbstractPlayerByName(playerName)), platform, type);
                            }
                        }
                    }

                }
            }
        };
        platform.getAbstractOmnibus().getEventBus().registerListener(PostPunishEvent.class, ListenerPriorities.NORMAL, postPunishEventEventConsumer);
    }

}
