package tocraft.walkers.traits.impl;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.Nullable;
import tocraft.craftedcore.patched.CRegistries;
import tocraft.craftedcore.patched.Identifier;
import tocraft.walkers.Walkers;
import tocraft.walkers.traits.ShapeTrait;

public class ImmunityTrait<E extends LivingEntity> extends ShapeTrait<E> {
    public static final ResourceLocation ID = Walkers.id("immunity");
    @SuppressWarnings("unchecked")
    public static final MapCodec<ImmunityTrait<?>> CODEC = RecordCodecBuilder.mapCodec((instance) -> instance.group(
            ResourceLocation.CODEC.fieldOf("id").forGetter(o -> ((Registry<MobEffect>) CRegistries.getRegistry(Identifier.parse("mob_effect"))).getKey(o.effect))
    ).apply(instance, instance.stable((effect) -> new ImmunityTrait<>(((Registry<MobEffect>) CRegistries.getRegistry(Identifier.parse("mob_effect"))).get(effect)))));

    public final MobEffect effect;

    public ImmunityTrait(MobEffect effect) {
        this.effect = effect;
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

    @Override
    @Environment(EnvType.CLIENT)
    public @Nullable TextureAtlasSprite getIcon() {
        return null;
    }
}
