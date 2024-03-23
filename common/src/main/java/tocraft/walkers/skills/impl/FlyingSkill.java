package tocraft.walkers.skills.impl;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Items;
import org.jetbrains.annotations.Nullable;
import tocraft.walkers.Walkers;
import tocraft.walkers.skills.ShapeSkill;

public class FlyingSkill<E extends LivingEntity> extends ShapeSkill<E> {
    public static final ResourceLocation ID = Walkers.id("flying");
    public static final Codec<FlyingSkill<?>> CODEC = RecordCodecBuilder.create((instance) -> instance.group(
            Codec.BOOL.optionalFieldOf("slow_falling", true).forGetter(o -> o.slowFalling)
    ).apply(instance, instance.stable(FlyingSkill::new)));

    public FlyingSkill() {
        this.slowFalling = false;
    }

    public FlyingSkill(boolean slowFalling) {
        this.slowFalling = slowFalling;
    }

    public final boolean slowFalling;

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
        BakedModel itemModel = Minecraft.getInstance().getItemRenderer().getItemModelShaper().getItemModel(Items.ELYTRA);
        if (itemModel != null) {
            return itemModel.getParticleIcon();
        } else {
            return super.getIcon();
        }
    }
}
