package tocraft.walkers.ability.impl;

import net.minecraft.world.entity.animal.Pufferfish;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import tocraft.walkers.ability.ShapeAbility;
import tocraft.walkers.mixin.accessor.PufferfishAccessor;

public class PufferfishAbility<T extends Pufferfish> extends ShapeAbility<T> {
    @Override
    public void onUse(Player player, T shape, Level world) {
        if (!world.isClientSide()) {
            if (shape.getPuffState() == 0) {
                ((PufferfishAccessor) shape).setInflateCounter(1);
                ((PufferfishAccessor) shape).setDeflateTimer(0);
            } else {
                ((PufferfishAccessor) shape).setInflateCounter(0);
            }
        }
    }

    @Override
    public Item getIcon() {
        return Items.PUFFERFISH;
    }
}
