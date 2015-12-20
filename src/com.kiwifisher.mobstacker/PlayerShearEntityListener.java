package com.kiwifisher.mobstacker;

import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.player.PlayerShearEntityEvent;


public class PlayerShearEntityListener implements Listener {

    @EventHandler
    public void playerShearEntityEvent(PlayerShearEntityEvent event) {

        if (event.getEntity() instanceof LivingEntity && ((LivingEntity) event.getEntity()).getType() == EntityType.SHEEP) {

            LivingEntity entity = (LivingEntity) event.getEntity();

            if (entity.hasMetadata("quantity")) {

                if (entity.getMetadata("quantity").get(0).asInt() > 1) {
                    LivingEntity newEntity = StackUtils.peelOff(entity, true);

                } else {
                    StackUtils.attemptToStack(0, entity, CreatureSpawnEvent.SpawnReason.CUSTOM);
                }

            }

        }

    }

}
