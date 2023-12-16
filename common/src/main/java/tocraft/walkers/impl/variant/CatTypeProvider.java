package tocraft.walkers.impl.variant;

import java.util.Map;

import com.google.common.collect.ImmutableMap;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.animal.Cat;
import net.minecraft.world.level.Level;
import tocraft.walkers.api.variant.TypeProvider;

public class CatTypeProvider extends TypeProvider<Cat> {

    private static final Map<Integer, String> PREFIX_BY_ID = ImmutableMap
            .<Integer, String>builder()
            .put(0, "Tabby")
            .put(1, "Black")
            .put(2, "Red")
            .put(3, "Siamese")
            .put(4, "British Shorthair")
            .put(5, "Calico")
            .put(6, "Persian")
            .put(7, "Ragdoll")
            .put(8, "White")
            .put(9, "Jellie")
            .put(10, "Black")
            .build();

    @Override
    public int getVariantData(Cat entity) {
        return entity.getCatType();
    }

    @Override
    public Cat create(EntityType<Cat> type, Level world, int data) {
        Cat cat = new Cat(type, world);
        cat.setCatType(data);
        return cat;
    }

    @Override
    public int getFallbackData() {
        return 0;
    }

    @Override
    public int getRange() {
        return 10;
    }

    @Override
    public Component modifyText(Cat cat, MutableComponent text) {
        int variant = getVariantData(cat);
        return new TextComponent(PREFIX_BY_ID.containsKey(variant) ? PREFIX_BY_ID.get(variant) + " " : "").append(text);
    }
}
