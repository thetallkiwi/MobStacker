package com.kiwifisher.mobstacker.listeners;

import com.kiwifisher.mobstacker.MobStacker;
import com.kiwifisher.mobstacker.utils.StackUtils;
import org.bukkit.entity.Entity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerLeashEntityEvent;
import org.bukkit.event.player.PlayerUnleashEntityEvent;
import org.bukkit.metadata.FixedMetadataValue;

public class PlayerLeashEntityListener implements Listener {

    @EventHandler
    public void playerLeashEvent(PlayerLeashEntityEvent event) {

        Entity entity = event.getEntity();

        if (entity.hasMetadata("quantity") && entity.getMetadata("quantity").get(0).asInt() > 1 && !MobStacker.plugin.getConfig().getBoolean("leash-whole-stack")) {
            StackUtils.peelOff(entity, false);
        }

    }

    @EventHandler
    public void playerUnleashEvent(PlayerUnleashEntityEvent event) {
        Entity entity = event.getEntity();

        if (!entity.hasMetadata("quantity")) {
            entity.setMetadata("quantity", new FixedMetadataValue(MobStacker.plugin, 1));
        }

    }

}
