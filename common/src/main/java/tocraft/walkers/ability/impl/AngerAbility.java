package tocraft.walkers.ability.impl;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.NeutralMob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import tocraft.walkers.ability.ShapeAbility;
import tocraft.walkers.api.PlayerShape;
import tocraft.walkers.mixin.EntityTrackerAccessor;
import tocraft.walkers.mixin.ThreadedAnvilChunkStorageAccessor;

public class AngerAbility<T extends LivingEntity> extends ShapeAbility<T> {
    SoundEvent notAggressiveSound;
    SoundEvent aggressiveSound;

    public AngerAbility(SoundEvent notAggressiveSound, SoundEvent aggressionSound) {
        this.notAggressiveSound = notAggressiveSound;
        this.aggressiveSound = aggressionSound;
    }

    @Override
    public void onUse(Player player, T oShape, Level world) {
        if (oShape instanceof NeutralMob shape) {
            if (shape.isAngry()) {
                shape.stopBeingAngry();
                world.playSound(null, player, notAggressiveSound, SoundSource.PLAYERS, 1.0F,
                        (world.random.nextFloat() - world.random.nextFloat()) * 0.2F + 1.0F);
            } else {
                shape.startPersistentAngerTimer();
                world.playSound(null, player, aggressiveSound, SoundSource.PLAYERS, 1.0F,
                        (world.random.nextFloat() - world.random.nextFloat()) * 0.2F + 1.0F);

            }

            if (!world.isClientSide()) {
                Int2ObjectMap<Object> trackers = ((ThreadedAnvilChunkStorageAccessor) ((ServerLevel) world)
                        .getChunkSource().chunkMap).getEntityMap();
                Object tracking = trackers.get(player.getId());
                ((EntityTrackerAccessor) tracking).getSeenBy().forEach(
                        listener -> PlayerShape.sync((ServerPlayer) player, listener.getPlayer())
                );
            }
        }
    }

    @Override
    public Item getIcon() {
        return Items.RED_DYE;
    }
}
