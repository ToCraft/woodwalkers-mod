package dev.tocraft.walkers;

import dev.tocraft.walkers.api.PlayerShape;
import mcp.mobius.waila.api.*;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.NotNull;

@SuppressWarnings("unused")
public class WalkersWailaPlugin implements IWailaClientPlugin {

    @Override
    public void register(@NotNull IClientRegistrar registrar) {
        registrar.override(new EntityOverride(), Player.class);
    }

    public static class EntityOverride implements IEntityComponentProvider {
        @Override
        public Entity getOverride(@NotNull IEntityAccessor accessor, IPluginConfig config) {
            return PlayerShape.getCurrentShape(accessor.getEntity());
        }
    }
}
