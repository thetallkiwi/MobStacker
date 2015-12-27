package com.kiwifisher.mobstacker.listeners;

import com.kiwifisher.mobstacker.utils.StackUtils;
import org.bukkit.entity.Ageable;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Sheep;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityTameEvent;
import org.bukkit.material.Colorable;

public class EntityTameListener implements Listener {

    @EventHandler
    public void onEntityTameEvent(EntityTameEvent event) {

        /*
        Get the entity being tamed.
         */
        LivingEntity entity = event.getEntity();

        if (StackUtils.hasRequiredData(entity)) {

            /*
            Get the new quantity. Current size, less one (One being tamed)
             */
            int newQuantity = StackUtils.getStackSize(entity) - 1;

            /*
            Clear the name of the entity that was just tamed.
             */
            entity.setCustomName("");

            /*
            If there are any mobs remaining in the stack, then peel em off to form a new stack.
             */
            if (newQuantity > 0) {

                LivingEntity newEntity = StackUtils.peelOff(entity, false);

                /*
                If there was an age in question, then assign it.
                 */
                if (newEntity instanceof Ageable) {
                    ((Ageable) newEntity).setAge(((Ageable) event.getEntity()).getAge());
                }

                /*
                 * Yes I know the following aren't possible, but if someone uses a plugin that allows for taming of other mobs through NMS,
                 * then this will account for that.
                 */

                /*
                If the stack had a colour, assign it.
                 */
                if (newEntity instanceof Colorable) {
                    ((Colorable) newEntity).setColor(((Colorable) event.getEntity()).getColor());
                }

                /*
                If it was a sheep, keep it's sheared status.
                 */
                if (newEntity instanceof Sheep) {
                    ((Sheep) newEntity).setSheared(((Sheep) event.getEntity()).isSheared());
                }

            }

        }
    }

}
