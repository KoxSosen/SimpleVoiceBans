package com.github.koxsosen.velocity;

import com.github.koxsosen.common.LibertyBansApiHelper;
import com.github.koxsosen.common.abstraction.AbstractPlatform;
import com.github.koxsosen.common.abstraction.Constants;
import com.github.koxsosen.common.abstraction.MessageSender;
import com.github.koxsosen.common.abstraction.ServerType;
import com.google.inject.Inject;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.event.proxy.ProxyShutdownEvent;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ProxyServer;
import com.velocitypowered.api.proxy.ServerConnection;
import com.velocitypowered.api.proxy.messages.MinecraftChannelIdentifier;
import com.velocitypowered.api.proxy.server.RegisteredServer;
import org.slf4j.Logger;
import space.arim.libertybans.api.LibertyBans;
import space.arim.omnibus.Omnibus;
import space.arim.omnibus.OmnibusProvider;

import java.net.InetAddress;
import java.util.*;

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

    public static final MinecraftChannelIdentifier IDENTIFIER = MinecraftChannelIdentifier.from(Constants.getChannelIdentifier());

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
        } catch (NoSuchElementException | NoClassDefFoundError ignored) {
            getLogger().info(Constants.getChannelIdentifier());
            getServer().getEventManager().unregisterListeners(this);
        }

        libertyBansApiHelper = new LibertyBansApiHelper();
        messageSender = new MessageSender();
        platform = new VelocityPluginLoader(getServer(), getLogger());
        getLibertyBansApiHelper().listenToPunishmentEvents(getPlatform(), getMessageSender());
        getLogger().info(Constants.getMsgLoaded());

        getServer().getChannelRegistrar().register(IDENTIFIER);
        getServer().getEventManager().register(this, new MessageReceiver());
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
    public UUID getAbstractPlayerUUID(Object player) {
        Player player1 = (Player) player;
        return player1.getUniqueId();
    }

    @Override
    public InetAddress getAbstractPlayerInetAddress(Object player) {
        Player player1 = (Player) player;
        return player1.getRemoteAddress().getAddress();
    }

    @Override
    public void getAbstractPluginMessaging(UUID player, String identifier, byte[] data) {
        if (getServer().getPlayer(player).isPresent()) {
            Optional<ServerConnection> connection = getServer().getPlayer(player).get().getCurrentServer();
            if (connection.isPresent()) {
                connection.map(ServerConnection::getServer).ifPresent((RegisteredServer server) -> server.sendPluginMessage(Constants::getChannelIdentifier, data));
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
    public void sendToAbstractLogger(String data) {
        getLogger().info(data);
    }

    @Override
    public int getConnectedPlayers(UUID player) {
        Collection<Player> players = List.of();
        if (getServer().getPlayer(player).isPresent()) {
            if (getServer().getPlayer(player).get().getCurrentServer().isPresent()) {
                Optional<ServerConnection> connection = getServer().getPlayer(player).get().getCurrentServer();
                if (connection.isPresent()) {
                    players = connection.get().getServer().getPlayersConnected();
                    if (players == null || players.isEmpty()) {
                        return 0;
                    }
                }
            }
        }
        return players.size();
    }

    @Override
    public Omnibus getAbstractOmnibus() {
        return getOmnibus();
    }

    @Override
    public ServerType getAbstractServerType() {
        return ServerType.PROXY;
    }

    @Override
    public boolean verifyAbstractSource(Object source) {
        return source instanceof ServerConnection;
    }

}
