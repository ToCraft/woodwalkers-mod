package tocraft.walkers.ability.impl;

import net.minecraft.world.entity.animal.Pufferfish;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import tocraft.walkers.ability.ShapeAbility;

public class PufferfishAbility<T extends Pufferfish> extends ShapeAbility<T> {
    @Override
    public void onUse(Player player, T shape, Level world) {
        if (!world.isClientSide()) {
            if (shape.getPuffState() == 0) {
                shape.inflateCounter = 1;
                shape.deflateTimer = 0;
            } else {
                shape.inflateCounter = 0;
            }
        }
    }

    @Override
    public Item getIcon() {
        return Items.PUFFERFISH;
    }
}
