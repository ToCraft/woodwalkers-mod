package tocraft.walkers.ability.impl.generic;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.HitResult;
import tocraft.walkers.Walkers;
import tocraft.walkers.ability.GenericShapeAbility;

public class TeleportationAbility<T extends LivingEntity> extends GenericShapeAbility<T> {
    public static final ResourceLocation ID = Walkers.id("teleportation");
    public static final MapCodec<TeleportationAbility<?>> CODEC = RecordCodecBuilder.mapCodec((instance) -> instance.stable(new TeleportationAbility<>()));

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
        HitResult lookingAt = player.pick(Walkers.CONFIG.endermanAbilityTeleportDistance, 0, true);
        player.teleportTo(lookingAt.getLocation().x, lookingAt.getLocation().y, lookingAt.getLocation().z);
        player.playNotifySound(SoundEvents.ENDERMAN_TELEPORT, SoundSource.PLAYERS, 1, 1);
    }

    @Override
    public Item getIcon() {
        return Items.ENDER_PEARL;
    }

    @Override
    public int getDefaultCooldown() {
        return 100;
    }
}
