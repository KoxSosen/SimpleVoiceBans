package com.github.koxsosen.bukkit;

import com.github.koxsosen.common.PunishmentPlayerType;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import de.maxhenkel.voicechat.api.VoicechatPlugin;
import de.maxhenkel.voicechat.api.events.EventRegistration;
import de.maxhenkel.voicechat.api.events.MicrophonePacketEvent;
import org.bukkit.entity.Player;
import space.arim.libertybans.api.NetworkAddress;
import space.arim.libertybans.api.punish.Punishment;
import space.arim.omnibus.util.concurrent.ReactionStage;

import java.net.InetAddress;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;

public class SimpleVoiceBans implements VoicechatPlugin {

    public static Map<Map<UUID, InetAddress>, Boolean> getMuteCache() {
        return muteCache;
    }

    // ((UUID - IP) - Represents the player.) - (Boolean) - Determines if they are muted or not.)
    public static Map<Map<UUID, InetAddress>, Boolean> muteCache = new ConcurrentHashMap<>();

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
        if (eventPlayer != null) {
            UUID uuid = eventPlayer.getUniqueId();
            InetAddress inetAddress;

            // If the players address is null something went wrong, or they are using some short of protocol
            // where the address isn't passed down to the backend. However this is wrong, as we need to preform the check
            // for the IP too, to see if they are IP muted.
            try {
                inetAddress = eventPlayer.getAddress().getAddress();
            } catch (NullPointerException ignored) {
                return;
            }

            if (BukkitPluginLoader.isIsBungee()) {

                // If the map has the details of a player, and they are muted, cancel the event.
                // If the response from the proxy is delayed, we may not receive it in time, so some audio on the initial check may leak thru.
                if (getMuteCache().containsKey(Map.of(uuid, inetAddress))) {
                    if (getMuteCache().get(Map.of(uuid, inetAddress))) {
                        event.cancel();
                    }
                } else {
                    // If the server is bungeecord, we need to send a custom plugin message to the proxy where LibertyBans is installed.
                    // Then based on the response we determine if the player is muted or not.
                    // Since this is a network request, it may take a long time, so we need to avoid blocking anything in the meantime.
                    sendCustomData(eventPlayer, new PunishmentPlayerType(uuid, inetAddress));
                }

            } else {

                AtomicBoolean isMuted = new AtomicBoolean(false);
                ReactionStage<Optional<Punishment>> mutes = BukkitPluginLoader.getApi().getSelector().getCachedMute(eventPlayer.getUniqueId(), NetworkAddress.of(eventPlayer.getAddress().getAddress()));
                mutes.thenAccept(punishment -> {
                    if (punishment.isPresent()) {
                        isMuted.set(true);
                    }
                }).exceptionally((ex) -> null);

                if (isMuted.get()) {
                    event.cancel();
                }
            }
        }

    }

    private void sendCustomData(Player player, PunishmentPlayerType punishmentPlayerType) {
        ByteArrayDataOutput out = ByteStreams.newDataOutput();
        out.writeUTF(punishmentPlayerType.getUuid().toString());
        out.writeUTF(punishmentPlayerType.getInetAddress().toString());
        player.sendPluginMessage(BukkitPluginLoader.getInstance(),"simplevoicechat:custom", out.toByteArray());
    }

}
