package com.kiwifisher.mobstacker.algorithms;

import com.kiwifisher.mobstacker.algorithms.creatures.*;

public enum AlgorithmEnum {

    BLAZE (new BlazeLootAlgorithm()),
    WITCH (new WitchLootAlgorithm()),
    PIG_ZOMBIE(new PigZombieLootAlgorithm()),
    CAVE_SPIDER(new CaveSpiderLootAlgorithm()),
    CHICKEN (new ChickenLootAlgorithm()),
    COW (new CowLootAlgorithm()),
    CREEPER (new CreeperLootAlgorithm()),
    ENDERMAN (new EndermanLootAlgorithm()),
    HORSE (new HorseLootAlgorithm()),
    IRON_GOLEM (new IronGolemLootAlgorithm()),
    MUSHROOM_COW (new MushroomCowLootAlgorithm()),
    PIG (new PigLootAlgorithm()),
    RABBIT (new RabbitLootAlgorithm()),
    SHEEP (new SheepLootAlgorithm()),
    SKELETON (new SkeletonLootAlgorithm()),
    SLIME (new SlimeLootAlgorithm()),
    SNOW_GOLEM (new SnowGolemLootAlgorithm()),
    SPIDER (new SpiderLootAlgorithm()),
    SQUID (new SquidLootAlgorithm()),
    GUARDIAN (new GuardianLootAlgorithm()),
    ZOMBIE (new ZombieLootAlgorithm());


    private LootAlgorithm lootAlgorithm;

    private int exp;

    AlgorithmEnum(LootAlgorithm lootAlgorithm) {
        this.lootAlgorithm = lootAlgorithm;
        this.exp = lootAlgorithm.getExp();
    }

    public LootAlgorithm getLootAlgorithm() {
        return this.lootAlgorithm;
    }
    public int getExp() { return this.exp; }

}
