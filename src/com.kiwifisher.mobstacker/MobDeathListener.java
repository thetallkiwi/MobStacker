package com.kiwifisher.mobstacker;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.metadata.FixedMetadataValue;

import java.util.List;

public class MobDeathListener implements Listener {

    boolean stackByAge = MobStacker.plugin.getConfig().getBoolean("stack-by-age");
    boolean stackLeashed = MobStacker.plugin.getConfig().getBoolean("stack-leashed-mobs");
    boolean protectTamed = MobStacker.plugin.getConfig().getBoolean("protect-tamed");



    @EventHandler (ignoreCancelled = true)
    public void mobDeathListener(EntityDeathEvent event) {

        if (MobStacker.isStacking()) {
            LivingEntity entity = event.getEntity();

            if (entity.hasMetadata("quantity")) {

                if (event.getEntity().getLastDamageCause().getCause() == EntityDamageEvent.DamageCause.FALL && MobStacker.plugin.getConfig().getBoolean("kill-whole-stack-on-fall-death")) {
                    return;
                }

                List<Entity> nearbyEntities = entity.getNearbyEntities(MobStacker.plugin.getConfig().getInt("stack-range.x"), MobStacker.plugin.getConfig().getInt("stack-range.y"), MobStacker.plugin.getConfig().getInt("stack-range.z"));

                if (nearbyEntities.size() >= 1) {

                    for (Entity nearbyEntity : nearbyEntities) {

                        if (nearbyEntity.getType() == entity.getType() && !entity.isDead() &&
                                (stackLeashed || stackLeashed == ((LivingEntity) nearbyEntity).isLeashed()) &&
                                (!stackByAge || !(entity instanceof Ageable) || (((Ageable) entity).isAdult() == ((Ageable) nearbyEntity).isAdult())) &&
                                (!protectTamed || !(nearbyEntity instanceof Tameable)  || (nearbyEntity instanceof Tameable && !((Tameable) nearbyEntity).isTamed() && !((Tameable) nearbyEntity).isTamed()))) {

                            nearbyEntity.setMetadata("quantity", new FixedMetadataValue(MobStacker.plugin, entity.getMetadata("quantity").get(0).asInt() - 1 + nearbyEntity.getMetadata("quantity").get(0).asInt()));

                            String configNaming = MobStacker.plugin.getConfig().getString("stack-naming");
                            configNaming = configNaming.replace("{QTY}", nearbyEntity.getMetadata("quantity").get(0).asInt() + "");
                            configNaming = configNaming.replace("{TYPE}", entity.getType().toString().replace("_", " "));
                            configNaming = ChatColor.translateAlternateColorCodes('&', configNaming);
                            nearbyEntity.setCustomName(configNaming);

                            return;

                        }
                    }

                }

                int stackedEntityQuantity = entity.getMetadata("quantity").get(0).asInt();
                int newQuantity = stackedEntityQuantity - 1;

                Location entityLocation = entity.getLocation();
                EntityType entityType = entity.getType();

                if (newQuantity > 0) {

                    LivingEntity newEntity = (LivingEntity) entity.getLocation().getWorld().spawnEntity(entityLocation, entityType);

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

}
