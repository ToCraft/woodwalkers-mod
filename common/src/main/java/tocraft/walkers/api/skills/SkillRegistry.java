package tocraft.walkers.api.skills;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ambient.Bat;
import tocraft.walkers.ability.ShapeAbility;
import tocraft.walkers.api.skills.impl.MobEffectSkill;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

public class SkillRegistry {
    private static final Map<Predicate<LivingEntity>, ShapeSkill<?>> skills = new LinkedHashMap<>();


    public static void init() {
        register(Bat.class, new MobEffectSkill<>(MobEffects.NIGHT_VISION, 100000, 0, false, false));
    }

    /**
     * @return a list of every available skill for the specified entity
     */
    @SuppressWarnings("unchecked")
    public static <L extends LivingEntity> List<ShapeSkill<L>> getAll(L shape) {
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
    @SuppressWarnings("unchecked")
    public static <L extends LivingEntity> List<ShapeSkill<L>> get(L shape, ResourceLocation skillId) {
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

    public static <L extends LivingEntity> boolean has(L shape, ResourceLocation skillId) {
        for (ShapeSkill<L> skill : getAll(shape)) {
            if (skill.getId() == skillId) {
                return true;
            }
        }
        return false;
    }
}
