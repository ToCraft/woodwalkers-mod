package tocraft.walkers.mixin.accessor;

import net.minecraft.world.entity.animal.Squid;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(Squid.class)
public interface SquidEntityAccessor {

    @Accessor
    float getRotateSpeed();

    @Accessor
    void setRotateSpeed(float rotateSpeed);

    @Accessor
    float getSpeed();

    @Accessor
    void setSpeed(float speed);
}
