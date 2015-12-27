package com.kiwifisher.mobstacker.utils;

import com.kiwifisher.mobstacker.MobStacker;
import com.kiwifisher.mobstacker.listeners.MobSpawnListener;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.*;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.material.Colorable;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.scheduler.BukkitRunnable;
import java.util.List;

/**
 * The main guts of this plugin. All the stacking, peeling, searching and renaming is done here.
 */
public class StackUtils {

    public static void attemptToStack(final int searchTime, final LivingEntity entity, final CreatureSpawnEvent.SpawnReason spawnReason) {

        new BukkitRunnable() {

            int count = 0;
            int limit = searchTime / 10;
            boolean flop = true;

            @Override
            public void run() {

                /*
                If search time is > 0, then this block is run, incrementing count.
                 */
                if (count <= limit) {

                    count++;

                    List<Entity> nearbyEntities = entity.getNearbyEntities(MobStacker.plugin.getConfig().getInt("stack-range.x"), MobStacker.plugin.getConfig().getInt("stack-range.y"), MobStacker.plugin.getConfig().getInt("stack-range.z"));

                    /*
                    Get all nearby entities.
                     */
                    if (nearbyEntities.size() > 0) {
                        for (Entity nearbyEntity : nearbyEntities) {

                            int maxStackSize = 0;

                            /*
                            If a max stack size is imposed, assign it to the integer maxStackSize.
                             */
                            if (MobStacker.plugin.getConfig().contains(("max-stack-sizes.") + nearbyEntity.getType().toString())) {
                                maxStackSize = MobStacker.plugin.getConfig().getInt("max-stack-sizes." + nearbyEntity.getType().toString());
                            }


                            /*
                            If the mobs are allowed to be stacking, then stack 'em
                             */
                            if (nearbyEntity instanceof LivingEntity && !nearbyEntity.isDead() &&
                                    (maxStackSize == 0 || (nearbyEntity.hasMetadata("quantity") && nearbyEntity.getMetadata("quantity").get(0).asInt() < maxStackSize))) {

                                /*
                                If the mobs successfully stack, then stop searching, else keep trying to stack.
                                 */
                                if (stackEntities((LivingEntity) nearbyEntity, entity, spawnReason)) {
                                    count = limit + 1;
                                    cancel();
                                    break;
                                }

                            }
                        }
                    }

                    /*
                    If search time is 0 that means mobs will only try stack when they spawn.
                     */
                } else if(searchTime == 0) {

                    List<Entity> nearbyEntities = entity.getNearbyEntities(MobStacker.plugin.getConfig().getInt("stack-range.x"), MobStacker.plugin.getConfig().getInt("stack-range.y"), MobStacker.plugin.getConfig().getInt("stack-range.z"));

                    /*
                    Get the nearby entities.
                     */
                    for (Entity nearbyEntity : nearbyEntities) {

                        int maxStackSize = 0;

                        /*
                        If a max stack size is imposed, assign it to the integer maxStackSize.
                         */
                        if (MobStacker.plugin.getConfig().contains(("max-stack-sizes.") + nearbyEntity.getType().toString())) {
                            maxStackSize = MobStacker.plugin.getConfig().getInt("max-stack-sizes." + nearbyEntity.getType().toString());
                        }

                        /*
                        If the mobs are allowed to be stacking, then stack 'em
                         */
                        if (nearbyEntity instanceof LivingEntity && !nearbyEntity.isDead() &&
                                (maxStackSize == 0 || ( nearbyEntity.hasMetadata("quantity") && nearbyEntity.getMetadata("quantity").get(0).asInt() < maxStackSize))) {

                            /*
                            If the mobs successfully stack, then stop searching, else keep trying to stack.
                             */
                            if (stackEntities((LivingEntity) nearbyEntity, entity, spawnReason)) {
                                cancel();
                                break;
                            }
                        }
                    }

                    cancel();

                    /*
                    If search time is -20, that means in the config search time was set to -1, meaning continuous search mode.
                     */
                } else if(searchTime == -20) {

                    /*
                    The flop boolean was implemented so that the attempts to stack happen every second, rather than every half second. This is just because
                    there will be more mobs searching to stack over time, so it eases up on resources a bit.
                     */
                    if (flop) {

                        /*
                        Gets nearby entities.
                         */
                        List<Entity> nearbyEntities = entity.getNearbyEntities(MobStacker.plugin.getConfig().getInt("stack-range.x"), MobStacker.plugin.getConfig().getInt("stack-range.y"), MobStacker.plugin.getConfig().getInt("stack-range.z"));

                        for (Entity nearbyEntity : nearbyEntities) {

                            int maxStackSize = 0;

                            /*
                            If a max stack size is imposed, assign it to the integer maxStackSize.
                            */
                            if (MobStacker.plugin.getConfig().contains(("max-stack-sizes.") + nearbyEntity.getType().toString())) {
                                maxStackSize = MobStacker.plugin.getConfig().getInt("max-stack-sizes." + nearbyEntity.getType().toString());
                            }

                            /*
                            If the mobs are allowed to be stacking, then stack 'em
                             */
                            if (nearbyEntity instanceof LivingEntity && !nearbyEntity.isDead() &&
                                    (maxStackSize == 0 || ( nearbyEntity.hasMetadata("quantity") && nearbyEntity.getMetadata("quantity").get(0).asInt() < maxStackSize))) {

                                /*
                                If the mobs successfully stack, then stop searching, else keep trying to stack.
                                 */
                                if (stackEntities((LivingEntity) nearbyEntity, entity, spawnReason)) {
                                    cancel();
                                    break;
                                }
                            }

                        }
                    }

                    flop = !flop;

                } else {
                    cancel();
                }

            }

            /*
            Search every 10 ticks (0.5 seconds on a fully performing server.)
             */
        }.runTaskTimer(MobStacker.plugin, 0L, 10L);

    }


