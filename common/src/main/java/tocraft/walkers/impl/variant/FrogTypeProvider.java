package tocraft.walkers.impl.variant;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.animal.frog.Frog;
import net.minecraft.world.level.Level;
import tocraft.walkers.api.variant.TypeProvider;

import java.util.Objects;

public class FrogTypeProvider extends TypeProvider<Frog> {

    @Override
    public int getVariantData(Frog entity) {
        return BuiltInRegistries.FROG_VARIANT.getId(entity.getVariant());
    }

    @Override
    public Frog create(EntityType<Frog> type, Level world, int data) {
        Frog frog = new Frog(type, world);
        frog.setVariant(Objects.requireNonNull(BuiltInRegistries.FROG_VARIANT.byId(data)));
        return frog;
    }

    @Override
    public int getFallbackData() {
        return 0;
    }

    @Override
    public int getRange() {
        return BuiltInRegistries.FROG_VARIANT.size() - 1;
    }

    @Override
    public Component modifyText(Frog frog, MutableComponent text) {
        return Component.literal(frog.getVariant().texture().getPath() + " ").append(text);
    }
}
