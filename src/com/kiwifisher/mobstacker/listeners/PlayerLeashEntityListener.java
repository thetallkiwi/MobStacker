package com.kiwifisher.mobstacker.listeners;

import com.kiwifisher.mobstacker.MobStacker;
import com.kiwifisher.mobstacker.utils.StackUtils;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.PlayerLeashEntityEvent;
import org.bukkit.event.player.PlayerUnleashEntityEvent;
import org.bukkit.metadata.FixedMetadataValue;

public class PlayerLeashEntityListener implements Listener {

    @EventHandler
    public void playerLeashEvent(PlayerLeashEntityEvent event) {

        /*
        Get the entity being leashed
         */
        LivingEntity entity = (LivingEntity) event.getEntity();

        /*
        If configs are permitting, and is a valid stack, peel off the one we leashed and set it's restack to false so it doesn't
        just jump back in to the stack we got it from.
         */
        if (StackUtils.hasRequiredData(entity) && StackUtils.getStackSize(entity) > 1 && !MobStacker.plugin.getConfig().getBoolean("leash-whole-stack")) {
            StackUtils.peelOff(entity, false);
        }

    }

    @EventHandler
    public void playerUnleashEvent(PlayerUnleashEntityEvent event) {
        LivingEntity entity = (LivingEntity) event.getEntity();

        /*
        If we unleash a mob that has no data (Because it was removed when we leashed it), then add the data back in
         */
        if (!StackUtils.hasRequiredData(entity)) {
            entity.setMetadata("quantity", new FixedMetadataValue(MobStacker.plugin, 1));
            entity.setMetadata("max-stack", new FixedMetadataValue(MobStacker.plugin, false));
            StackUtils.attemptToStack(MobSpawnListener.getSearchTime(), entity, CreatureSpawnEvent.SpawnReason.CUSTOM);
        }

    }

}
