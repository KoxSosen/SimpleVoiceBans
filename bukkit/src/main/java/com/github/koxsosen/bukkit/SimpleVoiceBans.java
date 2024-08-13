package com.github.koxsosen.bukkit;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.koxsosen.common.PunishmentPlayerType;
import com.github.koxsosen.common.abstraction.Constants;
import de.maxhenkel.voicechat.api.VoicechatPlugin;
import de.maxhenkel.voicechat.api.events.EventRegistration;
import de.maxhenkel.voicechat.api.events.MicrophonePacketEvent;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.entity.Player;
import space.arim.omnibus.util.concurrent.ReactionStage;

import java.net.InetAddress;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

import static com.github.koxsosen.bukkit.BukkitPluginLoader.*;

public class SimpleVoiceBans implements VoicechatPlugin {

    public static Cache<PunishmentPlayerType, Boolean> getMuteCache() {
        return muteCache;
    }

    private static final Cache<PunishmentPlayerType, Boolean> muteCache = Caffeine.newBuilder()
            .expireAfterWrite(10, TimeUnit.MINUTES)
            .maximumSize(10_1000)
            .build();

    public static Set<UUID> getUuidSet() {
        return uuidSet;
    }

    private static final Set<UUID> uuidSet = ConcurrentHashMap.newKeySet();

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
                if (isIsBungee()) {
                    if (!getUuidSet().contains(eventPlayer.getUniqueId())) {
                        getMorePaperLib().scheduling().asyncScheduler().run(() -> getMessageSender().sendPluginMessage(eventPlayer.getUniqueId(), getPlatform(), new PunishmentPlayerType(uuid, inetAddress)));
                    }
                    getUuidSet().add(eventPlayer.getUniqueId());
                } else {
                        ReactionStage<Integer> isMuted = getLibertyBansApiHelper().checkMuted(getApi(), punishmentPlayerType);
                        isMuted.thenAcceptAsync(mutedState -> checkResponse(punishmentPlayerType, mutedState))
                                .exceptionally(ex -> {
                                    getPluginLogger().info(Constants.getErrMute() + ex);
                                    return null;
                                });
                }
            }
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
                getPluginLogger().info(Constants.getErrMute());
                break;
        }
    }
}
