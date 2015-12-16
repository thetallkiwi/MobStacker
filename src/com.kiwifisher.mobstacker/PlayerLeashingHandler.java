package com.kiwifisher.mobstacker;

import org.bukkit.entity.Entity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerLeashEntityEvent;
import org.bukkit.event.player.PlayerUnleashEntityEvent;
import org.bukkit.metadata.FixedMetadataValue;

public class PlayerLeashingHandler implements Listener {

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
