package tocraft.walkers.skills;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.EntityTypeTags;
import net.minecraft.tags.FluidTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.*;
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
import tocraft.walkers.skills.impl.*;

import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;

@SuppressWarnings("unused")
public class SkillRegistry {
    private static final Map<Predicate<LivingEntity>, List<ShapeSkill<?>>> skillsByPredicates = new HashMap<>();
    private static final Map<EntityType<? extends LivingEntity>, List<ShapeSkill<?>>> skillsByEntityTypes = new HashMap<>();
    private static final Map<TagKey<EntityType<?>>, List<ShapeSkill<?>>> skillsByEntityTags = new HashMap<>();
    private static final Map<Class<? extends LivingEntity>, List<ShapeSkill<?>>> skillsByEntityClasses = new HashMap<>();
    private static final Map<ResourceLocation, MapCodec<? extends ShapeSkill<?>>> skillCodecById = new HashMap<>();
    private static final Map<MapCodec<? extends ShapeSkill<?>>, ResourceLocation> skillIdByCodec = new IdentityHashMap<>();

    public static void initialize() {
        // register skill codecs
        registerCodec(MobEffectSkill.ID, MobEffectSkill.CODEC);
        registerCodec(BurnInDaylightSkill.ID, BurnInDaylightSkill.CODEC);
        registerCodec(FlyingSkill.ID, FlyingSkill.CODEC);
        registerCodec(PreySkill.ID, PreySkill.CODEC);
        registerCodec(TemperatureSkill.ID, TemperatureSkill.CODEC);
        registerCodec(RiderSkill.ID, RiderSkill.CODEC);
        registerCodec(StandOnFluidSkill.ID, StandOnFluidSkill.CODEC);
        registerCodec(NoPhysicsSkill.ID, NoPhysicsSkill.CODEC);
        registerCodec(CantSwimSkill.ID, CantSwimSkill.CODEC);
        registerCodec(UndrownableSkill.ID, UndrownableSkill.CODEC);
        registerCodec(SlowFallingSkill.ID, SlowFallingSkill.CODEC);
        registerCodec(FearedSkill.ID, FearedSkill.CODEC);
        registerCodec(ClimbBlocksSkill.ID, ClimbBlocksSkill.CODEC);
        registerCodec(ReinforcementsSkill.ID, ReinforcementsSkill.CODEC);
        registerCodec(InstantDieOnDamageMsgSkill.ID, InstantDieOnDamageMsgSkill.CODEC);
        registerCodec(AquaticSkill.ID, AquaticSkill.CODEC);
        registerCodec(WalkOnPowderSnow.ID, WalkOnPowderSnow.CODEC);
        registerCodec(HumanoidSkill.ID, HumanoidSkill.CODEC);
        registerCodec(AttackForHealthSkill.ID, AttackForHealthSkill.CODEC);
        registerCodec(NocturnalSkill.ID, NocturnalSkill.CODEC);
    }

