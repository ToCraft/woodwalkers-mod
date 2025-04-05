package tocraft.walkers.impl.variant;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.monster.Shulker;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import tocraft.walkers.api.variant.TypeProvider;
import tocraft.walkers.mixin.accessor.ShulkerAccessor;

import java.util.Optional;

public class ShulkerTypeProvider extends TypeProvider<Shulker> {

    @Override
    public int getVariantData(Shulker entity) {
        Optional<DyeColor> color = entity.getVariant();
        return color.map(DyeColor::getId).orElse(16);
    }

    @Override
    public Shulker create(EntityType<Shulker> type, Level world, @NotNull Player player, int data) {
        Shulker shulker = new Shulker(type, world);
        if (data < 16) {
            ((ShulkerAccessor) shulker).callSetVariant(Optional.of(DyeColor.byId(data)));
        }
        return shulker;
    }

    @Override
    public int getFallbackData() {
        return 16;
    }

    @Override
    public int getRange(Level level) {
        return 17;
    }

    @Override
    public Component modifyText(Shulker entity, MutableComponent text) {
        int data = getVariantData(entity);
        String prefix = data < 16 ? formatTypePrefix(DyeColor.byId(getVariantData(entity)).getName()) : "Natural";
        return Component.literal(prefix + " ").append(text);
    }
}
