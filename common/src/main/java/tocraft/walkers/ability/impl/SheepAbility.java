package tocraft.walkers.ability.impl;

import net.minecraft.world.entity.animal.Sheep;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.ShearsItem;
import net.minecraft.world.level.Level;

public class SheepAbility extends GrassEaterAbility<Sheep> {
    public SheepAbility() {
        super("eatAnimationTick");
    }

    @Override
    public void onUse(Player player, Sheep shape, Level world) {
        if (!shape.isSheared() && player.getMainHandItem().getItem() instanceof ShearsItem) {
            shape.shear(player.getSoundSource());
        } else {
            eatGrass(shape);
        }
    }

    @Override
    public Item getIcon() {
        return Items.WHITE_WOOL;
    }
}
