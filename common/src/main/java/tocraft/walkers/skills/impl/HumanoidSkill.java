package tocraft.walkers.skills.impl;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import tocraft.walkers.Walkers;
import tocraft.walkers.skills.ShapeSkill;

public class HumanoidSkill<E extends LivingEntity> extends ShapeSkill<E> {
    public static final ResourceLocation ID = Walkers.id("humanoid");
    public static final Codec<HumanoidSkill<?>> CODEC = RecordCodecBuilder.create((instance) -> instance.group(
            Codec.FLOAT.optionalFieldOf("crouching_height", -1F).forGetter(o -> null),
            Codec.FLOAT.optionalFieldOf("crouching_eye_pos", -1F).forGetter(o -> null)
    ).apply(instance, instance.stable(HumanoidSkill::new)));

    public final float crouchingHeight;
    public final float crouchingEyePos;

    public HumanoidSkill() {
        this(-1F, -1F);
    }

    public HumanoidSkill(float crouchingHeight, float crouchingEyePos) {
        this.crouchingHeight = crouchingHeight;
        this.crouchingEyePos = crouchingEyePos;
    }

    @Override
    public ResourceLocation getId() {
        return ID;
    }

    @Override
    public Codec<? extends ShapeSkill<?>> codec() {
        return CODEC;
    }
}
