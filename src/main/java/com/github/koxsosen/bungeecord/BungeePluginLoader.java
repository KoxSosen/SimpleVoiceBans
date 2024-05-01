package com.github.koxsosen.bungeecord;

import com.github.koxsosen.bungeecord.messaging.MessageReceiver;
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

    @Override
    public void onEnable() {
        try {
            Omnibus omnibus = OmnibusProvider.getOmnibus();
            api = omnibus.getRegistry().getProvider(LibertyBans.class).orElseThrow();
        } catch (NoSuchElementException ignored) {
            getLogger().info("SimpleVoiceBans on the proxy requires LibertyBans to be installed too.");
            getLogger().info("Install LibertyBans on the proxy, as well as SimpleVoiceChat and SimpleVoiceBans on all backends, and the proxy.");
            getProxy().getPluginManager().unregisterListeners(this);
        }


        libertyBansApiHelper = new LibertyBansApiHelper();
        getLogger().info("Loaded SimpleVoiceBans.");
        getLogger().info("Make sure you have SimpleVoiceChat, and SimpleVoiceBans installed on all backend servers.");

        getProxy().registerChannel("simplevoicechat:custom");
        getProxy().getPluginManager().registerListener(this, new MessageReceiver());
    }

    @Override
    public void onDisable() {
        getProxy().getPluginManager().unregisterListeners(this);
        getProxy().unregisterChannel("simplevoicechat:custom");
    }

    public static LibertyBansApiHelper getLibertyBansApiHelper() {
        return libertyBansApiHelper;
    }

}
