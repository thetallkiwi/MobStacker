package com.kiwifisher.mobstacker.listeners;

import com.kiwifisher.mobstacker.MobStacker;
import com.kiwifisher.mobstacker.utils.StackUtils;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityExplodeEvent;

public class EntityExplodeListener implements Listener {

    private final MobStacker plugin;

    public EntityExplodeListener(MobStacker plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void entityExplodeListener(EntityExplodeEvent event) {

        /*
        Quick return conditions: Entity is not a LivingEntity or is not stacked.
        */
        if (!(event.getEntity() instanceof LivingEntity) || !StackUtils.hasRequiredData(event.getEntity())) {
            return;
        }

        LivingEntity entity = ((LivingEntity) event.getEntity());

        /*
        If there is a LivingEntity exploding and we have kill full stack set to false, then execute this block.
         */
        if (!getPlugin().getConfig().getBoolean("exploding-creeper-kills-stack")) {

            /*
            Removes one for the chap that just blew up.
             */
            int newQuantity = StackUtils.getStackSize(entity) - 1;

            /*
            If a stack still remains then follow.
             */
            if (newQuantity > 0) {

                /*
                Spawn in a new entity to replace the old stack.
                 */
                LivingEntity newEntity = (LivingEntity) entity.getLocation().getWorld().spawnEntity(entity.getLocation(), entity.getType());

                /*
                Set the stacks new size.
                 */
                getPlugin().getStackUtils().setStackSize(newEntity, newQuantity);

                /*
                If a stack is larger than one, then give it the appropriate name.
                 */
                if (newQuantity > 1) {

                    getPlugin().getStackUtils().renameStack(newEntity, newQuantity);

                }

            }

            /*
            If config is set to kill the full stack AND amplify explosions, then follow.
             */
        } else if (getPlugin().getConfig().getBoolean("magnify-stack-explosion.enable")) {

            /*
            Get how many entities are exploding
             */
            int quantity = StackUtils.getStackSize(entity);

            /*
            Set there to be only one mob in the stack so when it blows up, it dies, giving the illusion that all have died.
             */
            getPlugin().getStackUtils().setStackSize(entity, 1);

            /*
            If the number of creepers in the stack is greater than the max explosion size, set the explosion size to the max.
             */
            if (quantity > getPlugin().getConfig().getInt("magnify-stack-explosion.max-creeper-explosion-size")) {
                quantity = getPlugin().getConfig().getInt("magnify-stack-explosion.max-creeper-explosion-size");
            }

            /*
            Create this bad ass explosion where the creeper was.
             */
            event.getLocation().getWorld().createExplosion(event.getLocation(), quantity + 1);

        }

    }

    public MobStacker getPlugin() {
        return plugin;
    }
}
