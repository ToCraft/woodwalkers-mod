package tocraft.walkers.traits;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.Codec;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.EntityTypeTags;
import net.minecraft.tags.FluidTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.FlyingMob;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.ambient.Bat;
import net.minecraft.world.entity.animal.*;
import net.minecraft.world.entity.animal.allay.Allay;
import net.minecraft.world.entity.animal.horse.AbstractHorse;
import net.minecraft.world.entity.boss.enderdragon.EnderDragon;
import net.minecraft.world.entity.boss.wither.WitherBoss;
import net.minecraft.world.entity.monster.*;
import net.minecraft.world.level.block.Blocks;
import org.jetbrains.annotations.Nullable;
import tocraft.walkers.Walkers;
import tocraft.walkers.ability.ShapeAbility;
import tocraft.walkers.integrations.Integrations;
import tocraft.walkers.traits.impl.*;

import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;

@SuppressWarnings("unused")
public class TraitRegistry {
    private static final Map<Predicate<LivingEntity>, List<ShapeTrait<?>>> traitsByPredicates = new HashMap<>();
    private static final Map<EntityType<? extends LivingEntity>, List<ShapeTrait<?>>> traitsByEntityTypes = new HashMap<>();
    private static final Map<TagKey<EntityType<?>>, List<ShapeTrait<?>>> traitsByEntityTags = new HashMap<>();
    private static final Map<Class<? extends LivingEntity>, List<ShapeTrait<?>>> traitsByEntityClasses = new HashMap<>();
    private static final Map<ResourceLocation, Codec<? extends ShapeTrait<?>>> traitCodecById = new HashMap<>();
    private static final Map<Codec<? extends ShapeTrait<?>>, ResourceLocation> traitIdByCodec = new IdentityHashMap<>();

    public static void initialize() {
        // register trait codecs
        registerCodec(MobEffectTrait.ID, MobEffectTrait.CODEC);
        registerCodec(BurnInDaylightTrait.ID, BurnInDaylightTrait.CODEC);
        registerCodec(FlyingTrait.ID, FlyingTrait.CODEC);
        registerCodec(PreyTrait.ID, PreyTrait.CODEC);
        registerCodec(TemperatureTrait.ID, TemperatureTrait.CODEC);
        registerCodec(RiderTrait.ID, RiderTrait.CODEC);
        registerCodec(StandOnFluidTrait.ID, StandOnFluidTrait.CODEC);
        registerCodec(NoPhysicsTrait.ID, NoPhysicsTrait.CODEC);
        registerCodec(CantSwimTrait.ID, CantSwimTrait.CODEC);
        registerCodec(UndrownableTrait.ID, UndrownableTrait.CODEC);
        registerCodec(SlowFallingTrait.ID, SlowFallingTrait.CODEC);
        registerCodec(FearedTrait.ID, FearedTrait.CODEC);
        registerCodec(ClimbBlocksTrait.ID, ClimbBlocksTrait.CODEC);
        registerCodec(ReinforcementsTrait.ID, ReinforcementsTrait.CODEC);
        registerCodec(InstantDieOnDamageMsgTrait.ID, InstantDieOnDamageMsgTrait.CODEC);
        registerCodec(AquaticTrait.ID, AquaticTrait.CODEC);
        registerCodec(WalkOnPowderSnow.ID, WalkOnPowderSnow.CODEC);
        registerCodec(HumanoidTrait.ID, HumanoidTrait.CODEC);
        registerCodec(AttackForHealthTrait.ID, AttackForHealthTrait.CODEC);
        registerCodec(NocturnalTrait.ID, NocturnalTrait.CODEC);
    }

