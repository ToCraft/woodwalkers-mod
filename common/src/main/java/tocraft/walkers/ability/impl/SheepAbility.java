package tocraft.walkers.ability.impl;

import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.animal.Sheep;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;

public class SheepAbility extends GrassEaterAbility<Sheep> {

    public SheepAbility() {
        super(SoundEvents.SHEEP_STEP);
    }

    @Override
    public void onUse(Player player, Sheep shape, Level world) {
        if (player.isCrouching())
            super.eatGrass(player, world);
        else {
            shape.shear(player.getSoundSource());
        }
    }

    @Override
    public Item getIcon() {
        return Items.WHITE_WOOL;
    }
}
