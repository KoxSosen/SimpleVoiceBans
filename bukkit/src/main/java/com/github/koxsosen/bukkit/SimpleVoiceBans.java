package com.github.koxsosen.bukkit;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
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
import space.arim.omnibus.util.concurrent.ReactionStage;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class SimpleVoiceBans implements VoicechatPlugin {

    public static Cache<PunishmentPlayerType, Boolean> getMuteCache() {
        return muteCache;
    }

    public static Cache<PunishmentPlayerType, Boolean> muteCache = Caffeine.newBuilder()
            .expireAfterWrite(10, TimeUnit.MINUTES)
            .maximumSize(10_1000)
            .build();

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
                inetAddress = Objects.requireNonNull(eventPlayer.getAddress()).getAddress();
            } catch (NullPointerException ignored) {
                return;
            }

            BaseComponent baseComponent = new TextComponent("You are muted, you can't use voice chat!");
            baseComponent.setColor(ChatColor.GREEN);

            PunishmentPlayerType punishmentPlayerType = new PunishmentPlayerType(uuid, inetAddress);

            if (getMuteCache().getIfPresent(punishmentPlayerType) != null) {
                if (Boolean.TRUE.equals(getMuteCache().getIfPresent(punishmentPlayerType))) {
                    sendActionBar(eventPlayer, baseComponent);
                    event.cancel();
                }
            } else {
                if (BukkitPluginLoader.isIsBungee()) {
                    Bukkit.getScheduler().runTaskAsynchronously(BukkitPluginLoader.getInstance(), () -> sendCustomData(eventPlayer, new PunishmentPlayerType(uuid, inetAddress)));
                } else {
                    ReactionStage<Integer> isMuted = BukkitPluginLoader.getLibertyBansApiHelper().checkMuted(BukkitPluginLoader.getApi(), punishmentPlayerType);
                    isMuted.thenAcceptAsync(mutedState -> checkResponse(punishmentPlayerType, mutedState))
                            .exceptionally(ex -> {
                                Bukkit.getLogger().info("Unable to determine weather player is muted or not: " + ex);
                                return null;
                            });
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
        Bukkit.getServer().sendPluginMessage(BukkitPluginLoader.getInstance(), "simplevbans:main", byao.toByteArray());
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

    public static void checkResponse(PunishmentPlayerType punishmentPlayerType, Integer state) {
        switch (state) {
            case 0:
                SimpleVoiceBans.getMuteCache().put(punishmentPlayerType, false);
                break;
            case 1:
                SimpleVoiceBans.getMuteCache().put(punishmentPlayerType, true);
                break;
            case 2:
                SimpleVoiceBans.getMuteCache().invalidate(punishmentPlayerType);
                break;
            case 3:
                if (SimpleVoiceBans.getMuteCache().getIfPresent(punishmentPlayerType) != null) {
                    SimpleVoiceBans.getMuteCache().invalidate(punishmentPlayerType);
                }
                Bukkit.getServer().getLogger().info("Something went wrong with determining the muted state on the proxy.");
                break;
        }
    }
}
