package tocraft.walkers.impl.variant;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.level.Level;
import tocraft.walkers.api.variant.TypeProvider;

public class CreeperTypeProvider extends TypeProvider<Creeper> {
    @Override
    public int getVariantData(Creeper entity) {
        return entity.isPowered() ? 1 : 0;
    }

    @Override
    public Creeper create(EntityType<Creeper> type, Level world, int data) {
        CompoundTag tag = new CompoundTag();
        tag.putBoolean("powered", data == 1);
        CompoundTag compoundTag = tag.copy();
        compoundTag.putString("id", EntityType.getKey(type).toString());
        return (Creeper) EntityType.loadEntityRecursive(compoundTag, world, entity -> entity);
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
    public Component modifyText(Creeper creeper, MutableComponent text) {
        int variant = getVariantData(creeper);
        return Component.literal(variant == 1 ? "Powered " : "").append(text);
    }
}

