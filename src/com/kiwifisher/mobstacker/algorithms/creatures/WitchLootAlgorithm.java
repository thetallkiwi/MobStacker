package com.kiwifisher.mobstacker.algorithms.creatures;

import com.kiwifisher.mobstacker.algorithms.Loot;
import com.kiwifisher.mobstacker.algorithms.LootAlgorithm;
import org.bukkit.Material;
import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class WitchLootAlgorithm implements LootAlgorithm {

    private List<Loot> dropArrayList = new ArrayList<>();

    public WitchLootAlgorithm() {
        /*
        Stick has been added twice as there is twice the chance of getting sticks with witch deaths.
         */
        dropArrayList.add(new Loot(Material.GLASS_BOTTLE, 0, 2));
        dropArrayList.add(new Loot(Material.GLOWSTONE_DUST, 0, 2));
        dropArrayList.add(new Loot(Material.SULPHUR, 0, 2));
        dropArrayList.add(new Loot(Material.REDSTONE, 0, 2));
        dropArrayList.add(new Loot(Material.SPIDER_EYE, 0, 2));
        dropArrayList.add(new Loot(Material.SUGAR, 0, 2));
        dropArrayList.add(new Loot(Material.STICK, 0, 2));
        dropArrayList.add(new Loot(Material.STICK, 0, 2));
    }

    @Override
    public int getExp() {
        return 0;
    }

    @Override
    public List<Loot> getLootArray() {
        return this.dropArrayList;
    }

    /**
     * Get random loot results for specified number of mobs.
     * @param numberOfMobsWorth number of mobs to get loot for.
     * @return Returns the drops as an array
     */
    @Override
    public List<ItemStack> getRandomLoot(LivingEntity entity, int numberOfMobsWorth) {

        List<ItemStack> drops = new ArrayList<>();

        /*
        Iterate through for amount of mobs
         */
        for (int i = 0; i < numberOfMobsWorth; i++) {

            /*
            Iterate through all the possible loot for each mob.
             */
            for (int j = 0; j < 3; j++) {


                /*
                Get a random loot from the array. Sticks have twice the change of being chosen as described by minecraft mechanics.
                 */
                int randomLoot = new Random().nextInt(getLootArray().size());

                /*
                Assigns the loot.
                 */
                Loot loot = getLootArray().get(randomLoot);

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