    /**
     * Will attempt to stack two entities together taking in to account everything from age and colour to whether the mob is tamed. Returns a boolean
     * representing whether the two mobs were stacked successfully.
     * @param existingEntity The base entity to try stack to.
     * @param newEntity The entity trying to stack. This one is removed if the stack is successful.
     * @param newEntitySpawnReason Spawn reason needed to check if this reason is allowed to stack in the config
     * @return Returns a boolean stating whether the stacks were merged successfully.
     */
    public static boolean stackEntities(LivingEntity existingEntity, LivingEntity newEntity, CreatureSpawnEvent.SpawnReason newEntitySpawnReason) {

        /*
        Booleans representing various config values.
         */
        boolean mobAllowedToStack = MobStacker.plugin.getConfig().getBoolean("stack-mob-type." + newEntity.getType().toString());
        boolean spawnReasonAllowedToStack = MobStacker.plugin.getConfig().getBoolean("stack-spawn-method." + newEntitySpawnReason);

        /*
        Various checks to make sure the mob is allowed to stack.
         */
        if (newEntity.getType() == existingEntity.getType() && mobAllowedToStack && spawnReasonAllowedToStack && hasRequiredData(newEntity) && hasRequiredData(existingEntity)
                && !existingEntity.isDead() && !newEntity.isDead()) {

            /*
            If the entity was previously a max stack then it can't be stacked to.
             */
            if (existingEntity.getMetadata("max-stack").get(0).asBoolean() || newEntity.getMetadata("max-stack").get(0).asBoolean()) {
                return false;
            }

            /*
            If the mob is meant to stack down, and the new entity is lower than the existing entity, then this method will be recursively called
            flipping newEntity and existingEntity as parameters.
             */
            if (existingEntity.getLocation().getBlockY() > newEntity.getLocation().getBlockY() && MobStacker.plugin.getConfig().getBoolean("stack-mobs-down.enable") &&
                    MobStacker.plugin.getConfig().getList("stack-mobs-down.mob-types").contains(newEntity.getType().toString())) {
                if (stackEntities(newEntity, existingEntity, newEntitySpawnReason)) {
                    return true;
                }

                /*
                If the mobs have the same attributes, then attempt to stack.
                 */
            } else if (mobsHaveSameAttributes(newEntity, existingEntity)) {

                int stackedEntityQuantity = getStackSize(existingEntity);
                int newQuantity = stackedEntityQuantity + getStackSize(newEntity);

                /*
                Sets max stack default to false.
                 */
                boolean maxStack = false;

                /*
                If the config say the plugin has a max stack size, then check what it is, and handle it.
                 */
                if (MobStacker.plugin.getConfig().contains(("max-stack-sizes.") + newEntity.getType().toString())) {

                    int maxStackSize = MobStacker.plugin.getConfig().getInt("max-stack-sizes." + newEntity.getType().toString());
                    int remainderSize = 0;

                    /*
                    If the max stack size is less that what is trying to be stacked, then get the remained, and make a different stack.
                     */
                    if (newQuantity > maxStackSize) {
                        remainderSize = newQuantity - maxStackSize;
                        newQuantity = maxStackSize;
                        maxStack = true;

                        /*
                        For every mob over, peel off one at a time until it is the right size.
                         */
                        for (int i = 0; i < remainderSize; i++) {
                            newEntity.getLocation().getWorld().spawnEntity(newEntity.getLocation(), existingEntity.getType());
                        }

                    }

                }

                /*
                If all has gone well, rename and remove the correct mob.
                 */
                renameAndRemove(existingEntity, newEntity, newQuantity, maxStack);

                return true;
            }

            return false;
        }

        return false;
    }

