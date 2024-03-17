package tocraft.walkers.integrations;

import dev.architectury.platform.Platform;
import net.minecraft.world.entity.Entity;
import tocraft.walkers.integrations.impl.GuardVillagersIntegration;
import tocraft.walkers.integrations.impl.MobBattleModIntegration;

import java.util.HashMap;
import java.util.Map;

public class Integrations {
    private static final Map<String, AbstractIntegration> INTEGRATIONS = new HashMap<>();

    static {
        register(MobBattleModIntegration.MODID, new MobBattleModIntegration());
        register(GuardVillagersIntegration.MODID, new GuardVillagersIntegration());
    }

    public static void initialize() {
        for (AbstractIntegration loadedIntegration : INTEGRATIONS.values()) {
            loadedIntegration.initialize();
        }
    }

    public static boolean mightAttackInnocent(Entity entity1, Entity entity2) {
        boolean bool = true;
        for (AbstractIntegration loadedIntegration : INTEGRATIONS.values()) {
            bool = bool && loadedIntegration.mightAttackInnocent(entity1, entity2);
        }

        return bool;
    }

    public static void register(String modid, AbstractIntegration integration) {
        if (Platform.isModLoaded(modid))
            INTEGRATIONS.put(modid, integration);
    }
}
