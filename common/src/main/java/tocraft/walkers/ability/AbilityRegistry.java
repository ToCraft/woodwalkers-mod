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
        registerByPredicate(livingEntity -> livingEntity instanceof NeutralMob, new AngerAbility<>());

        // Register 'normal' Abilities
        registerByClass(AbstractHorse.class, new HorseAbility<>());
        registerByClass(Blaze.class, new BlazeAbility<>());
        registerByClass(Creeper.class, new CreeperAbility<>());
        registerByClass(EnderDragon.class, new EnderDragonAbility<>());
        registerByClass(EnderMan.class, new EndermanAbility<>());
        registerByClass(Ghast.class, new GhastAbility<>());
        registerByClass(SnowGolem.class, new SnowGolemAbility<>());
        registerByClass(WitherBoss.class, new WitherAbility<>());
        registerByClass(Cow.class, new CowAbility<>());
        registerByClass(Goat.class, new CowAbility<>());
        registerByClass(Endermite.class, new EndermiteAbility<>());
        registerByClass(Llama.class, new LlamaAbility<>());
        registerByClass(Witch.class, new WitchAbility<>());
        registerByClass(Evoker.class, new EvokerAbility<>());
        registerByClass(Warden.class, new WardenAbility<>());
        registerByClass(Wolf.class, new AngerAbility<>(SoundEvents.WOLF_PANT, SoundEvents.WOLF_GROWL));
        registerByClass(Sheep.class, new SheepAbility<>());
        registerByClass(Sniffer.class, new SnifferAbility<>());
        registerByClass(Chicken.class, new ChickenAbility<>());
        registerByClass(MushroomCow.class, new MushroomCowAbility<>());
        registerByClass(Bee.class, new AngerAbility<>(SoundEvents.BEE_LOOP, SoundEvents.BEE_LOOP_AGGRESSIVE));
        registerByClass(Shulker.class, new ShulkerAbility<>());
        registerByClass(Pufferfish.class, new PufferfishAbility<>());
        registerByClass(Turtle.class, new TurtleAbility<>());
        registerByClass(Rabbit.class, new RabbitAbility<>());
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

    public static <A extends LivingEntity> void registerByType(EntityType<A> type, ShapeAbility<A> ability) {
        registerByPredicate(livingEntity -> type.equals(livingEntity.getType()), ability);
    }

    public static <A extends LivingEntity> void registerByClass(Class<A> entityClass, ShapeAbility<A> ability) {
        registerByPredicate(entityClass::isInstance, ability);
    }

    /**
     * Register an ability for a predicate
     *
     * @param entityPredicate this should only be true, if the entity is the correct class for the ability!
     * @param ability         your {@link ShapeAbility}
     */
    public static void registerByPredicate(Predicate<LivingEntity> entityPredicate, ShapeAbility<?> ability) {
        abilities.put(entityPredicate, ability);
    }

    public static <L extends LivingEntity> boolean has(L shape) {
        // check ability blacklist

        if (Walkers.CONFIG.abilityBlacklist.contains(BuiltInRegistries.ENTITY_TYPE.getKey(shape.getType()).toString()))
            return false;
        return abilities.keySet().stream().anyMatch(predicate -> predicate.test(shape));
    }
}
