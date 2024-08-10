package com.github.koxsosen.bungee;

import com.github.koxsosen.common.LibertyBansApiHelper;
import com.github.koxsosen.common.abstraction.AbstractPlatform;
import com.github.koxsosen.common.abstraction.Constants;
import com.github.koxsosen.common.abstraction.MessageSender;
import com.github.koxsosen.common.abstraction.ServerType;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Plugin;
import space.arim.libertybans.api.LibertyBans;
import space.arim.omnibus.Omnibus;
import space.arim.omnibus.OmnibusProvider;

import java.net.InetAddress;
import java.util.Collection;
import java.util.NoSuchElementException;
import java.util.UUID;
import java.util.logging.Logger;

public class BungeePluginLoader extends Plugin implements AbstractPlatform {

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

    public static AbstractPlatform getPlatform() {
        return platform;
    }

    public static MessageSender getMessageSender() {
        return messageSender;
    }

    private static MessageSender messageSender;

    private static AbstractPlatform platform;

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
    public Object getAbstractServer() {
        return ProxyServer.getInstance();
    }

    @Override
    public Object getAbstractPlayerByUUID(UUID uuid) {
        return ProxyServer.getInstance().getPlayer(uuid);
    }

    @Override
    public Object getAbstractPlayerByName(String name) {
        return ProxyServer.getInstance().getPlayer(name);
    }

    @Override
    public UUID getAbstractPlayerUUID(Object player) {
        ProxiedPlayer player1 = (ProxiedPlayer) player;
        return player1.getUniqueId();
    }

    @Override
    public InetAddress getAbstractPlayerInetAddress(Object player) {
        ProxiedPlayer player1 = (ProxiedPlayer) player;
        return player1.getAddress().getAddress();
    }

    @Override
    public void getAbstractPluginMessaging(UUID player, String identifier, byte[] data) {
        ProxyServer.getInstance().getPlayer(player).sendData(identifier, data);
    }

    @Override
    public Object getAbstractConnection(UUID player) {
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
    public boolean verifyAbstractSource(Object source) {
        return source instanceof ProxiedPlayer;
    }

}
