package tocraft.walkers.impl.variant;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.animal.horse.Llama;
import net.minecraft.world.level.Level;
import tocraft.craftedcore.patched.TComponent;
import tocraft.walkers.api.variant.TypeProvider;

public class LlamaTypeProvider<L extends Llama> extends TypeProvider<L> {

    @Override
    public int getVariantData(L llama) {
        //#if MC>1182
        return llama.getVariant().getId();
        //#else
        //$$ return llama.getVariant();
        //#endif
    }

    @Override
    public L create(EntityType<L> type, Level world, int data) {
        L llama = type.create(world);
        if (llama != null) {
            //#if MC>1182
            llama.setVariant(L.Variant.byId(data));
            //#else
            //$$ llama.setVariant(data);
            //#endif
        }
        return llama;
    }

    @Override
    public int getFallbackData() {
        return 0;
    }

    @Override
    public int getRange() {
        //#if MC>1182
        return L.Variant.values().length - 1;
        //#else
        //$$ return 4;
        //#endif
    }

    @Override
    public Component modifyText(Llama entity, MutableComponent text) {
        //#if MC>1182
        return TComponent.literal(entity.getVariant().getSerializedName() + " ").append(text);
        //#else
        //$$ return text;
        //#endif
    }
}