    /**
     * This method takes a stack and peels one off, spawning in a new mob in the same location.
     * @param mobStack The stack to peel off of.
     * @param restackable Should the mob that has peeled off be allowed to stack again?
     * @return Returns the new stack.
     */
    public static LivingEntity peelOff(LivingEntity mobStack, boolean restackable) {

        /*
        Collecting information on the mob.
         */
        Location location = mobStack.getLocation();
        EntityType type = mobStack.getType();
        int newQuantity = mobStack.getMetadata("quantity").get(0).asInt() - 1;

        /*
        Set the search time. If this doesn't happen, shit breaks.
         */
        MobSpawnListener.setSearchTime(0);

        /*
        Spawn the new entity in.
         */
        LivingEntity newEntity = (LivingEntity) location.getWorld().spawnEntity(location, type);

        /*
        If the mob has an age, colour, or is a sheep, then set the new mob to have the same attributes as the one it came from.
         */
        if (mobStack instanceof Ageable) {
            ((Ageable) newEntity).setAge(((Ageable) mobStack).getAge());
        }

        if (mobStack instanceof Colorable) {
            ((Colorable) newEntity).setColor(((Colorable) mobStack).getColor());
        }

        if (mobStack instanceof Sheep) {
            ((Sheep) newEntity).setSheared(((Sheep) mobStack).isSheared());
        }

        /*
        The the new stack size, being one less than the existing as one has been peeled off.
         */
        setStackSize(newEntity, newQuantity);

        /*
        If the mob is restackable, then set all its meta.
         */
        if (restackable) {
            setStackSize(mobStack, 1);
            mobStack.setCustomName("");
            mobStack.setCustomNameVisible(true);

        } else {
            mobStack.setCustomName("");
            mobStack.setCustomNameVisible(true);
            mobStack.removeMetadata("quantity", MobStacker.plugin);
        }

        /*
        If there is still a stack remaining, then follow.
         */
        if (newQuantity > 1) {

            renameStack(mobStack, newQuantity);

            /*
            If it is re-stackable, then attempt to try stack it again.
             */
            if (restackable) {
                attemptToStack(0, (mobStack), CreatureSpawnEvent.SpawnReason.CUSTOM);
            }
        }

        /*
        Set the search time back to normal
         */
        MobSpawnListener.setSearchTime(MobStacker.plugin.getConfig().getInt("seconds-to-try-stack") * 20);

        /*
        Returns the new stack.
         */
        return newEntity;

    }

    /**
     * Returns whether param has required data to try stack
     * @param entity The entity being checked.
     * @return Bool representing if the mob can stack.
     */
    public static boolean hasRequiredData(Entity entity) {
        return entity.hasMetadata("quantity") && entity.hasMetadata("max-stack");
    }

