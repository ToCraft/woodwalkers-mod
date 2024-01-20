package tocraft.walkers.impl.variant;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.animal.horse.Horse;
import net.minecraft.world.entity.animal.horse.Variant;
import net.minecraft.world.level.Level;
import tocraft.walkers.api.variant.TypeProvider;

public class HorseTypeProvider extends TypeProvider<Horse> {

    @Override
    public int getVariantData(Horse horse) {
        return horse.getVariant().getId();
    }

    @Override
    public Horse create(EntityType<Horse> type, Level world, int data) {
        Horse horse = new Horse(type, world);
        horse.setVariant(Variant.byId(data));
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
        return Component.literal(entity.getVariant().getSerializedName() + " ").append(text);
    }
}