    @SuppressWarnings("unchecked")
    public static void registerDefault() {
        // register traits
        // mob effects
        registerByClass(Bat.class, new MobEffectTrait<>(new MobEffectInstance(MobEffects.NIGHT_VISION, 100000, 0, false, false)));
        // burn in daylight
        registerByClass(Zombie.class, new BurnInDaylightTrait<>());
        registerByClass(Skeleton.class, new BurnInDaylightTrait<>());
        registerByClass(Stray.class, new BurnInDaylightTrait<>());
        registerByClass(Phantom.class, new BurnInDaylightTrait<>());
        // flying
        registerByClass(Allay.class, new FlyingTrait<>());
        registerByClass(Bat.class, new FlyingTrait<>());
        registerByClass(Bee.class, new FlyingTrait<>());
        registerByClass(Blaze.class, new FlyingTrait<>());
        registerByClass(EnderDragon.class, new FlyingTrait<>());
        registerByClass(FlyingMob.class, new FlyingTrait<>());
        registerByClass(Parrot.class, new FlyingTrait<>());
        registerByClass(Vex.class, new FlyingTrait<>());
        registerByClass(WitherBoss.class, new FlyingTrait<>());
        // wolf prey
        registerByClass(Bat.class, (PreyTrait<Bat>) PreyTrait.ofHunterClass(Wolf.class));
        registerByClass(Fox.class, (PreyTrait<Fox>) PreyTrait.ofHunterClass(Wolf.class));
        registerByClass(Sheep.class, (PreyTrait<Sheep>) PreyTrait.ofHunterClass(Wolf.class));
        registerByClass(Skeleton.class, (PreyTrait<Skeleton>) PreyTrait.ofHunterClass(Wolf.class));
        registerByClass(Parrot.class, (PreyTrait<Parrot>) PreyTrait.ofHunterClass(Wolf.class));
        registerByClass(Rabbit.class, (PreyTrait<Rabbit>) PreyTrait.ofHunterClass(Wolf.class));
        // fox prey
        registerByClass(Chicken.class, (PreyTrait<Chicken>) PreyTrait.ofHunterClass(Fox.class));
        registerByClass(Rabbit.class, (PreyTrait<Rabbit>) PreyTrait.ofHunterClass(Fox.class));
        registerByPredicate(entity -> entity instanceof Turtle && entity.isBaby(), PreyTrait.ofHunterClass(Fox.class));
        // ocelot prey
        registerByClass(Chicken.class, (PreyTrait<Chicken>) PreyTrait.ofHunterClass(Ocelot.class));
        // hostile attacked by iron golem
        registerByPredicate(entity -> entity instanceof Enemy && !(entity instanceof Creeper), PreyTrait.ofHunterClass(IronGolem.class));
        // hurt by high temperature
        registerByClass(SnowGolem.class, new TemperatureTrait<>());
        // ravager riding
        registerByTag(EntityTypeTags.RAIDERS, (RiderTrait<Evoker>) RiderTrait.ofRideableClass(Ravager.class));
        registerByClass(Skeleton.class, (RiderTrait<Skeleton>) RiderTrait.ofRideableClass(Spider.class));
        // Zombie Horse and Skeleton Horse riding
        registerByPredicate(entity -> entity instanceof Enemy, new RiderTrait<>(List.of(rideable -> rideable instanceof AbstractHorse && rideable instanceof Enemy)));
        // lava walking
        registerByClass(Strider.class, new StandOnFluidTrait<>(FluidTags.LAVA));
        // fall through blocks
        registerByClass(Vex.class, new NoPhysicsTrait<>());
        // can't swim
        registerByClass(IronGolem.class, new CantSwimTrait<>());
        // undrownable
        registerByClass(IronGolem.class, new UndrownableTrait<>());
        // feared
        registerByClass(Wolf.class, (FearedTrait<Wolf>) FearedTrait.ofFearfulClass(AbstractSkeleton.class));
        registerByPredicate(entity -> entity instanceof Ocelot || entity instanceof Cat, FearedTrait.ofFearfulClass(Creeper.class));
        registerByClass(Ocelot.class, (FearedTrait<Ocelot>) FearedTrait.ofFearfulClass(Chicken.class));
        // climb blocks
        registerByClass(Spider.class, new ClimbBlocksTrait<>());
        registerByClass(Spider.class, new ClimbBlocksTrait<>(List.of(Blocks.COBWEB), new ArrayList<>()));
        // reinforcements
        registerByClass(Wolf.class, new ReinforcementsTrait<>());
        registerByClass(Bee.class, new ReinforcementsTrait<>());
        registerByTag(EntityTypeTags.RAIDERS, new ReinforcementsTrait<>(32, new ArrayList<>(), List.of(EntityTypeTags.RAIDERS)));
        // instant die on lightning
        registerByClass(Turtle.class, new InstantDieOnDamageMsgTrait<>("lightningBolt"));
        // cats hunt rabbits
        registerByClass(Rabbit.class, new PreyTrait<>(List.of(entity -> entity instanceof Cat cat && !cat.isTame())));
        // aquatic
        registerByPredicate(entity -> entity.getType().getCategory().getName().contains("water") && entity.canBreatheUnderwater(), new AquaticTrait<>());
        registerByPredicate(entity -> entity.getType().getCategory().getName().contains("water") != entity.canBreatheUnderwater(), new AquaticTrait<>(true, true));
        // dolphin don't like sun
        registerByClass(Dolphin.class, new BurnInDaylightTrait<>());
        // walk on powder snow
        registerByClass(Rabbit.class, new WalkOnPowderSnow<>());
        // slow falling
        registerByClass(Chicken.class, new SlowFallingTrait<>());
        // support deprecated entity tags
        registerByTag(TagKey.create(Registries.ENTITY_TYPE, Walkers.id("burns_in_daylight")), new BurnInDaylightTrait<>());
        registerByTag(TagKey.create(Registries.ENTITY_TYPE, Walkers.id("flying")), new FlyingTrait<>(false));
        registerByTag(TagKey.create(Registries.ENTITY_TYPE, Walkers.id("slow_falling")), new SlowFallingTrait<>());
        registerByTag(TagKey.create(Registries.ENTITY_TYPE, Walkers.id("wolf_prey")), PreyTrait.ofHunterClass(Wolf.class));
        registerByTag(TagKey.create(Registries.ENTITY_TYPE, Walkers.id("fox_prey")), PreyTrait.ofHunterClass(Fox.class));
        registerByTag(TagKey.create(Registries.ENTITY_TYPE, Walkers.id("hurt_by_high_temperature")), new TemperatureTrait<>());
        registerByTag(TagKey.create(Registries.ENTITY_TYPE, Walkers.id("ravager_riding")), RiderTrait.ofRideableClass(Ravager.class));
        registerByTag(TagKey.create(Registries.ENTITY_TYPE, Walkers.id("lava_walking")), new StandOnFluidTrait<>(FluidTags.LAVA));
        registerByTag(TagKey.create(Registries.ENTITY_TYPE, Walkers.id("fall_through_blocks")), new NoPhysicsTrait<>());
        registerByTag(TagKey.create(Registries.ENTITY_TYPE, Walkers.id("cant_swim")), new CantSwimTrait<>());
        registerByTag(TagKey.create(Registries.ENTITY_TYPE, Walkers.id("undrownable")), new UndrownableTrait<>());
        // Attack for Health
        registerByPredicate(entity -> entity.getType().getCategory().equals(MobCategory.MONSTER), new AttackForHealthTrait<>());
        // nocturnal
        registerByPredicate(entity -> entity.getType().getCategory().equals(MobCategory.MONSTER), new NocturnalTrait<>());

        // handle Integrations
        Integrations.registerTraits();
    }

