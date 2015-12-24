package com.kiwifisher.mobstacker.algorithms;

import org.bukkit.Material;

public class Loot {

    private Material material;
    private int maxQuantity;
    private int minimumQuantity;

    public Loot(Material material, int minimumQuantity, int maxQuantity) {
        this.material = material;
        this.minimumQuantity = minimumQuantity;
        this.maxQuantity = maxQuantity;
    }

    public Material getMaterial() {
        return material;
    }

    public void setMaterial(Material material) {
        this.material = material;
    }

    public int getMaxQuantity() {
        return maxQuantity;
    }

    public void setMaxQuantity(int maxQuantity) {
        this.maxQuantity = maxQuantity;
    }

    public int getMinimumQuantity() {
        return minimumQuantity;
    }

    public void setMinimumQuantity(int minimumQuantity) {
        this.minimumQuantity = minimumQuantity;
    }
}
