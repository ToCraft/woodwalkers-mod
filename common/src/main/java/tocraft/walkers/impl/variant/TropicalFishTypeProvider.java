package tocraft.walkers.impl.variant;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.animal.TropicalFish;
import net.minecraft.world.level.Level;
import tocraft.walkers.Walkers;
import tocraft.walkers.api.variant.TypeProvider;

import java.util.Random;

public class TropicalFishTypeProvider extends TypeProvider<TropicalFish> {
    @Override
    public int getVariantData(TropicalFish entity) {
        return Math.min((entity.getVariant() & '\uff00') >> 8, getRange());
    }

    @Override
    public TropicalFish create(EntityType<TropicalFish> type, Level world, int data) {
        TropicalFish fish = new TropicalFish(type, world);
        int index = Math.min((data & '\uff00') >> 8, 5);
        if (Walkers.CONFIG.multiVectorVariants > 0) {
            int i = new Random().nextInt(2);
            int j = new Random().nextInt(6);
            int k = new Random().nextInt(15);
            int l = new Random().nextInt(15);

            fish.setVariant(Math.min(data & 255, 1) | index << 8 | k << 16 | l << 24);
        } else {
            fish.setVariant(index);
        }
        return fish;
    }

    @Override
    public int getFallbackData() {
        return 0;
    }

    @Override
    public int getRange() {
        return 6 - 1;
    }

    @Override
    public Component modifyText(TropicalFish entity, MutableComponent text) {
        return new TextComponent(formatTypePrefix(TropicalFish.getFishTypeName(entity.getVariant())) + " ").append(text);
    }
}
