package tocraft.walkers.api;

import dev.architectury.networking.NetworkManager;
import io.netty.buffer.Unpooled;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import tocraft.walkers.impl.PlayerDataProvider;
import tocraft.walkers.network.NetworkHandler;

public class PlayerShape {

    /**
     * Returns the shape associated with the {@link Player} this component is attached to.
     *
     * <p>Note that this method may return null, which represents "no shape."
     *
     * @return the current {@link LivingEntity} shape associated with this component's player owner, or null if they have no shape equipped
     */
    public static LivingEntity getCurrentShape(Player player) {
        return ((PlayerDataProvider) player).walkers$getCurrentShape();
    }

    /**
     * Sets the shape of the specified player.
     *
     * <p>Setting a shape refreshes the player's dimensions/hitbox, and toggles flight capabilities depending on the entity.
     * To clear this component's shape, pass null.
     *
     * @param entity {@link LivingEntity} new shape for this component, or null to clear
     */
    public static boolean updateShapes(ServerPlayer player, LivingEntity entity) {
        return ((PlayerDataProvider) player).walkers$updateShapes(entity);
    }

    public static void sync(ServerPlayer player) {
        sync(player, player);
    }

    public static void sync(ServerPlayer changed, ServerPlayer packetTarget) {
        FriendlyByteBuf packet = new FriendlyByteBuf(Unpooled.buffer());
        CompoundTag entityTag = new CompoundTag();

        // serialize current shape data to tag if it exists
        LivingEntity shape = getCurrentShape(changed);
        if (shape != null) {
            shape.saveWithoutId(entityTag);
        }

        // put entity type ID under the key "id", or "minecraft:empty" if no shape is equipped (or the shape entity type is invalid)
        packet.writeUUID(changed.getUUID());
        packet.writeUtf(shape == null ? "minecraft:empty" : BuiltInRegistries.ENTITY_TYPE.getKey(shape.getType()).toString());
        packet.writeNbt(entityTag);
        NetworkManager.sendToPlayer(packetTarget, NetworkHandler.SHAPE_SYNC, packet);
    }
}
