package tocraft.walkers.impl.variant;

import com.google.common.collect.ImmutableMap;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.animal.FrogVariant;
import net.minecraft.world.entity.animal.frog.Frog;
import net.minecraft.world.level.Level;
import tocraft.walkers.api.variant.TypeProvider;

import java.util.Map;

public class FrogTypeProvider extends TypeProvider<Frog> {

    private static final Map<Integer, String> PREFIX_BY_ID = ImmutableMap
            .<Integer, String>builder()
            .put(0, "Temperate")
            .put(1, "Warm")
            .put(2, "Cold")
            .build();

    @Override
    public int getVariantData(Frog entity) {
        return BuiltInRegistries.FROG_VARIANT.getId(entity.getVariant().value());
    }

    @Override
    public Frog create(EntityType<Frog> type, Level world, int data) {
        Frog frog = new Frog(type, world);
        frog.setVariant(BuiltInRegistries.FROG_VARIANT.get(data).orElse(BuiltInRegistries.FROG_VARIANT.get(FrogVariant.TEMPERATE).orElseThrow()));
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
        int variant = getVariantData(frog);
        return Component.literal(PREFIX_BY_ID.containsKey(variant) ? PREFIX_BY_ID.get(variant) + " " : "").append(text);
    }
}
