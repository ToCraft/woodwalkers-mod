package tocraft.walkers.mixin.accessor;

import net.minecraft.world.entity.animal.Pufferfish;
import net.minecraft.world.entity.animal.Sheep;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(Pufferfish.class)
public interface PufferfishAccessor {
    @Accessor
    void setInflateCounter(int inflateCounter);
    @Accessor
    int getInflateCounter();
    @Accessor
    void setDeflateTimer(int deflateTimer);
    @Accessor
    int getDeflateTimer();
}
