package tocraft.walkers.impl.variant;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.animal.Rabbit;
import net.minecraft.world.level.Level;
import tocraft.walkers.api.variant.TypeProvider;

public class RabbitTypeProvider extends TypeProvider<Rabbit> {

    @Override
    public int getVariantData(Rabbit entity) {
        int id = entity.getVariant().id();
        if (id == 99) return 6;
        else return id;
    }

    @Override
    public Rabbit create(EntityType<Rabbit> type, Level level, int data) {
        Rabbit rabbit = new Rabbit(type, level);
        if (data == 6) data = 99;
        rabbit.setVariant(Rabbit.Variant.byId(data));
        return rabbit;
    }

    @Override
    public int getFallbackData() {
        return Rabbit.Variant.BROWN.id();
    }

    @Override
    public int getRange() {
        return Rabbit.Variant.values().length - 1;
    }

    @Override
    public Component modifyText(Rabbit entity, MutableComponent text) {
        return Component.literal(entity.getVariant().getSerializedName() + " ").append(text);
    }
}