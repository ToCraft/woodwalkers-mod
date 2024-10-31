package tocraft.walkers.ability.impl.specific;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.animal.Sheep;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.ShearsItem;
import tocraft.walkers.Walkers;

public class SheepAbility<T extends Sheep> extends GrassEaterAbility<T> {
    public static final ResourceLocation ID = Walkers.id("sheep");

    @Override
    public ResourceLocation getId() {
        return ID;
    }

    @Override
    public void onUse(ServerPlayer player, T shape, ServerLevel world) {
        if (!shape.isSheared() && player.getMainHandItem().getItem() instanceof ShearsItem) {
            shape.shear(world, player.getSoundSource(), new ItemStack(Items.SHEARS));
        } else {
            eatGrass(player);
        }
    }

    @Override
    public Item getIcon() {
        return Items.WHITE_WOOL;
    }
}
