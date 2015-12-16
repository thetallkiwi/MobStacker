package com.kiwifisher.mobstacker;

import org.apache.commons.io.FileUtils;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

import java.io.*;
import java.util.Scanner;

public class MobStackerCommands implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] args) {

        if (command.getLabel().equalsIgnoreCase("mobstacker") && commandSender instanceof Player) {

            Player player = (Player) commandSender;

            if (args.length == 0 || (args.length == 1 && args[0].equalsIgnoreCase("help"))) {
                if (MobStacker.usesWorldGuard()) {player.sendMessage(ChatColor.GREEN + "Region Flags: " + ChatColor.YELLOW + "/mobstacker region <regionID> <true || false>"
                        + ChatColor.GRAY + " - True meaning mobs do stack.");}
                player.sendMessage(ChatColor.GREEN + "Toggle: " + ChatColor.YELLOW + "/mobstacker toggle" + ChatColor.GRAY + " - Toggles whether mobs stack globally");
                player.sendMessage(ChatColor.GREEN + "Toggle: " + ChatColor.YELLOW + "/mobstacker reload" + ChatColor.GRAY + " - Reloads the config");

            } else if (args.length == 1 && args[0].equalsIgnoreCase("reload") && player.hasPermission("mobstacker.reload")) {
                MobStacker.plugin.reloadConfig();
                player.sendMessage(ChatColor.GREEN + "Reloaded the config for MobStacker");

            } else if (args.length == 1 && args[0].equalsIgnoreCase("toggle") && player.hasPermission("mobstacker.toggle")) {
                MobStacker.setStacking(!MobStacker.isStacking());

                player.sendMessage(ChatColor.GREEN + "Mob stacking is now " + (MobStacker.isStacking() ? ChatColor.GREEN + "enabled" : ChatColor.RED + "disabled"));

            } else if (args.length == 3 && args[0].equalsIgnoreCase("region") && player.hasPermission("mobstacker.setregions")
                    && (args[2].equalsIgnoreCase("true") || args[2].equalsIgnoreCase("false"))) {

                if (!MobStacker.usesWorldGuard()) {
                    player.sendMessage(ChatColor.RED + "WorldGuard integration is not enabled. Please add WorldGuard to your server");
                    return false;
                }

                String regionName = args[1];

                boolean nowStacking;

                nowStacking = args[2].equalsIgnoreCase("false");

                File excludedFile =  new File(MobStacker.getWorldGuard().getDataFolder() + "/mobstacker-excluded-regions.yml");

                if (MobStacker.getWorldGuard().getRegionManager(player.getWorld()).hasRegion(regionName) && nowStacking) {

                    try {
                        FileUtils.writeStringToFile(excludedFile, (regionName + "\n"), true);
                        player.sendMessage(ChatColor.GREEN + "Mobs are now" + ChatColor.RED + " not " + ChatColor.GREEN + "stacking in " + regionName);

                        MobStacker.updateExcludedRegions();

                    } catch (IOException ex) {
                        MobStacker.log("Couldn't save the region exclusion! Please contact the author of this plugin");
                        player.sendMessage(ChatColor.DARK_RED + "An error occurred. Please report this ErrCode: 1");
                    }

                } else if (!nowStacking) {

                    File tempFile = MobStacker.loadResource(MobStacker.plugin, "temp.yml");

                    boolean removedFromFile = false;

                    try {
                        Scanner reader = new Scanner(excludedFile);

                        while (reader.hasNextLine()) {
                            String line = reader.nextLine();

                            if (!line.contains(regionName)) {
                                FileUtils.writeStringToFile(tempFile, line + "\n", true);
                            } else {
                                removedFromFile = true;
                            }
                        }

                        if (removedFromFile) {
                            FileUtils.copyFile(tempFile, excludedFile);
                            player.sendMessage(ChatColor.GREEN + "Mobs are now stacking in " + regionName);

                        } else {
                            player.sendMessage(ChatColor.RED + "Mobs are already stacking in " + regionName);
                        }

                        FileUtils.forceDelete(tempFile);
                        reader.close();

                        MobStacker.updateExcludedRegions();

                    } catch (IOException e) {
                        player.sendMessage(ChatColor.DARK_RED + "An error occurred. Please report this ErrCode: 2");
                        e.printStackTrace();
                    }


                } else {
                    player.sendMessage(ChatColor.RED + regionName + ChatColor.YELLOW + " isn't a valid region name");
                }

            } else {
                player.sendMessage(ChatColor.RED + "Unrecognised command. Please check /mobstacker help");
            }

        } else if (command.getLabel().equalsIgnoreCase("mobstacker") && commandSender instanceof ConsoleCommandSender) {

            if (args.length == 1 && args[0].equalsIgnoreCase("reload")) {
                MobStacker.plugin.reloadConfig();
                MobStacker.log("Reloaded the config for MobStacker");

            }

            if (args.length == 1 && args[0].equalsIgnoreCase("toggle")) {
                MobStacker.setStacking(!MobStacker.isStacking());

                MobStacker.log("Mob stacking is now " + (MobStacker.isStacking() ? "enabled" : "disabled"));

            }

        }

        return false;
    }

}
