package tocraft.walkers.impl.variant;

import net.minecraft.core.registries.BuiltInRegistries;
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
        TropicalFish.Variant variant = new TropicalFish.Variant(entity.getVariant(), entity.getBaseColor(), entity.getPatternColor());
        for (TropicalFish.Variant commonVariant : TropicalFish.COMMON_VARIANTS) {
            if (commonVariant.getPackedId() == variant.getPackedId()) {
                return TropicalFish.COMMON_VARIANTS.indexOf(commonVariant);
            }
        }
        return getFallbackData();
    }

    @Override
    public TropicalFish create(EntityType<TropicalFish> type, Level world, int data) {
        CompoundTag tag = new CompoundTag();

        tag.putInt("Variant", TropicalFish.COMMON_VARIANTS.get(data).getPackedId());

        CompoundTag compoundTag = tag.copy();
        compoundTag.putString("id", BuiltInRegistries.ENTITY_TYPE.getKey(type).toString());
        return (TropicalFish) EntityType.loadEntityRecursive(compoundTag, world, entity -> entity);
    }

    @Override
    public int getFallbackData() {
        return 0;
    }

    @Override
    public int getRange() {
        return TropicalFish.COMMON_VARIANTS.size() - 1;
    }

    @Override
    public Component modifyText(TropicalFish entity, MutableComponent text) {
        return entity.getVariant().displayName();
    }
}
