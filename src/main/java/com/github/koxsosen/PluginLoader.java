package com.github.koxsosen;

import de.maxhenkel.voicechat.api.BukkitVoicechatService;
import org.bukkit.plugin.java.JavaPlugin;
import space.arim.libertybans.api.LibertyBans;
import space.arim.omnibus.Omnibus;
import space.arim.omnibus.OmnibusProvider;

public class PluginLoader extends JavaPlugin {

    public static LibertyBans api;

    private SimpleVoiceBans simpleVoiceBans;

    @Override
    public void onEnable() {
        BukkitVoicechatService bukkitVoicechatService = getServer().getServicesManager().load(BukkitVoicechatService.class);
        if (bukkitVoicechatService != null) {
            simpleVoiceBans = new SimpleVoiceBans();
            bukkitVoicechatService.registerPlugin(simpleVoiceBans);
            getLogger().info("Successfully registered!");

        } else {
            getLogger().info("Service is null!");
        }
        Omnibus omnibus = OmnibusProvider.getOmnibus();
        api = omnibus.getRegistry().getProvider(LibertyBans.class).orElseThrow();
        getLogger().info("Registered the LibertyBans api.");
    }

    @Override
    public void onDisable() {
        if (simpleVoiceBans != null) {
            getServer().getServicesManager().unregister(simpleVoiceBans);
            getLogger().info("Successfully unregistered!");
        }
    }
}

