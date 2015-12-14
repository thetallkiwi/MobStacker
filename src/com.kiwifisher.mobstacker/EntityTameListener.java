package com.kiwifisher.mobstacker;

import org.bukkit.ChatColor;
import org.bukkit.entity.Ageable;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityTameEvent;
import org.bukkit.metadata.FixedMetadataValue;

public class EntityTameListener implements Listener {

    @EventHandler
    public void onEntityTameEvent(EntityTameEvent event) {

        LivingEntity entity = event.getEntity();

        if (entity.hasMetadata("quantity")) {

            int newQuantity = entity.getMetadata("quantity").get(0).asInt() - 1;

            entity.setCustomName("");

            if (newQuantity > 0) {

                LivingEntity newEntity = (LivingEntity) entity.getLocation().getWorld().spawnEntity(entity.getLocation(), entity.getType());

                if (newEntity instanceof Ageable) {
                    ((Ageable) newEntity).setAge(((Ageable) event.getEntity()).getAge());
                }

                newEntity.setMetadata("quantity", new FixedMetadataValue(MobStacker.plugin, newQuantity));

                if (newQuantity > 1) {

                    String configNaming = MobStacker.plugin.getConfig().getString("stack-naming");
                    configNaming = configNaming.replace("{QTY}", newQuantity + "");
                    configNaming = configNaming.replace("{TYPE}", entity.getType().toString().replace("_", " "));
                    configNaming = ChatColor.translateAlternateColorCodes('&', configNaming);
                    newEntity.setCustomName(configNaming);

                }

            }

        }
    }

}
