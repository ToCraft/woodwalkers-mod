package tocraft.walkers.impl.variant;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.animal.Sheep;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.Level;
import tocraft.walkers.api.variant.TypeProvider;

public class SheepTypeProvider extends TypeProvider<Sheep> {

    @Override
    public int getVariantData(Sheep entity) {
        return entity.getColor().ordinal();
    }

    @Override
    public Sheep create(EntityType<Sheep> type, Level world, int data) {
        Sheep sheep = new Sheep(type, world);
        sheep.setColor(DyeColor.byId(data));
        return sheep;
    }

    @Override
    public int getFallbackData() {
        return DyeColor.WHITE.getId();
    }

    @Override
    public int getRange() {
        return DyeColor.BLACK.getId();
    }

    @Override
    public Component modifyText(Sheep sheep, MutableComponent text) {
        return new TextComponent(formatTypePrefix(DyeColor.byId(getVariantData(sheep)).getName()) + " ").append(text);
    }
}
