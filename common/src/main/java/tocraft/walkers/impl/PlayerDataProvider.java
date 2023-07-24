package tocraft.walkers.impl;

import tocraft.walkers.api.variant.ShapeType;
import net.minecraft.entity.LivingEntity;
import org.jetbrains.annotations.Nullable;

public interface PlayerDataProvider {

    ShapeType<?> get2ndShape();
    void set2ndShape(ShapeType<?> unlocked);

    int getRemainingHostilityTime();
    void setRemainingHostilityTime(int max);

    int getAbilityCooldown();
    void setAbilityCooldown(int cooldown);

    LivingEntity getCurrentShape();
    void setCurrentShape(@Nullable LivingEntity shape);
    boolean updateShapes(@Nullable LivingEntity shape);

    ShapeType<?> getCurrentShapeType();
}
