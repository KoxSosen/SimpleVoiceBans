package com.github.koxsosen.velocity;

import com.github.koxsosen.common.LibertyBansApiHelper;
import com.google.inject.Inject;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.event.proxy.ProxyShutdownEvent;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.proxy.ProxyServer;
import com.velocitypowered.api.proxy.messages.MinecraftChannelIdentifier;
import org.slf4j.Logger;
import space.arim.libertybans.api.LibertyBans;
import space.arim.omnibus.Omnibus;
import space.arim.omnibus.OmnibusProvider;

import java.util.NoSuchElementException;

@Plugin(id = "simplevoicebans", name = "SimpleVoiceBans", version = "1.4-SNAPSHOT", authors = {"KoxSosen"})

public class VelocityPluginLoader {

    public static LibertyBansApiHelper getLibertyBansApiHelper() {
        return libertyBansApiHelper;
    }

    public static LibertyBansApiHelper libertyBansApiHelper;

    public static LibertyBans getApi() {
        return api;
    }

    public static LibertyBans api;

    public static Omnibus getOmnibus() {
        return omnibus;
    }

    private static Omnibus omnibus;

    public static ProxyServer getServer() {
        return server;
    }

    public static Logger getLogger() {
        return logger;
    }

    public static ProxyServer server;
    public static  Logger logger;

    public static final MinecraftChannelIdentifier IDENTIFIER = MinecraftChannelIdentifier.from("simplevbans:main");

    @Inject
    public VelocityPluginLoader(ProxyServer server, Logger logger) {
        VelocityPluginLoader.server = server;
        VelocityPluginLoader.logger = logger;
    }

    @Subscribe
    @SuppressWarnings("unused")
    public void onProxyInitialization(ProxyInitializeEvent event) {
        try {
            omnibus = OmnibusProvider.getOmnibus();
            api = omnibus.getRegistry().getProvider(LibertyBans.class).orElseThrow();
            PunishmentListener punishmentListener = new PunishmentListener();
            punishmentListener.listenToPostPunishEvent();
            punishmentListener.listenToPostPardonEvent();
        } catch (NoSuchElementException | NoClassDefFoundError ignored) {
            logger.info("SimpleVoiceBans on the proxy requires LibertyBans to be installed too.");
            logger.info("Install LibertyBans on the proxy, as well as SimpleVoiceChat and SimpleVoiceBans on all backends, and the proxy.");
            server.getEventManager().unregisterListeners(this);
        }

        libertyBansApiHelper = new LibertyBansApiHelper();
        getLogger().info("Loaded SimpleVoiceBans.");
        getLogger().info("Make sure you have SimpleVoiceChat, and SimpleVoiceBans installed on all backend servers.");

        getServer().getChannelRegistrar().register(IDENTIFIER);
        server.getEventManager().register(this, new MessageReceiver());
    }

    @Subscribe
    @SuppressWarnings("unused")
    public void onProxyShutdownEvent(ProxyShutdownEvent event) {
        getServer().getEventManager().unregisterListeners(this);
        getServer().getChannelRegistrar().unregister(IDENTIFIER);
    }

}
