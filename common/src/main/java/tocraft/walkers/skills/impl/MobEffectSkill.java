package tocraft.walkers.skills.impl;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.Nullable;
import tocraft.walkers.Walkers;
import tocraft.walkers.skills.ShapeSkill;

public class MobEffectSkill<E extends LivingEntity> extends ShapeSkill<E> {
    public static final ResourceLocation ID = Walkers.id("mob_effect");
    public static final Codec<MobEffectSkill<?>> CODEC = RecordCodecBuilder.create((instance) -> instance.group(
            CompoundTag.CODEC.fieldOf("mob_effect").forGetter(o -> o.mobEffectInstance.save(new CompoundTag())),
            Codec.BOOL.optionalFieldOf("show_in_inventory", true).forGetter(o -> o.showInInventory),
            Codec.BOOL.optionalFieldOf("apply_to_self", true).forGetter(o -> o.applyToSelf),
            Codec.INT.optionalFieldOf("apply_to_nearby", -1).forGetter(o -> o.applyToNearby),
            Codec.INT.optionalFieldOf("max_distance_for_entities", -1).forGetter(o -> o.maxDistanceForEntities),
            Codec.INT.optionalFieldOf("amount_of_entities_to_apply_to", -1).forGetter(o -> o.amountOfEntitiesToApplyTo)
    ).apply(instance, instance.stable((mobEffectNBT, showInInventory, applyToSelf, applyToNearby, maxDistanceForEntities, amountOfEntitiesToApplyTo) -> new MobEffectSkill<>(MobEffectInstance.load(mobEffectNBT), showInInventory, applyToSelf, applyToNearby, maxDistanceForEntities, amountOfEntitiesToApplyTo))));

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
    public @Nullable TextureAtlasSprite getIcon() {
        return Minecraft.getInstance().getMobEffectTextures().get(mobEffectInstance.getEffect());
    }
}
