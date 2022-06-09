package com.sweetmimike.perfectmobfarm.config;

import net.minecraftforge.common.ForgeConfigSpec;

public class CommonConfigs {

    public static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();
    public static final ForgeConfigSpec SPECS;

    public static final ForgeConfigSpec.ConfigValue<Integer> IRON_MOB_FARM_COOLDOWN;
    public static final ForgeConfigSpec.ConfigValue<Integer> GOLD_MOB_FARM_COOLDOWN;
    public static final ForgeConfigSpec.ConfigValue<Integer> DIAMOND_MOB_FARM_COOLDOWN;
    public static final ForgeConfigSpec.ConfigValue<Integer> EMERALD_MOB_FARM_COOLDOWN;
    public static final ForgeConfigSpec.ConfigValue<Integer> NETHERITE_MOB_FARM_COOLDOWN;
    public static final ForgeConfigSpec.ConfigValue<Integer> MOB_SHARD_KILL_NEEDED;


    static {
        BUILDER.push("Perfect Mob farm Configs");

        IRON_MOB_FARM_COOLDOWN = BUILDER.comment("Time needed by an Iron Mob farm to generate loot (in ticks).").define("Iron Cooldown", 800);
        GOLD_MOB_FARM_COOLDOWN = BUILDER.comment("Time needed by a Gold Mob farm to generate loot (in ticks).").define("Gold Cooldown", 400);
        DIAMOND_MOB_FARM_COOLDOWN = BUILDER.comment("Time needed by a Diamond Mob farm to generate loot (in ticks).").define("Diamond Cooldown", 200);
        EMERALD_MOB_FARM_COOLDOWN = BUILDER.comment("Time needed by an Emerald Mob farm to generate loot (in ticks).").define("Emerald Cooldown", 100);
        NETHERITE_MOB_FARM_COOLDOWN = BUILDER.comment("Time needed by a Netherite Mob farm to generate loot (in ticks).").define("Netherite Cooldown", 40);
        MOB_SHARD_KILL_NEEDED = BUILDER.comment("Number of kills needed to complete a mob shard.").define("Kill Needed", 10);


        BUILDER.pop();
        SPECS = BUILDER.build();
    }
}
