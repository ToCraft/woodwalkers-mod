package mcp.mobius.waila.api;

public interface IClientRegistrar {
    <T> void override(IEntityComponentProvider provider, Class<T> clazz);
}
