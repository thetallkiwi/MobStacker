package com.kiwifisher.mobstacker;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.inventory.ItemStack;

public class PlayerEntityInteractListener implements Listener {

    @EventHandler
    public void interactEvent(PlayerInteractEntityEvent event) {

        if (!MobStacker.plugin.getConfig().getBoolean("stack-custom-named-mobs") && event.getPlayer().getItemInHand().getType() == Material.NAME_TAG) {

            Entity entity = event.getRightClicked();
            ItemStack normalNameTag = new ItemStack(Material.NAME_TAG, 1, (byte) 0);
            ItemStack itemInHand = event.getPlayer().getItemInHand();

            if (entity.hasMetadata("quantity") && !itemInHand.getItemMeta().getDisplayName().equalsIgnoreCase(normalNameTag.getItemMeta().getDisplayName())) {

                if (entity.getMetadata("quantity").get(0).asInt() > 1) { StackUtils.peelOff(entity, false); }

            }


        }

        if (event.getPlayer().getItemInHand().getType() == Material.INK_SACK && event.getRightClicked().getType() == EntityType.SHEEP) {

            Entity entity = event.getRightClicked();

            if (entity.hasMetadata("quantity")) {

                if (entity.getMetadata("quantity").get(0).asInt() > 1) {
                    LivingEntity newEntity = StackUtils.peelOff(entity, true);

                } else {
                    StackUtils.attemptToStack(0, ((LivingEntity) entity), CreatureSpawnEvent.SpawnReason.CUSTOM);
                }

            }


        }

    }

}
