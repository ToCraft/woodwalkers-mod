package tocraft.walkers.impl.variant;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.animal.horse.Llama;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import tocraft.walkers.api.variant.TypeProvider;
import tocraft.walkers.mixin.accessor.LlamaAccessor;

public class LlamaTypeProvider<L extends Llama> extends TypeProvider<L> {

    @Override
    public int getVariantData(L llama) {
        return llama.getVariant().getId();
    }

    @Override
    public L create(EntityType<L> type, Level world, @NotNull Player player, int data) {
        L llama = type.create(world, EntitySpawnReason.LOAD);
        if (llama != null) {
            ((LlamaAccessor) llama).callSetVariant(L.Variant.byId(data));
        }
        return llama;
    }

    @Override
    public int getFallbackData() {
        return 0;
    }

    @Override
    public int getRange(Level level) {
        return L.Variant.values().length;
    }

    @Override
    public Component modifyText(Llama entity, MutableComponent text) {
        return Component.literal(entity.getVariant().getSerializedName() + " ").append(text);
    }
}
