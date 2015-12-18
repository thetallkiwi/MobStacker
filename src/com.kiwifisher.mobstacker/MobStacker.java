package com.kiwifisher.mobstacker;

import com.google.common.io.ByteStreams;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.LivingEntity;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.mcstats.Metrics;
import java.io.*;
import java.util.ArrayList;
import java.util.Scanner;

public class MobStacker extends JavaPlugin {

    public static Plugin plugin;
    private static boolean stacking = true;
    private static boolean worldGuardEnabled;
    private  static WorldGuardPlugin worldGuard;
    private static ArrayList<String> regionsArray = new ArrayList<>();

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

        worldGuard = initialiseWorldGuard();
        if (worldGuard == null) {
            log("Didn't hook in to WorldGuard");
            setUsesWorldGuard(false);

        } else {
            loadResource(worldGuard, "mobstacker-excluded-regions.yml");
            log("Successfully hooked in to WorldGuard!");
            setUsesWorldGuard(true);
            updateExcludedRegions();
        }

        plugin.getServer().getPluginManager().registerEvents(new MobSpawnListener(), this);
        plugin.getServer().getPluginManager().registerEvents(new MobDeathListener(), this);
        plugin.getServer().getPluginManager().registerEvents(new PlayerEntityInteractListener(), this);
        plugin.getServer().getPluginManager().registerEvents(new EntityTameListener(), this);
        plugin.getServer().getPluginManager().registerEvents(new EntityExplodeListener(), this);
        plugin.getServer().getPluginManager().registerEvents(new PlayerLeashingHandler(), this);

        getCommand("mobstacker").setExecutor(new MobStackerCommands());

        log("MobStacker has successfully started!");

    }

    @Override
    public void onDisable() {

        for (World world : getServer().getWorlds()) {
            for (LivingEntity entity : world.getLivingEntities()) {
                if (entity.hasMetadata("quantity")) {
                    entity.removeMetadata("quantity", plugin);
                    entity.setCustomName("");
                }
            }
        }

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
        } catch (Exception ignored) {}
        return resourceFile;
    }

    public static void updateExcludedRegions() {
        try {
            Scanner scanner = new Scanner(new File(getWorldGuard().getDataFolder() + "/mobstacker-excluded-regions.yml"));
            regionsArray.clear();

            while (scanner.hasNextLine()) {
                String region = scanner.nextLine();
                regionsArray.add(region);
            }

            scanner.close();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        for (String id : regionsArray) {
            log("Loaded in exclusions for region: " + id);
        }


    }

    public static boolean regionAllowedToStack(String regionID) {
        return !regionsArray.contains(regionID);
    }

    private WorldGuardPlugin initialiseWorldGuard() {

        Plugin worldGuard = getServer().getPluginManager().getPlugin("WorldGuard");

        if (worldGuard == null || !(worldGuard instanceof WorldGuardPlugin) || !Bukkit.getVersion().contains("1.8")) {
            return null;
        }

        return (WorldGuardPlugin) worldGuard;

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

    public static void setUsesWorldGuard(boolean status) { worldGuardEnabled = status; }

    public static boolean usesWorldGuard() { return worldGuardEnabled; }

    public static WorldGuardPlugin getWorldGuard() { return worldGuard;}

}
