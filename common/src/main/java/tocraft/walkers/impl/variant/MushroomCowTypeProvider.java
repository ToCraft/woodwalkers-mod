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
        mooshroom.setVariant(MushroomCow.MushroomType.values()[data]);
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
        return Component.literal(formatTypePrefix(entity.getVariant().getSerializedName()) + " ").append(text);
    }
}