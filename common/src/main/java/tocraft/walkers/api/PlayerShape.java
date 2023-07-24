package tocraft.walkers.api;

import dev.architectury.networking.NetworkManager;
import tocraft.walkers.api.variant.ShapeType;
import tocraft.walkers.impl.PlayerDataProvider;
import tocraft.walkers.network.NetworkHandler;
import io.netty.buffer.Unpooled;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.registry.Registries;

public class PlayerShape {

    /**
     * Returns the shape associated with the {@link PlayerEntity} this component is attached to.
     *
     * <p>Note that this method may return null, which represents "no shape."
     *
     * @return the current {@link LivingEntity} shape associated with this component's player owner, or null if they have no shape equipped
     */
    public static LivingEntity getCurrentShape(PlayerEntity player) {
        return ((PlayerDataProvider) player).getCurrentShape();
    }

    public static ShapeType<?> getCurrentShapeType(PlayerEntity player) {
        return ((PlayerDataProvider) player).getCurrentShapeType();
    }

    /**
     * Sets the shape of the specified player.
     *
     * <p>Setting a shape refreshes the player's dimensions/hitbox, and toggles flight capabilities depending on the entity.
     * To clear this component's shape, pass null.
     *
     * @param entity {@link LivingEntity} new shape for this component, or null to clear
     */
    public static boolean updateShapes(ServerPlayerEntity player, ShapeType<?> type, LivingEntity entity) {
        return ((PlayerDataProvider) player).updateShapes(entity);
    }

    public static void sync(ServerPlayerEntity player) {
        sync(player, player);
    }

    public static void sync(ServerPlayerEntity changed, ServerPlayerEntity packetTarget) {
        PacketByteBuf packet = new PacketByteBuf(Unpooled.buffer());
        NbtCompound entityTag = new NbtCompound();

        // serialize current shape data to tag if it exists
        LivingEntity shape = getCurrentShape(changed);
        if(shape != null) {
            shape.writeNbt(entityTag);
        }

        // put entity type ID under the key "id", or "minecraft:empty" if no shape is equipped (or the shape entity type is invalid)
        packet.writeUuid(changed.getUuid());
        packet.writeString(shape == null ? "minecraft:empty" : Registries.ENTITY_TYPE.getId(shape.getType()).toString());
        packet.writeNbt(entityTag);
        NetworkManager.sendToPlayer(packetTarget, NetworkHandler.SHAPE_SYNC, packet);
    }
}
