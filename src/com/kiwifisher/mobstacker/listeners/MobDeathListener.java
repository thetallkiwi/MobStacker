package com.kiwifisher.mobstacker.listeners;

import com.kiwifisher.mobstacker.MobStacker;
import com.kiwifisher.mobstacker.algorithms.AlgorithmEnum;
import com.kiwifisher.mobstacker.utils.StackUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.material.Colorable;
import org.bukkit.metadata.FixedMetadataValue;
import java.util.List;

public class MobDeathListener implements Listener {

    private MobStacker plugin;

    public MobDeathListener(MobStacker plugin) {
        this.plugin = plugin;
    }

    @EventHandler (ignoreCancelled = true)
    public void mobDeathListener(EntityDeathEvent event) {

        /*
        Checks that we are stacking.
         */
        if (getPlugin().isStacking()) {

            /*
            Get the entity that has just died.
             */
            LivingEntity entity = event.getEntity();

            /*
            If the entity was a stack, follow.
             */
            if (StackUtils.hasRequiredData(entity)) {

                /*
                The list of reasons that will kill the whole stack.
                 */
                List<String> validDeathReasons = getPlugin().getConfig().getStringList("kill-whole-stack-on-death.reasons");

                /*
                If the stack fell to it's death, and we are killing the full stack on death my fall damage, follow.
                 */
                if (event.getEntity().getLastDamageCause() != null && getPlugin().getConfig().getBoolean("kill-whole-stack-on-death.enable") && validDeathReasons.contains(event.getEntity().getLastDamageCause().getCause().name())) {
                    int quantity = StackUtils.getStackSize(entity);

                    /*
                    If we are dropping proportionate loot, then follow.
                     */
                    if (getPlugin().getConfig().getBoolean("kill-whole-stack-on-death.multiply-loot") && quantity > 1) {

                            /*
                            Try to drop the proportionate loot.
                            */
                        try {

                            /**
                             * If it's a magma cube and is too small to drop loot then we don't drop anything
                             */
                            if (entity.getType() == EntityType.MAGMA_CUBE && ((MagmaCube) entity).getSize() <= 1) {
                                return;
                            }

                            if (entity.getType() == EntityType.MAGMA_CUBE && ((MagmaCube) entity).getSize() > 1) {
                                MagmaCube magmaCube = (MagmaCube) entity;
                                magmaCube.setSize(1);
                            }

                            event.getDrops().addAll(AlgorithmEnum.valueOf(entity.getType().name()).getLootAlgorithm().getRandomLoot(entity, quantity - 1));

                            /*
                            If this fails, then log which entity and request it's implementation.
                             */
                        } catch (Exception e) {
                            getPlugin().log(e.getMessage());
                            getPlugin().log(entity.getType().name() + " doesn't have proportionate loot implemented - please request it be added if you need it");

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
                If the mob has died any way other than a listed damage, then follow.
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
                    entity.removeMetadata("quantity", getPlugin());

                    /*
                    If it's a max stack, then don't try stack to anything around it.
                     */
                    if (maxStack) {
                        getPlugin().setSearchTime(-50);
                    }

                    /*
                    Spawn in a replacement entity.
                     */
                    LivingEntity newEntity = (LivingEntity) entity.getLocation().getWorld().spawnEntity(entityLocation, entityType);


                    /**
                     * Mobs spawned by a blacklisted method will nto give mcmmo points.
                     */
                    List<String> noMcmmoSpawnReasons = getPlugin().getConfig().getStringList("no-mcmmo-exp");

                    if (getPlugin().usesmcMMO() && noMcmmoSpawnReasons.contains(entity.getMetadata("spawn-reason").get(0).toString())) {

                        newEntity.setMetadata("mcMMO: Spawned Entity", new FixedMetadataValue(Bukkit.getPluginManager().getPlugin("mcMMO"), true));

                    }

                    /*
                    Spawn method continuity for whole stack.
                     */
                    if (entity.hasMetadata("spawn-reason")) {

                        String oldSpawnReason = entity.getMetadata("spawn-reason").get(0).asString();

                        newEntity.removeMetadata("spawn-reason", getPlugin());

                        newEntity.setMetadata("spawn-reason", new FixedMetadataValue(getPlugin(), oldSpawnReason));

                    }

                    /*
                    If the entity was in fire, or burning, then any remaining ticks left on the previous mob will be passed on to the new one.
                     */
                    if ((entity.getLastDamageCause().getCause() == EntityDamageEvent.DamageCause.FIRE ||
                            entity.getLastDamageCause().getCause() == EntityDamageEvent.DamageCause.FIRE_TICK) &&
                            getPlugin().getConfig().getBoolean("carry-over-fire.enabled")) {

                        if(!getPlugin().getConfig().getBoolean("carry-over-fire.start-new-burn")) {
                            newEntity.setFireTicks(entity.getFireTicks());
                        } else if(getPlugin().getConfig().getBoolean("carry-over-fire.start-new-burn")) {
                            newEntity.setFireTicks(entity.getMaxFireTicks());
                        }

                    }

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
                    getPlugin().getStackUtils().setMaxStack(newEntity, maxStack);
                    getPlugin().getStackUtils().setStackSize(newEntity, newQuantity);

                    /*
                    If there is still a remaining stack, rename it.
                     */
                    if (newQuantity > 1) {

                        getPlugin().getStackUtils().renameStack(newEntity, newQuantity);

                    }

                    /*
                    Set search time back to normal.
                     */
                    getPlugin().setSearchTime(getPlugin().getConfig().getInt("seconds-to-try-stack") * 20);

                }

            }
        }

    }

    public MobStacker getPlugin() {
        return plugin;
    }
}