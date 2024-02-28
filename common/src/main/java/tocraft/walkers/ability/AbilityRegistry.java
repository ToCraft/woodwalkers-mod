package tocraft.walkers.ability;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.NeutralMob;
import net.minecraft.world.entity.animal.*;
import net.minecraft.world.entity.animal.goat.Goat;
import net.minecraft.world.entity.animal.horse.AbstractHorse;
import net.minecraft.world.entity.animal.horse.Llama;
import net.minecraft.world.entity.animal.sniffer.Sniffer;
import net.minecraft.world.entity.boss.enderdragon.EnderDragon;
import net.minecraft.world.entity.boss.wither.WitherBoss;
import net.minecraft.world.entity.monster.*;
import net.minecraft.world.entity.monster.warden.Warden;
import tocraft.walkers.Walkers;
import tocraft.walkers.ability.impl.*;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

public class AbilityRegistry {

    private static final Map<Predicate<LivingEntity>, ShapeAbility<?>> abilities = new LinkedHashMap<>();

    private AbilityRegistry() {

    }

    public static void init() {
        // Register generic Abilities first (since the last registered ability will be the used one
        register((Predicate<LivingEntity>) livingEntity -> livingEntity instanceof NeutralMob, new AngerAbility<>());

        // Register 'normal' Abilities
        register(Blaze.class, new BlazeAbility<>());
        register(Creeper.class, new CreeperAbility<>());
        register(EnderDragon.class, new EnderDragonAbility<>());
        register(EnderMan.class, new EndermanAbility<>());
        register(Ghast.class, new GhastAbility<>());
        register(SnowGolem.class, new SnowGolemAbility<>());
        register(WitherBoss.class, new WitherAbility<>());
        register(Cow.class, new CowAbility<>());
        register(Goat.class, new CowAbility<>());
        register(Endermite.class, new EndermiteAbility<>());
        register(Llama.class, new LlamaAbility<>());
        register(Witch.class, new WitchAbility<>());
        register(Evoker.class, new EvokerAbility<>());
        register(Warden.class, new WardenAbility<>());
        register(Wolf.class, new AngerAbility<>(SoundEvents.WOLF_PANT, SoundEvents.WOLF_GROWL));
        register(Sheep.class, new SheepAbility<>());
        register(Sniffer.class, new SnifferAbility<>());
        register(Chicken.class, new ChickenAbility<>());
        register(MushroomCow.class, new MushroomCowAbility<>());
        register(AbstractHorse.class, new HorseAbility<>());
        register(Bee.class, new AngerAbility<>(SoundEvents.BEE_LOOP, SoundEvents.BEE_LOOP_AGGRESSIVE));
        register(Shulker.class, new ShulkerAbility<>());
        register(Pufferfish.class, new PufferfishAbility<>());
    }

    /**
     * @return the last registered {@link ShapeAbility} for the specified shape
     */
    @SuppressWarnings("unchecked")
    public static <L extends LivingEntity> ShapeAbility<L> get(L shape) {
        // check ability blacklist
        if (Walkers.CONFIG.abilityBlacklist.contains(BuiltInRegistries.ENTITY_TYPE.getKey(shape.getType()).toString()))
            return null;

        List<ShapeAbility<?>> shapeAbilities = new ArrayList<>(abilities.entrySet().stream().filter(entry -> entry.getKey().test(shape)).map(Map.Entry::getValue).toList());
        return !shapeAbilities.isEmpty() ? (ShapeAbility<L>) shapeAbilities.get(shapeAbilities.size() - 1) : null;
    }

    public static <A extends LivingEntity, T extends EntityType<A>> void register(T type, ShapeAbility<A> ability) {
        register((Predicate<LivingEntity>) livingEntity -> type.equals(livingEntity.getType()), ability);
    }

    public static <A extends LivingEntity> void register(Class<A> entityClass, ShapeAbility<A> ability) {
        register((Predicate<LivingEntity>) entityClass::isInstance, ability);
    }

    /**
     * Register an ability for a predicate
     *
     * @param entityPredicate this should only be true, if the entity is the correct class for the ability!
     * @param ability         your {@link ShapeAbility}
     */
    public static void register(Predicate<LivingEntity> entityPredicate, ShapeAbility<?> ability) {
        abilities.put(entityPredicate, ability);
    }

    public static <L extends LivingEntity> boolean has(L shape) {
        // check ability blacklist

        if (Walkers.CONFIG.abilityBlacklist.contains(BuiltInRegistries.ENTITY_TYPE.getKey(shape.getType()).toString()))
            return false;
        return abilities.keySet().stream().anyMatch(predicate -> predicate.test(shape));
    }
}
