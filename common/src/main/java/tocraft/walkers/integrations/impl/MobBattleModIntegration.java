package tocraft.walkers.integrations.impl;

import net.minecraft.world.entity.Entity;
import tocraft.walkers.integrations.AbstractIntegration;

public class MobBattleModIntegration extends AbstractIntegration {
    @Override
    public boolean mightAttackInnocent(Entity entity1, Entity entity2) {
        return entity1.getTeam() == null || entity2.getTeam() == null;
    }
}
