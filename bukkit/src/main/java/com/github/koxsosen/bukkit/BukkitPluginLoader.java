package com.github.koxsosen.bukkit;

import com.github.koxsosen.common.LibertyBansApiHelper;
import com.github.koxsosen.common.PunishmentPlayerType;
import com.github.koxsosen.common.abstraction.AbstractPlatform;
import com.github.koxsosen.common.abstraction.Constants;
import com.github.koxsosen.common.abstraction.MessageSender;
import com.github.koxsosen.common.abstraction.ServerType;
import de.maxhenkel.voicechat.api.BukkitVoicechatService;
import org.bukkit.Bukkit;
import org.bukkit.Server;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import space.arim.libertybans.api.LibertyBans;
import space.arim.morepaperlib.MorePaperLib;
import space.arim.omnibus.Omnibus;
import space.arim.omnibus.OmnibusProvider;

import java.net.InetAddress;
import java.time.Duration;
import java.util.NoSuchElementException;
import java.util.UUID;
import java.util.logging.Logger;

import static com.github.koxsosen.bukkit.SimpleVoiceBans.*;

public class BukkitPluginLoader extends JavaPlugin implements AbstractPlatform<Server, Player, Server> {

    private static LibertyBansApiHelper libertyBansApiHelper;

    private static LibertyBans api;

    public static boolean isIsBungee() {
        return isBungee;
    }

    private static boolean isBungee = false;

    public static MorePaperLib getMorePaperLib() {
        return morePaperLib;
    }

    private static MorePaperLib morePaperLib;

    public static MessageSender getMessageSender() {
        return messageSender;
    }

    public static AbstractPlatform<Server, Player, Server> getPlatform() {
        return platform;
    }

    private static MessageSender messageSender;

    private static AbstractPlatform<Server, Player, Server> platform;

    public static Logger getPluginLogger() {
        return logger;
    }

    private static Logger logger;

    public SimpleVoiceBans getSimpleVoiceBans() {
        return simpleVoiceBans;
    }

    private SimpleVoiceBans simpleVoiceBans;

    private static BukkitPluginLoader instance;

    public static Omnibus getOmnibus() {
        return omnibus;
    }

    private static Omnibus omnibus;

    @Override
    public void onEnable() {
        instance = this;
        BukkitVoicechatService bukkitVoicechatService = getServer().getServicesManager().load(BukkitVoicechatService.class);
        if (bukkitVoicechatService != null) {
            simpleVoiceBans = new SimpleVoiceBans();
            bukkitVoicechatService.registerPlugin(getSimpleVoiceBans());
        }

        logger = getLogger();
        platform = getInstance();

        // TODO: We can save a bit of memory here, as in case of standalone setups MessageSender isn't actually needed.
        messageSender = new MessageSender();

        try {
            omnibus = OmnibusProvider.getOmnibus();
            api = getOmnibus().getRegistry().getProvider(LibertyBans.class).orElseThrow();
            getPluginLogger().info(Constants.getMsgBackend());
            libertyBansApiHelper = new LibertyBansApiHelper();
            getLibertyBansApiHelper().listenToPunishmentEvents(getPlatform(), getMessageSender());
        } catch (NoSuchElementException | NoClassDefFoundError ignored) {
            api = null;
            getPluginLogger().info(Constants.getMsgProxy());
            isBungee = checkIfBungee();
            if (checkIfBungee()) {
                getServer().getMessenger().registerOutgoingPluginChannel(this, Constants.getChannelIdentifier());
                getServer().getMessenger().registerIncomingPluginChannel( this, Constants.getChannelIdentifier(), new MessageReceiver());
            } else {
                getPluginLogger().info(Constants.getErrBackendMissing());
                getServer().getPluginManager().disablePlugin(this);
            }
        }

        morePaperLib = new MorePaperLib(getInstance());

        getMorePaperLib().scheduling().asyncScheduler().runAtFixedRate(() -> getUuidSet().clear(), Duration.ofSeconds(5), Duration.ofMinutes(10));

    }

    @Override
    public void onDisable() {
        if (getSimpleVoiceBans() != null) {
            getServer().getServicesManager().unregister(getSimpleVoiceBans());
        }
        if (checkIfBungee()) {
            this.getServer().getMessenger().unregisterOutgoingPluginChannel(this);
            this.getServer().getMessenger().unregisterIncomingPluginChannel(this);
        }
    }

    public boolean checkIfBungee() {
        boolean isPaper;
        boolean isVelocity = false;
        try {
            Class.forName("io.papermc.paper.entity");
            isPaper = true;
        } catch (ClassNotFoundException e) {
            isPaper = false;
        }

        if (isPaper) {
            try {
                isVelocity = getServer().spigot().getPaperConfig().getConfigurationSection("proxies").getConfigurationSection("velocity").getBoolean("enabled");
            } catch (NullPointerException ignored) { }
        }

        return getServer().spigot().getConfig().getConfigurationSection("settings").getBoolean("bungeecord") || isVelocity;
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

    // TODO: Deal with nullables here.
    @Override
    public Server getAbstractServer() {
        return Bukkit.getServer();
    }

    @Override
    public Player getAbstractPlayerByUUID(UUID uuid) {
        return Bukkit.getPlayer(uuid);
    }

    @Override
    public Player getAbstractPlayerByName(String name) {
        return Bukkit.getPlayer(name);
    }

    @Override
    public UUID getAbstractPlayerUUID(Player player) {
        return player.getUniqueId();
    }

    @Override
    public InetAddress getAbstractPlayerInetAddress(Player player) {
        return player.getAddress().getAddress();
    }

    @Override
    public void getAbstractPluginMessaging(UUID player, String identifier, byte[] data) {
        if (Bukkit.getPlayer(player) != null) {
            Bukkit.getPlayer(player).sendPluginMessage(this, identifier, data);
        }
    }

    @Override
    public Server getAbstractConnection(UUID player) {
        return Bukkit.getPlayer(player).getServer();
    }

    @Override
    public void sendToAbstractLogger(String data) {
        Bukkit.getLogger().info(data);
    }

    @Override
    public int getConnectedPlayers(UUID player) {
        return Bukkit.getPlayer(player).getServer().getOnlinePlayers().size();
    }

    @Override
    public Omnibus getAbstractOmnibus() {
        return getOmnibus();
    }

    @Override
    public ServerType getAbstractServerType() {
        return ServerType.BACKEND;
    }

    @Override
    public void addToAbstractServerCache(PunishmentPlayerType type, Boolean value) {
        SimpleVoiceBans.getMuteCache().put(type, value);
    }

}

