package com.kiwifisher.mobstacker;

import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.inventory.ItemStack;

public class CustomNamingListener implements Listener {

    @EventHandler
    public void nameTagListener(PlayerInteractEntityEvent event) {
        if (!MobStacker.plugin.getConfig().getBoolean("stack-custom-named-mobs") && event.getPlayer().getItemInHand().getType() == Material.NAME_TAG) {

            Entity entity = event.getRightClicked();
            ItemStack normalNameTag = new ItemStack(Material.NAME_TAG, 1, (byte) 0);
            ItemStack itemInHand = event.getPlayer().getItemInHand();

            if (entity.hasMetadata("quantity") && itemInHand.getItemMeta().getDisplayName() != normalNameTag.getItemMeta().getDisplayName()) {

                entity.removeMetadata("quantity", MobStacker.plugin);

            }


        }
    }

}
