package dev.tocraft.walkers.traits;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import dev.tocraft.walkers.Walkers;
import dev.tocraft.walkers.ability.ShapeAbility;
import dev.tocraft.walkers.integrations.AbstractIntegration;
import dev.tocraft.walkers.integrations.Integrations;
import dev.tocraft.walkers.traits.impl.*;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.EntityTypeTags;
import net.minecraft.tags.FluidTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.ambient.Bat;
import net.minecraft.world.entity.animal.*;
import net.minecraft.world.entity.animal.allay.Allay;
import net.minecraft.world.entity.animal.axolotl.Axolotl;
import net.minecraft.world.entity.animal.horse.AbstractHorse;
import net.minecraft.world.entity.animal.sheep.Sheep;
import net.minecraft.world.entity.animal.wolf.Wolf;
import net.minecraft.world.entity.boss.enderdragon.EnderDragon;
import net.minecraft.world.entity.boss.wither.WitherBoss;
import net.minecraft.world.entity.monster.*;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.raid.Raider;
import net.minecraft.world.level.block.Blocks;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Function;
import java.util.function.Predicate;

@SuppressWarnings("unused")
public class TraitRegistry {
    private static final Map<Predicate<LivingEntity>, List<ShapeTrait<?>>> traitsByPredicates = new ConcurrentHashMap<>();
    private static final Map<EntityType<? extends LivingEntity>, List<ShapeTrait<?>>> traitsByEntityTypes = new ConcurrentHashMap<>();
    private static final Map<TagKey<EntityType<?>>, List<ShapeTrait<?>>> traitsByEntityTags = new ConcurrentHashMap<>();
    private static final Map<Class<? extends LivingEntity>, List<ShapeTrait<?>>> traitsByEntityClasses = new ConcurrentHashMap<>();
    private static final Map<ResourceLocation, MapCodec<? extends ShapeTrait<?>>> traitCodecById = new HashMap<>();
    private static final Map<MapCodec<? extends ShapeTrait<?>>, ResourceLocation> traitIdByCodec = new IdentityHashMap<>();

