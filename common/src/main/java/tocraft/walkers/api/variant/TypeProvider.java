package tocraft.walkers.api.variant;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;

import java.util.Locale;

public abstract class TypeProvider<T extends LivingEntity> {

    public ShapeType<T> create(EntityType<T> type, T entity) {
        return new ShapeType<>((EntityType<T>) entity.getType(), getVariantData(entity));
    }

    public abstract int getVariantData(T entity);

    public abstract T create(EntityType<T> type, Level world, int data);

    public abstract int getFallbackData();

    public abstract int getRange();

    public abstract Component modifyText(T entity, MutableComponent text);

    public final String formatTypePrefix(String prefix) {
        return String.valueOf(prefix.charAt(0)).toUpperCase(Locale.ROOT) + prefix.substring(1);
    }
}
