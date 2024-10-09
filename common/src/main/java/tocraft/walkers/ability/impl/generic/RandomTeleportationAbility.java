package tocraft.walkers.ability.impl.generic;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import tocraft.walkers.Walkers;
import tocraft.walkers.ability.GenericShapeAbility;

public class RandomTeleportationAbility<T extends LivingEntity> extends GenericShapeAbility<T> {
    public static final ResourceLocation ID = Walkers.id("random_teleportation");
    public static final MapCodec<RandomTeleportationAbility<?>> CODEC = RecordCodecBuilder.mapCodec((instance) -> instance.stable(new RandomTeleportationAbility<>()));

    @Override
    public ResourceLocation getId() {
        return ID;
    }

    @Override
    public MapCodec<? extends GenericShapeAbility<?>> codec() {
        return CODEC;
    }

    @Override
    public void onUse(Player player, T shape, Level world) {
        super.onUse(player, shape, world);
        double x = player.getX();
        double y = player.getY();
        double z = player.getZ();

        for (int i = 0; i < 16; ++i) {
            // Pick a random location nearby to teleport to.
            double g = player.getX() + (player.getRandom().nextDouble() - 0.5D) * 16.0D;
            double h = Mth.clamp(player.getY() + (double) (player.getRandom().nextInt(16) - 8), 0.0D, world.getHeight() - 1);
            double j = player.getZ() + (player.getRandom().nextDouble() - 0.5D) * 16.0D;

            // Cancel vehicle/riding mechanics.
            if (player.isPassenger()) {
                player.stopRiding();
            }

            // Teleport the player and play sound FX if it succeeds.
            if (player.randomTeleport(g, h, j, true)) {
                SoundEvent soundEvent = SoundEvents.CHORUS_FRUIT_TELEPORT;
                world.playSound(null, x, y, z, soundEvent, SoundSource.PLAYERS, 1.0F, 1.0F);
                player.playSound(soundEvent, 1.0F, 1.0F);
                break;
            }
        }
    }

    @Override
    public Item getIcon() {
        return Items.CHORUS_FRUIT;
    }
}