    @SuppressWarnings("unchecked")
    public static void registerDefault() {
        // register skills
        // mob effects
        registerByClass(Bat.class, new MobEffectSkill<>(new MobEffectInstance(MobEffects.NIGHT_VISION, 100000, 0, false, false)));
        // burn in daylight
        registerByClass(Zombie.class, new BurnInDaylightSkill<>());
        registerByClass(Skeleton.class, new BurnInDaylightSkill<>());
        registerByClass(Stray.class, new BurnInDaylightSkill<>());
        registerByClass(Phantom.class, new BurnInDaylightSkill<>());
        // flying
        registerByClass(Allay.class, new FlyingSkill<>());
        registerByClass(Bat.class, new FlyingSkill<>());
        registerByClass(Bee.class, new FlyingSkill<>());
        registerByClass(Blaze.class, new FlyingSkill<>());
        registerByClass(EnderDragon.class, new FlyingSkill<>());
        registerByClass(FlyingMob.class, new FlyingSkill<>());
        registerByClass(Parrot.class, new FlyingSkill<>());
        registerByClass(Vex.class, new FlyingSkill<>());
        registerByClass(WitherBoss.class, new FlyingSkill<>());
        // wolf prey
        registerByClass(Bat.class, (PreySkill<Bat>) PreySkill.ofHunterClass(Wolf.class));
        registerByClass(Fox.class, (PreySkill<Fox>) PreySkill.ofHunterClass(Wolf.class));
        registerByClass(Sheep.class, (PreySkill<Sheep>) PreySkill.ofHunterClass(Wolf.class));
        registerByClass(Skeleton.class, (PreySkill<Skeleton>) PreySkill.ofHunterClass(Wolf.class));
        registerByClass(Parrot.class, (PreySkill<Parrot>) PreySkill.ofHunterClass(Wolf.class));
        registerByClass(Rabbit.class, (PreySkill<Rabbit>) PreySkill.ofHunterClass(Wolf.class));
        // fox prey
        registerByClass(Chicken.class, (PreySkill<Chicken>) PreySkill.ofHunterClass(Fox.class));
        registerByClass(Rabbit.class, (PreySkill<Rabbit>) PreySkill.ofHunterClass(Fox.class));
        registerByPredicate(entity -> entity instanceof Turtle && entity.isBaby(), PreySkill.ofHunterClass(Fox.class));
        // ocelot prey
        registerByClass(Chicken.class, (PreySkill<Chicken>) PreySkill.ofHunterClass(Ocelot.class));
        // hostile attacked by iron golem
        registerByPredicate(entity -> entity instanceof Enemy && !(entity instanceof Creeper), PreySkill.ofHunterClass(IronGolem.class));
        // hurt by high temperature
        registerByClass(SnowGolem.class, new TemperatureSkill<>());
        // ravager riding
        registerByTag(EntityTypeTags.RAIDERS, (RiderSkill<Evoker>) RiderSkill.ofRideableClass(Ravager.class));
        registerByClass(Skeleton.class, (RiderSkill<Skeleton>) RiderSkill.ofRideableClass(Spider.class));
        // Zombie Horse and Skeleton Horse riding
        registerByPredicate(entity -> entity instanceof Enemy, new RiderSkill<>(List.of(rideable -> rideable instanceof AbstractHorse && rideable instanceof Enemy)));
        // lava walking
        registerByClass(Strider.class, new StandOnFluidSkill<>(FluidTags.LAVA));
        // fall through blocks
        registerByClass(Vex.class, new NoPhysicsSkill<>());
        // can't swim
        registerByClass(IronGolem.class, new CantSwimSkill<>());
        // undrownable
        registerByClass(IronGolem.class, new UndrownableSkill<>());
        // feared
        registerByClass(Wolf.class, (FearedSkill<Wolf>) FearedSkill.ofFearfulClass(AbstractSkeleton.class));
        registerByPredicate(entity -> entity instanceof Ocelot || entity instanceof Cat, FearedSkill.ofFearfulClass(Creeper.class));
        registerByClass(Ocelot.class, (FearedSkill<Ocelot>) FearedSkill.ofFearfulClass(Chicken.class));
        // climb blocks
        registerByClass(Spider.class, new ClimbBlocksSkill<>());
        registerByClass(Spider.class, new ClimbBlocksSkill<>(List.of(Blocks.COBWEB), new ArrayList<>()));
        // reinforcements
        registerByClass(Wolf.class, new ReinforcementsSkill<>());
        registerByClass(Bee.class, new ReinforcementsSkill<>());
        registerByTag(EntityTypeTags.RAIDERS, new ReinforcementsSkill<>(32, new ArrayList<>(), List.of(EntityTypeTags.RAIDERS)));
        // instant die on lightning
        registerByClass(Turtle.class, new InstantDieOnDamageMsgSkill<>("lightningBolt"));
        // cats hunt rabbits
        registerByClass(Rabbit.class, new PreySkill<>(List.of(entity -> entity instanceof Cat cat && !cat.isTame())));
        // aquatic
        registerByPredicate(entity -> entity instanceof Mob mob && mob.getType().getCategory().getName().contains("water") && mob instanceof WaterAnimal, new AquaticSkill<>(0));
        registerByPredicate(entity -> entity instanceof Mob mob && mob.getType().getCategory().getName().contains("water") && !(mob instanceof WaterAnimal), new AquaticSkill<>(1));
        // dolphin don't like sun
        registerByClass(Dolphin.class, new BurnInDaylightSkill<>());
        // walk on powder snow
        registerByClass(Rabbit.class, new WalkOnPowderSnow<>());
        // slow falling
        registerByClass(Chicken.class, new SlowFallingSkill<>());
        // support deprecated entity tags
        registerByTag(TagKey.create(Registries.ENTITY_TYPE, Walkers.id("burns_in_daylight")), new BurnInDaylightSkill<>());
        registerByTag(TagKey.create(Registries.ENTITY_TYPE, Walkers.id("flying")), new FlyingSkill<>(false));
        registerByTag(TagKey.create(Registries.ENTITY_TYPE, Walkers.id("slow_falling")), new SlowFallingSkill<>());
        registerByTag(TagKey.create(Registries.ENTITY_TYPE, Walkers.id("wolf_prey")), PreySkill.ofHunterClass(Wolf.class));
        registerByTag(TagKey.create(Registries.ENTITY_TYPE, Walkers.id("fox_prey")), PreySkill.ofHunterClass(Fox.class));
        registerByTag(TagKey.create(Registries.ENTITY_TYPE, Walkers.id("hurt_by_high_temperature")), new TemperatureSkill<>());
        registerByTag(TagKey.create(Registries.ENTITY_TYPE, Walkers.id("ravager_riding")), RiderSkill.ofRideableClass(Ravager.class));
        registerByTag(TagKey.create(Registries.ENTITY_TYPE, Walkers.id("lava_walking")), new StandOnFluidSkill<>(FluidTags.LAVA));
        registerByTag(TagKey.create(Registries.ENTITY_TYPE, Walkers.id("fall_through_blocks")), new NoPhysicsSkill<>());
        registerByTag(TagKey.create(Registries.ENTITY_TYPE, Walkers.id("cant_swim")), new CantSwimSkill<>());
        registerByTag(TagKey.create(Registries.ENTITY_TYPE, Walkers.id("undrownable")), new UndrownableSkill<>());
        // Attack for Health
        registerByPredicate(entity -> entity.getType().getCategory().equals(MobCategory.MONSTER), new AttackForHealthSkill<>());
        // nocturnal
        registerByPredicate(entity -> entity.getType().getCategory().equals(MobCategory.MONSTER), new NocturnalSkill<>());

        // handle Integrations
        Integrations.registerSkills();
    }

