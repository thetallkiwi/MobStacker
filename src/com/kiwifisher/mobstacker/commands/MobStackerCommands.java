package com.kiwifisher.mobstacker.commands;

import com.kiwifisher.mobstacker.MobStacker;
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

    private MobStacker plugin;

    public MobStackerCommands(MobStacker plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] args) {

        if (command.getLabel().equalsIgnoreCase("mobstacker") && commandSender instanceof Player) {

            Player player = (Player) commandSender;

            if (args.length == 0 || (args.length == 1 && args[0].equalsIgnoreCase("help"))) {
                if (getPlugin().usesWorldGuard()) {player.sendMessage(ChatColor.GREEN + "Region Flags: " + ChatColor.YELLOW + "/mobstacker region <regionID> <true || false>"
                        + ChatColor.GRAY + " - True meaning mobs do stack.");}
                player.sendMessage(ChatColor.GREEN + "Toggle: " + ChatColor.YELLOW + "/mobstacker toggle" + ChatColor.GRAY + " - Toggles whether mobs stack globally");
                player.sendMessage(ChatColor.GREEN + "Toggle: " + ChatColor.YELLOW + "/mobstacker reload" + ChatColor.GRAY + " - Reloads the config");
                player.sendMessage(ChatColor.GREEN + "KillAll: " + ChatColor.YELLOW + "/mobstacker killall" + ChatColor.GRAY + " - Removes all stacks in all worlds");
                return true;

            } else if (args.length == 1 && args[0].equalsIgnoreCase("reload") && player.hasPermission("mobstacker.reload")) {
                getPlugin().reloadConfig();
                player.sendMessage(ChatColor.GREEN + "Reloaded the config for MobStacker");
                return true;

            } else if (args.length == 1 && args[0].equalsIgnoreCase("toggle") && player.hasPermission("mobstacker.toggle")) {
                getPlugin().setStacking(!getPlugin().isStacking());

                player.sendMessage(ChatColor.GREEN + "Mob stacking is now " + (getPlugin().isStacking() ? ChatColor.GREEN + "enabled" : ChatColor.RED + "disabled"));
                return true;

            } else if (args.length == 3 && args[0].equalsIgnoreCase("region") && player.hasPermission("mobstacker.setregions")
                    && (args[2].equalsIgnoreCase("true") || args[2].equalsIgnoreCase("false"))) {

                if (!getPlugin().usesWorldGuard()) {
                    player.sendMessage(ChatColor.RED + "WorldGuard integration is not enabled. Please add WorldGuard to your server");
                    return false;
                }

                String regionName = args[1];

                boolean nowStacking;

                nowStacking = args[2].equalsIgnoreCase("false");

                File excludedFile =  new File(getPlugin().getWorldGuard().getDataFolder() + "/mobstacker-excluded-regions.yml");

                if (getPlugin().getWorldGuard().getRegionManager(player.getWorld()).hasRegion(regionName) && nowStacking) {

                    try {
                        FileUtils.writeStringToFile(excludedFile, (regionName + "\n"), true);
                        player.sendMessage(ChatColor.GREEN + "Mobs are now" + ChatColor.RED + " not " + ChatColor.GREEN + "stacking in " + regionName);

                        getPlugin().updateExcludedRegions();

                    } catch (IOException ex) {
                        getPlugin().log("Couldn't save the region exclusion! Please contact the author of this plugin");
                        player.sendMessage(ChatColor.DARK_RED + "An error occurred. Please report this ErrCode: 1");
                    }

                } else if (!nowStacking) {

                    File tempFile = getPlugin().loadResource(getPlugin(), "temp.yml");

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

                        getPlugin().updateExcludedRegions();

                    } catch (IOException e) {
                        player.sendMessage(ChatColor.DARK_RED + "An error occurred. Please report this ErrCode: 2");
                        e.printStackTrace();
                    }


                } else {
                    player.sendMessage(ChatColor.RED + regionName + ChatColor.YELLOW + " isn't a valid region name");
                }

                return true;

            } if (args.length == 1 && args[0].equalsIgnoreCase("killall") && player.hasPermission("mobstacker.killall")) {

                getPlugin().removeAllStacks();
                player.sendMessage(ChatColor.GREEN + "All stacks were successfully removed");
                return true;

            } else {
                player.sendMessage(ChatColor.RED + "Unrecognised command. Please check /mobstacker help");
            }

        } else if (command.getLabel().equalsIgnoreCase("mobstacker") && commandSender instanceof ConsoleCommandSender) {

            if (args.length == 1 && args[0].equalsIgnoreCase("reload")) {
                getPlugin().reloadConfig();
                getPlugin().log("Reloaded the config for MobStacker");
                return true;

            }

            if (args.length == 1 && args[0].equalsIgnoreCase("toggle")) {
                getPlugin().setStacking(!getPlugin().isStacking());

                getPlugin().log("Mob stacking is now " + (getPlugin().isStacking() ? "enabled" : "disabled"));
                return true;

            }

        }

        return false;
    }

    private MobStacker getPlugin() {
        return this.plugin;
    }

}
