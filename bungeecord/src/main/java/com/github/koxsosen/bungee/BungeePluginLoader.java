package com.github.koxsosen.bungee;

import com.github.koxsosen.common.LibertyBansApiHelper;
import com.github.koxsosen.common.abstraction.AbstractPlatform;
import com.github.koxsosen.common.abstraction.Constants;
import com.github.koxsosen.common.abstraction.MessageSender;
import com.github.koxsosen.common.abstraction.ServerType;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.Connection;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.connection.Server;
import net.md_5.bungee.api.plugin.Plugin;
import space.arim.libertybans.api.LibertyBans;
import space.arim.omnibus.Omnibus;
import space.arim.omnibus.OmnibusProvider;

import java.net.InetAddress;
import java.util.Collection;
import java.util.NoSuchElementException;
import java.util.UUID;
import java.util.logging.Logger;

public class BungeePluginLoader extends Plugin implements AbstractPlatform<ProxyServer, ProxiedPlayer, Server, Connection> {

    public static LibertyBansApiHelper getLibertyBansApiHelper() {
        return libertyBansApiHelper;
    }

    public static LibertyBansApiHelper libertyBansApiHelper;

    public static LibertyBans getApi() {
        return api;
    }

    private static LibertyBans api;

    public static Omnibus getOmnibus() {
        return omnibus;
    }

    private static Omnibus omnibus;

    public static AbstractPlatform<ProxyServer, ProxiedPlayer, Server, Connection> getPlatform() {
        return platform;
    }

    public static MessageSender getMessageSender() {
        return messageSender;
    }

    private static MessageSender messageSender;

    private static AbstractPlatform<ProxyServer, ProxiedPlayer, Server, Connection> platform;

    public static Logger getPluginLogger() {
        return logger;
    }

    private static Logger logger;

    @Override
    public void onEnable() {
        logger = ProxyServer.getInstance().getLogger();
        try {
            omnibus = OmnibusProvider.getOmnibus();
            api = omnibus.getRegistry().getProvider(LibertyBans.class).orElseThrow();
        } catch (NoSuchElementException | NoClassDefFoundError ignored) {
            getPluginLogger().info(Constants.getMsgProxyRequirement());
            getProxy().getPluginManager().unregisterListeners(this);
        }

        libertyBansApiHelper = new LibertyBansApiHelper();
        getPluginLogger().info(Constants.getMsgLoaded());

        getProxy().registerChannel(Constants.getChannelIdentifier());
        getProxy().getPluginManager().registerListener(this, new MessageReceiver());
        messageSender = new MessageSender();
        platform = new BungeePluginLoader();
        getLibertyBansApiHelper().listenToPunishmentEvents(getPlatform(), getMessageSender());
    }

    @Override
    public void onDisable() {
        getProxy().getPluginManager().unregisterListeners(this);
        getProxy().unregisterChannel(Constants.getChannelIdentifier());
    }

    @Override
    public ProxyServer getAbstractServer() {
        return ProxyServer.getInstance();
    }

    @Override
    public ProxiedPlayer getAbstractPlayerByUUID(UUID uuid) {
        return ProxyServer.getInstance().getPlayer(uuid);
    }

    @Override
    public ProxiedPlayer getAbstractPlayerByName(String name) {
        return ProxyServer.getInstance().getPlayer(name);
    }

    @Override
    public UUID getAbstractPlayerUUID(ProxiedPlayer player) {
        return player.getUniqueId();
    }

    @Override
    public InetAddress getAbstractPlayerInetAddress(ProxiedPlayer player) {
        return player.getAddress().getAddress();
    }

    @Override
    public void getAbstractPluginMessaging(UUID player, String identifier, byte[] data) {
        ProxyServer.getInstance().getPlayer(player).sendData(identifier, data);
    }

    @Override
    public Server getAbstractConnection(UUID player) {
        return ProxyServer.getInstance().getPlayer(player).getServer();
    }

    @Override
    public void sendToAbstractLogger(String data) {
        ProxyServer.getInstance().getLogger().info(data);
    }

    @Override
    public int getConnectedPlayers(UUID player) {
        Collection<ProxiedPlayer> serverPlayers = ProxyServer.getInstance().getPlayer(player).getServer().getInfo().getPlayers();
        if (serverPlayers == null || serverPlayers.isEmpty()) {
            return 0;
        }
        return serverPlayers.size();
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
    public boolean verifyAbstractSource(Connection source) {
        return source instanceof ProxiedPlayer;
    }

}
