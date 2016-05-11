package com.kiwifisher.mobstacker.listeners;

import com.kiwifisher.mobstacker.MobStacker;
import com.kiwifisher.mobstacker.utils.StackUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.world.ChunkLoadEvent;
import org.bukkit.event.world.ChunkUnloadEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ChunkChangeListener implements Listener {

    private MobStacker plugin;

    public ChunkChangeListener(MobStacker p) {
        this.plugin = p;
    }

    public MobStacker getPlugin() {
        return this.plugin;
    }

    @EventHandler
    public void mobUnloadEvent(ChunkUnloadEvent event) {

        for (Entity entity : event.getChunk().getEntities()) {

            if (StackUtils.hasRequiredData(entity)) {

                int quantity = StackUtils.getStackSize((LivingEntity) entity);
                entity.setCustomName(quantity + "-" + MobStacker.RELOAD_UUID + "-" + entity.getMetadata("spawn-reason").get(0).asString());

            }

        }

    }

    @EventHandler
    public void mobLoadEvent(ChunkLoadEvent event) {

        // Check if loading stacks and stacking is enabled in this world.
        if (!getPlugin().getConfig().getBoolean("load-existing-stacks.enabled") || !getPlugin().getStackUtils().isStackable(event.getWorld())) {
            return;
        }

        // Get acceptable mob types
        List<String> types = getPlugin().getConfig().getStringList("load-existing-stacks.mob-types");

        // Check if any mob types are acceptable
        if (types.isEmpty()) {
            return;
        }

        getPlugin().getStackUtils().reviveStacks(event.getChunk().getEntities());

    }


}
