package com.kiwifisher.mobstacker;

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

    @EventHandler
    public void mobSpawnEvent(CreatureSpawnEvent event) {

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
                    && MobStacker.plugin.getConfig().getBoolean("stack-spawn-method." + spawnReason) && !entityIsArmorStand) {

                spawnedCreature.setMetadata("quantity", new FixedMetadataValue(MobStacker.plugin, 1));

                StackUtils.attemptToStack(getSearchTime(), spawnedCreature, spawnReason);

            }
        }
    }


}
