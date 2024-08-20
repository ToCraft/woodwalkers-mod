package tocraft.walkers.integrations;

import net.minecraft.world.entity.Entity;
import tocraft.craftedcore.platform.PlatformData;
import tocraft.walkers.integrations.impl.*;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

public class Integrations {
    private static final Map<String, AbstractIntegration> INTEGRATIONS = new HashMap<>();

    public static void initIntegrations() {
        register(MobBattleModIntegration.MODID, MobBattleModIntegration::new);
        register(GuardVillagersIntegration.MODID, GuardVillagersIntegration::new);
        register(MoreMobVariantsIntegration.MODID, MoreMobVariantsIntegration::new);
        register(MutantMonstersIntegration.MODID, MutantMonstersIntegration::new);
        register(AlexMobsIntegration.MODID, AlexMobsIntegration::new);
        register(PlayerAbilityLibIntegration.MODID, PlayerAbilityLibIntegration::new);
    }

    public static void registerAbilities() {
        for (AbstractIntegration loadedIntegration : INTEGRATIONS.values()) {
            loadedIntegration.registerAbilities();
        }
    }

    public static void registerTraits() {
        for (AbstractIntegration loadedIntegration : INTEGRATIONS.values()) {
            loadedIntegration.registerTraits();
        }
    }

    public static void registerTypeProvider() {
        for (AbstractIntegration loadedIntegration : INTEGRATIONS.values()) {
            loadedIntegration.registerTypeProvider();
        }
    }

    public static void registerEntityBlacklist() {
        for (AbstractIntegration loadedIntegration : INTEGRATIONS.values()) {
            loadedIntegration.registerEntityBlacklist();
        }
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
        if (PlatformData.isModLoaded(modid)) {
            INTEGRATIONS.put(modid, integration);
        }
    }

    public static void register(String modid, Supplier<AbstractIntegration> integration) {
        if (PlatformData.isModLoaded(modid)) {
            INTEGRATIONS.put(modid, integration.get());
        }
    }
}
