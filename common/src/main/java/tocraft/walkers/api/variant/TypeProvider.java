package tocraft.walkers.api.variant;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

import java.util.Locale;

public abstract class TypeProvider<T extends LivingEntity> {
    @SuppressWarnings("unchecked")
    public ShapeType<T> create(@NotNull T entity) {
        return ShapeType.from((EntityType<T>) entity.getType(), getVariantData(entity));
    }

    public abstract int getVariantData(T entity);

    /**
     * Create the entity based on player data
     */
    public abstract T create(EntityType<T> type, Level world, @NotNull Player player, int data);

    public abstract int getFallbackData();

    /**
     * @return the highest variant id + 1
     */
    public abstract int getRange(Level level);

    public abstract Component modifyText(T entity, MutableComponent text);

    public final @NotNull String formatTypePrefix(@NotNull String prefix) {
        return String.valueOf(prefix.charAt(0)).toUpperCase(Locale.ROOT) + prefix.substring(1);
    }
}
