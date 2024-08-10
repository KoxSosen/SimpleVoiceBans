package com.github.koxsosen.bungee;

import com.github.koxsosen.common.LibertyBansApiHelper;
import com.github.koxsosen.common.abstraction.AbstractPlatform;
import com.github.koxsosen.common.abstraction.MessageSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Plugin;
import space.arim.libertybans.api.LibertyBans;
import space.arim.omnibus.Omnibus;
import space.arim.omnibus.OmnibusProvider;

import java.util.Collection;
import java.util.NoSuchElementException;
import java.util.UUID;

public class BungeePluginLoader extends Plugin implements AbstractPlatform {

    public static LibertyBansApiHelper libertyBansApiHelper;

    public static LibertyBans getApi() {
        return api;
    }

    public static LibertyBans api;

    public static Omnibus getOmnibus() {
        return omnibus;
    }

    public static Omnibus omnibus;

    public static MessageSender messageSender;

    public static AbstractPlatform platform;

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

        getProxy().registerChannel("simplevbans:main");
        getProxy().getPluginManager().registerListener(this, new MessageReceiver());
        messageSender = new MessageSender();
        platform = new BungeePluginLoader();
    }

    @Override
    public void onDisable() {
        getProxy().getPluginManager().unregisterListeners(this);
        getProxy().unregisterChannel("simplevbans:main");
    }

    public static LibertyBansApiHelper getLibertyBansApiHelper() {
        return libertyBansApiHelper;
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
}
