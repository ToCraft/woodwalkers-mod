package tocraft.walkers.ability.impl;

import tocraft.walkers.ability.WalkersAbility;
import net.minecraft.entity.passive.WolfEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.world.World;
import tocraft.walkers.api.PlayerShape;
import tocraft.walkers.mixin.EntityTrackerAccessor;
import tocraft.walkers.mixin.ThreadedAnvilChunkStorageAccessor;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;

public class WolfAbility extends WalkersAbility<WolfEntity> {

    @Override
    public void onUse(PlayerEntity player, WolfEntity shape, World world) {
        if (shape.hasAngerTime())
            shape.stopAnger();
        else
            shape.chooseRandomAngerTime();

        if (!world.isClient()) {
            Int2ObjectMap<Object> trackers = ((ThreadedAnvilChunkStorageAccessor) ((ServerWorld) world).getChunkManager().threadedAnvilChunkStorage).getEntityTrackers();
            Object tracking = trackers.get(player.getId());
            ((EntityTrackerAccessor) tracking).getListeners().forEach(listener -> {
                PlayerShape.sync((ServerPlayerEntity) player, listener.getPlayer());
            });
        }
    }

    @Override
    public Item getIcon() {
        return Items.RED_DYE;
    }
}