    /**
     * @return a list of every available skill for the specified entity
     */
    @SuppressWarnings("unchecked")
    public static synchronized <L extends LivingEntity> List<ShapeSkill<L>> getAll(L shape) {
        List<ShapeSkill<L>> skills = new ArrayList<>();
        if (shape != null) {
            if (skillsByEntityTypes.containsKey(shape.getType())) {
                skills.addAll(skillsByEntityTypes.get(shape.getType()).stream().map(skill -> (ShapeSkill<L>) skill).toList());
            }
            for (Class<? extends LivingEntity> aClass : skillsByEntityClasses.keySet()) {
                if (aClass.isInstance(shape))
                    skills.addAll(skillsByEntityClasses.get(aClass).stream().map(skill -> (ShapeSkill<L>) skill).toList());
            }
            for (TagKey<EntityType<?>> entityTypeTagKey : skillsByEntityTags.keySet()) {
                if (shape.getType().is(entityTypeTagKey)) {
                    skills.addAll(skillsByEntityTags.get(entityTypeTagKey).stream().map(skill -> (ShapeSkill<L>) skill).toList());
                }
            }
            for (Predicate<LivingEntity> predicate : skillsByPredicates.keySet()) {
                if (predicate.test(shape)) {
                    skills.addAll(skillsByPredicates.get(predicate).stream().map(skill -> (ShapeSkill<L>) skill).toList());
                }
            }
        }
        return skills;
    }

    /**
     * @return a list of every available skill for the specified entity
     */
    public static synchronized <L extends LivingEntity> List<ShapeSkill<L>> get(L shape, ResourceLocation skillId) {
        List<ShapeSkill<L>> skills = getAll(shape);
        List<ShapeSkill<L>> filteredSkills = new ArrayList<>();
        for (ShapeSkill<L> skill : skills) {
            if (skill.getId() == skillId) {
                filteredSkills.add(skill);
            }
        }
        return filteredSkills;
    }

    public static <A extends LivingEntity> void registerByType(EntityType<A> type, ShapeSkill<A> skill) {
        registerByType(type, List.of(skill));
    }

    public static <A extends LivingEntity> void registerByType(EntityType<A> type, List<ShapeSkill<A>> newSkills) {
        List<ShapeSkill<?>> skills = skillsByEntityTypes.containsKey(type) ? skillsByEntityTypes.get(type) : new ArrayList<>();
        for (ShapeSkill<A> skill : newSkills) {
            if (skill.canBeRegisteredMultipleTimes() || skills.stream().noneMatch(entry -> entry.getId().equals(skill.getId()))) {
                skills.add(skill);
            }
        }
        skillsByEntityTypes.put(type, skills);
    }

    public static <A extends LivingEntity> void registerByTag(TagKey<EntityType<?>> tag, ShapeSkill<A> skill) {
        registerByTag(tag, List.of(skill));
    }

    public static <A extends LivingEntity> void registerByTag(TagKey<EntityType<?>> tag, List<ShapeSkill<A>> newSkills) {
        List<ShapeSkill<?>> skills = skillsByEntityTags.containsKey(tag) ? skillsByEntityTags.get(tag) : new ArrayList<>();
        for (ShapeSkill<A> skill : newSkills) {
            if (skill.canBeRegisteredMultipleTimes() || skills.stream().noneMatch(entry -> entry.getId().equals(skill.getId()))) {
                skills.add(skill);
            }
        }
        skillsByEntityTags.put(tag, skills);
    }

