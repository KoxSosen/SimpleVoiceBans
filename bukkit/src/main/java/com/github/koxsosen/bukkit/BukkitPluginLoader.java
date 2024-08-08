package com.github.koxsosen.bukkit;

import com.github.koxsosen.common.LibertyBansApiHelper;
import de.maxhenkel.voicechat.api.BukkitVoicechatService;
import org.bukkit.plugin.java.JavaPlugin;
import space.arim.libertybans.api.LibertyBans;
import space.arim.morepaperlib.MorePaperLib;
import space.arim.omnibus.Omnibus;
import space.arim.omnibus.OmnibusProvider;

import java.util.NoSuchElementException;

public class BukkitPluginLoader extends JavaPlugin {

    public static LibertyBansApiHelper libertyBansApiHelper;

    public static LibertyBans api;

    public static boolean isIsBungee() {
        return isBungee;
    }

    public static boolean isBungee = false;

    public static MorePaperLib getMorePaperLib() {
        return morePaperLib;
    }

    public static MorePaperLib morePaperLib;

    private SimpleVoiceBans simpleVoiceBans;

    public static BukkitPluginLoader instance;

    public static Omnibus getOmnibus() {
        return omnibus;
    }

    public static Omnibus omnibus;

    @Override
    public void onEnable() {
        instance = this;
        BukkitVoicechatService bukkitVoicechatService = getServer().getServicesManager().load(BukkitVoicechatService.class);
        if (bukkitVoicechatService != null) {
            simpleVoiceBans = new SimpleVoiceBans();
            bukkitVoicechatService.registerPlugin(simpleVoiceBans);
            getLogger().info("Successfully registered!");

        } else {
            getLogger().info("Service is null!");
        }

        try {
            omnibus = OmnibusProvider.getOmnibus();
            api = omnibus.getRegistry().getProvider(LibertyBans.class).orElseThrow();
            getLogger().info("Registered the LibertyBans api.");
            getLogger().info("Since this backend server has LibertyBans installed, SimpleVoiceBans presumes that you don't have it installed on the proxy.");
            getLogger().info("Therefore we disable the proxy specific support code in SimpleVoiceBans.");
            libertyBansApiHelper = new LibertyBansApiHelper();
            PunishmentListener punishmentListener = new PunishmentListener();
            punishmentListener.listenToPostPunishEvent();
            punishmentListener.listenToPostPardonEvent();
            morePaperLib = new MorePaperLib(getInstance());
        } catch (NoSuchElementException | NoClassDefFoundError ignored) {
            getLogger().info("We determined that you do not have LibertyBans installed on this backend server.");
            getLogger().info("Therefore we assume that you have it installed on the proxy.");
            getLogger().info("Enabling proxy support.");
            isBungee = checkIfBungee();
            if (isBungee) {
                getLogger().info("This server is proxied.");
                // We need this channel to be able to send the request.
                getServer().getMessenger().registerOutgoingPluginChannel(this, "simplevbans:main");
                // We need this channel to be able to receive the response.
                getServer().getMessenger().registerIncomingPluginChannel( this, "simplevbans:main", new MessageReceiver());
            } else {
                getLogger().info("This server is not proxied.");
                getLogger().info("SimpleVoiceBans without LibertyBans installed requires a proxied backend server.");
                getLogger().info("Disabling.");
                getServer().getPluginManager().disablePlugin(this);
            }
        }
    }

    @Override
    public void onDisable() {
        if (simpleVoiceBans != null) {
            getServer().getServicesManager().unregister(simpleVoiceBans);
            getLogger().info("Successfully unregistered!");
        }
        if (isBungee) {
            this.getServer().getMessenger().unregisterOutgoingPluginChannel(this);
            this.getServer().getMessenger().unregisterIncomingPluginChannel(this);
        }
    }

    public boolean checkIfBungee() {
        return getServer().spigot().getConfig().getConfigurationSection("settings").getBoolean("bungeecord");
    }

    public static BukkitPluginLoader getInstance() {
        return instance;
    }

    public static LibertyBans getApi() {
        return api;
    }

    public static LibertyBansApiHelper getLibertyBansApiHelper() {
        return libertyBansApiHelper;
    }

}

