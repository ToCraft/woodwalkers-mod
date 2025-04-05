package tocraft.walkers.impl.variant;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.monster.Slime;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import tocraft.walkers.api.variant.TypeProvider;
import tocraft.walkers.mixin.accessor.SlimeEntityAccessor;

public class SlimeTypeProvider extends TypeProvider<Slime> {

    @Override
    public int getVariantData(Slime entity) {
        return entity.getSize();
    }

    @Override
    public Slime create(EntityType<Slime> type, Level world, @NotNull Player player, int data) {
        Slime slime = new Slime(type, world);
        ((SlimeEntityAccessor) slime).callSetSize(data + 1, true);
        return slime;
    }

    @Override
    public int getFallbackData() {
        return 0;
    }

    @Override
    public int getRange(Level level) {
        return 5;
    }

    @Override
    public Component modifyText(@NotNull Slime entity, MutableComponent text) {
        return Component.literal(String.format("Size %d ", entity.getSize())).append(text);
    }
}