    public static <A extends LivingEntity> void registerByClass(Class<A> entityClass, ShapeSkill<A> skill) {
        registerByClass(entityClass, List.of(skill));
    }

    public static <A extends LivingEntity> void registerByClass(Class<A> entityClass, List<ShapeSkill<A>> newSkills) {
        List<ShapeSkill<?>> skills = skillsByEntityClasses.containsKey(entityClass) ? skillsByEntityClasses.get(entityClass) : new ArrayList<>();
        for (ShapeSkill<A> skill : newSkills) {
            if (skill.canBeRegisteredMultipleTimes() || skills.stream().noneMatch(entry -> entry.getId().equals(skill.getId()))) {
                skills.add(skill);
            }
        }
        skillsByEntityClasses.put(entityClass, skills);
    }

    /**
     * Register a skill for a predicate
     *
     * @param entityPredicate this should only be true, if the entity is the correct class for the ability!
     * @param skill           your {@link ShapeAbility}
     */
    public static void registerByPredicate(Predicate<LivingEntity> entityPredicate, ShapeSkill<?> skill) {
        registerByPredicate(entityPredicate, List.of(skill));
    }

    public static void registerByPredicate(Predicate<LivingEntity> entityPredicate, List<ShapeSkill<?>> newSkills) {
        List<ShapeSkill<?>> skills = skillsByPredicates.containsKey(entityPredicate) ? skillsByPredicates.get(entityPredicate) : new ArrayList<>();
        for (ShapeSkill<?> skill : newSkills) {
            if (skill.canBeRegisteredMultipleTimes() || skills.stream().noneMatch(entry -> entry.getId().equals(skill.getId()))) {
                skills.add(skill);
            }
        }
        skillsByPredicates.put(entityPredicate, skills);
    }

    public static void registerCodec(ResourceLocation skillId, MapCodec<? extends ShapeSkill<?>> skillCodec) {
        skillCodecById.put(skillId, skillCodec);
        skillIdByCodec.put(skillCodec, skillId);
    }

    @Nullable
    public static MapCodec<? extends ShapeSkill<?>> getSkillCodec(ResourceLocation skillId) {
        return skillCodecById.get(skillId);
    }

    @Nullable
    public static ResourceLocation getSkillId(MapCodec<? extends ShapeSkill<?>> skillCodec) {
        return skillIdByCodec.get(skillCodec);
    }

    public static <L extends LivingEntity> boolean has(L shape, ResourceLocation skillId) {
        if (shape != null) {
            if (skillsByEntityTypes.containsKey(shape.getType()) && skillsByEntityTypes.get(shape.getType()).stream().anyMatch(skill -> skill.getId() == skillId)) {
                return true;
            }
            for (Class<? extends LivingEntity> aClass : skillsByEntityClasses.keySet()) {
                if (aClass.isInstance(shape) && skillsByEntityClasses.get(aClass).stream().anyMatch(skill -> skill.getId() == skillId)) {
                    return true;
                }
            }
            for (TagKey<EntityType<?>> entityTypeTagKey : skillsByEntityTags.keySet()) {
                if (shape.getType().is(entityTypeTagKey) && skillsByEntityTags.get(entityTypeTagKey).stream().anyMatch(skill -> skill.getId() == skillId)) {
                    return true;
                }
            }
            for (Predicate<LivingEntity> predicate : skillsByPredicates.keySet()) {
                if (predicate.test(shape) && skillsByPredicates.get(predicate).stream().anyMatch(skill -> skill.getId() == skillId)) {
                    return true;
                }
            }
        }
        return false;
    }

    public static void clearAll() {
        skillsByEntityTypes.clear();
        skillsByEntityClasses.clear();
        skillsByEntityTags.clear();
        skillsByPredicates.clear();
    }

    public static Codec<ShapeSkill<?>> getSkillCodec() {
        Codec<MapCodec<? extends ShapeSkill<?>>> codec = ResourceLocation.CODEC.flatXmap(
                resourceLocation -> Optional.ofNullable(SkillRegistry.getSkillCodec(resourceLocation))
                        .map(DataResult::success)
                        .orElseGet(() -> DataResult.error(() -> "Unknown shape skill: " + resourceLocation)),
                skillCodec -> Optional.ofNullable(getSkillId(skillCodec))
                        .map(DataResult::success)
                        .orElseGet(() -> DataResult.error(() -> "Unknown shape skill codec: " + skillCodec))
        );
        return codec.dispatchStable(ShapeSkill::codec, Function.identity());
    }
}
