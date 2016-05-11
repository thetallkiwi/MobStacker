package com.kiwifisher.mobstacker.listeners;

import com.kiwifisher.mobstacker.MobStacker;

import com.kiwifisher.mobstacker.utils.StackUtils;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason;
import org.bukkit.event.world.ChunkLoadEvent;
import org.bukkit.metadata.FixedMetadataValue;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MobSpawnListener implements Listener {

    private final MobStacker plugin;

    public MobSpawnListener(MobStacker plugin) {
        this.plugin = plugin;
    }


    @EventHandler
    public void mobSpawnEvent(CreatureSpawnEvent event) {

        final LivingEntity spawnedCreature = event.getEntity();
        final CreatureSpawnEvent.SpawnReason spawnReason = event.getSpawnReason();

        // Check if the spawned entity is stackable.
        if (!getPlugin().getStackUtils().isStackable(spawnedCreature, spawnReason, false)) {
            return;
        }

        /*
        Set stack size to 1 and max stack to false;
         */
        getPlugin().getStackUtils().setStackSize(spawnedCreature, 1);
        getPlugin().getStackUtils().setMaxStack(spawnedCreature, false);

        /*
        Check if the mob is from a spawner, and add the tag so that when it dies we can have continuity for
        nerf-spawner-mobs
         */
        if (!spawnedCreature.hasMetadata("spawn-reason")) {

            spawnedCreature.setMetadata("spawn-reason", new FixedMetadataValue(getPlugin(), spawnReason));

        }

        /*
        Make sure search time is positive and try to stack.
         */
        if (getPlugin().getSearchTime() >= -20) {
            getPlugin().getStackUtils().attemptToStack(getPlugin().getSearchTime(), spawnedCreature, spawnReason);
        }

    }





    public MobStacker getPlugin() {
        return plugin;
    }
}
