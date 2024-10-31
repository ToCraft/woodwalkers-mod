package tocraft.walkers.mixin.accessor;

import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.animal.Dolphin;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(Dolphin.class)
public interface DolphinAccessor {
    @Accessor
    static TargetingConditions getSWIM_WITH_PLAYER_TARGETING() {
        throw new AssertionError();
    }
}
