package tocraft.walkers.integrations;

import net.minecraft.world.entity.Entity;

public abstract class AbstractIntegration {
    public void initialize() {

    }

    public void registerAbilities() {

    }

    public void registerTraits() {

    }

    public void registerTypeProvider() {

    }

    public void registerEntityBlacklist() {

    }

    public boolean mightAttackInnocent(Entity entity1, Entity entity2) {
        return true;
    }
}
