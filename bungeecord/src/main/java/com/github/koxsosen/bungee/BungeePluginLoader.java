package com.github.koxsosen.bungee;

import com.github.koxsosen.common.LibertyBansApiHelper;
import net.md_5.bungee.api.plugin.Plugin;
import space.arim.libertybans.api.LibertyBans;
import space.arim.omnibus.Omnibus;
import space.arim.omnibus.OmnibusProvider;

import java.util.NoSuchElementException;

public class BungeePluginLoader extends Plugin {

    public static LibertyBansApiHelper libertyBansApiHelper;

    public static LibertyBans getApi() {
        return api;
    }

    public static LibertyBans api;

    public static Omnibus getOmnibus() {
        return omnibus;
    }

    public static Omnibus omnibus;

    @Override
    public void onEnable() {
        try {
            omnibus = OmnibusProvider.getOmnibus();
            api = omnibus.getRegistry().getProvider(LibertyBans.class).orElseThrow();
            PunishmentListener punishmentListener = new PunishmentListener();
            punishmentListener.listenToPostPunishEvent();
            punishmentListener.listenToPostPardonEvent();
        } catch (NoSuchElementException | NoClassDefFoundError ignored) {
            getLogger().info("SimpleVoiceBans on the proxy requires LibertyBans to be installed too.");
            getLogger().info("Install LibertyBans on the proxy, as well as SimpleVoiceChat and SimpleVoiceBans on all backends, and the proxy.");
            getProxy().getPluginManager().unregisterListeners(this);
        }

        libertyBansApiHelper = new LibertyBansApiHelper();
        getLogger().info("Loaded SimpleVoiceBans.");
        getLogger().info("Make sure you have SimpleVoiceChat, and SimpleVoiceBans installed on all backend servers.");

        getProxy().registerChannel("simplevbans:custom");
        getProxy().getPluginManager().registerListener(this, new MessageReceiver());
    }

    @Override
    public void onDisable() {
        getProxy().getPluginManager().unregisterListeners(this);
        getProxy().unregisterChannel("simplevbans:custom");
    }

    public static LibertyBansApiHelper getLibertyBansApiHelper() {
        return libertyBansApiHelper;
    }

}
