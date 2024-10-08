package tocraft.walkers.ability.impl.specific;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import tocraft.walkers.ability.ShapeAbility;

public class RaidAbility<T extends LivingEntity> extends ShapeAbility<T> {

    @Override
    public void onUse(Player player, T shape, Level world) {
        if (world instanceof ServerLevel serverLevel) {
            //#if MC>=1205
            serverLevel.getRaids().createOrExtendRaid((ServerPlayer) player, player.getOnPos());
            //#else
            //$$ serverLevel.getRaids().createOrExtendRaid((ServerPlayer) player);
            //#endif
            //#if MC>1182
            player.playSound(SoundEvents.RAID_HORN.value());
            //#else
            //$$ player.playSound(SoundEvents.RAID_HORN, 1.0F, 1.0F);
            //#endif
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
