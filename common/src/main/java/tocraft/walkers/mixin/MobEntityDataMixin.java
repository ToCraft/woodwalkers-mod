package tocraft.walkers.mixin;

import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import tocraft.walkers.impl.ShapeDataProvider;

@Mixin(Mob.class)
public abstract class MobEntityDataMixin extends LivingEntity implements ShapeDataProvider {
    @Unique
    private boolean walkers$isShape = false;
    @Nullable
    @Unique
    private DamageSource walkers$playerDamageSource = null;

    protected MobEntityDataMixin(EntityType<? extends LivingEntity> entityType, Level level) {
        super(entityType, level);
    }

    @Override
    public boolean walkers$isShape() {
        return walkers$isShape;
    }

    @Override
    @Unique
    public void walkers$setIsShape(boolean isShape) {
        walkers$isShape = isShape;
    }

    @Unique
    @Override
    public DamageSource walkers$playerDamageSource() {
        return walkers$playerDamageSource;
    }

    @Unique
    @Override
    public void walkers$setPlayerDamageSource(DamageSource playerDamageSource) {
        walkers$playerDamageSource = playerDamageSource;
    }
}
