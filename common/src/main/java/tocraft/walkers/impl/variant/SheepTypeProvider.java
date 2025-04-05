package tocraft.walkers.impl.variant;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.animal.sheep.Sheep;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import tocraft.walkers.api.variant.TypeProvider;

public class SheepTypeProvider extends TypeProvider<Sheep> {

    @Override
    public int getVariantData(Sheep entity) {
        return entity.getColor().ordinal();
    }

    @Override
    public Sheep create(EntityType<Sheep> type, Level world, @NotNull Player player, int data) {
        Sheep sheep = new Sheep(type, world);
        sheep.setColor(DyeColor.byId(data));
        return sheep;
    }

    @Override
    public int getFallbackData() {
        return DyeColor.WHITE.getId();
    }

    @Override
    public int size(Level level) {
        return 16;
    }

    @Override
    public Component modifyText(Sheep sheep, MutableComponent text) {
        return Component.literal(formatTypePrefix(DyeColor.byId(getVariantData(sheep)).getName()) + " ").append(text);
    }
}
