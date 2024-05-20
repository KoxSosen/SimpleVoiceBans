package com.github.koxsosen.bukkit;

import com.github.koxsosen.common.PunishmentPlayerType;
import de.maxhenkel.voicechat.api.VoicechatPlugin;
import de.maxhenkel.voicechat.api.events.EventRegistration;
import de.maxhenkel.voicechat.api.events.MicrophonePacketEvent;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import space.arim.libertybans.api.NetworkAddress;
import space.arim.libertybans.api.punish.Punishment;
import space.arim.omnibus.util.concurrent.ReactionStage;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
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

            try {
                inetAddress = eventPlayer.getAddress().getAddress();
            } catch (NullPointerException ignored) {
                return;
            }

            BaseComponent baseComponent = new TextComponent("You are muted, you can't use voice chat!");
            baseComponent.setColor(ChatColor.GREEN);

            if (BukkitPluginLoader.isIsBungee()) {
                // If the map has the details of a player, and they are muted, cancel the event.
                // If the response from the proxy is delayed, we may not receive it in time, so some audio on the initial check may leak thru.
                if (getMuteCache().containsKey(Map.of(uuid, inetAddress))) {
                    if (getMuteCache().get(Map.of(uuid, inetAddress))) {
                        sendActionBar(eventPlayer, baseComponent);
                        event.cancel();
                    }
                } else {
                    Bukkit.getScheduler().runTaskAsynchronously(BukkitPluginLoader.getInstance(), () -> sendCustomData(eventPlayer, new PunishmentPlayerType(uuid, inetAddress)));
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
                    sendActionBar(eventPlayer, baseComponent);
                    event.cancel();
                }
            }
        }
    }

    private void sendCustomData(Player player, PunishmentPlayerType punishmentPlayerType) {

        ByteArrayOutputStream byao = new ByteArrayOutputStream();
        ObjectOutputStream outputStream;
        try {
            outputStream = new ObjectOutputStream(byao);
            outputStream.writeObject(punishmentPlayerType);
            outputStream.flush();
            outputStream.close();
        } catch (IOException e) {
            Bukkit.getServer().getLogger().info("Unable to serialize: " + e);
            return;
        }

        player.sendPluginMessage(BukkitPluginLoader.getInstance(),"simplevoicebans:custom", byao.toByteArray());
        try {
            byao.close();
        } catch (IOException e) {
            Bukkit.getServer().getLogger().info("Unable to close stream: " + e);
        }

    }

    private void sendActionBar(Player player, BaseComponent component) {
        if (player.hasPermission("simplevoicebans.actionbar")) {
            player.spigot().sendMessage(ChatMessageType.ACTION_BAR, component);
        }
    }

}
