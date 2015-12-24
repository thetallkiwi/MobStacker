package com.kiwifisher.mobstacker.algorithms;

import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public interface LootAlgorithm {

    public abstract List<Loot> getLootArray();

    public abstract List<ItemStack> getRandomLoot(LivingEntity entity, int numberOfMobsWorth);

}
