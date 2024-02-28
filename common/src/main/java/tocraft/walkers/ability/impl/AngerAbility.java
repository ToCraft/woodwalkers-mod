package tocraft.walkers.ability.impl;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.NeutralMob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import tocraft.walkers.ability.ShapeAbility;
import tocraft.walkers.api.PlayerShape;
import tocraft.walkers.mixin.EntityTrackerAccessor;
import tocraft.walkers.mixin.ThreadedAnvilChunkStorageAccessor;

public class AngerAbility<T extends Mob> extends ShapeAbility<T> {
    private final SoundEvent notAggressiveSound;
    private final SoundEvent aggressiveSound;
    private final Item icon;

    public AngerAbility() {
        this(SoundEvents.PLAYER_BREATH, SoundEvents.PLAYER_ATTACK_CRIT);
    }

    public AngerAbility(SoundEvent notAggressiveSound, SoundEvent aggressionSound) {
        this(notAggressiveSound, aggressionSound, Items.RED_DYE);
    }

    public AngerAbility(SoundEvent notAggressiveSound, SoundEvent aggressionSound, Item icon) {
        this.notAggressiveSound = notAggressiveSound;
        this.aggressiveSound = aggressionSound;
        this.icon = icon;
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
        return icon;
    }
}
