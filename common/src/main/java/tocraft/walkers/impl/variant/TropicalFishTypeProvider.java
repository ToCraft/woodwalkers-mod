package tocraft.walkers.impl.variant;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.animal.TropicalFish;
import net.minecraft.world.level.Level;
import tocraft.walkers.api.variant.TypeProvider;

import java.util.Arrays;
import java.util.List;

public class TropicalFishTypeProvider extends TypeProvider<TropicalFish> {
    public static List<TropicalFish.Pattern> patternValues = Arrays.asList(TropicalFish.Pattern.values());

    @Override
    public int getVariantData(TropicalFish entity) {
        return Arrays.asList(TropicalFish.Pattern.values()).indexOf(entity.getVariant());
    }

    @Override
    public TropicalFish create(EntityType<TropicalFish> type, Level world, int data) {
        TropicalFish fish = new TropicalFish(type, world);
        fish.setVariant(patternValues.get(data));
        return fish;
    }

    @Override
    public int getFallbackData() {
        return 0;
    }

    @Override
    public int getRange() {
        return patternValues.size() - 1;
    }

    @Override
    public Component modifyText(TropicalFish entity, MutableComponent text) {
        return Component.literal(entity.getVariant().displayName().getString()).append(text);
    }
}
