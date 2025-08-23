package dev.tocraft.walkers.traits.impl;

import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.tocraft.walkers.Walkers;
import dev.tocraft.walkers.traits.ShapeTrait;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public class MobEffectTrait<E extends LivingEntity> extends ShapeTrait<E> {
    public static final ResourceLocation ID = Walkers.id("mob_effect");
    public static final MapCodec<MobEffectInstance> MOB_EFFECT_INSTANCE_CODEC = RecordCodecBuilder.mapCodec((instance) -> instance.group(
            ResourceLocation.CODEC.fieldOf("id").forGetter(o -> BuiltInRegistries.MOB_EFFECT.getKey(o.getEffect().value())),
            Codec.INT.fieldOf("duration").forGetter(MobEffectInstance::getDuration),
            Codec.INT.fieldOf("amplifier").forGetter(MobEffectInstance::getAmplifier),
            Codec.BOOL.optionalFieldOf("ambient", false).forGetter(MobEffectInstance::isAmbient),
            Codec.BOOL.optionalFieldOf("show_particles", true).forGetter(MobEffectInstance::isVisible),
            Codec.BOOL.optionalFieldOf("show_icon").forGetter(o -> Optional.of(o.showIcon()))
    ).apply(instance, instance.stable((id, duration, amplifier, ambient, show_particles, show_icon) -> new MobEffectInstance(BuiltInRegistries.MOB_EFFECT.get(id).orElseThrow(), duration, amplifier, ambient, show_particles, show_icon.orElse(show_particles)))));
    public static final MapCodec<MobEffectTrait<?>> CODEC = RecordCodecBuilder.mapCodec((instance) -> instance.group(
            MOB_EFFECT_INSTANCE_CODEC.fieldOf("mob_effect").forGetter(o -> o.mobEffectInstance),
            Codec.BOOL.optionalFieldOf("show_in_inventory", true).forGetter(o -> o.showInInventory),
            Codec.BOOL.optionalFieldOf("apply_to_self", true).forGetter(o -> o.applyToSelf),
            Codec.INT.optionalFieldOf("apply_to_nearby", -1).forGetter(o -> o.applyToNearby),
            Codec.INT.optionalFieldOf("max_distance_for_entities", -1).forGetter(o -> o.maxDistanceForEntities),
            Codec.INT.optionalFieldOf("amount_of_entities_to_apply_to", -1).forGetter(o -> o.amountOfEntitiesToApplyTo)
    ).apply(instance, instance.stable(MobEffectTrait::new)));

    public final MobEffectInstance mobEffectInstance;
    public final boolean showInInventory;
    public final boolean applyToSelf;
    public final int applyToNearby;
    public final int maxDistanceForEntities;
    public final int amountOfEntitiesToApplyTo;

    public MobEffectTrait(MobEffectInstance mobEffectInstance) {
        this(mobEffectInstance, false);
    }

    public MobEffectTrait(MobEffectInstance mobEffectInstance, boolean showInInventory) {
        this(mobEffectInstance, showInInventory, true, -1, -1, -1);
    }

    /**
     * @param mobEffectInstance         the effect to apply
     * @param showInInventory           should the effect be displayed to the player? Only for self
     * @param applyToSelf               should the player get it?
     * @param applyToNearby             should nearby entities get it? negative - no, 0 - player only, 1 - mobs only, 2 - players and mobs
     * @param maxDistanceForEntities    only used when applyToNearby is true
     * @param amountOfEntitiesToApplyTo only used when applyToNearby is true
     */
    public MobEffectTrait(MobEffectInstance mobEffectInstance, boolean showInInventory, boolean applyToSelf, int applyToNearby, int maxDistanceForEntities, int amountOfEntitiesToApplyTo) {
        this.showInInventory = showInInventory;
        this.mobEffectInstance = mobEffectInstance;
        this.applyToSelf = applyToSelf;
        this.applyToNearby = applyToNearby;
        this.maxDistanceForEntities = maxDistanceForEntities;
        this.amountOfEntitiesToApplyTo = amountOfEntitiesToApplyTo;
    }

    @Override
    public ResourceLocation getId() {
        return ID;
    }

    @Override
    public MapCodec<? extends ShapeTrait<?>> codec() {
        return CODEC;
    }

    @Override
    @Environment(EnvType.CLIENT)
    public boolean iconMightDiffer() {
        return true;
    }

    @Environment(EnvType.CLIENT)
    @Override
    public boolean renderIcon(RenderPipeline pipeline, @NotNull GuiGraphics graphics, int x, int y, int width, int height) {
        graphics.blitSprite(RenderPipelines.GUI_TEXTURED, Gui.getMobEffectSprite(mobEffectInstance.getEffect()), x, y, width, height);
        return true;
    }
}
