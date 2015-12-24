package com.kiwifisher.mobstacker.algorithms.creatures;

import com.kiwifisher.mobstacker.algorithms.Loot;
import com.kiwifisher.mobstacker.algorithms.LootAlgorithm;
import org.bukkit.Material;
import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class ZombieLootAlgorithm implements LootAlgorithm {

    private List<Loot> dropArrayList = new ArrayList<>();

    public ZombieLootAlgorithm() {
        dropArrayList.add(new Loot(Material.ROTTEN_FLESH, 0, 2));
        dropArrayList.add(new Loot(Material.IRON_INGOT, 0, 1));
        dropArrayList.add(new Loot(Material.CARROT, 0, 1));
        dropArrayList.add(new Loot(Material.POTATO, 0, 1));
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
                If loot is a IRON_INGOT, have a 1/12 chance of dropping, otherwise go on to next loot.
                 */
                if (loot.getMaterial() == Material.IRON_INGOT) {

                    int rareDropCheck = new Random().nextInt((12) + 1);

                    if (rareDropCheck != 1) {
                        break;
                    }

                }

                /*
                If loot is a POTATO, have a 1/8 chance of dropping, otherwise go on to next loot.
                 */
                if (loot.getMaterial() == Material.POTATO) {

                    int rareDropCheck = new Random().nextInt((12) + 1);

                    if (rareDropCheck != 1) {
                        break;
                    }

                }

                /*
                If loot is a CARROT, have a 1/10 chance of dropping, otherwise go on to next loot.
                 */
                if (loot.getMaterial() == Material.CARROT) {

                    int rareDropCheck = new Random().nextInt((10) + 1);

                    if (rareDropCheck != 1) {
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
