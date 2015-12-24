package com.kiwifisher.mobstacker.algorithms.creatures;

import com.kiwifisher.mobstacker.algorithms.Loot;
import com.kiwifisher.mobstacker.algorithms.LootAlgorithm;
import org.bukkit.Material;
import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class CaveSpiderLootAlgorithm implements LootAlgorithm {

    private List<Loot> dropArrayList = new ArrayList<>();

    public CaveSpiderLootAlgorithm() {
        dropArrayList.add(new Loot(Material.STRING, 0, 2));
        dropArrayList.add(new Loot(Material.SPIDER_EYE, 0, 1));
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
            for (Loot loot : getLootArray()) {

                /*
                Selects random amount of loot based on definitions in Loot object.
                 */
                int randomNumber = new Random().nextInt((loot.getMaxQuantity() - loot.getMinimumQuantity()) + 1) + loot.getMinimumQuantity();

                /*
                If loot is a SPIDER_EYE, have a 1/3 chance of dropping, otherwise go on to next loot.
                 */
                if (loot.getMaterial() == Material.SPIDER_EYE) {

                    int rareDropCheck = new Random().nextInt((3) + 1);

                    if (rareDropCheck == 1) {
                        break;
                    }

                }

                /*
                Add the loot to the drops array.
                 */
                drops.add(new ItemStack(loot.getMaterial(), randomNumber));

            }

        }

        return drops;
    }
}
