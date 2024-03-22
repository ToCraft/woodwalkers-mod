package tocraft.walkers.skills.impl;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import tocraft.walkers.Walkers;
import tocraft.walkers.skills.ShapeSkill;

public class BurnInDaylightSkill<E extends LivingEntity> extends ShapeSkill<E> {
    public static final ResourceLocation ID = Walkers.id("burn_in_daylight");
    public static final Codec<BurnInDaylightSkill<?>> CODEC = RecordCodecBuilder.create((instance) -> instance.group(
            Codec.BOOL.optionalFieldOf("burn_in_moonlight_instead", false).forGetter(o -> o.burnInMoonlightInstead)
    ).apply(instance, instance.stable(BurnInDaylightSkill::new)));

    public BurnInDaylightSkill() {
        this.burnInMoonlightInstead = false;
    }

    public BurnInDaylightSkill(boolean burnInMoonlightInstead) {
        this.burnInMoonlightInstead = burnInMoonlightInstead;
    }

    public final boolean burnInMoonlightInstead;

    @Override
    public ResourceLocation getId() {
        return ID;
    }

    @Override
    public Codec<? extends ShapeSkill<?>> codec() {
        return CODEC;
    }
}
