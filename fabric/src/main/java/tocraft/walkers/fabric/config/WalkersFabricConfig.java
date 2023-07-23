package tocraft.walkers.fabric.config;


import tocraft.walkers.Walkers;
import tocraft.walkers.api.platform.WalkersConfig;
import draylar.omegaconfig.api.Comment;
import draylar.omegaconfig.api.Config;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class WalkersFabricConfig extends WalkersConfig implements Config {

    @Comment(value = "Whether an overlay message appears above the hotbar when a new walkers is unlocked.")
    public boolean overlayShapesUnlocks = true;

    @Comment(value = "Whether an overlay message appears above the hotbar when a new walkers is revoked.")
    public boolean overlay2ndShapesRevokes = true;

    @Comment(value = "Whether a player's equipped walkers is revoked on death.")
    public boolean revoke2ndShapeOnDeath = false;

    @Comment(value = "Whether shapes equip the items (swords, items, tools) held by the underlying player.")
    public boolean shapesEquipItems = true;

    @Comment(value = "Whether shapes equip the armor (chestplate, leggings, elytra) worn by the underlying player.")
    public boolean shapesEquipArmor = true;

    @Comment(value = "Whether hostile mobs ignore players with hostile mob shapes.")
    public boolean hostilesIgnoreHostileShapedPlayer = true;

    @Comment(value = "Whether a hostile mob will stop targeting you after switching to a hostile mob walkers.")
    public boolean hostilesForgetNewHostileShapedPlayer = false;

    @Comment(value = "Whether Wolves will attack Players with an walkers that the Wolf would normally hunt (Sheep, Fox, Skeleton).")
    public boolean wolvesAttack2ndShapedPrey = true;

    @Comment(value = "Whether owned Wolves will attack Players with an walkers that the Wolf would normally hunt (Sheep, Fox, Skeleton).")
    public boolean ownedwolvesAttack2ndShapedPrey = false;

    @Comment(value = "Whether Villagers will run from Players morphed as shapes villagers normally run from (Zombies).")
    public boolean villagersRunFrom2ndShapes = true;

    @Comment(value = "Whether Foxes will attack Players with an walkers that the Fox would normally hunt (Fish, Chicken).")
    public boolean foxesAttack2ndShapedPrey = true;

    @Comment(value = "Whether Walkers sounds take priority over Player Sounds (eg. Blaze hurt sound when hit).")
    public boolean useShapeSounds = true;

    @Comment(value = "Whether disguised players should randomly emit the ambient sound of their Walkers.")
    public boolean playAmbientSounds = true;

    @Comment(value = "Whether disguised players should hear their own ambient sounds (only if playAmbientSounds is true).")
    public boolean hearSelfAmbient = false;

    @Comment(value = "Whether mobs in the flying entity tag can fly.")
    public boolean enableFlight = true;

    @Comment(value = "How long hostility lasts for players morphed as hostile mobs (think: Pigman aggression")
    public int hostilityTime = 20 * 15;

    @Comment(value = "A list of Advancements required before the player can fly using an Walkers.")
    public List<String> advancementsRequiredForFlight = new ArrayList<>();

    @Comment(value = "Whether Shapes modify your max health value based on their max health value.")
    public boolean scalingHealth = true;

    @Comment(value = "This gets the percentage of your current health and implements it into your new health. Only works with scalingHealth!")
    public boolean percentScalingHealth = true;

    @Comment(value = "The maximum value of scaling health. Useful for not giving players 300 HP when they turn into a wither.")
    public int maxHealth = 40;

    @Comment(value = "Whether Shapes modify your attack damage value based on their attack damage value.")
    public Boolean scalingAttackDamage  = true;

    @Comment(value = "The maximum value of scaling attack damage. Useful for not giving players 300 ATK when they turn into a boss.")
    public Double maxAttackDamage = 4D;

    @Comment(value = "In blocks, how far can the Enderman ability teleport?")
    public int endermanAbilityTeleportDistance = 32;

    @Comment(value = "Should player nametags render above players disguised with an walkers? Note that the server is the authority for this config option.")
    public boolean showPlayerNametag = false;

    @Comment(value = "If true, /walkers commands will send feedback in the action bar.")
    public boolean logCommands = true;

    public float flySpeed = 0.05f;

    @Comment(value = "If true, players with the Warden Walkers will have a shorter view range with the darkness effect.")
    public boolean wardenIsBlinded = true;

    @Comment(value = "If true, players with the Warden Walkers will blind other nearby players.")
    public boolean wardenBlindsNearby = true;

    @Comment(value = "If true, players must unlock shapes first. If false, there will be a menu.")
    public boolean enableUnlockSystem = true;

    @Comment(value = "If true, unlocking a shape will ignore current second shapes.")
    public boolean unlockOveridesCurrentShape = false;

    @Comment(value = "Requieres enableUnlockSystem to be true- timer in ticks how long the unlock key shall be pressed.")
    public float unlockTimer = 100f;

    @Comment(value = "If true, Devs will have a third shape, if false, devs will only have two variants of their second shape.")
    public boolean devShapeIsThirdShape = false;

    public Map<String, Integer> abilityCooldownMap = new HashMap<>() {
        {
            put("minecraft:blaze", 20);
            put("minecraft:cow", 20);
            put("minecraft:creeper", 100);
            put("minecraft:ender_dragon", 20);
            put("minecraft:enderman", 100);
            put("minecraft:endermite", 20);
            put("minecraft:evoker", 10);
            put("minecraft:ghast", 60);
            put("minecraft:llama", 20);
            put("minecraft:sheep", 20);
            put("minecraft:snow_golem", 10);
            put("minecraft:warden", 200);
            put("minecraft:witch", 200);
            put("minecraft:wither", 200);
            put("minecraft:wolf", 20);
        }
    };

    @Override
    public String getName() {
        return Walkers.MODID;
    }

    @Override
    public String getExtension() {
        return "json5";
    }

    @Override
    public boolean enableFlight() {
        return enableFlight;
    }

    @Override
    public List<String> advancementsRequiredForFlight() {
        return advancementsRequiredForFlight;
    }

    @Override
    public Map<String, Integer> getAbilityCooldownMap() {
        return abilityCooldownMap;
    }

    @Override
    public boolean shouldOverlayShapesUnlocks() {
        return overlayShapesUnlocks;
    }

    @Override
    public boolean logCommands() {
        return logCommands;
    }

    @Override
    public boolean foxesAttack2ndShapedPrey() {
        return foxesAttack2ndShapedPrey;
    }

    @Override
    public boolean ownedwolvesAttack2ndShapedPrey() {
        return ownedwolvesAttack2ndShapedPrey;
    }

    @Override
    public boolean villagersRunFrom2ndShapes() {
        return villagersRunFrom2ndShapes;
    }

    @Override
    public boolean revoke2ndShapeOnDeath() {
        return revoke2ndShapeOnDeath;
    }

    @Override
    public boolean overlay2ndShapesRevokes() {
        return overlay2ndShapesRevokes;
    }

    @Override
    public float flySpeed() {
        return flySpeed;
    }

    @Override
    public boolean scalingHealth() {
        return scalingHealth;
    }

    @Override
    public boolean percentScalingHealth() {
        return percentScalingHealth;
    }

    @Override
    public int maxHealth() {
        return maxHealth;
    }

    @Override
    public Boolean scalingAttackDamage() {
        return scalingAttackDamage;
    }

    @Override
    public Double maxAttackDamage() {
        return maxAttackDamage;
    }

    @Override
    public boolean shapesEquipItems() {
        return shapesEquipItems;
    }

    @Override
    public boolean shapesEquipArmor() {
        return shapesEquipArmor;
    }

    @Override
    public boolean showPlayerNametag() {
        return showPlayerNametag;
    }

    @Override
    public boolean wolvesAttack2ndShapedPrey() {
        return wolvesAttack2ndShapedPrey;
    }

    @Override
    public boolean hostilesForgetNewHostileShapedPlayer() {
        return hostilesForgetNewHostileShapedPlayer;
    }

    @Override
    public boolean hostilesIgnoreHostileShapedPlayer() {
        return hostilesIgnoreHostileShapedPlayer;
    }

    @Override
    public boolean playAmbientSounds() {
        return playAmbientSounds;
    }

    @Override
    public boolean useShapeSounds() {
        return useShapeSounds;
    }

    @Override
    public boolean hearSelfAmbient() {
        return hearSelfAmbient;
    }

    @Override
    public double endermanAbilityTeleportDistance() {
        return endermanAbilityTeleportDistance;
    }

    @Override
    public int hostilityTime() {
        return hostilityTime;
    }

    @Override
    public boolean wardenIsBlinded() {
        return wardenIsBlinded;
    }

    @Override
    public boolean wardenBlindsNearby() {
        return wardenBlindsNearby;
    }

    @Override
    public boolean enableUnlockSystem() {
        return enableUnlockSystem;
    }

    @Override
    public float unlockTimer() {
        return unlockTimer;
    }

    @Override
    public boolean unlockOveridesCurrentShape() {
        return unlockOveridesCurrentShape;
    }

    @Override
    public boolean devShapeIsThirdShape() {
        return devShapeIsThirdShape;
    }
}
