package tocraft.walkers.mixin.accessor;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

import net.minecraft.tags.EntityTypeTags;
import net.minecraft.tags.Tag;
import net.minecraft.world.entity.EntityType;

@Mixin(EntityTypeTags.class)
public interface EntityTypeTagsAccessor {
    @Invoker
	static Tag.Named<EntityType<?>> callBind(String id) {
    	throw new UnsupportedOperationException();
	}
}