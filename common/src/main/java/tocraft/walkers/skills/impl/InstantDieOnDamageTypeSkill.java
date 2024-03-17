package tocraft.walkers.skills.impl;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.entity.LivingEntity;
import tocraft.walkers.Walkers;
import tocraft.walkers.skills.ShapeSkill;

public class InstantDieOnDamageTypeSkill<E extends LivingEntity> extends ShapeSkill<E> {
    public static final ResourceLocation ID = Walkers.id("instant_die_on_damage_type");
    public static final Codec<InstantDieOnDamageTypeSkill<?>> CODEC = RecordCodecBuilder.create((instance) -> instance.group(
            ResourceLocation.CODEC.fieldOf("damage_type").forGetter(o -> null)
    ).apply(instance, instance.stable(InstantDieOnDamageTypeSkill::new)));

    public InstantDieOnDamageTypeSkill(ResourceKey<DamageType> damageType) {
        this(damageType.location());
    }

    public InstantDieOnDamageTypeSkill(ResourceLocation damageType) {
        this.damageType = damageType;
    }

    public final ResourceLocation damageType;

    @Override
    public ResourceLocation getId() {
        return ID;
    }

    @Override
    public Codec<? extends ShapeSkill<?>> codec() {
        return CODEC;
    }
}
