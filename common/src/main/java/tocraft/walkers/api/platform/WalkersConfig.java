package tocraft.walkers.api.platform;

import tocraft.craftedcore.config.Config;
import tocraft.craftedcore.config.annotions.Comment;
import tocraft.craftedcore.config.annotions.Synchronize;
import tocraft.walkers.Walkers;

import java.util.*;

@SuppressWarnings("CanBeFinal")
public class WalkersConfig implements Config {

    @Comment("Whether a player's equipped shape is revoked on death.")
    public boolean revoke2ndShapeOnDeath = false;
    @Comment("Whether shapes equip the items (swords, items, tools) held by the underlying player.")
    public boolean shapesEquipItems = true;
    @Comment("Whether shapes equip the armor (chestplate, leggings, elytra) worn by the underlying player.")
    public boolean shapesEquipArmor = true;
    @Comment("If true, the player can cause hostiles to attack via right-clicking.")
    public boolean playerCanTriggerHostiles = true;
    @Comment("Whether hostile mobs ignore players with hostile mob shapes.")
    public boolean hostilesIgnoreHostileShapedPlayer = true;
    @Comment("Whether a hostile mob will stop targeting you after switching to a hostile mob shape.")
    public boolean hostilesForgetNewHostileShapedPlayer = false;
    @Comment("hunterAttackAsPreyMorphedPlayer")
    public boolean hunterAttackAsPreyMorphedPlayer = true;
    @Comment("If false, the PreyTrait doesn't cause owned hunter (e.g. tamed wolves) to hunt the player.")
    public boolean ownedHunterAttackAsPreyMorphedPlayer = false;
    @Comment("Whether Villagers will run from Players morphed as shapes villagers normally run from (Zombies).")
    public boolean villagersRunFrom2ndShapes = true;
    @Comment("Whether shape sounds take priority over Player Sounds (e.g. Blaze hurt sound when hit).")
    public boolean useShapeSounds = true;
    @Comment("Whether disguised players should randomly emit the ambient sound of their shape.")
    public boolean playAmbientSounds = true;
    @Comment("Whether disguised players should hear their own ambient sounds (only if playAmbientSounds is true).")
    public boolean hearSelfAmbient = false;
    @Comment("Whether mobs in the flying entity tag can fly.")
    public boolean enableFlight = true;
    @Comment("How long hostility lasts for players morphed as hostile mobs (think: Pigman aggression)")
    public int hostilityTime = 20 * 15;
    @Comment("A list of Advancements required before the player can fly using a shape.")
    public List<String> advancementsRequiredForFlight = new ArrayList<>();
    @Comment("Whether Shapes modify your max health value based on their max health value.")
    public boolean scalingHealth = true;
    @Comment("This gets the percentage of your current health and implements it into your new health. Only works with scalingHealth!")
    public boolean percentScalingHealth = true;
    @Comment("The maximum value of scaling health. Useful for not giving players 300 HP when they turn into a wither.")
    public int maxHealth = 40;
    @Comment("Whether the player should have the same default amor values as the mob.")
    public boolean scalingAmor = true;
    @Comment("The maximum value of default amor. Useful for not having players who can't take damage.")
    public int maxAmor = 30;
    @Comment("The maximum value of default amor toughness. Useful for not having players who can't take damage.")
    public int maxAmorToughness = 20;
    @Comment("Whether the player step height should be adjusted to the one of the mob.")
    public boolean scalingStepHeight = true;
    @Comment("In blocks, how far can the Enderman ability teleport?")
    public int endermanAbilityTeleportDistance = 32;
    @Comment("Should player nametags render above players disguised with a shape? Note that the server is the authority for this config option.")
    @Synchronize
    public boolean showPlayerNametag = false;
    @Comment("The default fly speed for transformed players.")
    public float flySpeed = 0.05f;
    @Comment("If true, players with the Warden shape will have a shorter view range with the darkness effect.")
    public boolean wardenIsBlinded = true;
    @Comment("If true, players with the Warden shape will blind other nearby players.")
    public boolean wardenBlindsNearby = true;
    @Comment("If true, unlocking a shape will ignore current second shapes.")
    @Synchronize
    public boolean unlockOverridesCurrentShape = false;
    @Comment("If true, unlocking a shape will ignore current second shapes.")
    @Synchronize
    public float unlockTimer = 60f;
    @Comment("This allows players to unlock every possible variant per entity type. This must be set to true for the variant menu to be available.")
    @Synchronize
    public boolean unlockEveryVariant = true;

    @Comment("Configure the Cooldown for the abilities of specified mobs. Take a look at Abilities")
    public Map<String, Integer> abilityCooldownMap = new HashMap<>() {
        {
            put("minecraft:bee", 20);
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
            put("minecraft:illusioner", 2400);
            put("minecraft:iron_golem", 20);
            put("minecraft:llama", 20);
            put("minecraft:mooshroom", 300);
            put("minecraft:mule", 40);
            put("minecraft:pillager", 2400);
            put("minecraft:polar_bear", 20);
            put("minecraft:pufferfish", 20);
            put("minecraft:rabbit", 40);
            put("minecraft:ravager", 2400);
            put("minecraft:sheep", 20);
            put("minecraft:shulker", 80);
            put("minecraft:skeleton_horse", 40);
            put("minecraft:sniffer", 9600);
            put("minecraft:snow_golem", 10);
            put("minecraft:trader_llama", 20);
            put("minecraft:turtle", 6000);
            put("minecraft:vindicator", 2400);
            put("minecraft:warden", 200);
            put("minecraft:witch", 200);
            put("minecraft:wither", 200);
            put("minecraft:wolf", 20);
            put("minecraft:zombie_horse", 40);
            put("minecraft:zombified_piglin", 20);
        }
    };

    @Comment("0 - none, 1 - completely random, 2 - biome based")
    public int multiVectorVariants = 2;

    @Comment("Blacklist entity types (e.g. minecraft:blaze) to disable abilities for the specified mob.")
    @Synchronize
    public List<String> abilityBlacklist = new ArrayList<>();
    @Comment("Map entity types to a list of traits to disable the traits for the specified entity type.")
    @Synchronize
    public Map<String, List<String>> traitBlacklist = new HashMap<>() {
        {
            put("example:my_mob", List.of("example:first_trait", "example:second_trait"));
        }
    };
    @Comment("Blacklist entity types (e.g. minecraft:blaze) to disable morphing into those.")
    @Synchronize
    public Set<String> entityBlacklist = new HashSet<>();
    @Comment("True - the entity blacklist will be treated as a whitelist. This can be modified via commands.")
    @Synchronize
    public boolean entityBlacklistIsWhitelist = false;
    @Comment("Blacklist Players with their UUIDs, so they can't unlock shapes. This can be modified via commands.")
    @Synchronize
    public List<UUID> playerUUIDBlacklist = new ArrayList<>();
    @Comment("True - the player blacklist will be treated as a whitelist. This can be modified via commands.")
    @Synchronize
    public boolean playerBlacklistIsWhitelist = false;
    @Comment("Whether the player blacklist should prevent unlocking.")
    @Synchronize
    public boolean blacklistPreventsUnlocking = true;
    @Comment("Whether the player blacklist should prevent morphing.")
    @Synchronize
    public boolean blacklistPreventsMorphing = true;

    @Override
    public String getName() {
        return Walkers.MODID;
    }
}
