package tocraft.walkers.impl.variant;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.animal.MushroomCow;
import net.minecraft.world.level.Level;
import tocraft.walkers.api.variant.TypeProvider;
import tocraft.walkers.mixin.accessor.MushroomCowAccessor;

public class MushroomCowTypeProvider extends TypeProvider<MushroomCow> {

    @Override
    public int getVariantData(MushroomCow entity) {
        return entity.getMushroomType().ordinal();
    }

    @Override
    public MushroomCow create(EntityType<MushroomCow> type, Level level, int data) {
        MushroomCow mooshroom = new MushroomCow(type, level);
        ((MushroomCowAccessor) mooshroom).callSetMushroomType(MushroomCow.MushroomType.values()[data]);
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
        return new TextComponent(formatTypePrefix(entity.getMushroomType().name()) + " ").append(text);
    }
}