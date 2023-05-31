package tocraft.walkers.impl.variant;

import tocraft.walkers.api.variant.TypeProvider;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.passive.WolfEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.world.World;

public class WolfTypeProvider extends TypeProvider<WolfEntity> {

    @Override
    public int getVariantData(WolfEntity entity) {
        NbtCompound nbt = new NbtCompound();
        entity.writeNbt(nbt);

        if (nbt.contains("isDev")) {
            if(nbt.getBoolean("isDev"))
                return 1;
        }
        return 0;
    }

    @Override
    public WolfEntity create(EntityType<WolfEntity> type, World world, int data) {
        WolfEntity wolf = new WolfEntity(type, world);

        NbtCompound nbt = new NbtCompound();
        wolf.writeNbt(nbt);
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
    public Text modifyText(WolfEntity wolf, MutableText text) {
        if (getVariantData(wolf) == 1)
            return Text.literal(formatTypePrefix("Dev" + " ")).append(text);
        return text;
    }
}