    /**
     * @return a list of every available trait for the specified entity
     */
    @SuppressWarnings("unchecked")
    public static synchronized <L extends LivingEntity> List<ShapeTrait<L>> getAll(L shape) {
        List<ShapeTrait<L>> traits = new ArrayList<>();
        if (shape != null) {
            if (traitsByEntityTypes.containsKey(shape.getType())) {
                traits.addAll(traitsByEntityTypes.get(shape.getType()).stream().map(trait -> (ShapeTrait<L>) trait).toList());
            }
            for (Class<? extends LivingEntity> aClass : traitsByEntityClasses.keySet()) {
                if (aClass.isInstance(shape))
                    traits.addAll(traitsByEntityClasses.get(aClass).stream().map(trait -> (ShapeTrait<L>) trait).toList());
            }
            for (TagKey<EntityType<?>> entityTypeTagKey : traitsByEntityTags.keySet()) {
                if (shape.getType().is(entityTypeTagKey)) {
                    traits.addAll(traitsByEntityTags.get(entityTypeTagKey).stream().map(trait -> (ShapeTrait<L>) trait).toList());
                }
            }
            for (Predicate<LivingEntity> predicate : traitsByPredicates.keySet()) {
                if (predicate.test(shape)) {
                    traits.addAll(traitsByPredicates.get(predicate).stream().map(trait -> (ShapeTrait<L>) trait).toList());
                }
            }
        }
        return traits;
    }

