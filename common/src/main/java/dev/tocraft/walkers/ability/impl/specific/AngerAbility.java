package dev.tocraft.walkers.ability.impl.specific;

import dev.tocraft.walkers.Walkers;
import dev.tocraft.walkers.ability.ShapeAbility;
import dev.tocraft.walkers.api.PlayerShape;
import dev.tocraft.walkers.mixin.EntityTrackerAccessor;
import dev.tocraft.walkers.mixin.ThreadedAnvilChunkStorageAccessor;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.NeutralMob;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;

public class AngerAbility<T extends Mob> extends ShapeAbility<T> {
    public static final ResourceLocation ID = Walkers.id("anger");

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
    public ResourceLocation getId() {
        return ID;
    }

    @Override
    public void onUse(ServerPlayer player, T oShape, ServerLevel world) {
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
                Int2ObjectMap<Object> trackers = ((ThreadedAnvilChunkStorageAccessor) world
                        .getChunkSource().chunkMap).getEntityMap();
                Object tracking = trackers.get(player.getId());
                ((EntityTrackerAccessor) tracking).getSeenBy().forEach(
                        listener -> PlayerShape.sync(player, listener.getPlayer())
                );
            }
        } else {
            Walkers.LOGGER.error("{}: Registered for unvalid entity {}!", AngerAbility.class.getSimpleName(), EntityType.getKey(oShape.getType()));
        }
    }

    @Override
    public Item getIcon() {
        return icon;
    }
}