    @ApiStatus.Internal
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
        registerCodec(CantInteractTrait.ID, CantInteractTrait.CODEC);
        registerCodec(ImmunityTrait.ID, ImmunityTrait.CODEC);
        registerCodec(CantFreezeTrait.ID, CantFreezeTrait.CODEC);
        registerCodec(InvulnerabilityTrait.ID, InvulnerabilityTrait.CODEC);
    }

    @SuppressWarnings("unchecked")
    @ApiStatus.Internal
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
        registerByClass(Allay.class, new MobEffectTrait<>(new MobEffectInstance(MobEffects.REGENERATION, 40, 2, false, false)));
        registerByClass(Allay.class, new AttackForHealthTrait<>());
        registerByClass(Bat.class, new FlyingTrait<>());
        registerByClass(Bee.class, new FlyingTrait<>());
        registerByClass(Blaze.class, new FlyingTrait<>());
        registerByClass(EnderDragon.class, new FlyingTrait<>());
        registerByPredicate(entity -> entity instanceof FlyingAnimal, new FlyingTrait<>());
        registerByClass(Parrot.class, new FlyingTrait<>());
        registerByClass(Vex.class, new FlyingTrait<>());
        registerByClass(WitherBoss.class, new FlyingTrait<>());
        registerByClass(Ghast.class, new FlyingTrait<>());
        registerByClass(HappyGhast.class, new FlyingTrait<>());
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
        // axolotl prey
        registerByTag(EntityTypeTags.AXOLOTL_HUNT_TARGETS, PreyTrait.ofHunterClass(Axolotl.class));
        registerByTag(EntityTypeTags.AXOLOTL_ALWAYS_HOSTILES, PreyTrait.ofHunterClass(Axolotl.class));
        // hostile attacked by iron golem
        registerByPredicate(entity -> entity instanceof Enemy && !(entity instanceof Creeper), new PreyTrait<>(List.of(), List.of(), List.of(IronGolem.class), List.of(), 3, 5));
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
        registerByClass(Axolotl.class, (FearedTrait<Axolotl>) FearedTrait.ofFearfulTag(EntityTypeTags.AXOLOTL_HUNT_TARGETS));
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
        registerByClass(Dolphin.class, new AquaticTrait<>());
        // walk on powder snow
        registerByClass(Rabbit.class, new WalkOnPowderSnow<>());
        registerByTag(EntityTypeTags.POWDER_SNOW_WALKABLE_MOBS, new WalkOnPowderSnow<>());
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
        // cant interact with...
        registerByClass(Raider.class, new CantInteractTrait<>(List.of(Villager.class)));
        // immunity
        registerByClass(WitherBoss.class, new ImmunityTrait<>(MobEffects.WITHER.value()));
        registerByClass(WitherSkeleton.class, new ImmunityTrait<>(MobEffects.WITHER.value()));
        // can't freeze
        registerByClass(SnowGolem.class, new CantFreezeTrait<>());
        registerByClass(PolarBear.class, new CantFreezeTrait<>());
        registerByClass(Stray.class, new CantFreezeTrait<>());
        registerByClass(WitherBoss.class, new CantFreezeTrait<>());
        registerByTag(EntityTypeTags.FREEZE_IMMUNE_ENTITY_TYPES, new CantFreezeTrait<>());
        // no damage
        registerByType(EntityType.CREAKING, new InvulnerabilityTrait<>());

        // handle Integrations
        Integrations.registerTraits();
    }

    /**
     * @return a list of every available trait for the specified entity
     */
    @SuppressWarnings("unchecked")
    public static synchronized <L extends LivingEntity> @NotNull List<ShapeTrait<L>> getAll(L shape) {
        List<ShapeTrait<L>> traits = new ArrayList<>();
        if (shape != null) {
            List<ShapeTrait<?>> list = traitsByEntityTypes.get(shape.getType());
            if (list != null) {
                traits.addAll(list.stream().map(trait -> (ShapeTrait<L>) trait).toList());
            }
            for (Map.Entry<Class<? extends LivingEntity>, List<ShapeTrait<?>>> entry : traitsByEntityClasses.entrySet()) {
                if (entry.getKey().isInstance(shape)) {
                    traits.addAll(entry.getValue().stream().map(trait -> (ShapeTrait<L>) trait).toList());
                }
            }
            for (Map.Entry<TagKey<EntityType<?>>, List<ShapeTrait<?>>> entry : traitsByEntityTags.entrySet()) {
                if (shape.getType().is(entry.getKey())) {
                    traits.addAll(entry.getValue().stream().map(trait -> (ShapeTrait<L>) trait).toList());
                }
            }
            for (Map.Entry<Predicate<LivingEntity>, List<ShapeTrait<?>>> entry : traitsByPredicates.entrySet()) {
                if (entry.getKey().test(shape)) {
                    traits.addAll(entry.getValue().stream().map(trait -> (ShapeTrait<L>) trait).toList());
                }
            }

            return filterTraits(shape.getType(), traits);
        }

        return traits;
    }

    /**
     * @return a list of every available trait for the specified entity
     */
    public static synchronized <L extends LivingEntity> @NotNull List<ShapeTrait<L>> get(L shape, ResourceLocation traitId) {
        List<ShapeTrait<L>> traits = getAll(shape);
        List<ShapeTrait<L>> filteredTraits = new ArrayList<>();
        for (ShapeTrait<L> trait : traits) {
            if (trait.getId() == traitId) {
                filteredTraits.add(trait);
            }
        }
        return filteredTraits;
    }

    @ApiStatus.Experimental
    public static synchronized @NotNull Map<ShapeTrait<?>, Predicate<LivingEntity>> getAllRegisteredById(ResourceLocation traitId) {
        Map<ShapeTrait<?>, Predicate<LivingEntity>> traits = new HashMap<>();
        for (Map.Entry<EntityType<? extends LivingEntity>, List<ShapeTrait<?>>> traitList : traitsByEntityTypes.entrySet()) {
            for (ShapeTrait<?> trait : traitList.getValue()) {
                if (trait.getId() == traitId) {
                    traits.put(trait, entity -> entity.getType().equals(traitList.getKey()) && notBlacklisted(entity.getType(), traitId));
                }
            }
        }
        for (Map.Entry<Class<? extends LivingEntity>, List<ShapeTrait<?>>> traitList : traitsByEntityClasses.entrySet()) {
            for (ShapeTrait<?> trait : traitList.getValue()) {
                if (trait.getId() == traitId) {
                    traits.put(trait, entity -> traitList.getKey().isInstance(entity) && notBlacklisted(entity.getType(), traitId));
                }
            }
        }
        for (Map.Entry<TagKey<EntityType<?>>, List<ShapeTrait<?>>> traitList : traitsByEntityTags.entrySet()) {
            for (ShapeTrait<?> trait : traitList.getValue()) {
                if (trait.getId() == traitId) {
                    traits.put(trait, entity -> entity.getType().is(traitList.getKey()) && notBlacklisted(entity.getType(), traitId));
                }
            }
        }
        for (Map.Entry<Predicate<LivingEntity>, List<ShapeTrait<?>>> traitList : traitsByPredicates.entrySet()) {
            for (ShapeTrait<?> trait : traitList.getValue()) {
                if (trait.getId() == traitId) {
                    traits.put(trait, traitList.getKey());
                }
            }
        }
        return traits;
    }

    /**
     * must be called within {@link #registerDefault()} or {@link AbstractIntegration#registerTraits() Integration.registerTraits()}
     */
    public static <A extends LivingEntity> void registerByType(EntityType<A> type, ShapeTrait<A> trait) {
        registerByType(type, List.of(trait));
    }

    /**
     * must be called within {@link #registerDefault()} or {@link AbstractIntegration#registerTraits() Integration.registerTraits()}
     */
    public static <A extends LivingEntity> void registerByType(EntityType<A> type, @NotNull List<ShapeTrait<A>> newtraits) {
        List<ShapeTrait<?>> traits = traitsByEntityTypes.getOrDefault(type, new CopyOnWriteArrayList<>());
        for (ShapeTrait<A> trait : newtraits) {
            if (trait.canBeRegisteredMultipleTimes() || traits.stream().noneMatch(entry -> entry.getId().equals(trait.getId()))) {
                traits.add(trait);
            }
        }
        traitsByEntityTypes.put(type, traits);
    }

    /**
     * must be called within {@link #registerDefault()} or {@link AbstractIntegration#registerTraits() Integration.registerTraits()}
     */
    public static <A extends LivingEntity> void registerByTag(TagKey<EntityType<?>> tag, ShapeTrait<A> trait) {
        registerByTag(tag, List.of(trait));
    }

    /**
     * must be called within {@link #registerDefault()} or {@link AbstractIntegration#registerTraits() Integration.registerTraits()}
     */
    public static <A extends LivingEntity> void registerByTag(TagKey<EntityType<?>> tag, @NotNull List<ShapeTrait<A>> newtraits) {
        List<ShapeTrait<?>> traits = traitsByEntityTags.getOrDefault(tag, new CopyOnWriteArrayList<>());
        for (ShapeTrait<A> trait : newtraits) {
            if (trait.canBeRegisteredMultipleTimes() || traits.stream().noneMatch(entry -> entry.getId().equals(trait.getId()))) {
                traits.add(trait);
            }
        }
        traitsByEntityTags.put(tag, traits);
    }

    /**
     * must be called within {@link #registerDefault()} or {@link AbstractIntegration#registerTraits() Integration.registerTraits()}
     */
    public static <A extends LivingEntity> void registerByClass(Class<A> entityClass, ShapeTrait<A> trait) {
        registerByClass(entityClass, List.of(trait));
    }

    /**
     * must be called within {@link #registerDefault()} or {@link AbstractIntegration#registerTraits() Integration.registerTraits()}
     */
    public static <A extends LivingEntity> void registerByClass(Class<A> entityClass, @NotNull List<ShapeTrait<A>> newtraits) {
        List<ShapeTrait<?>> traits = traitsByEntityClasses.getOrDefault(entityClass, new CopyOnWriteArrayList<>());
        for (ShapeTrait<A> trait : newtraits) {
            if (trait.canBeRegisteredMultipleTimes() || traits.stream().noneMatch(entry -> entry.getId().equals(trait.getId()))) {
                traits.add(trait);
            }
        }
        traitsByEntityClasses.put(entityClass, traits);
    }

    /**
     * Register a trait for a predicate
     * must be called within {@link #registerDefault()} or {@link AbstractIntegration#registerTraits() Integration.registerTraits()}
     *
     * @param entityPredicate this should only be true, if the entity is the correct class for the ability!
     * @param trait           your {@link ShapeAbility}
     */
    public static void registerByPredicate(Predicate<LivingEntity> entityPredicate, ShapeTrait<?> trait) {
        registerByPredicate(entityPredicate, List.of(trait));
    }

    /**
     * Register a list of traits for a predicate
     * must be called within {@link #registerDefault()} or {@link AbstractIntegration#registerTraits() Integration.registerTraits()}
     */
    public static void registerByPredicate(Predicate<LivingEntity> entityPredicate, @NotNull List<ShapeTrait<?>> newTraits) {
        List<ShapeTrait<?>> traits = traitsByPredicates.getOrDefault(entityPredicate, new CopyOnWriteArrayList<>());
        for (ShapeTrait<?> trait : newTraits) {
            if (trait.canBeRegisteredMultipleTimes() || traits.stream().noneMatch(entry -> entry.getId().equals(trait.getId()))) {
                traits.add(trait);
            }
        }
        traitsByPredicates.put(entityPredicate, traits);
    }

    public static void registerCodec(ResourceLocation traitId, MapCodec<? extends ShapeTrait<?>> traitCodec) {
        traitCodecById.put(traitId, traitCodec);
        traitIdByCodec.put(traitCodec, traitId);
    }

    @Nullable
    @ApiStatus.Internal
    public static MapCodec<? extends ShapeTrait<?>> getTraitCodec(ResourceLocation traitId) {
        return traitCodecById.get(traitId);
    }

    @Nullable
    @ApiStatus.Internal
    public static ResourceLocation getTraitId(MapCodec<? extends ShapeTrait<?>> traitCodec) {
        return traitIdByCodec.get(traitCodec);
    }

    public static <L extends LivingEntity> boolean has(L shape, ResourceLocation traitId) {
        if (shape != null) {
            List<ShapeTrait<?>> list = traitsByEntityTypes.get(shape.getType());
            if (list != null && list.stream().anyMatch(trait -> trait.getId() == traitId)) {
                return notBlacklisted(shape.getType(), traitId);
            }
            for (Map.Entry<Class<? extends LivingEntity>, List<ShapeTrait<?>>> entry : traitsByEntityClasses.entrySet()) {
                if (entry.getKey().isInstance(shape) && entry.getValue().stream().anyMatch(trait -> trait.getId() == traitId)) {
                    return notBlacklisted(shape.getType(), traitId);
                }
            }
            for (Map.Entry<TagKey<EntityType<?>>, List<ShapeTrait<?>>> entry : traitsByEntityTags.entrySet()) {
                if (shape.getType().is(entry.getKey()) && entry.getValue().stream().anyMatch(trait -> trait.getId() == traitId)) {
                    return notBlacklisted(shape.getType(), traitId);
                }
            }
            for (Map.Entry<Predicate<LivingEntity>, List<ShapeTrait<?>>> entry : traitsByPredicates.entrySet()) {
                if (entry.getKey().test(shape) && entry.getValue().stream().anyMatch(trait -> trait.getId() == traitId)) {
                    return notBlacklisted(shape.getType(), traitId);
                }
            }
        }
        return false;
    }

    @ApiStatus.Internal
    private static boolean notBlacklisted(EntityType<?> type, @NotNull ResourceLocation traitId) {
        return notBlacklisted(EntityType.getKey(type).toString(), traitId.toString());
    }

    private static boolean notBlacklisted(String type, String traitId) {
        return !Walkers.CONFIG.traitBlacklist.getOrDefault(type, List.of()).contains(traitId);
    }

    @ApiStatus.Internal
    private static <L extends LivingEntity> @NotNull List<ShapeTrait<L>> filterTraits(EntityType<?> type, @NotNull List<ShapeTrait<L>> traits) {
        List<ShapeTrait<L>> filtered = new ArrayList<>();

        String typeId = EntityType.getKey(type).toString();
        for (ShapeTrait<L> trait : traits) {
            if (notBlacklisted(typeId, trait.getId().toString())) {
                filtered.add(trait);
            }
        }

        return filtered;
    }

    @ApiStatus.Internal
    public static void clearAll() {
        traitsByEntityTypes.clear();
        traitsByEntityClasses.clear();
        traitsByEntityTags.clear();
        traitsByPredicates.clear();
    }

    @ApiStatus.Internal
    public static Codec<ShapeTrait<?>> getTraitCodec() {
        Codec<MapCodec<? extends ShapeTrait<?>>> codec = ResourceLocation.CODEC.flatXmap(
                resourceLocation -> Optional.ofNullable(TraitRegistry.getTraitCodec(resourceLocation))
                        .map(DataResult::success)
                        .orElseGet(() -> DataResult.error(() -> "Unknown shape trait: " + resourceLocation)),
                traitCodec -> Optional.ofNullable(getTraitId(traitCodec))
                        .map(DataResult::success)
                        .orElseGet(() -> DataResult.error(() -> "Unknown shape trait codec: " + traitCodec))
        );
        return codec.dispatchStable(ShapeTrait::codec, Function.identity());
    }
}
