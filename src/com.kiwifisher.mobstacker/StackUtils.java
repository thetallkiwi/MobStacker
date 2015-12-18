package com.kiwifisher.mobstacker;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.*;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.material.Colorable;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.scheduler.BukkitRunnable;
import java.util.List;

public class StackUtils {

    public static void attemptToStack(final int searchTime, final LivingEntity entity, final CreatureSpawnEvent.SpawnReason spawnReason) {

        new BukkitRunnable() {

            int count = 0;
            int limit = searchTime / 10;
            boolean flop = true;

            @Override
            public void run() {

                count++;

                if (count <= limit) {

                    List<Entity> nearbyEntities = entity.getNearbyEntities(MobStacker.plugin.getConfig().getInt("stack-range.x"), MobStacker.plugin.getConfig().getInt("stack-range.y"), MobStacker.plugin.getConfig().getInt("stack-range.z"));

                    if (nearbyEntities.size() > 0) {
                        for (Entity nearbyEntity : nearbyEntities) {

                            if (nearbyEntity instanceof LivingEntity && !nearbyEntity.isDead() && stackEntities((LivingEntity) nearbyEntity, entity, spawnReason)) {
                                count = limit + 1;
                                cancel();
                                break;
                            }
                        }
                    }

                    cancel();

                } else if(searchTime == 0) {

                    List<Entity> nearbyEntities = entity.getNearbyEntities(MobStacker.plugin.getConfig().getInt("stack-range.x"), MobStacker.plugin.getConfig().getInt("stack-range.y"), MobStacker.plugin.getConfig().getInt("stack-range.z"));

                    for (Entity nearbyEntity : nearbyEntities) {

                        if (nearbyEntity instanceof LivingEntity && !nearbyEntity.isDead() && stackEntities((LivingEntity) nearbyEntity, entity, spawnReason)) {
                            cancel();
                            break;
                        }
                    }

                    cancel();

                } else if(searchTime == -20) {

                    if (flop) {
                        List<Entity> nearbyEntities = entity.getNearbyEntities(MobStacker.plugin.getConfig().getInt("stack-range.x"), MobStacker.plugin.getConfig().getInt("stack-range.y"), MobStacker.plugin.getConfig().getInt("stack-range.z"));

                        for (Entity nearbyEntity : nearbyEntities) {

                            if (nearbyEntity instanceof LivingEntity && !nearbyEntity.isDead() &&  stackEntities((LivingEntity) nearbyEntity, entity, spawnReason)) {
                                cancel();
                                break;
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


    public static boolean stackEntities(LivingEntity existingEntity, LivingEntity newEntity, CreatureSpawnEvent.SpawnReason newEntitySpawnReason) {

        boolean stackByAge = MobStacker.plugin.getConfig().getBoolean("stack-by-age");
        boolean stackLeashed = MobStacker.plugin.getConfig().getBoolean("stack-leashed-mobs");
        boolean protectTamed = MobStacker.plugin.getConfig().getBoolean("protect-tamed");
        boolean separateColour = MobStacker.plugin.getConfig().getBoolean("separate-stacks-by-color");

        if (newEntity.getType() == existingEntity.getType() && MobStacker.plugin.getConfig().getBoolean("stack-mob-type." + newEntity.getType().toString())
                && MobStacker.plugin.getConfig().getBoolean("stack-spawn-method." + newEntitySpawnReason) &&
                existingEntity.hasMetadata("quantity") && !existingEntity.isDead()) {

            if (existingEntity.getLocation().getBlockY() > newEntity.getLocation().getBlockY() && MobStacker.plugin.getConfig().getBoolean("stack-mobs-down.enable") &&
                    MobStacker.plugin.getConfig().getList("stack-mobs-down.mob-types").contains(newEntity.getType().toString())) {
                if (stackEntities(newEntity, existingEntity, newEntitySpawnReason)) {
                    return true;
                }

            } else if (newEntity.getType() == existingEntity.getType() && !existingEntity.isDead() && !newEntity.isDead() && newEntity.hasMetadata("quantity") &&
                    (stackLeashed || !existingEntity.isLeashed() && !newEntity.isLeashed()) &&
                    (!stackByAge || !(newEntity instanceof Ageable) || (((Ageable) newEntity).isAdult() == ((Ageable) existingEntity).isAdult())) &&
                    (!protectTamed || !(newEntity instanceof Tameable) || (!((Tameable) newEntity).isTamed() && !((Tameable) existingEntity).isTamed())) &&
                    (!separateColour || !(newEntity instanceof Colorable) || (((Colorable) newEntity).getColor() == ((Colorable) existingEntity).getColor()))) {

                int stackedEntityQuantity = existingEntity.getMetadata("quantity").get(0).asInt();
                int newQuantity = stackedEntityQuantity + newEntity.getMetadata("quantity").get(0).asInt();

                existingEntity.setMetadata("quantity", new FixedMetadataValue(MobStacker.plugin, newQuantity));
                String configNaming = MobStacker.plugin.getConfig().getString("stack-naming");
                configNaming = configNaming.replace("{QTY}", newQuantity + "");
                configNaming = configNaming.replace("{TYPE}", existingEntity.getType().toString().replace("_", " "));
                configNaming = ChatColor.translateAlternateColorCodes('&', configNaming);
                existingEntity.setCustomName(configNaming);
                existingEntity.setCustomNameVisible(true);

                newEntity.remove();

                return true;
            }

            return false;

        }

        return false;

    }

    public static LivingEntity peelOff(Entity mobStack, boolean restackable) {

        Location location = mobStack.getLocation();
        EntityType type = mobStack.getType();
        int newQuantity = mobStack.getMetadata("quantity").get(0).asInt() - 1;

        MobSpawnListener.setSearchTime(0);

        LivingEntity newEntity = (LivingEntity) location.getWorld().spawnEntity(location, type);

        if (mobStack instanceof Ageable) {
            ((Ageable) newEntity).setAge(((Ageable) mobStack).getAge());
        }

        if (mobStack instanceof Colorable) {
            ((Colorable) newEntity).setColor(((Colorable) mobStack).getColor());
        }

        newEntity.setMetadata("quantity", new FixedMetadataValue(MobStacker.plugin, newQuantity));

        if (restackable) {
            mobStack.setMetadata("quantity", new FixedMetadataValue(MobStacker.plugin, 1));
            mobStack.setCustomName("");
            mobStack.setCustomNameVisible(true);
        } else {
            mobStack.setCustomName("");
            mobStack.setCustomNameVisible(true);
            mobStack.removeMetadata("quantity", MobStacker.plugin);
        }

        if (newQuantity > 1) {
            String configNaming = MobStacker.plugin.getConfig().getString("stack-naming");
            configNaming = configNaming.replace("{QTY}", newQuantity + "");
            configNaming = configNaming.replace("{TYPE}", newEntity.getType().toString().replace("_", " "));
            configNaming = ChatColor.translateAlternateColorCodes('&', configNaming);
            newEntity.setCustomName(configNaming);
            newEntity.setCustomNameVisible(true);

            if (restackable) {
                attemptToStack(0, ((LivingEntity) mobStack), CreatureSpawnEvent.SpawnReason.CUSTOM);
            }
        }

        MobSpawnListener.setSearchTime(MobStacker.plugin.getConfig().getInt("seconds-to-try-stack") * 20);

        return newEntity;

    }

}
