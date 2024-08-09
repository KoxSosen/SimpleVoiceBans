package com.github.koxsosen.velocity;

import com.github.koxsosen.common.LibertyBansApiHelper;
import com.github.koxsosen.common.abstraction.AbstractPlatform;
import com.github.koxsosen.common.abstraction.MessageSender;
import com.google.inject.Inject;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.event.proxy.ProxyShutdownEvent;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.proxy.ProxyServer;
import com.velocitypowered.api.proxy.ServerConnection;
import com.velocitypowered.api.proxy.messages.ChannelIdentifier;
import com.velocitypowered.api.proxy.messages.MinecraftChannelIdentifier;
import com.velocitypowered.api.proxy.server.RegisteredServer;
import org.slf4j.Logger;
import space.arim.libertybans.api.LibertyBans;
import space.arim.omnibus.Omnibus;
import space.arim.omnibus.OmnibusProvider;

import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;

@Plugin(id = "simplevoicebans", name = "SimpleVoiceBans", version = "1.4-SNAPSHOT", authors = {"KoxSosen"})

public class VelocityPluginLoader implements AbstractPlatform {

    public static LibertyBansApiHelper getLibertyBansApiHelper() {
        return libertyBansApiHelper;
    }

    private static LibertyBansApiHelper libertyBansApiHelper;

    public static LibertyBans getApi() {
        return api;
    }

    private static LibertyBans api;

    public static Omnibus getOmnibus() {
        return omnibus;
    }

    private static Omnibus omnibus;

    public static MessageSender getMessageSender() {
        return messageSender;
    }

    private static MessageSender messageSender;

    public static AbstractPlatform getPlatform() {
        return platform;
    }

    private static AbstractPlatform platform;

    public static ProxyServer getServer() {
        return server;
    }

    private static ProxyServer server;

    public static Logger getLogger() {
        return logger;
    }

    private static  Logger logger;

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
        messageSender = new MessageSender();
        platform = new VelocityPluginLoader(getServer(), getLogger());
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

    @Override
    public Object getAbstractServer() {
        return getServer();
    }

    @Override
    public Object getAbstractPlayerByUUID(UUID uuid) {
        return getServer().getPlayer(uuid);
    }

    @Override
    public Object getAbstractPlayerByName(String name) {
        return getServer().getPlayer(name);
    }

    @Override
    public void getAbstractPluginMessaging(UUID player, String identifier, byte[] data) {
        if (getServer().getPlayer(player).isPresent()) {
            Optional<ServerConnection> connection = getServer().getPlayer(player).get().getCurrentServer();
            if (connection.isPresent()) {
                connection.map(ServerConnection::getServer).ifPresent((RegisteredServer server) -> server.sendPluginMessage(() -> "simplevbans:main", data));
            }
        }
    }

    @Override
    public Object getAbstractConnection(UUID player) {
        if (getServer().getPlayer(player).isPresent()) {
            if (getServer().getPlayer(player).get().getCurrentServer().isPresent()) {
                return getServer().getPlayer(player).get().getCurrentServer().get();
            }
        }
        return null;
    }

    @Override
    public Object getAbstractCurrentServer(UUID player) {
        if (getServer().getPlayer(player).isPresent()) {
            if (getServer().getPlayer(player).get().getCurrentServer().isPresent()) {
                return getServer().getPlayer(player).get().getCurrentServer().get();
            }
        }
        return null;
    }

    // TODO: Find out if there is a common logging level implementation across platforms somehow.
    @Override
    public void sendToAbstractLogger(String data) {
        getLogger().info(data);
    }


}
