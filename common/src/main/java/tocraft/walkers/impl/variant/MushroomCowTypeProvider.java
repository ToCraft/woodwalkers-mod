package tocraft.walkers.impl.variant;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.animal.MushroomCow;
import net.minecraft.world.level.Level;
import tocraft.craftedcore.patched.TComponent;
import tocraft.walkers.api.variant.TypeProvider;
//#if MC<=1182
//$$ import tocraft.walkers.mixin.accessor.MushroomCowAccessor;
//#endif

public class MushroomCowTypeProvider extends TypeProvider<MushroomCow> {

    @Override
    public int getVariantData(MushroomCow entity) {
        //#if MC>1182
        return entity.getVariant().ordinal();
        //#else
        //$$ return entity.getMushroomType().ordinal();
        //#endif
    }

    @Override
    public MushroomCow create(EntityType<MushroomCow> type, Level level, int data) {
        MushroomCow mooshroom = new MushroomCow(type, level);
        //#if MC>1182
        mooshroom.setVariant(MushroomCow.MushroomType.values()[data]);
        //#else
        //$$ ((MushroomCowAccessor) mooshroom).callSetMushroomType(MushroomCow.MushroomType.values()[data]);
        //#endif
        return mooshroom;
    }

    @Override
    public int getFallbackData() {
        return MushroomCow.MushroomType.RED.ordinal();
    }

    @Override
    public int getRange() {
        return MushroomCow.MushroomType.values().length - 1;
    }

    @Override
    public Component modifyText(MushroomCow entity, MutableComponent text) {
        //#if MC>1182
        String variantName = entity.getVariant().getSerializedName();
        //#else
        //$$ String variantName = entity.getMushroomType().name();
        //#endif
        return TComponent.literal(formatTypePrefix(variantName) + " ").append(text);
    }
}
