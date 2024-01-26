package tocraft.walkers.ability.impl;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.animal.Wolf;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import tocraft.walkers.ability.ShapeAbility;
import tocraft.walkers.api.PlayerShape;
import tocraft.walkers.mixin.EntityTrackerAccessor;
import tocraft.walkers.mixin.ThreadedAnvilChunkStorageAccessor;

public class WolfAbility extends ShapeAbility<Wolf> {

    @Override
    public void onUse(Player player, Wolf shape, Level world) {
        if (shape.isAngry()) {
            shape.stopBeingAngry();
            world.playSound(null, player, SoundEvents.WOLF_PANT, SoundSource.PLAYERS, 1.0F,
                    (world.random.nextFloat() - world.random.nextFloat()) * 0.2F + 1.0F);
        } else
            shape.startPersistentAngerTimer();

        if (!world.isClientSide()) {
            Int2ObjectMap<Object> trackers = ((ThreadedAnvilChunkStorageAccessor) ((ServerLevel) world)
                    .getChunkSource().chunkMap).getEntityMap();
            Object tracking = trackers.get(player.getId());
            ((EntityTrackerAccessor) tracking).getSeenBy().forEach(
                    listener -> PlayerShape.sync((ServerPlayer) player, listener.getPlayer())
            );
            world.playSound(null, player, SoundEvents.WOLF_GROWL, SoundSource.PLAYERS, 1.0F,
                    (world.random.nextFloat() - world.random.nextFloat()) * 0.2F + 1.0F);
        }
    }

    @Override
    public Item getIcon() {
        return Items.RED_DYE;
    }
}
