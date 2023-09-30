package tocraft.walkers.impl.variant;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.animal.Wolf;
import net.minecraft.world.level.Level;
import tocraft.walkers.api.variant.TypeProvider;

public class WolfTypeProvider extends TypeProvider<Wolf> {

    @Override
    public int getVariantData(Wolf entity) {
        CompoundTag nbt = new CompoundTag();
        entity.saveWithoutId(nbt);

        if (nbt.contains("isDev")) {
            if(nbt.getBoolean("isDev"))
                return 1;
        }
        return 0;
    }

    @Override
    public Wolf create(EntityType<Wolf> type, Level world, int data) {
        Wolf wolf = new Wolf(type, world);

        CompoundTag nbt = new CompoundTag();
        wolf.saveWithoutId(nbt);
        if (data == 1)
            nbt.putBoolean("isDev", true);
        return wolf;
    }

    @Override
    public int getFallbackData() {
        return 0;
    }

    @Override
    public int getRange() {
        return 1;
    }

    @Override
    public Component modifyText(Wolf wolf, MutableComponent text) {
        if (getVariantData(wolf) == 1)
            return Component.literal(formatTypePrefix("Dev" + " ")).append(text);
        return text;
    }
}
