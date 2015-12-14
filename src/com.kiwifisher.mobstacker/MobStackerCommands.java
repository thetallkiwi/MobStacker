package com.kiwifisher.mobstacker;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.*;
import java.util.logging.Level;

public class MobStackerCommands implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] args) {

        if (command.getLabel().equalsIgnoreCase("mobstacker") && commandSender instanceof Player) {

            Player player = (Player) commandSender;

            if (args.length == 1 && args[0].equalsIgnoreCase("reload") && player.hasPermission("mobstacker.reload")) {
                MobStacker.plugin.reloadConfig();
                player.sendMessage(ChatColor.GREEN + "Reloaded the config for MobStacker");

            }

            if (args.length == 1 && args[0].equalsIgnoreCase("toggle") && player.hasPermission("mobstacker.toggle")) {
                MobStacker.setStacking(!MobStacker.isStacking());

                player.sendMessage(ChatColor.GREEN + "Mob stacking is now " + (MobStacker.isStacking() ? ChatColor.GREEN + "enabled" : ChatColor.RED + "disabled"));

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
