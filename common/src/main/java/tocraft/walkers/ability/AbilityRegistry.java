package tocraft.walkers.ability;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import tocraft.walkers.ability.impl.*;

import java.util.HashMap;
import java.util.Map;

public class AbilityRegistry {

    private static final Map<EntityType<? extends LivingEntity>, ShapeAbility<?>> abilities = new HashMap<>();

    private AbilityRegistry() {

    }

    public static void init() {
        register(EntityType.BLAZE, new BlazeAbility());
        register(EntityType.CREEPER, new CreeperAbility());
        register(EntityType.ENDER_DRAGON, new EnderDragonAbility());
        register(EntityType.ENDERMAN, new EndermanAbility());
        register(EntityType.GHAST, new GhastAbility());
        register(EntityType.SNOW_GOLEM, new SnowGolemAbility());
        register(EntityType.WITHER, new WitherEntityAbility());
        register(EntityType.COW, new CowAbility<>());
        register(EntityType.GOAT, new CowAbility<>());
        register(EntityType.ENDERMITE, new EndermiteAbility());
        register(EntityType.LLAMA, new LlamaAbility<>());
        register(EntityType.TRADER_LLAMA, new LlamaAbility<>());
        register(EntityType.WITCH, new WitchAbility());
        register(EntityType.EVOKER, new EvokerAbility());
        register(EntityType.WARDEN, new WardenAbility());
        register(EntityType.WOLF, new WolfAbility());
        register(EntityType.SHEEP, new SheepAbility<>());
        register(EntityType.SNIFFER, new SnifferAbility());
        register(EntityType.CHICKEN, new ChickenAbility<>());
        register(EntityType.MOOSHROOM, new MushroomCowAbility());
        register(EntityType.CAMEL, new HorseAbility<>());
        register(EntityType.HORSE, new HorseAbility<>());
        register(EntityType.MULE, new HorseAbility<>());
        register(EntityType.DONKEY, new HorseAbility<>());
        register(EntityType.SKELETON_HORSE, new HorseAbility<>());
        register(EntityType.ZOMBIE_HORSE, new HorseAbility<>());
        register(EntityType.BEE, new BeeAbility());
        register(EntityType.SHULKER, new ShulkerAbility());
    }

    public static ShapeAbility get(EntityType<?> type) {
        return abilities.get(type);
    }

    public static <A extends LivingEntity, T extends EntityType<A>> void register(T type, ShapeAbility<A> ability) {
        abilities.put(type, ability);
    }

    public static boolean has(EntityType<?> type) {
        return abilities.containsKey(type);
    }
}
