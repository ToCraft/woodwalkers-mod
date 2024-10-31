package tocraft.walkers.traits.impl;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.jetbrains.annotations.Nullable;
import tocraft.walkers.Walkers;
import tocraft.walkers.traits.ShapeTrait;

public class FlyingTrait<E extends LivingEntity> extends ShapeTrait<E> {
    public static final ResourceLocation ID = Walkers.id("flying");
    public static final MapCodec<FlyingTrait<?>> CODEC = RecordCodecBuilder.mapCodec((instance) -> instance.group(
            Codec.BOOL.optionalFieldOf("slow_falling", true).forGetter(o -> o.slowFalling)
    ).apply(instance, instance.stable(FlyingTrait::new)));

    public FlyingTrait() {
        this.slowFalling = false;
    }

    public FlyingTrait(boolean slowFalling) {
        this.slowFalling = slowFalling;
    }

    public final boolean slowFalling;

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
    public @Nullable TextureAtlasSprite getIcon() {
        BakedModel itemModel = Minecraft.getInstance().getItemRenderer().getModel(new ItemStack(Items.ELYTRA), null, null, 15);
        if (itemModel != null) {
            return itemModel.getParticleIcon();
        } else {
            return super.getIcon();
        }
    }
}
