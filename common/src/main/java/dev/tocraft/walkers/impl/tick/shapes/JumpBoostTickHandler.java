package dev.tocraft.walkers.impl.tick.shapes;

import dev.tocraft.walkers.api.WalkersTickHandler;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;

public class JumpBoostTickHandler<T extends LivingEntity> implements WalkersTickHandler<T> {

    private final int level;

    public JumpBoostTickHandler(int level) {
        this.level = level;
    }

    @Override
    public void tick(Player player, LivingEntity entity) {
        if (!player.level().isClientSide) {
            if (player.tickCount % 5 == 0) {
                player.addEffect(new MobEffectInstance(MobEffects.JUMP_BOOST, 20 * 2, level, true, false));
            }
        }
    }
}
