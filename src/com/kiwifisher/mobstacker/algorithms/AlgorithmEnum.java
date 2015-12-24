package com.kiwifisher.mobstacker.algorithms;

import org.bukkit.entity.EntityType;

public enum AlgorithmEnum {

    BLAZE (new BlazeLootAlgorithm()),
    PIG_ZOMBIE(new PigZombieLootAlgorithm());

    private LootAlgorithm lootAlgorithm;

    AlgorithmEnum(LootAlgorithm lootAlgorithm) {
        this.lootAlgorithm = lootAlgorithm;
    }

    public LootAlgorithm getLootAlgorithm() {
        return this.lootAlgorithm;
    }

}
