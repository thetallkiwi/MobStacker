package com.kiwifisher.mobstacker;

import com.google.common.io.ByteStreams;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.mcstats.Metrics;
import java.io.*;

public class MobStacker extends JavaPlugin {

    public static Plugin plugin;
    public static boolean stacking = true;

    final String uid = "%%__USER__%%";
    final String rid = "%%__RESOURCE__%%";
    final String nonce = "%%__NONCE__%%";


    @Override
    public void onEnable() {

        plugin = this;

        log("MobStacker is starting");

        loadResource(this, "config.yml");

        try {
            Metrics metrics = new Metrics(this);
            metrics.start();
            log("Connected to metrics");
        } catch (IOException e) {
            log("Failed to send Metrics data");
        }

        plugin.getServer().getPluginManager().registerEvents(new MobSpawnListener(), this);
        plugin.getServer().getPluginManager().registerEvents(new MobDeathListener(), this);
        plugin.getServer().getPluginManager().registerEvents(new CustomNamingListener(), this);
        plugin.getServer().getPluginManager().registerEvents(new EntityTameListener(), this);
        plugin.getServer().getPluginManager().registerEvents(new EntityExplodeListener(), this);
        getCommand("mobstacker").setExecutor(new MobStackerCommands());

        log("MobStacker has successfully started!");

    }

    @Override
    public void onDisable() {

        log("Thanks for using MobStacker!");

    }
    
    public static File loadResource(Plugin plugin, String resource) {
        File folder = plugin.getDataFolder();
        if (!folder.exists())
            folder.mkdir();
        File resourceFile = new File(folder, resource);

        try {
            if (!resourceFile.exists() && resourceFile.createNewFile()) {
                try (InputStream in = plugin.getResource(resource);
                     OutputStream out = new FileOutputStream(resourceFile)) {
                    ByteStreams.copy(in, out);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return resourceFile;
    }


    public static void log(String string) {
        plugin.getLogger().info(string);
    }

    public static boolean isStacking() {
        return stacking;
    }

    public static void setStacking(boolean bool) {
        stacking = bool;
    }

}
