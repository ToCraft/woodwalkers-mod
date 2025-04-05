package tocraft.walkers.mixin.accessor;

import net.minecraft.world.entity.animal.horse.Llama;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(Llama.class)
public interface LlamaAccessor {
    @Invoker
    void callSetVariant(Llama.Variant variant);
}
