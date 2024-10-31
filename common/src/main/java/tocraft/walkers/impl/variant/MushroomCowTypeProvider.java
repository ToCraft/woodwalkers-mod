package tocraft.walkers.impl.variant;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.animal.MushroomCow;
import net.minecraft.world.level.Level;
import tocraft.walkers.api.variant.TypeProvider;

public class MushroomCowTypeProvider extends TypeProvider<MushroomCow> {

    @Override
    public int getVariantData(MushroomCow entity) {
        return entity.getVariant().ordinal();
    }

    @Override
    public MushroomCow create(EntityType<MushroomCow> type, Level level, int data) {
        MushroomCow mooshroom = new MushroomCow(type, level);
        mooshroom.setVariant(MushroomCow.Variant.values()[data]);
        return mooshroom;
    }

    @Override
    public int getFallbackData() {
        return MushroomCow.Variant.RED.ordinal();
    }

    @Override
    public int getRange() {
        return MushroomCow.Variant.values().length - 1;
    }

    @Override
    public Component modifyText(MushroomCow entity, MutableComponent text) {
        String variantName = entity.getVariant().getSerializedName();
        return Component.literal(formatTypePrefix(variantName) + " ").append(text);
    }
}
