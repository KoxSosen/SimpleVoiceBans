package com.github.koxsosen.common;

import space.arim.libertybans.api.LibertyBans;
import space.arim.libertybans.api.NetworkAddress;
import space.arim.libertybans.api.punish.Punishment;
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

}
