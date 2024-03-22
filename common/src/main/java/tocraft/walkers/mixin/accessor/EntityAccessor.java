package tocraft.walkers.mixin.accessor;

import com.google.common.collect.ImmutableList;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(Entity.class)
public interface EntityAccessor {
    @Accessor("wasTouchingWater")
    void setTouchingWater(boolean touchingWater);

    @Accessor
    void setVehicle(Entity vehicle);

    @Accessor
    void setPassengers(ImmutableList<Entity> passengers);

    @Invoker("setSharedFlag")
    void shape_callSetFlag(int index, boolean value);

    @Invoker("playStepSound")
    void shape_callPlayStepSound(BlockPos pos, BlockState state);
}
