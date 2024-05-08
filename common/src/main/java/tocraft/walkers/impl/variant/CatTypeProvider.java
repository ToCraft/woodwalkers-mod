package tocraft.walkers.impl.variant;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.animal.Cat;
import net.minecraft.world.level.Level;
import tocraft.walkers.api.variant.TypeProvider;

import java.util.Objects;

public class CatTypeProvider extends TypeProvider<Cat> {
    @Override
    public int getVariantData(Cat entity) {
        return BuiltInRegistries.CAT_VARIANT.getId(entity.getVariant());
    }

    @Override
    public Cat create(EntityType<Cat> type, Level world, int data) {
        Cat cat = new Cat(type, world);
        cat.setVariant(Objects.requireNonNull(BuiltInRegistries.CAT_VARIANT.byId(data)));
        return cat;
    }

    @Override
    public int getFallbackData() {
        return 0;
    }

    @Override
    public int getRange() {
        return BuiltInRegistries.CAT_VARIANT.size() - 1;
    }

    @Override
    public Component modifyText(Cat cat, MutableComponent text) {
        return Component.literal(formatTypePrefix(cat.getVariant().texture().getPath() + " ")).append(text);
    }
}
