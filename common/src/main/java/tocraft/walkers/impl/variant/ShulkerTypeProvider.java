package tocraft.walkers.impl.variant;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.monster.Shulker;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.Level;
import tocraft.walkers.api.variant.TypeProvider;

public class ShulkerTypeProvider extends TypeProvider<Shulker> {

    @Override
    public int getVariantData(Shulker entity) {
        DyeColor color = entity.getColor();
        return color != null ? color.getId() : 16;
    }

    @Override
    public Shulker create(EntityType<Shulker> type, Level world, int data) {
        Shulker shulker = new Shulker(type, world);
        if (data < 16) shulker.setColor(DyeColor.byId(data));
        return shulker;
    }

    @Override
    public int getFallbackData() {
        return 16;
    }

    @Override
    public int getRange() {
        return 16;
    }

    @Override
    public Component modifyText(Shulker entity, MutableComponent text) {
        int data = getVariantData(entity);
        String prefix = data < 16 ? formatTypePrefix(DyeColor.byId(getVariantData(entity)).getName()) : "Natural";
        return new TextComponent(prefix + " ").append(text);
    }
}
