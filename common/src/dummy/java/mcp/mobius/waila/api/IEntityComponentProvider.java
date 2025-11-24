package mcp.mobius.waila.api;

import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.Nullable;

@SuppressWarnings("unused")
public interface IEntityComponentProvider {
    /**
     * Callback used to override the default Waila lookup system.
     *
     * @param accessor contains most of the relevant information about the current environment
     * @param config   current plugin configuration
     * @return {@code null} if override is not required, an {@link Entity} otherwise
     * @see IClientRegistrar#override(IEntityComponentProvider, Class)
     */
    @Nullable
    default Entity getOverride(IEntityAccessor accessor, IPluginConfig config) {
        return null;
    }
}
