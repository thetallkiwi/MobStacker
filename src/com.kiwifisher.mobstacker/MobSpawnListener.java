package com.kiwifisher.mobstacker;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.scheduler.BukkitRunnable;
import java.util.List;

public class MobSpawnListener implements Listener {

    @EventHandler
    public void mobSpawnEvent(CreatureSpawnEvent event) {

        if (MobStacker.isStacking()) {

            LivingEntity spawnedCreature = event.getEntity();
            CreatureSpawnEvent.SpawnReason spawnReason = event.getSpawnReason();

            if (MobStacker.plugin.getConfig().getBoolean("stack-mob-type." + spawnedCreature.getType().toString())
                    && MobStacker.plugin.getConfig().getBoolean("stack-spawn-method." + spawnReason) && spawnedCreature.getType() != EntityType.ARMOR_STAND) {

                spawnedCreature.setMetadata("quantity", new FixedMetadataValue(MobStacker.plugin, 1));

                attemptToStack(MobStacker.plugin.getConfig().getInt("seconds-to-try-stack") * 20, spawnedCreature, spawnReason);

            }
        }
    }


    private void attemptToStack(final int searchTime, final LivingEntity entity, final CreatureSpawnEvent.SpawnReason spawnReason) {

        new BukkitRunnable() {

            int count = 0;
            int limit = searchTime / 10;
            boolean stackByAge = MobStacker.plugin.getConfig().getBoolean("stack-by-age");
            boolean stackLeashed = MobStacker.plugin.getConfig().getBoolean("stack-leashed-mobs");
            boolean protectTamed = MobStacker.plugin.getConfig().getBoolean("protect-tamed");
            boolean flop = true;

            @Override
            public void run() {

                if (count < limit) {

                    List<Entity> nearbyEntities = entity.getNearbyEntities(MobStacker.plugin.getConfig().getInt("stack-range.x"), MobStacker.plugin.getConfig().getInt("stack-range.y"), MobStacker.plugin.getConfig().getInt("stack-range.z"));

                    for (Entity nearbyEntity : nearbyEntities) {

                        if (nearbyEntity.getType() == entity.getType() && !entity.isDead() &&
                                (stackLeashed || stackLeashed == ((LivingEntity) nearbyEntity).isLeashed()) &&
                                (!stackByAge || !(entity instanceof Ageable) || (((Ageable) entity).isAdult() == ((Ageable) nearbyEntity).isAdult())) &&
                                (!protectTamed || !(nearbyEntity instanceof Tameable) || (!((Tameable) nearbyEntity).isTamed() && !((Tameable) entity).isTamed()))) {

                            stackEntities((LivingEntity) nearbyEntity, entity, spawnReason);
                            cancel();
                        }
                    }

                    count++;

                } else if(searchTime == 0) {

                    List<Entity> nearbyEntities = entity.getNearbyEntities(MobStacker.plugin.getConfig().getInt("stack-range.x"), MobStacker.plugin.getConfig().getInt("stack-range.y"), MobStacker.plugin.getConfig().getInt("stack-range.z"));

                    for (Entity nearbyEntity : nearbyEntities) {

                        if (nearbyEntity.getType() == entity.getType() && !entity.isDead() &&
                                (stackLeashed || stackLeashed == ((LivingEntity) nearbyEntity).isLeashed()) &&
                                (!stackByAge || !(entity instanceof Ageable) || (((Ageable) entity).isAdult() == ((Ageable) nearbyEntity).isAdult())) &&
                                (!protectTamed || !(nearbyEntity instanceof Tameable)  || (!((Tameable) nearbyEntity).isTamed() && !((Tameable) entity).isTamed()))) {

                            stackEntities((LivingEntity) nearbyEntity, entity, spawnReason);
                            cancel();

                        }
                    }

                    cancel();

                } else if(searchTime == -20) {

                    if (flop) {
                        List<Entity> nearbyEntities = entity.getNearbyEntities(MobStacker.plugin.getConfig().getInt("stack-range.x"), MobStacker.plugin.getConfig().getInt("stack-range.y"), MobStacker.plugin.getConfig().getInt("stack-range.z"));

                        for (Entity nearbyEntity : nearbyEntities) {

                            if (nearbyEntity.getType() == entity.getType() && !entity.isDead() &&
                                    (stackLeashed || stackLeashed == ((LivingEntity) nearbyEntity).isLeashed()) &&
                                    (!stackByAge || !(entity instanceof Ageable) || (((Ageable) entity).isAdult() == ((Ageable) nearbyEntity).isAdult())) &&
                                    (!protectTamed || !(nearbyEntity instanceof Tameable)  || (!((Tameable) nearbyEntity).isTamed() && !((Tameable) nearbyEntity).isTamed()))) {

                                stackEntities((LivingEntity) nearbyEntity, entity, spawnReason);
                                cancel();

                            }
                        }
                    }

                    flop = !flop;

                } else {
                    cancel();
                }
            }


        }.runTaskTimer(MobStacker.plugin, 0L, 10L);
    }

    private void stackEntities(LivingEntity existingEntity, LivingEntity newEntity, CreatureSpawnEvent.SpawnReason newEntitySpawnReason) {

    if (newEntity.getType() == existingEntity.getType() && MobStacker.plugin.getConfig().getBoolean("stack-mob-type." + newEntity.getType().toString())
                && MobStacker.plugin.getConfig().getBoolean("stack-spawn-method." + newEntitySpawnReason) &&
                existingEntity.getType() != EntityType.ARMOR_STAND && existingEntity.hasMetadata("quantity") && !existingEntity.isDead()) {

            int newQuantity;

            if (existingEntity.getLocation().getBlockY() > newEntity.getLocation().getBlockY() && MobStacker.plugin.getConfig().getBoolean("stack-mobs-down.enable") &&
                    MobStacker.plugin.getConfig().getList("stack-mobs-down.mob-types").contains(newEntity.getType().toString())) {
                stackEntities(newEntity, existingEntity, newEntitySpawnReason);

            } else {

                int stackedEntityQuantity = existingEntity.getMetadata("quantity").get(0).asInt();
                newQuantity = stackedEntityQuantity + newEntity.getMetadata("quantity").get(0).asInt();

                existingEntity.setMetadata("quantity", new FixedMetadataValue(MobStacker.plugin, newQuantity));
                String configNaming = MobStacker.plugin.getConfig().getString("stack-naming");
                configNaming = configNaming.replace("{QTY}", newQuantity + "");
                configNaming = configNaming.replace("{TYPE}", existingEntity.getType().toString().replace("_", " "));
                configNaming = ChatColor.translateAlternateColorCodes('&', configNaming);
                existingEntity.setCustomName(configNaming);
                existingEntity.setCustomNameVisible(true);

                newEntity.remove();
            }

        }

    }

}
