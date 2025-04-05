package tocraft.walkers.impl.variant;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.animal.Rabbit;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import tocraft.walkers.api.variant.TypeProvider;
import tocraft.walkers.mixin.accessor.RabbitAccessor;

public class RabbitTypeProvider extends TypeProvider<Rabbit> {

    @Override
    public int getVariantData(Rabbit entity) {
        int id = entity.getVariant().id();
        if (id == 99) return 6;
        else return id;
    }

    @Override
    public Rabbit create(EntityType<Rabbit> type, Level level, @NotNull Player player, int data) {
        Rabbit rabbit = new Rabbit(type, level);
        if (data == 6) data = 99;
        ((RabbitAccessor) rabbit).callSetVariant(Rabbit.Variant.byId(data));
        return rabbit;
    }

    @Override
    public int getFallbackData() {
        return Rabbit.Variant.BROWN.id();
    }

    @Override
    public int size(Level level) {
        return Rabbit.Variant.values().length;
    }

    @Override
    public Component modifyText(Rabbit entity, MutableComponent text) {
        String variantName = entity.getVariant().getSerializedName();
        return Component.literal(variantName + " ").append(text);
    }
}
