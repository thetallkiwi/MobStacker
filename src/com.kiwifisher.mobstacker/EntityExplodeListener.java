package com.kiwifisher.mobstacker;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Ageable;
import org.bukkit.entity.Explosive;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.metadata.FixedMetadataValue;

class EntityExplodeListener implements Listener {

    @EventHandler
    public void entityExplodeListener(EntityExplodeEvent event) {

        if (event.getEntity() instanceof LivingEntity && !MobStacker.plugin.getConfig().getBoolean("exploding-creeper-kills-stack") && !MobStacker.plugin.getConfig().getBoolean("magnify-stack-explosion.enable")) {

            LivingEntity entity = ((LivingEntity) event.getEntity());

            if (entity.hasMetadata("quantity")) {

                int newQuantity = entity.getMetadata("quantity").get(0).asInt() - 1;

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

        } else if (event.getEntity() instanceof LivingEntity && MobStacker.plugin.getConfig().getBoolean("exploding-creeper-kills-stack") &&
                MobStacker.plugin.getConfig().getBoolean("magnify-stack-explosion.enable")) {

            LivingEntity entity = ((LivingEntity) event.getEntity());
            int quantity = entity.getMetadata("quantity").get(0).asInt();
            entity.setMetadata("quantity", new FixedMetadataValue(MobStacker.plugin, 1));

            if (quantity > MobStacker.plugin.getConfig().getInt("magnify-stack-explosion.max-creeper-explosion-size")) {
                quantity = MobStacker.plugin.getConfig().getInt("magnify-stack-explosion.max-creeper-explosion-size");
            }

            event.getLocation().getWorld().createExplosion(event.getLocation(), quantity + 1);

        }

    }

}