    /**
     * @return a list of every available trait for the specified entity
     */
    public static synchronized <L extends LivingEntity> List<ShapeTrait<L>> get(L shape, ResourceLocation traitId) {
        List<ShapeTrait<L>> traits = getAll(shape);
        List<ShapeTrait<L>> filteredtraits = new ArrayList<>();
        for (ShapeTrait<L> trait : traits) {
            if (trait.getId() == traitId) {
                filteredtraits.add(trait);
            }
        }
        return filteredtraits;
    }

    public static <A extends LivingEntity> void registerByType(EntityType<A> type, ShapeTrait<A> trait) {
        registerByType(type, List.of(trait));
    }

    public static <A extends LivingEntity> void registerByType(EntityType<A> type, List<ShapeTrait<A>> newtraits) {
        List<ShapeTrait<?>> traits = traitsByEntityTypes.containsKey(type) ? traitsByEntityTypes.get(type) : new ArrayList<>();
        for (ShapeTrait<A> trait : newtraits) {
            if (trait.canBeRegisteredMultipleTimes() || traits.stream().noneMatch(entry -> entry.getId().equals(trait.getId()))) {
                traits.add(trait);
            }
        }
        traitsByEntityTypes.put(type, traits);
    }

    public static <A extends LivingEntity> void registerByTag(TagKey<EntityType<?>> tag, ShapeTrait<A> trait) {
        registerByTag(tag, List.of(trait));
    }

    public static <A extends LivingEntity> void registerByTag(TagKey<EntityType<?>> tag, List<ShapeTrait<A>> newtraits) {
        List<ShapeTrait<?>> traits = traitsByEntityTags.containsKey(tag) ? traitsByEntityTags.get(tag) : new ArrayList<>();
        for (ShapeTrait<A> trait : newtraits) {
            if (trait.canBeRegisteredMultipleTimes() || traits.stream().noneMatch(entry -> entry.getId().equals(trait.getId()))) {
                traits.add(trait);
            }
        }
        traitsByEntityTags.put(tag, traits);
    }

    public static <A extends LivingEntity> void registerByClass(Class<A> entityClass, ShapeTrait<A> trait) {
        registerByClass(entityClass, List.of(trait));
    }

    public static <A extends LivingEntity> void registerByClass(Class<A> entityClass, List<ShapeTrait<A>> newtraits) {
        List<ShapeTrait<?>> traits = traitsByEntityClasses.containsKey(entityClass) ? traitsByEntityClasses.get(entityClass) : new ArrayList<>();
        for (ShapeTrait<A> trait : newtraits) {
            if (trait.canBeRegisteredMultipleTimes() || traits.stream().noneMatch(entry -> entry.getId().equals(trait.getId()))) {
                traits.add(trait);
            }
        }
        traitsByEntityClasses.put(entityClass, traits);
    }

