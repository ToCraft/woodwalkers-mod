package tocraft.walkers.api.skills.impl;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.LivingEntity;
import tocraft.walkers.Walkers;
import tocraft.walkers.api.skills.ShapeSkill;

import java.util.Optional;

public class MobEffectSkill<E extends LivingEntity> extends ShapeSkill<E> {
    public static final ResourceLocation ID = Walkers.id("mob_effect_on_self");
    public static final Codec<MobEffectSkill<?>> CODEC = RecordCodecBuilder.create((instance) -> instance.group(
            ResourceLocation.CODEC.fieldOf("effect").forGetter(o -> BuiltInRegistries.MOB_EFFECT.getKey(o.effect)),
            Codec.INT.fieldOf("duration").forGetter(o -> o.duration),
            Codec.INT.fieldOf("amplifier").forGetter(o -> o.amplifier),
            Codec.BOOL.optionalFieldOf("ambient", false).forGetter(o -> o.ambient),
            Codec.BOOL.optionalFieldOf("visible", true).forGetter(o -> o.visible),
            Codec.BOOL.optionalFieldOf("showIcon").forGetter(o -> Optional.of(o.showIcon))
    ).apply(instance, instance.stable((effect, duration, amplifier, ambient, visible, showIconOptional) -> showIconOptional.<MobEffectSkill<?>>map(aBoolean -> new MobEffectSkill<>(BuiltInRegistries.MOB_EFFECT.get(effect), duration, amplifier, ambient, visible, aBoolean)).orElseGet(() -> new MobEffectSkill<>(BuiltInRegistries.MOB_EFFECT.get(effect), duration, amplifier, ambient, visible, visible)))));

    public final MobEffect effect;
    public final int duration;
    public final int amplifier;
    public final boolean ambient;
    public final boolean visible;
    public final boolean showIcon;

    public MobEffectSkill(MobEffect effect, int duration, int amplifier) {
        this(effect, duration, amplifier, false, true);
    }

    public MobEffectSkill(MobEffect effect, int duration, int amplifier, boolean ambient, boolean visible) {
        this(effect, duration, amplifier, ambient, visible, visible);
    }

    public MobEffectSkill(MobEffect effect, int duration, int amplifier, boolean ambient, boolean visible, boolean showIcon) {
        this.effect = effect;
        this.duration = duration;
        this.amplifier = amplifier;
        this.ambient = ambient;
        this.visible = visible;
        this.showIcon = showIcon;
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
