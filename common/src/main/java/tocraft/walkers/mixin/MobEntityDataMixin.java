package tocraft.walkers.mixin;

import net.minecraft.world.entity.*;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import tocraft.walkers.impl.ShapeDataProvider;

@Mixin(Mob.class)
public abstract class MobEntityDataMixin extends LivingEntity implements ShapeDataProvider {
    @Unique
    private int walkers$shapedPlayerId = -1;

    protected MobEntityDataMixin(EntityType<? extends LivingEntity> entityType, Level level) {
        super(entityType, level);
    }

    @Unique
    @Override
    public int walkers$shapedPlayer() {
        return walkers$shapedPlayerId;
    }

    @Unique
    @Override
    public void walkers$ShapedPlayer(int id) {
        this.walkers$shapedPlayerId = id;
    }
}