    /**
     * Register a trait for a predicate
     *
     * @param entityPredicate this should only be true, if the entity is the correct class for the ability!
     * @param trait           your {@link ShapeAbility}
     */
    public static void registerByPredicate(Predicate<LivingEntity> entityPredicate, ShapeTrait<?> trait) {
        registerByPredicate(entityPredicate, List.of(trait));
    }

    public static void registerByPredicate(Predicate<LivingEntity> entityPredicate, List<ShapeTrait<?>> newtraits) {
        List<ShapeTrait<?>> traits = traitsByPredicates.containsKey(entityPredicate) ? traitsByPredicates.get(entityPredicate) : new ArrayList<>();
        for (ShapeTrait<?> trait : newtraits) {
            if (trait.canBeRegisteredMultipleTimes() || traits.stream().noneMatch(entry -> entry.getId().equals(trait.getId()))) {
                traits.add(trait);
            }
        }
        traitsByPredicates.put(entityPredicate, traits);
    }

    public static void registerCodec(ResourceLocation traitId, Codec<? extends ShapeTrait<?>> traitCodec) {
        traitCodecById.put(traitId, traitCodec);
        traitIdByCodec.put(traitCodec, traitId);
    }

    @Nullable
    public static Codec<? extends ShapeTrait<?>> getTraitCodec(ResourceLocation traitId) {
        return traitCodecById.get(traitId);
    }

    @Nullable
    public static ResourceLocation gettraitId(Codec<? extends ShapeTrait<?>> traitCodec) {
        return traitIdByCodec.get(traitCodec);
    }

    public static <L extends LivingEntity> boolean has(L shape, ResourceLocation traitId) {
        if (shape != null) {
            if (traitsByEntityTypes.containsKey(shape.getType()) && traitsByEntityTypes.get(shape.getType()).stream().anyMatch(trait -> trait.getId() == traitId)) {
                return true;
            }
            for (Class<? extends LivingEntity> aClass : traitsByEntityClasses.keySet()) {
                if (aClass.isInstance(shape) && traitsByEntityClasses.get(aClass).stream().anyMatch(trait -> trait.getId() == traitId)) {
                    return true;
                }
            }
            for (TagKey<EntityType<?>> entityTypeTagKey : traitsByEntityTags.keySet()) {
                if (shape.getType().is(entityTypeTagKey) && traitsByEntityTags.get(entityTypeTagKey).stream().anyMatch(trait -> trait.getId() == traitId)) {
                    return true;
                }
            }
            for (Predicate<LivingEntity> predicate : traitsByPredicates.keySet()) {
                if (predicate.test(shape) && traitsByPredicates.get(predicate).stream().anyMatch(trait -> trait.getId() == traitId)) {
                    return true;
                }
            }
        }
        return false;
    }

    public static void clearAll() {
        traitsByEntityTypes.clear();
        traitsByEntityClasses.clear();
        traitsByEntityTags.clear();
        traitsByPredicates.clear();
    }

    public static Codec<ShapeTrait<?>> getTraitCodec() {
        Codec<Codec<? extends ShapeTrait<?>>> codec = ResourceLocation.CODEC.flatXmap(
                resourceLocation -> Optional.ofNullable(TraitRegistry.getTraitCodec(resourceLocation))
                        .map(DataResult::success)
                        .orElseGet(() -> DataResult.error(() -> "Unknown shape trait: " + resourceLocation)),
                traitCodec -> Optional.ofNullable(gettraitId(traitCodec))
                        .map(DataResult::success)
                        .orElseGet(() -> DataResult.error(() -> "Unknown shape trait codec: " + traitCodec))
        );
        return codec.dispatchStable(ShapeTrait::codec, Function.identity());
    }
}
