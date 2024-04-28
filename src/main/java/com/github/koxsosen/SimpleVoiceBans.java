package com.github.koxsosen;

import de.maxhenkel.voicechat.api.VoicechatPlugin;
import de.maxhenkel.voicechat.api.events.EventRegistration;
import de.maxhenkel.voicechat.api.events.MicrophonePacketEvent;
import org.bukkit.entity.Player;
import space.arim.libertybans.api.NetworkAddress;
import space.arim.libertybans.api.punish.Punishment;
import space.arim.omnibus.util.concurrent.ReactionStage;

import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;

public class SimpleVoiceBans implements VoicechatPlugin {

    @Override
    public String getPluginId() {
        return "simple_voice_bans";
    }

    @Override
    public void registerEvents(EventRegistration eventRegistration) {
        eventRegistration.registerEvent(MicrophonePacketEvent.class, this::onMicrophone);
    }

    private void onMicrophone(MicrophonePacketEvent event) {
        if (event.getSenderConnection() == null) {
            return;
        }

        Player eventPlayer = (Player) event.getSenderConnection().getPlayer().getPlayer();
        AtomicBoolean isMuted = new AtomicBoolean(false);
        if (eventPlayer != null) {
            ReactionStage<Optional<Punishment>> mutes = PluginLoader.api.getSelector().getCachedMute(eventPlayer.getUniqueId(), NetworkAddress.of(eventPlayer.getAddress().getAddress()));
            mutes.thenAccept(punishment -> {
                if (punishment.isPresent()) {
                    isMuted.set(true);
                }
            }).exceptionally((ex) -> null);
        }

        if (isMuted.get()) {
            event.cancel();
        }
    }

}
