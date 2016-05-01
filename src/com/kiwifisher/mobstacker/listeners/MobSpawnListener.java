package com.kiwifisher.mobstacker.listeners;

import com.kiwifisher.mobstacker.MobStacker;

import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason;
import org.bukkit.event.world.ChunkLoadEvent;
import org.bukkit.metadata.FixedMetadataValue;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MobSpawnListener implements Listener {

    private final MobStacker plugin;

    public MobSpawnListener(MobStacker plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void mobLoadEvent(ChunkLoadEvent event) {

        // Check if stacking is enabled in this world.
        if (!isStackable(event.getWorld())) {
            return;
        }

        /*
         * Assemble a regular expression for name matching.
         * 
         * Note that the pattern is very fiddly. I'd prefer to use named captures as there's no
         * guarantee of order. However, since we don't actually need to check the entity type -
         * that's optional security - I've opted to not capture the name group at all, meaning all
         * capture groups are the quantity group and should be identical. You also can't have
         * multiple named captures with the same name, which would occur if someone configured the
         * quantity to appear twice, etc. If we do want to check entity type, it's possible (though
         * somewhat a nuisance) to set up named captures by using a replaceFirst with the named
         * capture, then a replace with the same pattern, just not named or potentially even
         * non-capturing.
         */
        String type = getPlugin().getConfig().getString("stack-naming");

        if (!type.contains("{QTY}")) {
            // Quantity is not stored, we can't figure out how many there are.
            return;
        }

        type = ChatColor.translateAlternateColorCodes('&', type);
        StringBuilder builder = new StringBuilder("\\Q")
                .append(type.replace("{QTY}", "\\E([0-9]+)\\Q").replace("{TYPE}", "\\E[A-Z ]+\\Q"))
                .append("\\E");
        Pattern pattern = Pattern.compile(builder.toString());

        for (Entity entity : event.getChunk().getEntities()) {

            // Check for a custom name. If not found, not an existing stack.
            if (entity.getCustomName() == null) {
                continue;
            }

            // Check if the entity can stack.
            if (!isStackable(entity, null, true)) {
                continue;
            }

            // Ensure we have a match with the name pattern.
            Matcher matcher = pattern.matcher(entity.getCustomName());
            if (!matcher.find()) {
                continue;
            }

            // Cast to LivingEntity. This is safe, checked in isStackable.
            LivingEntity living = (LivingEntity) entity;

            // Parse stack size from matched group (see long comment above for group selection reasoning).
            int stackSize;
            try {
                stackSize = Integer.valueOf(matcher.group(1));
            } catch (NumberFormatException e) {
                // There should be no way to hit this block given the regex, but it's better safe than sorry.
                continue;
            }

            // Get max stack size for type.
            int maxStackSize = getPlugin().getConfig().getInt(("max-stack-sizes.") + entity.getType().toString(), 0);

            // Set metadata.
            getPlugin().getStackUtils().setStackSize(living, stackSize);
            getPlugin().getStackUtils().setMaxStack(living, maxStackSize > 0 && maxStackSize <= stackSize);

        }

    }

    @EventHandler
    public void mobSpawnEvent(CreatureSpawnEvent event) {

        final LivingEntity spawnedCreature = event.getEntity();
        final CreatureSpawnEvent.SpawnReason spawnReason = event.getSpawnReason();

        // Check if the spawned entity is stackable.
        if (!isStackable(spawnedCreature, spawnReason, false)) {
            return;
        }

        /*
        Set stack size to 1 and max stack to false;
         */
        getPlugin().getStackUtils().setStackSize(spawnedCreature, 1);
        getPlugin().getStackUtils().setMaxStack(spawnedCreature, false);

        /*
        Check if the mob is from a spawner, and add the tag so that when it dies we can have continuity for
        nerf-spawner-mobs
         */
        if (!spawnedCreature.hasMetadata("spawn-reason")) {

            spawnedCreature.setMetadata("spawn-reason", new FixedMetadataValue(getPlugin(), spawnReason));

        }

        /*
        Make sure search time is positive and try to stack.
         */
        if (getPlugin().getSearchTime() >= -20) {
            getPlugin().getStackUtils().attemptToStack(getPlugin().getSearchTime(), spawnedCreature, spawnReason);
        }

    }

    /**
     * Check if MobStacker is disabled in a world.
     * 
     * @param world the world to check
     * @return true if MobStacker is stacking in the World
     */
    private boolean isStackable(World world) {

        // Check if stacking is enabled.
        if (!getPlugin().isStacking()) {
            return false;
        }

        // Check if the world is in the blacklist.
        if (getPlugin().getConfig().getStringList("blacklist-world").contains(world.getName().toLowerCase())) {
            return false;
        }

        return true;
    }

    /**
     * Check if an Entity can stack.
     * 
     * @param entity the Entity
     * @param reason the SpawnReason, or null if the chunk is being loaded.
     * @param worldChecked
     * @return
     */
    private boolean isStackable(Entity entity, SpawnReason reason, boolean worldChecked) {

        // Check world if not already checked.
        if (!worldChecked && !isStackable(entity.getWorld())) {
            return false;
        }

        // Ensure we have a living LivingEntity.
        if (!(entity instanceof LivingEntity) || entity.isDead()) {
            return false;
        }

        // No armor stands.
        if (!Bukkit.getVersion().contains("1.7") && entity.getType() == EntityType.ARMOR_STAND) {
            return false;
        }

        // Check if mob type is allowed to stack.
        if (!getPlugin().getConfig().getBoolean("stack-mob-type." + entity.getType().toString())) {
            return false;
        }

        // Check if spawn reason is allowed to cause stacking.
        if (reason != null && !getPlugin().getConfig().getBoolean("stack-spawn-method." + reason)) {
            return false;
        }

        // If we're using WorldGuard, check if the mob is in a region with stacking disabled.
        if (getPlugin().usesWorldGuard()) {
            RegionManager regionManager = getPlugin().getWorldGuard().getRegionManager(entity.getWorld());

            for (ProtectedRegion region : regionManager.getApplicableRegions(entity.getLocation()).getRegions()) {
                if (!getPlugin().regionAllowedToStack(region.getId())) {
                    return false;
                }
            }
        }

        return true;

    }

    public MobStacker getPlugin() {
        return plugin;
    }
}
