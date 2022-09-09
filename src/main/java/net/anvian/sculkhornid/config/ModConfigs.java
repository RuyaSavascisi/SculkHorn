package net.anvian.sculkhornid.config;

import com.mojang.datafixers.util.Pair;
import net.anvian.sculkhornid.SculkHornMod;

public class ModConfigs {
    public static SimpleConfig CONFIG;
    private static ModConfigProvider configs;
    //sculk horn area
    public static double RADIUS;
    public static int COOLDOWN;
    public static double DAMAGE_EASY;
    public static double DAMAGE_NORMAL;
    public static double DAMAGE_HARD;

    public static int EXPERIENCE_LEVEL;
    public static int REMOVE_EXPERIENCE;

    //sculk horn range
    public static double DAMAGE;
    public static int RANGE_EXPERIENCE_LEVEL;
    public static int RANGE_REMOVE_EXPERIENCE;
    public static int DISTANCE;

    public static void registerConfigs() {
        configs = new ModConfigProvider();
        createConfigs();

        CONFIG = SimpleConfig.of(SculkHornMod.MOD_ID + "config").provider(configs).request();//create config file

        assignConfigs();
    }

    private static void createConfigs() {
        configs.addKeyValuePair(new Pair<>("area_radius", 3.5),"double");
        configs.addKeyValuePair(new Pair<>("area_cooldown", 300),"int/ticks");
        configs.addKeyValuePair(new Pair<>("area_damage_easy", 9),"double");
        configs.addKeyValuePair(new Pair<>("area_damage_normal", 15),"double");
        configs.addKeyValuePair(new Pair<>("area_damage_hard", 22.5),"double");
        configs.addKeyValuePair(new Pair<>("area_experience_level_required",5),"int");
        removeExperience();
        configs.addKeyValuePair(new Pair<>("area_remove_experience", -55),"int");
        configs.addKeyValuePair(new Pair<>("range_damage", 7.0),"double");
        configs.addKeyValuePair(new Pair<>("range_experience_level_required",5),"int");
        removeExperience();
        configs.addKeyValuePair(new Pair<>("range_remove_experience",-55),"int");
        configs.addKeyValuePair(new Pair<>("range_distance",16),"int");

    }

    private static void assignConfigs() {
        RADIUS = CONFIG.getOrDefault("area_radius", 3.5);
        COOLDOWN = CONFIG.getOrDefault("area_cooldown", 300);
        DAMAGE_EASY = CONFIG.getOrDefault("area_damage_easy", 9);
        DAMAGE_NORMAL = CONFIG.getOrDefault("area_damage_normal", 15);
        DAMAGE_HARD = CONFIG.getOrDefault("area_damage_hard", 22.5);
        EXPERIENCE_LEVEL = CONFIG.getOrDefault("area_experience_level", 5);
        REMOVE_EXPERIENCE = CONFIG.getOrDefault("area_remove_experience", -55);
        DAMAGE = CONFIG.getOrDefault("range_damage", 7.0);
        RANGE_EXPERIENCE_LEVEL = CONFIG.getOrDefault("range_experience_level", 5);
        RANGE_REMOVE_EXPERIENCE = CONFIG.getOrDefault("range_remove_experience", -55);
        DISTANCE = CONFIG.getOrDefault("range_distance", 16);

        System.out.println("All " + configs.getConfigsList().size() + " have been set properly");
    }

    private static void removeExperience(){
        configs.addComment("EXPERIENCE POINTS removed for using the SculkHorn");
        configs.addComment("Here you will be able to see how many experience points the levels are equivalent to:");
        configs.addComment("https://www.digminecraft.com/getting_started/experience.php");
        configs.addComment("The minus sign must be present");
    }
}
