package com.kiwifisher.mobstacker.listeners;

import com.kiwifisher.mobstacker.MobStacker;
import com.kiwifisher.mobstacker.utils.StackUtils;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import org.bukkit.Bukkit;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.metadata.FixedMetadataValue;

public class MobSpawnListener implements Listener {

    private static int searchTime = MobStacker.plugin.getConfig().getInt("seconds-to-try-stack") * 20;
    public static int getSearchTime() {
        return searchTime;
    }
    public static void setSearchTime(int searchTime) {
        MobSpawnListener.searchTime = searchTime;
    }
    private static RegionManager regionManager;

    @EventHandler
    public void mobSpawnEvent(CreatureSpawnEvent event) {

        if (MobStacker.usesWorldGuard()) {

            regionManager = MobStacker.getWorldGuard().getRegionManager(event.getEntity().getWorld());

            for (ProtectedRegion region : regionManager.getApplicableRegions(event.getEntity().getLocation()).getRegions()) {

                if (!MobStacker.regionAllowedToStack(region.getId())) {
                    return;
                }
            }

        }

        if (MobStacker.isStacking()) {

            final LivingEntity spawnedCreature = event.getEntity();
            final CreatureSpawnEvent.SpawnReason spawnReason = event.getSpawnReason();

            boolean entityIsArmorStand = false;

            if (Bukkit.getVersion().contains("1.7")) {
                entityIsArmorStand = false;
            } else if (spawnedCreature.getType() == EntityType.ARMOR_STAND) {
                entityIsArmorStand = true;
            }

            if (MobStacker.plugin.getConfig().getBoolean("stack-mob-type." + spawnedCreature.getType().toString())
                    && MobStacker.plugin.getConfig().getBoolean("stack-spawn-method." + spawnReason) && !entityIsArmorStand && !spawnedCreature.isDead()) {

                spawnedCreature.setMetadata("quantity", new FixedMetadataValue(MobStacker.plugin, 1));

                StackUtils.attemptToStack(getSearchTime(), spawnedCreature, spawnReason);

            }
        }
    }


}
