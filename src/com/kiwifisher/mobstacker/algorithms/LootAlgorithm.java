package com.kiwifisher.mobstacker.algorithms;

import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public interface LootAlgorithm {

    public List<Loot> getLootArray();

    public int getExp();

    public List<ItemStack> getRandomLoot(LivingEntity entity, int numberOfMobsWorth);

}
