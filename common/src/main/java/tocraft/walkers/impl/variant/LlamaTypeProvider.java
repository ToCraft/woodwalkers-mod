package tocraft.walkers.impl.variant;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.animal.horse.Llama;
import net.minecraft.world.level.Level;
import tocraft.walkers.api.variant.TypeProvider;

public class LlamaTypeProvider<L extends Llama> extends TypeProvider<L> {

    @Override
    public int getVariantData(L llama) {
        return llama.getVariant().getId();
    }

    @Override
    public L create(EntityType<L> type, Level world, int data) {
        L llama = type.create(world);
        if (llama != null) llama.setVariant(L.Variant.byId(data));
        return llama;
    }

    @Override
    public int getFallbackData() {
        return 0;
    }

    @Override
    public int getRange() {
        return L.Variant.values().length - 1;
    }

    @Override
    public Component modifyText(Llama entity, MutableComponent text) {
        return Component.literal(entity.getVariant().getSerializedName() + " ").append(text);
    }
}
