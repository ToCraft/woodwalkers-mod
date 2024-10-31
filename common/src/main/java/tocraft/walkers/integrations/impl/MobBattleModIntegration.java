package tocraft.walkers.integrations.impl;

import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import tocraft.walkers.integrations.AbstractIntegration;

public class MobBattleModIntegration extends AbstractIntegration {
    public static final String MODID = "mobbattle";

    @Override
    public boolean mightAttackInnocent(Mob mob, Player player) {
        return mob.getTeam() == null || player.getTeam() == null;
    }
}
