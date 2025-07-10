package dev.tocraft.walkers.impl.variant;

import dev.tocraft.walkers.api.variant.TypeProvider;
import dev.tocraft.walkers.mixin.accessor.HorseAccessor;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.animal.horse.Horse;
import net.minecraft.world.entity.animal.horse.Variant;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

public class HorseTypeProvider extends TypeProvider<Horse> {

    @Override
    public int getVariantData(Horse horse) {
        return horse.getVariant().getId();
    }

    @Override
    public Horse create(EntityType<Horse> type, Level world, @NotNull Player player, int data) {
        Horse horse = new Horse(type, world);
        ((HorseAccessor) horse).callSetVariant(Variant.byId(data));
        return horse;
    }

    @Override
    public int getFallbackData() {
        return 0;
    }

    @Override
    public int size(Level level) {
        return Variant.values().length;
    }

    @Override
    public Component modifyText(Horse entity, MutableComponent text) {
        String variantName = entity.getVariant().getSerializedName();
        return Component.literal(variantName + " ").append(text);
    }
}
