package tocraft.walkers.impl.variant;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.animal.TropicalFish;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.Level;
import tocraft.walkers.Walkers;
import tocraft.walkers.api.variant.TypeProvider;
import tocraft.walkers.mixin.accessor.TropicalFishAccessor;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class TropicalFishTypeProvider extends TypeProvider<TropicalFish> {
    public static final List<TropicalFish.Pattern> patternValues = Arrays.asList(TropicalFish.Pattern.values());

    @Override
    public int getVariantData(TropicalFish entity) {
        return Arrays.asList(TropicalFish.Pattern.values()).indexOf(entity.getVariant());
    }

    @Override
    public TropicalFish create(EntityType<TropicalFish> type, Level world, int data) {
        TropicalFish fish = new TropicalFish(type, world);
        TropicalFish.Pattern pattern = patternValues.get(data);
        if (Walkers.CONFIG.multiVectorVariants > 0) {
            DyeColor baseColor = DyeColor.byId(new Random().nextInt(0, 15));
            DyeColor patternColor = DyeColor.byId(new Random().nextInt(0, 15));
            ;
            ((TropicalFishAccessor) fish).callSetPackedVariant(new TropicalFish.Variant(pattern, baseColor, patternColor).getPackedId());

        } else {
            fish.setVariant(pattern);
        }
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
