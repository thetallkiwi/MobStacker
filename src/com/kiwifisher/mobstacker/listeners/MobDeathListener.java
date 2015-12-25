package com.kiwifisher.mobstacker.listeners;

import com.kiwifisher.mobstacker.MobStacker;
import com.kiwifisher.mobstacker.algorithms.AlgorithmEnum;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
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

    @EventHandler (ignoreCancelled = true)
    public void mobDeathListener(EntityDeathEvent event) {

        if (MobStacker.isStacking()) {
            LivingEntity entity = event.getEntity();

            if (entity.hasMetadata("quantity") && entity.hasMetadata("max-stack")) {

                if (event.getEntity().getLastDamageCause().getCause() == EntityDamageEvent.DamageCause.FALL && MobStacker.plugin.getConfig().getBoolean("kill-whole-stack-on-fall-death.enable") &&
                        entity.hasMetadata("quantity")) {
                    int quantity = entity.getMetadata("quantity").get(0).asInt();

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

                List<Entity> nearbyEntities = entity.getNearbyEntities(MobStacker.plugin.getConfig().getInt("stack-range.x"), MobStacker.plugin.getConfig().getInt("stack-range.y"), MobStacker.plugin.getConfig().getInt("stack-range.z"));

                int stackedEntityQuantity = entity.getMetadata("quantity").get(0).asInt();
                int newQuantity = stackedEntityQuantity - 1;

                Location entityLocation = entity.getLocation();
                EntityType entityType = entity.getType();
                boolean maxStack = entity.hasMetadata("max-stack") && entity.getMetadata("max-stack").get(0).asBoolean();

                if (newQuantity > 0) {

                    entity.removeMetadata("quantity", MobStacker.plugin);

                    if (maxStack) {
                        MobSpawnListener.setSearchTime(-1);
                    }

                    LivingEntity newEntity = (LivingEntity) entity.getLocation().getWorld().spawnEntity(entityLocation, entityType);

                    if (newEntity instanceof Ageable) {
                        ((Ageable) newEntity).setAge(((Ageable) event.getEntity()).getAge());
                    }

                    if (newEntity instanceof Colorable) {
                        ((Colorable) newEntity).setColor(((Colorable) event.getEntity()).getColor());
                    }

                    if (newEntity instanceof Sheep) {
                        ((Sheep) newEntity).setSheared(((Sheep) event.getEntity()).isSheared());
                    }

                    newEntity.setMetadata("quantity", new FixedMetadataValue(MobStacker.plugin, newQuantity));
                    newEntity.setMetadata("max-stack", new FixedMetadataValue(MobStacker.plugin, maxStack));

                    if (newQuantity > 1) {

                        String configNaming = MobStacker.plugin.getConfig().getString("stack-naming");
                        configNaming = configNaming.replace("{QTY}", newQuantity + "");
                        configNaming = configNaming.replace("{TYPE}", entity.getType().toString().replace("_", " "));
                        configNaming = ChatColor.translateAlternateColorCodes('&', configNaming);
                        newEntity.setCustomName(configNaming);

                    }

                    MobSpawnListener.setSearchTime(MobStacker.plugin.getConfig().getInt("seconds-to-try-stack") * 20);

                }

            }
        }

    }

}
