package tocraft.walkers.api.platform;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class WalkersConfig {

	public boolean revoke2ndShapeOnDeath = false;
	public boolean shapesEquipItems = true;
	public boolean shapesEquipArmor = true;
	public boolean hostilesIgnoreHostileShapedPlayer = true;
	public boolean hostilesForgetNewHostileShapedPlayer = false;
	public boolean wolvesAttack2ndShapedPrey = true;
	public boolean ownedwolvesAttack2ndShapedPrey = false;
	public boolean villagersRunFrom2ndShapes = true;
	public boolean foxesAttack2ndShapedPrey = true;
	public boolean useShapeSounds = true;
	public boolean playAmbientSounds = true;
	public boolean hearSelfAmbient = false;
	public boolean enableFlight = true;
	public int hostilityTime = 20 * 15;
	public List<String> advancementsRequiredForFlight = new ArrayList<>();
	public boolean scalingHealth = true;
	public boolean percentScalingHealth = true;
	public int maxHealth = 40;
	public Boolean scalingAttackDamage = true;
	public Double maxAttackDamage = 4D;
	public int endermanAbilityTeleportDistance = 32;
	public boolean showPlayerNametag = false;
	public boolean logCommands = true;
	public float flySpeed = 0.05f;
	public boolean wardenIsBlinded = true;
	public boolean wardenBlindsNearby = true;
	public boolean unlockOveridesCurrentShape = false;
	public float unlockTimer = 60f;
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
			put("minecraft:sniffer", 9600);
			put("minecraft:snow_golem", 10);
			put("minecraft:warden", 200);
			put("minecraft:witch", 200);
			put("minecraft:wither", 200);
			put("minecraft:wolf", 20);
		}
	};

	public List<String> shapeBlacklist = new ArrayList<>() {
		{
			add("minecraft:ender_dragon");
			add("minecraft:wither");
		}
	};

	public List<UUID> playerUUIDBlacklist = new ArrayList<>();

	public boolean enableFlight() {
		return enableFlight;
	}

	public List<String> advancementsRequiredForFlight() {
		return advancementsRequiredForFlight;
	}

	public Map<String, Integer> getAbilityCooldownMap() {
		return abilityCooldownMap;
	}

	public boolean logCommands() {
		return logCommands;
	}

	public boolean wolvesAttack2ndShapedPrey() {
		return wolvesAttack2ndShapedPrey;
	}

	public boolean ownedwolvesAttack2ndShapedPrey() {
		return ownedwolvesAttack2ndShapedPrey;
	}

	public boolean villagersRunFrom2ndShapes() {
		return villagersRunFrom2ndShapes;
	}

	public boolean revoke2ndShapeOnDeath() {
		return revoke2ndShapeOnDeath;
	}

	public float flySpeed() {
		return flySpeed;
	}

	public boolean scalingHealth() {
		return scalingHealth;
	}

	public boolean percentScalingHealth() {
		return percentScalingHealth;
	}

	public int maxHealth() {
		return maxHealth;
	}

	public Boolean scalingAttackDamage() {
		return scalingAttackDamage;
	}

	public Double maxAttackDamage() {
		return maxAttackDamage;
	}

	public boolean shapesEquipItems() {
		return shapesEquipItems;
	}

	public boolean shapesEquipArmor() {
		return shapesEquipArmor;
	}

	public boolean showPlayerNametag() {
		return showPlayerNametag;
	}

	public boolean foxesAttack2ndShapedPrey() {
		return foxesAttack2ndShapedPrey;
	}

	public boolean hostilesForgetNewHostileShapedPlayer() {
		return hostilesForgetNewHostileShapedPlayer;
	}

	public boolean hostilesIgnoreHostileShapedPlayer() {
		return hostilesIgnoreHostileShapedPlayer;
	}

	public boolean playAmbientSounds() {
		return playAmbientSounds;
	}

	public boolean useShapeSounds() {
		return useShapeSounds;
	}

	public boolean hearSelfAmbient() {
		return hearSelfAmbient;
	}

	public double endermanAbilityTeleportDistance() {
		return endermanAbilityTeleportDistance;
	}

	public int hostilityTime() {
		return hostilityTime;
	}

	public boolean wardenIsBlinded() {
		return wardenIsBlinded;
	}

	public boolean wardenBlindsNearby() {
		return wardenBlindsNearby;
	}

	public float unlockTimer() {
		return unlockTimer;
	}

	public boolean unlockOveridesCurrentShape() {
		return unlockOveridesCurrentShape;
	}

	public boolean devShapeIsThirdShape() {
		return devShapeIsThirdShape;
	}

	public List<String> shapeBlacklist() {
		return shapeBlacklist;
	}

	public List<UUID> playerUUIDBlacklist() {
		return playerUUIDBlacklist;
	}
}
