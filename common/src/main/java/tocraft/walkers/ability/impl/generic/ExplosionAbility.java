package tocraft.walkers.ability.impl.generic;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level.ExplosionInteraction;
import tocraft.walkers.Walkers;
import tocraft.walkers.ability.GenericShapeAbility;

public class ExplosionAbility<T extends LivingEntity> extends GenericShapeAbility<T> {
    public static final ResourceLocation ID = Walkers.id("explosion");
    public static final MapCodec<ExplosionAbility<?>> CODEC = RecordCodecBuilder.mapCodec((instance) -> instance.group(
            Codec.FLOAT.optionalFieldOf("radius", 3.0f).forGetter(o -> o.radius)
    ).apply(instance, instance.stable(ExplosionAbility::new)));

    private final float radius;

    public ExplosionAbility() {
        this(3.0f);
    }

    public ExplosionAbility(float radius) {
        this.radius = radius;
    }

    @Override
    public ResourceLocation getId() {
        return ID;
    }

    @Override
    public MapCodec<? extends GenericShapeAbility<?>> codec() {
        return CODEC;
    }

    @Override
    public void onUse(ServerPlayer player, T shape, ServerLevel world) {
        world.explode(player, player.getX(), player.getY(), player.getZ(), 3.0f, ExplosionInteraction.NONE);
    }

    @Override
    public Item getIcon() {
        return Items.TNT;
    }

    @Override
    public int getDefaultCooldown() {
        return 100;
    }
}
