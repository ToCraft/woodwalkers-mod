package tocraft.walkers.api.platform;

import tocraft.craftedcore.config.Config;
import tocraft.craftedcore.config.annotions.Synchronize;
import tocraft.walkers.Walkers;

import java.util.*;

@SuppressWarnings("ConstantConditions")
public class WalkersConfig implements Config {

    public boolean revoke2ndShapeOnDeath = false;
    public boolean shapesEquipItems = true;
    public boolean shapesEquipArmor = true;
    public boolean playerCanTriggerHostiles = true;
    public boolean hostilesIgnoreHostileShapedPlayer = true;
    public boolean hostilesForgetNewHostileShapedPlayer = false;
    public boolean hunterAttackAsPreyMorphedPlayer = true;
    public boolean ownedHunterAttackAsPreyMorphedPlayer = false;
    public boolean villagersRunFrom2ndShapes = true;
    public boolean useShapeSounds = true;
    public boolean playAmbientSounds = true;
    public boolean hearSelfAmbient = false;
    public boolean enableFlight = true;
    public int hostilityTime = 20 * 15;
    public List<String> advancementsRequiredForFlight = new ArrayList<>();
    public boolean scalingHealth = true;
    public boolean percentScalingHealth = true;
    public int maxHealth = 40;
    public int endermanAbilityTeleportDistance = 32;
    @Synchronize
    public boolean showPlayerNametag = false;
    public boolean logCommands = true;
    public float flySpeed = 0.05f;
    public boolean wardenIsBlinded = true;
    public boolean wardenBlindsNearby = true;
    @Synchronize
    public boolean unlockOverridesCurrentShape = false;
    @Synchronize
    public float unlockTimer = 60f;
    @Synchronize
    public boolean unlockEveryVariant = true;

    public Map<String, Integer> abilityCooldownMap = new HashMap<>() {
        {
            put("minecraft:blaze", 20);
            put("minecraft:camel", 40);
            put("minecraft:chicken", 1200);
            put("minecraft:cow", 20);
            put("minecraft:creeper", 100);
            put("minecraft:ender_dragon", 20);
            put("minecraft:enderman", 100);
            put("minecraft:endermite", 20);
            put("minecraft:evoker", 10);
            put("minecraft:ghast", 60);
            put("minecraft:goat", 20);
            put("minecraft:horse", 40);
            put("minecraft:llama", 20);
            put("minecraft:mooshroom", 300);
            put("minecraft:pufferfish", 20);
            put("minecraft:rabbit", 40);
            put("minecraft:sheep", 20);
            put("minecraft:shulker", 80);
            put("minecraft:skeleton_horse", 40);
            put("minecraft:sniffer", 9600);
            put("minecraft:snow_golem", 10);
            put("minecraft:trader_llama", 20);
            put("minecraft:turtle", 6000);
            put("minecraft:warden", 200);
            put("minecraft:witch", 200);
            put("minecraft:wither", 200);
            put("minecraft:wolf", 20);
            put("minecraft:zombie_horse", 40);
        }
    };

    // 0 - none, 1 - completely random, 2 - biome based
    public int multiVectorVariants = 2;

    @Synchronize
    public List<String> abilityBlacklist = new ArrayList<>();
    @Synchronize
    public List<UUID> playerUUIDBlacklist = new ArrayList<>();
    @Synchronize
    public boolean playerBlacklistIsWhitelist = false;

    @Override
    public String getName() {
        return Walkers.MODID;
    }
}
