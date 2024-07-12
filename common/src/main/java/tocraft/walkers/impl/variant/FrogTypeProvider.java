//#if MC>1182
package tocraft.walkers.impl.variant;

import com.google.common.collect.ImmutableMap;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.animal.FrogVariant;
import net.minecraft.world.entity.animal.frog.Frog;
import net.minecraft.world.level.Level;
import tocraft.craftedcore.patched.TComponent;
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
        //#if MC>=1205
        return BuiltInRegistries.FROG_VARIANT.getId(entity.getVariant().value());
        //#else
        //$$ return BuiltInRegistries.FROG_VARIANT.getId(entity.getVariant());
        //#endif
    }

    @Override
    public Frog create(EntityType<Frog> type, Level world, int data) {
        Frog frog = new Frog(type, world);
        //#if MC>=1205
        frog.setVariant(BuiltInRegistries.FROG_VARIANT.getHolder(data).orElse(BuiltInRegistries.FROG_VARIANT.getHolderOrThrow(FrogVariant.TEMPERATE)));
        //#else
        //$$ frog.setVariant(BuiltInRegistries.FROG_VARIANT.getHolder(data).map(Holder.Reference::value).orElse(FrogVariant.TEMPERATE));
        //#endif
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
        return TComponent.literal(PREFIX_BY_ID.containsKey(variant) ? PREFIX_BY_ID.get(variant) + " " : "").append(text);
    }
}
//#endif
