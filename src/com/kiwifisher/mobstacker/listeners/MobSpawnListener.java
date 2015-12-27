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
import java.util.List;

public class MobSpawnListener implements Listener {

    private static int searchTime = MobStacker.plugin.getConfig().getInt("seconds-to-try-stack") * 20;
    public static int getSearchTime() {
        return searchTime;
    }
    public static void setSearchTime(int searchTime) {
        MobSpawnListener.searchTime = searchTime;
    }

    @EventHandler
    public void mobSpawnEvent(CreatureSpawnEvent event) {

        final LivingEntity spawnedCreature = event.getEntity();
        final CreatureSpawnEvent.SpawnReason spawnReason = event.getSpawnReason();

        /*
        Make sure it isn't a bloody armour stand. Why the hell are these LivingEntities?
         */
        boolean entityIsArmorStand = false;

        /*
        If version used it 1.7 then there are no armour stands, so leave it false.
        If version use is 1.8, check if it's an armour stand.
         */
        if (Bukkit.getVersion().contains("1.7")) {
            entityIsArmorStand = false;
        } else if (spawnedCreature.getType() == EntityType.ARMOR_STAND) {
            entityIsArmorStand = true;
        }

        /*
        If we're using world guard, check if the mob is in a region. If mob isn't allowed ot stack there, stop here. Don't check armour stands.
         */
        if (MobStacker.usesWorldGuard() && !entityIsArmorStand) {

            RegionManager regionManager = MobStacker.getWorldGuard().getRegionManager(event.getEntity().getWorld());

            for (ProtectedRegion region : regionManager.getApplicableRegions(event.getEntity().getLocation()).getRegions()) {

                if (!MobStacker.regionAllowedToStack(region.getId())) {
                    return;
                }
            }

        }

        /*
        If the mob is in a valid region, or WG isn't enabled, and we have mobs stacking, then...
         */
        if (MobStacker.isStacking()) {

            /*
            Check that this mob is allowed to stack and that it isn't dead for some reason.
             */
            if (MobStacker.plugin.getConfig().getBoolean("stack-mob-type." + spawnedCreature.getType().toString())
                    && MobStacker.plugin.getConfig().getBoolean("stack-spawn-method." + spawnReason) && !entityIsArmorStand && !spawnedCreature.isDead()) {

                /*
                Set stack size to 1 and max stack to false;
                 */
                StackUtils.setStackSize(spawnedCreature, 1);
                StackUtils.setMaxStack(spawnedCreature, false);

                /*
                Check for black listed worlds.
                 */
                List<String> worldBlackList = MobStacker.plugin.getConfig().getStringList("blacklist-world");

                /*
                Make sure search time is positive and that the mob isn't in a blacklisted world, then try to stack it.
                 */
                if (getSearchTime() > 0 && !worldBlackList.contains(spawnedCreature.getWorld().getName().toLowerCase())) {
                    StackUtils.attemptToStack(getSearchTime(), spawnedCreature, spawnReason);
                }

            }
        }
    }


}
