package com.kiwifisher.mobstacker.listeners;

import com.kiwifisher.mobstacker.MobStacker;
import com.kiwifisher.mobstacker.utils.StackUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.world.ChunkLoadEvent;
import org.bukkit.event.world.ChunkUnloadEvent;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ChunkChangeListener implements Listener {

    private MobStacker plugin;

    public ChunkChangeListener(MobStacker p) {
        this.plugin = p;
    }

    public MobStacker getPlugin() {
        return this.plugin;
    }

    @EventHandler
    public void mobUnloadEvent(ChunkUnloadEvent event) {

        for (Entity entity : event.getChunk().getEntities()) {

            if (StackUtils.hasRequiredData(entity)) {

                int quantity = StackUtils.getStackSize((LivingEntity) entity);
                entity.setCustomName(quantity + "-" + MobStacker.RELOAD_UUID);

            }

        }

    }

    @EventHandler
    public void mobLoadEvent(ChunkLoadEvent event) {

        // Check if loading stacks and stacking is enabled in this world.
        if (!getPlugin().getConfig().getBoolean("load-existing-stacks.enabled") || !getPlugin().getStackUtils().isStackable(event.getWorld())) {
            return;
        }

        // Get acceptable mob types
        List<String> types = getPlugin().getConfig().getStringList("load-existing-stacks.mob-types");

        // Check if any mob types are acceptable
        if (types.isEmpty()) {
            return;
        }

        /*
         * Assemble a regular expression for name matching.
         *
         * Note that the pattern is very fiddly. I'd prefer to use named captures as there's no
         * guarantee of order. However, since we don't actually need to check the entity type -
         * that's optional security - I've opted to not capture the name group at all, meaning all
         * capture groups are the quantity group and should be identical. You also can't have
         * multiple named captures with the same name, which would occur if someone configured the
         * quantity to appear twice, etc. If we do want to check entity type, it's possible (though
         * somewhat a nuisance) to set up named captures by using a replaceFirst with the named
         * capture, then a replace with the same pattern, just not named or potentially even
         * non-capturing.
         */
        String type = getPlugin().getConfig().getString("stack-naming");

//        if (!type.contains("{QTY}")) {
//            // Quantity is not stored, we can't figure out how many there are.
//            return;
//        }

        Pattern pattern = Pattern.compile(MobStacker.RELOAD_UUID);

        for (Entity entity : event.getChunk().getEntities()) {

            // Check for a custom name and an approved type. If not, not an existing stack.
            if (entity.getCustomName() == null || !types.contains(entity.getType().name())) {
                continue;
            }

            // Check if the entity can stack.
            if (!getPlugin().getStackUtils().isStackable(entity, null, true)) {
                continue;
            }

            // Ensure we have a match with the name pattern.
            Matcher matcher = pattern.matcher(entity.getCustomName());
            if (!entity.getCustomName().contains(MobStacker.RELOAD_UUID)) {
                continue;
            }

            // Cast to LivingEntity. This is safe, checked in isStackable.
            LivingEntity living = (LivingEntity) entity;

            // Parse stack size from matched group (see long comment above for group selection reasoning).
            int stackSize;

            try {
                String quantityString = entity.getCustomName().substring(0, entity.getCustomName().length() - (MobStacker.RELOAD_UUID.length() + 1)); //One extra for the hyphen
                stackSize = Integer.valueOf(quantityString);
            } catch (NumberFormatException e) {
                // There should be no way to hit this block given the regex, but it's better safe than sorry.
                continue;
            }

            // Get max stack size for type.
            int maxStackSize = getPlugin().getConfig().getInt(("max-stack-sizes.") + entity.getType().toString(), 0);

            // Set metadata.
            getPlugin().getStackUtils().setStackSize(living, stackSize);
            getPlugin().getStackUtils().setMaxStack(living, maxStackSize > 0 && maxStackSize <= stackSize);
            getPlugin().getStackUtils().renameStack(living, stackSize);

        }

    }


}
