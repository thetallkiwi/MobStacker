package com.kiwifisher.mobstacker.listeners;

import com.kiwifisher.mobstacker.MobStacker;
import com.kiwifisher.mobstacker.utils.StackUtils;
import org.bukkit.Material;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.inventory.ItemStack;

public class PlayerRenameEntityListener implements Listener {

    private MobStacker plugin;

    public PlayerRenameEntityListener(MobStacker plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void interactEvent(PlayerInteractEntityEvent event) {

        /*
        If a LivingEntity was right clicked with a name tag, and stack custom named mobs is false, then follow.
         */
        if (!getPlugin().getConfig().getBoolean("stack-custom-named-mobs") && event.getPlayer().getItemInHand().getType() == Material.NAME_TAG
                && event.getRightClicked() instanceof LivingEntity) {

            LivingEntity entity = (LivingEntity) event.getRightClicked();;

            /*
            Initialised blank name tag to get default name if it changes in future updates.
             */
            ItemStack normalNameTag = new ItemStack(Material.NAME_TAG, 1, (byte) 0);

            /*
            Get the item the player is holding.
             */
            ItemStack itemInHand = event.getPlayer().getItemInHand();

            /*
            If the creature has the required data and the name tag isn't blank, then follow.
             */
            if (StackUtils.hasRequiredData(entity) && !itemInHand.getItemMeta().getDisplayName().equalsIgnoreCase(normalNameTag.getItemMeta().getDisplayName())) {

                /*
                If there is more than one creature in the stack, then peel one off and don't allow it to stack again.
                 */
                if (StackUtils.getStackSize(entity) > 1) { getPlugin().getStackUtils().peelOff(entity, false); }

            }


        }

    }

    public MobStacker getPlugin() {
        return plugin;
    }
}
