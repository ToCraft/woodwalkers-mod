package tocraft.walkers.impl.variant;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.animal.TropicalFish;
import net.minecraft.world.level.Level;
import tocraft.walkers.api.variant.TypeProvider;

public class TropicalFishTypeProvider extends TypeProvider<TropicalFish> {

    @Override
    public int getVariantData(TropicalFish entity) {
        if (TropicalFish.COMMON_VARIANTS.length > 0) {
            for (int i = 0; i < TropicalFish.COMMON_VARIANTS.length; i++) {
                if (TropicalFish.COMMON_VARIANTS[i] == entity.getVariant()) {
                    return i;
                }
            }
        }
        return getFallbackData();
    }

    @Override
    public TropicalFish create(EntityType<TropicalFish> type, Level world, int data) {
        CompoundTag tag = new CompoundTag();

        tag.putInt("Variant", TropicalFish.COMMON_VARIANTS[data]);

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
        return TropicalFish.COMMON_VARIANTS.length - 1;
    }

    @Override
    public Component modifyText(TropicalFish entity, MutableComponent text) {
        return text;
    }
}
