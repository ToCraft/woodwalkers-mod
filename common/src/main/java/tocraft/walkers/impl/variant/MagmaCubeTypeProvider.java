package tocraft.walkers.impl.variant;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.monster.MagmaCube;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import tocraft.walkers.api.variant.TypeProvider;
import tocraft.walkers.mixin.accessor.SlimeEntityAccessor;

public class MagmaCubeTypeProvider extends TypeProvider<MagmaCube> {

    @Override
    public int getVariantData(@NotNull MagmaCube entity) {
        return entity.getSize();
    }

    @Override
    public MagmaCube create(EntityType<MagmaCube> type, Level level, @NotNull Player player, int data) {
        MagmaCube magmaCube = new MagmaCube(type, level);
        ((SlimeEntityAccessor) magmaCube).callSetSize(data + 1, true);
        return magmaCube;
    }

    @Override
    public int getFallbackData() {
        return 0;
    }

    @Override
    public int size(Level level) {
        return 5;
    }

    @Override
    public Component modifyText(@NotNull MagmaCube entity, MutableComponent text) {
        return Component.literal(String.format("Size %d ", entity.getSize())).append(text);
    }
}
