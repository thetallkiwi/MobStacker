package com.kiwifisher.mobstacker;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.SheepRegrowWoolEvent;

public class SheepRegrowWoolListener implements Listener {

    @EventHandler
    public void regrowEvent(SheepRegrowWoolEvent event) {

        StackUtils.attemptToStack(0, event.getEntity(), CreatureSpawnEvent.SpawnReason.CUSTOM);

    }

}
