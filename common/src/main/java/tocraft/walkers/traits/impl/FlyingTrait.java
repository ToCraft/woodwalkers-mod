package tocraft.walkers.traits.impl;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import org.jetbrains.annotations.Contract;
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

    @Contract(pure = true)
    @Environment(EnvType.CLIENT)
    @Override
    public @Nullable Item getItemIcon() {
        return Items.ELYTRA;
    }
}
