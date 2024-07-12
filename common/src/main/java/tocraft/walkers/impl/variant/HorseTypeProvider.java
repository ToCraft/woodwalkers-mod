package tocraft.walkers.impl.variant;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.animal.horse.Horse;
import net.minecraft.world.entity.animal.horse.Variant;
import net.minecraft.world.level.Level;
import tocraft.craftedcore.patched.TComponent;
import tocraft.walkers.api.variant.TypeProvider;
import tocraft.walkers.mixin.accessor.HorseAccessor;

public class HorseTypeProvider extends TypeProvider<Horse> {

    @Override
    public int getVariantData(Horse horse) {
        return horse.getVariant().getId();
    }

    @Override
    public Horse create(EntityType<Horse> type, Level world, int data) {
        Horse horse = new Horse(type, world);
        //#if MC>1182
        horse.setVariant(Variant.byId(data));
        //#else
        //$$ ((HorseAccessor) horse).callSetVariantAndMarkings(Variant.byId(data), horse.getMarkings());
        //#endif
        return horse;
    }

    @Override
    public int getFallbackData() {
        return 0;
    }

    @Override
    public int getRange() {
        return 6;
    }

    @Override
    public Component modifyText(Horse entity, MutableComponent text) {
        //#if MC>1182
        String variantName = entity.getVariant().getSerializedName();
        //#else
        //$$ String variantName = formatTypePrefix(entity.getVariant().name());
        //#endif
        return TComponent.literal(variantName + " ").append(text);
    }
}
