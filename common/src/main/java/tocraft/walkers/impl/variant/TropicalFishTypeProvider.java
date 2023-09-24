package tocraft.walkers.impl.variant;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.animal.TropicalFish;
import net.minecraft.world.level.Level;
import tocraft.walkers.api.variant.TypeProvider;

// TODO: do we want to add this? There will be a boat-load of fish...
public class TropicalFishTypeProvider extends TypeProvider<TropicalFish> {

    @Override
    public int getVariantData(TropicalFish entity) {
        return entity.getVariant().getPackedId();
    }

    @Override
    public TropicalFish create(EntityType<TropicalFish> type, Level world, int data) {
        TropicalFish fish = new TropicalFish(type, world);
        fish.setVariant(TropicalFish.getPattern(data));
        return fish;
    }

    @Override
    public int getFallbackData() {
        return 0;
    }

    @Override
    public int getRange() {
        return 0;
    }

    @Override
    public Component modifyText(TropicalFish entity, MutableComponent text) {
        return null;
    }
}
