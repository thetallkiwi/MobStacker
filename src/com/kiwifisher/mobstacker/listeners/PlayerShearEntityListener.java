package com.kiwifisher.mobstacker.listeners;

import com.kiwifisher.mobstacker.utils.StackUtils;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.player.PlayerShearEntityEvent;


public class PlayerShearEntityListener implements Listener {

    @EventHandler
    public void playerShearEntityEvent(PlayerShearEntityEvent event) {

        /*
        If the mob in question is a Sheep, then follow...
         */
        if (event.getEntity() instanceof LivingEntity && (event.getEntity()).getType() == EntityType.SHEEP) {

            LivingEntity entity = (LivingEntity) event.getEntity();

            if (StackUtils.hasRequiredData(entity)) {

                /*
                If there's still a stack, peel it off.
                 */
                if (StackUtils.getStackSize(entity) > 1) {
                    LivingEntity newEntity = StackUtils.peelOff(entity, true);

                } else {
                    /*
                    Try stack the shorn sheep.
                     */
                    StackUtils.attemptToStack(0, entity, CreatureSpawnEvent.SpawnReason.CUSTOM);
                }

            }

        }

    }

}
