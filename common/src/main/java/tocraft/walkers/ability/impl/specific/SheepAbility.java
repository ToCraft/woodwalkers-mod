package tocraft.walkers.ability.impl.specific;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.animal.Sheep;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.ShearsItem;
import net.minecraft.world.level.Level;
import tocraft.walkers.Walkers;

public class SheepAbility<T extends Sheep> extends GrassEaterAbility<T> {
    public static final ResourceLocation ID = Walkers.id("sheep");

    @Override
    public ResourceLocation getId() {
        return ID;
    }

    @Override
    public void onUse(Player player, T shape, Level world) {
        if (!shape.isSheared() && player.getMainHandItem().getItem() instanceof ShearsItem) {
            shape.shear(player.getSoundSource());
        } else {
            eatGrass(player);
        }
    }

    @Override
    public Item getIcon() {
        return Items.WHITE_WOOL;
    }
}
