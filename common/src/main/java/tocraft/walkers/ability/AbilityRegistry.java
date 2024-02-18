package tocraft.walkers.ability;

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
import tocraft.walkers.ability.impl.*;

import java.util.*;
import java.util.function.Predicate;

public class AbilityRegistry {

    private static final Map<EntityType<? extends LivingEntity>, ShapeAbility<?>> abilities = new HashMap<>();
    private static final Map<Predicate<LivingEntity>, ShapeAbility<?>> abilityPredicates = new LinkedHashMap<>();

    private AbilityRegistry() {

    }

    public static void init() {
        // Register generic Abilities first (since the last registered ability will be the used one
        register((Predicate<LivingEntity>) livingEntity -> livingEntity instanceof NeutralMob, new AngerAbility<>(SoundEvents.PLAYER_BREATH, SoundEvents.PLAYER_ATTACK_CRIT));

        // Register 'normal' Abilities
        register(Blaze.class, new BlazeAbility());
        register(Creeper.class, new CreeperAbility());
        register(EnderDragon.class, new EnderDragonAbility());
        register(EnderMan.class, new EndermanAbility());
        register(Ghast.class, new GhastAbility());
        register(SnowGolem.class, new SnowGolemAbility());
        register(WitherBoss.class, new WitherEntityAbility());
        register(Cow.class, new CowAbility<>());
        register(Goat.class, new CowAbility<>());
        register(Endermite.class, new EndermiteAbility());
        register(Llama.class, new LlamaAbility<>());
        register(Witch.class, new WitchAbility());
        register(Evoker.class, new EvokerAbility());
        register(Warden.class, new WardenAbility());
        register(Wolf.class, new AngerAbility<>(SoundEvents.WOLF_PANT, SoundEvents.WOLF_GROWL));
        register(Sheep.class, new SheepAbility());
        register(Sniffer.class, new SnifferAbility());
        register(Chicken.class, new ChickenAbility<>());
        register(MushroomCow.class, new MushroomCowAbility());
        register(AbstractHorse.class, new HorseAbility<>());
        register(Bee.class, new AngerAbility<>(SoundEvents.BEE_LOOP, SoundEvents.BEE_LOOP_AGGRESSIVE));
        register(Shulker.class, new ShulkerAbility());
        register(Pufferfish.class, new PufferfishAbility());
    }

    /**
     * @return the last registered {@link ShapeAbility} for the specified shape
     */
    @SuppressWarnings("unchecked")
    public static <L extends LivingEntity> ShapeAbility<L> get(L shape) {
        List<ShapeAbility<?>> shapeAbilities = new ArrayList<>(abilityPredicates.entrySet().stream().filter(entry -> entry.getKey().test(shape)).map(Map.Entry::getValue).toList());
        return !shapeAbilities.isEmpty() ? (ShapeAbility<L>) shapeAbilities.get(shapeAbilities.size() - 1) : (ShapeAbility<L>) abilities.get(shape.getType());
    }

    public static <A extends LivingEntity, T extends EntityType<A>> void register(T type, ShapeAbility<A> ability) {
        abilities.put(type, ability);
        abilityPredicates.put(livingEntity -> type.equals(livingEntity.getType()), ability);
    }

    public static <A extends LivingEntity> void register(Class<A> entityClass, ShapeAbility<A> ability) {
        abilityPredicates.put(entityClass::isInstance, ability);
    }

    /**
     * Register an ability for a predicate
     *
     * @param entityPredicate this should only be true, if the entity is the correct class for the ability!
     * @param ability         your {@link ShapeAbility}
     */
    public static void register(Predicate<LivingEntity> entityPredicate, ShapeAbility<?> ability) {
        abilityPredicates.put(entityPredicate, ability);
    }

    public static <L extends LivingEntity> boolean has(L shape) {
        return abilityPredicates.keySet().stream().anyMatch(predicate -> predicate.test(shape)) || abilities.containsKey(shape.getType());
    }

    /**
     * @Deprecated Use {@link #get(LivingEntity)} instead
     */
    @Deprecated
    public static ShapeAbility get(EntityType<?> type) {
        return abilities.get(type);
    }

    /**
     * @Deprecated Use {@link #has(LivingEntity)} instead
     */
    @Deprecated
    public static boolean has(EntityType<?> type) {
        return abilities.containsKey(type);
    }
}
