package dev.tocraft.walkers.impl.variant;

import dev.tocraft.walkers.api.variant.TypeProvider;
import dev.tocraft.walkers.mixin.accessor.MushroomCowAccessor;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.animal.MushroomCow;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

public class MushroomCowTypeProvider extends TypeProvider<MushroomCow> {

    @Override
    public int getVariantData(@NotNull MushroomCow entity) {
        return entity.getVariant().ordinal();
    }

    @Override
    public MushroomCow create(EntityType<MushroomCow> type, Level level, @NotNull Player player, int data) {
        MushroomCow mooshroom = new MushroomCow(type, level);
        ((MushroomCowAccessor) mooshroom).callSetVariant(MushroomCow.Variant.values()[data]);
        return mooshroom;
    }

    @Override
    public int getFallbackData() {
        return MushroomCow.Variant.RED.ordinal();
    }

    @Override
    public int size(Level level) {
        return MushroomCow.Variant.values().length;
    }

    @Override
    public Component modifyText(@NotNull MushroomCow entity, MutableComponent text) {
        String variantName = entity.getVariant().getSerializedName();
        return Component.literal(formatTypePrefix(variantName) + " ").append(text);
    }
}
