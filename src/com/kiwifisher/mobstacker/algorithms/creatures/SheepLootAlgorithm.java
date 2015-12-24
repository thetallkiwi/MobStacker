package com.kiwifisher.mobstacker.algorithms.creatures;

import com.kiwifisher.mobstacker.algorithms.Loot;
import com.kiwifisher.mobstacker.algorithms.LootAlgorithm;
import org.bukkit.Material;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Sheep;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.Wool;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class SheepLootAlgorithm implements LootAlgorithm {

    private List<Loot> dropArrayList = new ArrayList<>();

    public SheepLootAlgorithm() {
        dropArrayList.add(new Loot(Material.WOOL, 1, 1));
        dropArrayList.add(new Loot(Material.MUTTON, 1, 2));
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
                Checks sheep is shorn. If it isnt, and the loot is wool, wool is added.
                 */
                if (loot.getMaterial() == Material.WOOL && !((Sheep) entity).isSheared()) {

                    /*
                    Add the coloured wool to the drops array.
                    */
                    Wool wool = new Wool(((Sheep) entity).getColor());
                    ItemStack woolItemStack = wool.toItemStack();
                    woolItemStack.setAmount(randomNumber);
                    drops.add(woolItemStack);
                    break;

                }

                drops.add(new ItemStack(loot.getMaterial(), randomNumber));

            }

        }

        return drops;
    }
}
