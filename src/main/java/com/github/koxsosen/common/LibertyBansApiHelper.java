package com.github.koxsosen.common;

import space.arim.libertybans.api.LibertyBans;
import space.arim.libertybans.api.NetworkAddress;
import space.arim.libertybans.api.punish.Punishment;
import space.arim.omnibus.util.concurrent.ReactionStage;

import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicBoolean;

public class LibertyBansApiHelper {

    public boolean isMuted(LibertyBans api, PunishmentPlayerType punishmentPlayerType) {
        AtomicBoolean isMuted = new AtomicBoolean(false);
        ReactionStage<Optional<Punishment>> mutes = api.getSelector().getCachedMute(punishmentPlayerType.getUuid(), NetworkAddress.of(punishmentPlayerType.getInetAddress()));
        try {
            mutes.thenAcceptAsync(punishment -> {
                if (punishment.isPresent()) {
                    isMuted.set(true);
                    System.out.println("Player is muted!");
                }
            }).toCompletableFuture().exceptionally(throwable -> {
                throwable.printStackTrace();
                return null;
            }).get();
        } catch (InterruptedException | ExecutionException ignored) { }

        return isMuted.get();
    }


}
