package tocraft.walkers.integrations.impl;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.Nullable;
import tocraft.walkers.Walkers;
import tocraft.walkers.api.FlightHelper;
import tocraft.walkers.integrations.AbstractIntegration;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class PlayerAbilityLibIntegration extends AbstractIntegration {
    public static final String MODID = "playerabilitylib";
    @Nullable
    private final Object FLYING_MORPH_SOURCE = getAbilitySource(Walkers.id("flying_morph"));
    @Nullable
    private final Object ALLOW_FLIGHT_ABILITY = getVanillaFlightAbility();

    @Override
    public void initialize() {
        FlightHelper.GRANT.register(player -> {
            boolean bool = grantFlight(player);
            return bool ? InteractionResult.SUCCESS : InteractionResult.PASS;
        });
        FlightHelper.REVOKE.register(player -> {
            boolean bool = revokeFlight(player);
            return bool ? InteractionResult.SUCCESS : InteractionResult.PASS;
        });
    }

    @Nullable
    private Object getAbilitySource(ResourceLocation id) {
        try {
            Class<?> palClass = Class.forName("io.github.ladysnake.pal.Pal");
            Method getAbilitySource = palClass.getDeclaredMethod("getAbilitySource", ResourceLocation.class);
            return getAbilitySource.invoke(null, id);
        } catch (ClassNotFoundException | IllegalAccessException | InvocationTargetException |
                 NoSuchMethodException e) {
            error(e);
            return null;
        }
    }

    @Nullable
    private Object getVanillaFlightAbility() {
        try {
            Class<?> vanillaAbilitiesClass = Class.forName("io.github.ladysnake.pal.VanillaAbilities");
            Field ALLOW_FLYING = vanillaAbilitiesClass.getDeclaredField("ALLOW_FLYING");
            return ALLOW_FLYING.get(null);
        } catch (ClassNotFoundException | IllegalAccessException | NoSuchFieldException e) {
            error(e);
            return null;
        }
    }

    private boolean grantFlight(ServerPlayer player) {
        if (FLYING_MORPH_SOURCE != null && ALLOW_FLIGHT_ABILITY != null) {
            try {
                Class<?> palClass = Class.forName("io.github.ladysnake.pal.Pal");
                Method grantAbility = palClass.getDeclaredMethod("grantAbility", Player.class, ALLOW_FLIGHT_ABILITY.getClass(), FLYING_MORPH_SOURCE.getClass());
                grantAbility.invoke(null, player, ALLOW_FLIGHT_ABILITY, FLYING_MORPH_SOURCE);
                return true;
            } catch (ReflectiveOperationException e) {
                error(e);
            }
        }
        return false;
    }

    private boolean revokeFlight(ServerPlayer player) {
        if (FLYING_MORPH_SOURCE != null && ALLOW_FLIGHT_ABILITY != null) {
            try {
                Class<?> palClass = Class.forName("io.github.ladysnake.pal.Pal");
                Method revokeAbility = palClass.getDeclaredMethod("revokeAbility", Player.class, ALLOW_FLIGHT_ABILITY.getClass(), FLYING_MORPH_SOURCE.getClass());
                revokeAbility.invoke(null, player, ALLOW_FLIGHT_ABILITY, FLYING_MORPH_SOURCE);
                return true;
            } catch (ReflectiveOperationException e) {
                error(e);
            }
        }
        return false;
    }

    private static void error(Throwable e) {
        Walkers.LOGGER.error("{} couldn't succeed, there was probably an API change in the PlayerAbilityLib. Please report this!", PlayerAbilityLibIntegration.class, e);
    }
}
