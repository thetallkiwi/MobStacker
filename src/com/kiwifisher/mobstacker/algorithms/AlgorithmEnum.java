package com.kiwifisher.mobstacker.algorithms;

import org.bukkit.entity.EntityType;

public enum AlgorithmEnum {

    BLAZE (new BlazeLootAlgorithm());

    private LootAlgorithm lootAlgorithm;

    AlgorithmEnum(LootAlgorithm lootAlgorithm) {
        this.lootAlgorithm = lootAlgorithm;
    }

    public LootAlgorithm getLootAlgorithm() {
        return this.lootAlgorithm;
    }

}
