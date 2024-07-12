package tocraft.walkers.impl.variant;

import com.google.common.collect.ImmutableMap;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.animal.Parrot;
import net.minecraft.world.level.Level;
import tocraft.craftedcore.patched.TComponent;
import tocraft.walkers.api.variant.TypeProvider;

public class ParrotTypeProvider extends TypeProvider<Parrot> {

    private static final ImmutableMap<Integer, String> PREFIX_BY_ID = ImmutableMap
            .<Integer, String>builder()
            .put(0, "Red Blue")
            .put(1, "Blue")
            .put(2, "Green")
            .put(3, "Yellow Blue")
            .put(4, "Gray")
            .build();

    @Override
    public int getVariantData(Parrot entity) {
        //#if MC>1182
        return entity.getVariant().getId();
        //#else
        //$$ return entity.getVariant();
        //#endif
    }

    @Override
    public Parrot create(EntityType<Parrot> type, Level world, int data) {
        Parrot parrot = new Parrot(type, world);
        //#if MC>1182
        parrot.setVariant(Parrot.Variant.byId(data));
        //#else
        //$$ parrot.setVariant(data);
        //#endif
        return parrot;
    }

    @Override
    public int getFallbackData() {
        return 0;
    }

    @Override
    public int getRange() {
        return 4;
    }

    @Override
    public Component modifyText(Parrot parrot, MutableComponent text) {
        int variant = getVariantData(parrot);
        return TComponent.literal(PREFIX_BY_ID.containsKey(variant) ? PREFIX_BY_ID.get(variant) + " " : "").append(text);
    }
}
