package tocraft.walkers.impl.variant;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.animal.TropicalFish;
import net.minecraft.world.level.Level;
import tocraft.walkers.Walkers;
import tocraft.walkers.api.variant.TypeProvider;

public class TropicalFishTypeProvider extends TypeProvider<TropicalFish> {

    @Override
    public int getVariantData(TropicalFish entity) {
        //#if MC>1182
        TropicalFish.Variant variant = new TropicalFish.Variant(entity.getVariant(), entity.getBaseColor(), entity.getPatternColor());
        for (TropicalFish.Variant commonVariant : TropicalFish.COMMON_VARIANTS) {
            if (commonVariant.getPackedId() == variant.getPackedId()) {
                return TropicalFish.COMMON_VARIANTS.indexOf(commonVariant);
            }
        }
        //#else
        //$$ if (TropicalFish.COMMON_VARIANTS.length > 0) {
        //$$     for (int i = 0; i < TropicalFish.COMMON_VARIANTS.length; i++) {
        //$$         if (TropicalFish.COMMON_VARIANTS[i] == entity.getVariant()) {
        //$$             return i;
        //$$         }
        //$$     }
        //$$ }
        //#endif
        return getFallbackData();
    }

    @Override
    public TropicalFish create(EntityType<TropicalFish> type, Level world, int data) {
        CompoundTag tag = new CompoundTag();
        //#if MC>1182
        tag.putInt("Variant", TropicalFish.COMMON_VARIANTS.get(data).getPackedId());
        //#else
        //$$ tag.putInt("Variant", TropicalFish.COMMON_VARIANTS[data]);
        //#endif

        CompoundTag compoundTag = tag.copy();
        compoundTag.putString("id", EntityType.getKey(type).toString());
        return (TropicalFish) EntityType.loadEntityRecursive(compoundTag, world, entity -> entity);
    }

    @Override
    public int getFallbackData() {
        return 0;
    }

    @Override
    public int getRange() {
        //#if MC>1182
        return TropicalFish.COMMON_VARIANTS.size() - 1;
        //#else
        //$$ return TropicalFish.COMMON_VARIANTS.length - 1;
        //#endif
    }

    @Override
    public Component modifyText(TropicalFish entity, MutableComponent text) {
        //#if MC>1182
        return entity.getVariant().displayName();
        //#else
        //$$ return text;
        //#endif
    }
}
