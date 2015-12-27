package com.kiwifisher.mobstacker.listeners;

import com.kiwifisher.mobstacker.MobStacker;
import com.kiwifisher.mobstacker.algorithms.AlgorithmEnum;
import com.kiwifisher.mobstacker.utils.StackUtils;
import org.bukkit.Location;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.material.Colorable;

public class MobDeathListener implements Listener {

    @EventHandler (ignoreCancelled = true)
    public void mobDeathListener(EntityDeathEvent event) {

        /*
        Checks that we are stacking.
         */
        if (MobStacker.isStacking()) {

            /*
            Get the entity that has just died.
             */
            LivingEntity entity = event.getEntity();

            /*
            If the entity was a stack, follow.
             */
            if (StackUtils.hasRequiredData(entity)) {

                /*
                If the stack fell to it's death, and we are killing the full stack on death my fall damage, follow.
                 */
                if (event.getEntity().getLastDamageCause().getCause() == EntityDamageEvent.DamageCause.FALL && MobStacker.plugin.getConfig().getBoolean("kill-whole-stack-on-fall-death.enable")) {
                    int quantity = StackUtils.getStackSize(entity);

                    /*
                    If we are dropping proportionate loot, then follow.
                     */
                    if (MobStacker.plugin.getConfig().getBoolean("kill-whole-stack-on-fall-death.multiply-loot") && quantity > 1) {

                            /*
                            Try to drop the proportionate loot.
                            */
                        try {
                            event.getDrops().addAll(AlgorithmEnum.valueOf(entity.getType().name()).getLootAlgorithm().getRandomLoot(entity, quantity - 1));

                            /*
                            If this fails, then log which entity and request it's implementation.
                             */
                        } catch (Exception e) {
                            MobStacker.log(e.getMessage());
                            MobStacker.log(entity.getType().name() + " doesn't have proportionate loot implemented - please request it be added if you need it");

                            /*
                            Regardless of it failing, drop the proportionate EXP.
                             */
                        } finally {
                            event.setDroppedExp(event.getDroppedExp() * quantity);
                        }
                    }

                    return;
                }

                /*
                If the mob has died any way other than fall damage, then follow.
                 */
                int newQuantity = StackUtils.getStackSize(entity) - 1;

                Location entityLocation = entity.getLocation();
                EntityType entityType = entity.getType();

                /*
                Check if the entity is or was ever a max stack for continuity.
                 */
                boolean maxStack = entity.getMetadata("max-stack").get(0).asBoolean();

                /*
                If there is a remaining stack...
                 */
                if (newQuantity > 0) {

                    /*
                    Remove it's quantity data so other mobs don't try stack to it is .isDead() fails.
                     */
                    entity.removeMetadata("quantity", MobStacker.plugin);

                    /*
                    If it's a max stack, then don't try stack to anything around it.
                     */
                    if (maxStack) {
                        MobSpawnListener.setSearchTime(-1);
                    }

                    /*
                    Spawn in a replacement entity.
                     */
                    LivingEntity newEntity = (LivingEntity) entity.getLocation().getWorld().spawnEntity(entityLocation, entityType);

                    /*
                    Assign all attributes so the mob looks the same.
                     */
                    if (newEntity instanceof Ageable) {
                        ((Ageable) newEntity).setAge(((Ageable) event.getEntity()).getAge());
                    }

                    if (newEntity instanceof Colorable) {
                        ((Colorable) newEntity).setColor(((Colorable) event.getEntity()).getColor());
                    }

                    if (newEntity instanceof Sheep) {
                        ((Sheep) newEntity).setSheared(((Sheep) event.getEntity()).isSheared());
                    }

                    /*
                    Set new meta data
                     */
                    StackUtils.setMaxStack(newEntity, maxStack);
                    StackUtils.setStackSize(newEntity, newQuantity);

                    /*
                    If there is still a remaining stack, rename it.
                     */
                    if (newQuantity > 1) {

                        StackUtils.renameStack(newEntity, newQuantity);

                    }

                    /*
                    Set search time back to normal.
                     */
                    MobSpawnListener.setSearchTime(MobStacker.plugin.getConfig().getInt("seconds-to-try-stack") * 20);

                }

            }
        }

    }

}
