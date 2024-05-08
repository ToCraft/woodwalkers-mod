package tocraft.walkers.skills.impl;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.Nullable;
import tocraft.walkers.Walkers;
import tocraft.walkers.skills.ShapeSkill;

import java.util.Optional;

public class MobEffectSkill<E extends LivingEntity> extends ShapeSkill<E> {
    public static final ResourceLocation ID = Walkers.id("mob_effect");
    @SuppressWarnings("DataFlowIssue")
    public static final Codec<MobEffectInstance> MOB_EFFECT_INSTANCE_CODEC = RecordCodecBuilder.create((instance) -> instance.group(
            ResourceLocation.CODEC.fieldOf("id").forGetter(o -> BuiltInRegistries.MOB_EFFECT.getKey(o.getEffect())),
            Codec.INT.fieldOf("duration").forGetter(MobEffectInstance::getDuration),
            Codec.INT.fieldOf("amplifier").forGetter(MobEffectInstance::getAmplifier),
            Codec.BOOL.optionalFieldOf("ambient", false).forGetter(MobEffectInstance::isAmbient),
            Codec.BOOL.optionalFieldOf("show_particles", true).forGetter(MobEffectInstance::isVisible),
            Codec.BOOL.optionalFieldOf("show_icon").forGetter(o -> Optional.of(o.showIcon()))
    ).apply(instance, instance.stable((id, duration, amplifier, ambient, show_particles, show_icon) -> new MobEffectInstance(BuiltInRegistries.MOB_EFFECT.get(id), duration, amplifier, ambient, show_particles, show_icon.orElse(show_particles)))));
    public static final Codec<MobEffectSkill<?>> CODEC = RecordCodecBuilder.create((instance) -> instance.group(
            MOB_EFFECT_INSTANCE_CODEC.fieldOf("mob_effect").forGetter(o -> o.mobEffectInstance),
            Codec.BOOL.optionalFieldOf("show_in_inventory", true).forGetter(o -> o.showInInventory),
            Codec.BOOL.optionalFieldOf("apply_to_self", true).forGetter(o -> o.applyToSelf),
            Codec.INT.optionalFieldOf("apply_to_nearby", -1).forGetter(o -> o.applyToNearby),
            Codec.INT.optionalFieldOf("max_distance_for_entities", -1).forGetter(o -> o.maxDistanceForEntities),
            Codec.INT.optionalFieldOf("amount_of_entities_to_apply_to", -1).forGetter(o -> o.amountOfEntitiesToApplyTo)
    ).apply(instance, instance.stable(MobEffectSkill::new)));

    public final MobEffectInstance mobEffectInstance;
    public final boolean showInInventory;
    public final boolean applyToSelf;
    public final int applyToNearby;
    public final int maxDistanceForEntities;
    public final int amountOfEntitiesToApplyTo;

    public MobEffectSkill(MobEffectInstance mobEffectInstance) {
        this(mobEffectInstance, false);
    }

    public MobEffectSkill(MobEffectInstance mobEffectInstance, boolean showInInventory) {
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
    public MobEffectSkill(MobEffectInstance mobEffectInstance, boolean showInInventory, boolean applyToSelf, int applyToNearby, int maxDistanceForEntities, int amountOfEntitiesToApplyTo) {
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
    public Codec<? extends ShapeSkill<?>> codec() {
        return CODEC;
    }

    @Override
    @Environment(EnvType.CLIENT)
    public boolean iconMightDiffer() {
        return true;
    }

    @Override
    @Environment(EnvType.CLIENT)
    public @Nullable TextureAtlasSprite getIcon() {
        return Minecraft.getInstance().getMobEffectTextures().get(mobEffectInstance.getEffect());
    }
}
