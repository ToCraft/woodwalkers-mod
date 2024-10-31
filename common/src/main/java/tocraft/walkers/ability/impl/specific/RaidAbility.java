package tocraft.walkers.ability.impl.specific;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import tocraft.walkers.Walkers;
import tocraft.walkers.ability.ShapeAbility;

public class RaidAbility<T extends LivingEntity> extends ShapeAbility<T> {
    public static final ResourceLocation ID = Walkers.id("raid");

    @Override
    public ResourceLocation getId() {
        return ID;
    }

    @Override
    public void onUse(ServerPlayer player, T shape, ServerLevel world) {
        if (world instanceof ServerLevel serverLevel) {
            serverLevel.getRaids().createOrExtendRaid(player, player.getOnPos());
            player.playSound(SoundEvents.RAID_HORN.value());
        }
    }

    @Override
    public int getDefaultCooldown() {
        return 2400;
    }

    @Override
    public Item getIcon() {
        return Items.CROSSBOW;
    }
}
