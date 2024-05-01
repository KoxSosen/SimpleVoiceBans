package com.github.koxsosen.common;

import space.arim.libertybans.api.LibertyBans;
import space.arim.libertybans.api.NetworkAddress;
import space.arim.libertybans.api.punish.Punishment;
import space.arim.omnibus.util.concurrent.ReactionStage;

import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;

public class LibertyBansApiHelper {

    public Boolean isMuted(LibertyBans api, PunishmentPlayerType punishmentPlayerType) {
        AtomicBoolean isMuted = new AtomicBoolean(false);
        ReactionStage<Optional<Punishment>> mutes = api.getSelector().getCachedMute(punishmentPlayerType.getUuid(), NetworkAddress.of(punishmentPlayerType.getInetAddress()));
            mutes.thenAccept(punishment -> {
                if (punishment.isPresent()) {
                    isMuted.set(true);
                }
            }).exceptionally((ex) -> null);
            return isMuted.get();
    }


}
