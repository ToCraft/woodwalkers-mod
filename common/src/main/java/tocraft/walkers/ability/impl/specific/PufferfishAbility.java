package tocraft.walkers.ability.impl.specific;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.animal.Pufferfish;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import tocraft.walkers.Walkers;
import tocraft.walkers.ability.ShapeAbility;
import tocraft.walkers.mixin.accessor.PufferfishAccessor;

public class PufferfishAbility<T extends Pufferfish> extends ShapeAbility<T> {
    public static final ResourceLocation ID = Walkers.id("pufferfish");

    @Override
    public ResourceLocation getId() {
        return ID;
    }

    @Override
    public void onUse(ServerPlayer player, T shape, ServerLevel world) {
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
