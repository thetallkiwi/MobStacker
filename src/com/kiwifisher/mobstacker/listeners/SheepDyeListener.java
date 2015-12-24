package com.kiwifisher.mobstacker.listeners;

import com.kiwifisher.mobstacker.utils.StackUtils;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.SheepDyeWoolEvent;

/**
 * Created by Julian on 20/12/2015.
 */
public class SheepDyeListener implements Listener {

    @EventHandler
    public void sheepDyeEvent(SheepDyeWoolEvent event) {

        Entity entity = event.getEntity();

        if (entity.hasMetadata("quantity")) {

            if (entity.getMetadata("quantity").get(0).asInt() > 1) {
                LivingEntity newEntity = StackUtils.peelOff(entity, true);

            } else {
                StackUtils.attemptToStack(0, ((LivingEntity) entity), CreatureSpawnEvent.SpawnReason.CUSTOM);
            }

        }

    }

}
