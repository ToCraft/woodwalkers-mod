package tocraft.walkers.integrations;

import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.ApiStatus;
import tocraft.craftedcore.platform.PlatformData;
import tocraft.walkers.integrations.impl.*;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

public class Integrations {
    private static final Map<String, AbstractIntegration> INTEGRATIONS = new HashMap<>();

    @ApiStatus.Internal
    public static void initIntegrations() {
        register(MobBattleModIntegration.MODID, MobBattleModIntegration::new);
        register(GuardVillagersIntegration.MODID, GuardVillagersIntegration::new);
        register(MoreMobVariantsIntegration.MODID, MoreMobVariantsIntegration::new);
        register(MutantMonstersIntegration.MODID, MutantMonstersIntegration::new);
        register(AlexMobsIntegration.MODID, AlexMobsIntegration::new);
        register(PlayerAbilityLibIntegration.MODID, PlayerAbilityLibIntegration::new);
        register(BackportedWolvesIntegration.MODID, BackportedWolvesIntegration::new);
    }

    @ApiStatus.Internal
    public static void registerAbilities() {
        for (AbstractIntegration loadedIntegration : INTEGRATIONS.values()) {
            loadedIntegration.registerAbilities();
        }
    }

    @ApiStatus.Internal
    public static void registerTraits() {
        for (AbstractIntegration loadedIntegration : INTEGRATIONS.values()) {
            loadedIntegration.registerTraits();
        }
    }

    @ApiStatus.Internal
    public static void registerTypeProvider() {
        for (AbstractIntegration loadedIntegration : INTEGRATIONS.values()) {
            loadedIntegration.registerTypeProvider();
        }
    }

    @ApiStatus.Internal
    public static void registerEntityBlacklist() {
        for (AbstractIntegration loadedIntegration : INTEGRATIONS.values()) {
            loadedIntegration.registerEntityBlacklist();
        }
    }

    @ApiStatus.Internal
    public static void initialize() {
        for (AbstractIntegration loadedIntegration : INTEGRATIONS.values()) {
            loadedIntegration.initialize();
        }
    }

    @ApiStatus.Internal
    public static boolean mightAttackInnocent(Mob mob, Player target) {
        boolean bool = true;
        for (AbstractIntegration loadedIntegration : INTEGRATIONS.values()) {
            bool = bool && loadedIntegration.mightAttackInnocent(mob, target);
        }

        return bool;
    }

    /**
     * Register an Integration for the specified mod id
     *
     * @param modid the mod that is required. Use "minecraft" if no mod is required
     * @param integration the {@link AbstractIntegration Integration} that should be registered
     */
    public static void register(String modid, AbstractIntegration integration) {
        register(modid, () -> integration);
    }

    /**
     * Register an Integration for the specified mod id
     *
     * @param modid the mod that is required. Use "minecraft" if no mod is required
     * @param integration the {@link AbstractIntegration Integration} that should be registered. Obtained as a supplier so the class is only instantiated once it's guaranteed that the mod is loaded. This way, you can use classes of the mod without worrying about {@link ClassNotFoundException ClassNotFoundExceptions} when the mod isn't loaded
     */
    public static void register(String modid, Supplier<AbstractIntegration> integration) {
        if (PlatformData.isModLoaded(modid)) {
            INTEGRATIONS.put(modid, integration.get());
        }
    }
}
