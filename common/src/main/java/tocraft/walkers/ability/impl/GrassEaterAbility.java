package tocraft.walkers.ability.impl;

import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import tocraft.walkers.Walkers;
import tocraft.walkers.ability.ShapeAbility;

import java.lang.reflect.Field;

public abstract class GrassEaterAbility<T extends LivingEntity> extends ShapeAbility<T> {
    private final String eatAnimationTickFieldName;

    protected GrassEaterAbility(String eatAnimationTickFieldName) {
        this.eatAnimationTickFieldName = eatAnimationTickFieldName;
    }

    public void eatGrass(T shape) {
        try {
            Field eatAnimationTickField = shape.getClass().getField(eatAnimationTickFieldName);
            eatAnimationTickField.setAccessible(true);
            eatAnimationTickField.set(shape, Mth.positiveCeilDiv(40, 2));
        } catch (IllegalAccessException | NoSuchFieldException e) {
            Walkers.LOGGER.error("Failed to use the \"eating grass\" ability!", e);
        }
    }
}
