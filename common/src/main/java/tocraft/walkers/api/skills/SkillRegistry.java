package tocraft.walkers.api.skills;

import com.mojang.serialization.Codec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.FlyingMob;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ambient.Bat;
import net.minecraft.world.entity.animal.*;
import net.minecraft.world.entity.animal.allay.Allay;
import net.minecraft.world.entity.boss.enderdragon.EnderDragon;
import net.minecraft.world.entity.boss.wither.WitherBoss;
import net.minecraft.world.entity.monster.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import tocraft.walkers.ability.ShapeAbility;
import tocraft.walkers.api.skills.impl.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

public class SkillRegistry {
    private static final Map<Predicate<LivingEntity>, ShapeSkill<?>> skills = new HashMap<>();
    private static final Map<ResourceLocation, Codec<? extends ShapeSkill<?>>> skillCodecs = new HashMap<>();


    @SuppressWarnings("unchecked")
    public static void init() {
        // register skill codecs
        registerCodec(MobEffectSkill.ID, MobEffectSkill.CODEC);
        registerCodec(BurnInDaylightSkill.ID, BurnInDaylightSkill.CODEC);
        registerCodec(FlyingSkill.ID, FlyingSkill.CODEC);
        registerCodec(PreySkill.ID, PreySkill.CODEC);
        // register skills
        // mob effects
        register(Bat.class, new MobEffectSkill<>(new MobEffectInstance(MobEffects.NIGHT_VISION, 100000, 0, false, false)));
        // burn in daylight
        register(Zombie.class, new BurnInDaylightSkill<>());
        register(Skeleton.class, new BurnInDaylightSkill<>());
        register(Stray.class, new BurnInDaylightSkill<>());
        register(Phantom.class, new BurnInDaylightSkill<>());
        // flying
        register(Allay.class, new FlyingSkill<>());
        register(Bat.class, new FlyingSkill<>());
        register(Bee.class, new FlyingSkill<>());
        register(Blaze.class, new FlyingSkill<>());
        register(EnderDragon.class, new FlyingSkill<>());
        register(FlyingMob.class, new FlyingSkill<>());
        register(Parrot.class, new FlyingSkill<>());
        register(Vex.class, new FlyingSkill<>());
        register(WitherBoss.class, new FlyingSkill<>());
        // wolf prey
        register(Bat.class, (PreySkill<Bat>) PreySkill.ofHunterClass(Wolf.class));
        register(Fox.class, (PreySkill<Fox>) PreySkill.ofHunterClass(Wolf.class));
        register(Sheep.class, (PreySkill<Sheep>) PreySkill.ofHunterClass(Wolf.class));
        register(Skeleton.class, (PreySkill<Skeleton>) PreySkill.ofHunterClass(Wolf.class));
        register(Parrot.class, (PreySkill<Parrot>) PreySkill.ofHunterClass(Wolf.class));
        // fox prey
        register(Chicken.class, (PreySkill<Chicken>) PreySkill.ofHunterClass(Fox.class));
        register(Rabbit.class, (PreySkill<Rabbit>) PreySkill.ofHunterClass(Fox.class));
        // hurt by high temperature
        register(SnowGolem.class, new TemperatureSkill<>());
    }

    /**
     * @return a list of every available skill for the specified entity
     */
    @SuppressWarnings("unchecked")
    public static <L extends LivingEntity> List<ShapeSkill<L>> getAll(@NotNull L shape) {
        List<ShapeSkill<L>> skillList = new ArrayList<>();
        List<ShapeSkill<?>> unformulatedSkills = new ArrayList<>(skills.entrySet().stream().filter(entry -> entry.getKey().test(shape)).map(Map.Entry::getValue).toList());
        for (ShapeSkill<?> unformatedSkill : unformulatedSkills) {
            skillList.add((ShapeSkill<L>) unformatedSkill);
        }
        return skillList;
    }

    /**
     * @return a list of every available skill for the specified entity
     */
    public static <L extends LivingEntity> List<ShapeSkill<L>> get(@NotNull L shape, ResourceLocation skillId) {
        List<ShapeSkill<L>> skillList = new ArrayList<>();
        for (ShapeSkill<L> skill : getAll(shape)) {
            if (skill.getId() == skillId) {
                skillList.add(skill);
            }
        }
        return skillList;
    }


    public static <A extends LivingEntity> void register(EntityType<A> type, ShapeSkill<A> skill) {
        register(livingEntity -> type.equals(livingEntity.getType()), skill);
    }

    public static <A extends LivingEntity> void register(Class<A> entityClass, ShapeSkill<A> skill) {
        register(entityClass::isInstance, skill);
    }

    /**
     * Register a skill for a predicate
     *
     * @param entityPredicate this should only be true, if the entity is the correct class for the ability!
     * @param skill           your {@link ShapeAbility}
     */
    public static void register(Predicate<LivingEntity> entityPredicate, ShapeSkill<?> skill) {
        skills.put(entityPredicate, skill);
    }

    public static void registerCodec(ResourceLocation skillId, Codec<? extends ShapeSkill<?>> skillCodec) {
        skillCodecs.put(skillId, skillCodec);
    }

    @Nullable
    public static Codec<? extends ShapeSkill<?>> getSkillCodec(ResourceLocation skillId) {
        return skillCodecs.get(skillId);
    }

    @Nullable
    public static ResourceLocation getSkillId(ShapeSkill<?> skill) {
        for (Map.Entry<ResourceLocation, Codec<? extends ShapeSkill<?>>> resourceLocationCodecEntry : skillCodecs.entrySet()) {
            if (resourceLocationCodecEntry.getValue() == skill.codec()) {
                return resourceLocationCodecEntry.getKey();
            }
        }
        return null;
    }

    public static <L extends LivingEntity> boolean has(@NotNull L shape, ResourceLocation skillId) {
        for (ShapeSkill<L> skill : getAll(shape)) {
            if (skill.getId() == skillId) {
                return true;
            }
        }
        return false;
    }
}