    /**
     * If two mobs have the same attribute in terms of leash status, age, tame status, colour and share status, then this will return true.
     * @param newEntity First entity
     * @param existingEntity Second entity
     * @return Bool representing whether attributes are the same.
     */
    public static boolean mobsHaveSameAttributes(LivingEntity newEntity, LivingEntity existingEntity) {

        boolean stackByAge = MobStacker.plugin.getConfig().getBoolean("stack-by-age");
        boolean stackLeashed = MobStacker.plugin.getConfig().getBoolean("stack-leashed-mobs");
        boolean protectTamed = MobStacker.plugin.getConfig().getBoolean("protect-tamed");
        boolean separateColour = MobStacker.plugin.getConfig().getBoolean("separate-stacks-by-color");
        boolean separateSheared = MobStacker.plugin.getConfig().getBoolean("separate-by-sheared");

        return newEntity.getType() == existingEntity.getType() && !existingEntity.isDead() && !newEntity.isDead() && newEntity.hasMetadata("quantity") &&
                (stackLeashed || !existingEntity.isLeashed() && !newEntity.isLeashed()) &&
                (!stackByAge || !(newEntity instanceof Ageable) || (((Ageable) newEntity).isAdult() == ((Ageable) existingEntity).isAdult())) &&
                (!protectTamed || !(newEntity instanceof Tameable) || (!((Tameable) newEntity).isTamed() && !((Tameable) existingEntity).isTamed())) &&
                (!separateColour || !(newEntity instanceof Colorable) || (((Colorable) newEntity).getColor() == ((Colorable) existingEntity).getColor())) &&
                (!separateSheared || !(newEntity instanceof Sheep) || (((Sheep) newEntity).isSheared() == ((Sheep) existingEntity).isSheared()));
    }

    /**
     * Used to get the number of mobs represented by a stack.
     * @param livingEntity Must be a valid stack or will return 0.
     * @return Returns number of mobs.
     */
    public static int getStackSize(LivingEntity livingEntity) {
        try {
            return livingEntity.getMetadata("quantity").get(0).asInt();
        } catch (NullPointerException e) {
            e.printStackTrace();
        }

        return 0;

    }

    /*
    Sets a stack size in metadata
     */
    public static void setStackSize(LivingEntity entity, int newQuantity) {
        entity.setMetadata("quantity", new FixedMetadataValue(MobStacker.plugin, newQuantity));
    }

    /*
    Renames a new stack and removes and old one. Useful method for when two mobs are merging.
     */
    public static void renameAndRemove(LivingEntity existingEntity, LivingEntity newEntity, int newQuantity, boolean maxStack) {

        existingEntity.setMetadata("quantity", new FixedMetadataValue(MobStacker.plugin, newQuantity));
        existingEntity.setMetadata("max-stack", new FixedMetadataValue(MobStacker.plugin, maxStack));
        String configNaming = MobStacker.plugin.getConfig().getString("stack-naming");
        configNaming = configNaming.replace("{QTY}", newQuantity + "");
        configNaming = configNaming.replace("{TYPE}", existingEntity.getType().toString().replace("_", " "));
        configNaming = ChatColor.translateAlternateColorCodes('&', configNaming);
        existingEntity.setCustomName(configNaming);
        existingEntity.setCustomNameVisible(true);

        newEntity.remove();

    }

    /*
    Sets the mobs custom name based on definitions in config.
     */
    public static void renameStack(LivingEntity stack, int newQuantity) {

        String configNaming = MobStacker.plugin.getConfig().getString("stack-naming");
        configNaming = configNaming.replace("{QTY}", newQuantity + "");
        configNaming = configNaming.replace("{TYPE}", stack.getType().toString().replace("_", " "));
        configNaming = ChatColor.translateAlternateColorCodes('&', configNaming);
        stack.setCustomName(configNaming);
        stack.setCustomNameVisible(true);

    }

    /*
    If the stack is, or has been a max stack this will return true.
     */
    public static boolean isMaxStack(LivingEntity entity) {

        if (entity.hasMetadata("max-stack")) {
            return entity.getMetadata("max-stack").get(0).asBoolean();
        }

        return true;

    }

    public static void setMaxStack(LivingEntity entity, boolean isMaxStack) {
        entity.setMetadata("max-stack", new FixedMetadataValue(MobStacker.plugin, isMaxStack));
    }

}
