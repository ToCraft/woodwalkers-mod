package tocraft.walkers.ability.impl.generic;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import tocraft.walkers.Walkers;
import tocraft.walkers.ability.GenericShapeAbility;

public class JumpAbility<T extends LivingEntity> extends GenericShapeAbility<T> {
    public static final ResourceLocation ID = Walkers.id("jump");
    public static final MapCodec<JumpAbility<?>> CODEC = RecordCodecBuilder.mapCodec((instance) -> instance.stable(new JumpAbility<>()));

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
        int level = 1 + (int) (Math.random() * ((3 - 1) + 1));
        player.addEffect(new MobEffectInstance(MobEffects.JUMP_BOOST, this.getCooldown(shape) / 2, level, true, false));
        world.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.HORSE_JUMP, SoundSource.PLAYERS, 1.0F, 1.0F);
    }

    @Override
    public Item getIcon() {
        return Items.IRON_HORSE_ARMOR;
    }

    @Override
    public int getDefaultCooldown() {
        return 40;
    }
}
