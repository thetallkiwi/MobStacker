package com.kiwifisher.mobstacker.algorithms;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class PigZombieLootAlgorithm implements LootAlgorithm{

    private List<Loot> itemHashMap = new ArrayList<>();

    public PigZombieLootAlgorithm() {
        itemHashMap.add(new Loot(Material.ROTTEN_FLESH, 0, 1));
        itemHashMap.add(new Loot(Material.GOLD_NUGGET, 0, 1));
    }

    @Override
    public List<Loot> getLootArray() {
        return this.itemHashMap;
    }

    /**
     * Get random loot results for specified number of mobs.
     * @param numberOfMobsWorth number of mobs to get loot for.
     * @return Returns the drops as an array
     */
    @Override
    public List<ItemStack> getRandomLoot(int numberOfMobsWorth) {

        List<ItemStack> drops = new ArrayList<>();

        /*
        Iterate through for amount of mobs
         */
        for (int i = 0; i < numberOfMobsWorth; i++) {

            /*
            Iterate through all the possible loot for each mob.
             */
            for (Loot loot : getLootArray()) {

                /*
                Selects random amount of loot based on definitions in Loot object.
                 */
                int randomNumber = new Random().nextInt((loot.getMaxQuantity() - loot.getMinimumQuantity()) + 1) + loot.getMinimumQuantity();

                /*
                Add the loot to the drops array.
                 */
                drops.add(new ItemStack(loot.getMaterial(), randomNumber));

            }

        }

        return drops;
    }
}
