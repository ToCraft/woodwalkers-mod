package tocraft.walkers.api;

import dev.tocraft.craftedcore.network.ModernNetworking;
import net.minecraft.core.UUIDUtil;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.storage.TagValueOutput;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;
import tocraft.walkers.Walkers;
import tocraft.walkers.api.blacklist.EntityBlacklist;
import tocraft.walkers.api.events.ShapeEvents;
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
    @Nullable
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
    public static boolean updateShapes(ServerPlayer player, @Nullable LivingEntity entity) {
        if (entity != null && EntityBlacklist.isBlacklisted(entity.getType())) {
            return false;
        }

        InteractionResult result = ShapeEvents.SWAP_SHAPE.invoke().swap(player, entity);
        if (result == InteractionResult.FAIL) {
            return false;
        }

        ((PlayerDataProvider) player).walkers$updateShapes(entity);

        return true;
    }

    @ApiStatus.Internal
    public static void sync(ServerPlayer player) {
        sync(player, player);
    }

    @ApiStatus.Internal
    public static void sync(ServerPlayer changed, ServerPlayer packetTarget) {
        CompoundTag data = new CompoundTag();
        CompoundTag entityTag = new CompoundTag();

        // serialize current shape data to tag if it exists
        LivingEntity shape = getCurrentShape(changed);
        if (shape != null) {
            TagValueOutput out = TagValueOutput.createWithContext(Walkers.PROBLEM_REPORTER, changed.registryAccess());
            shape.saveWithoutId(out);
            entityTag = out.buildResult();
        }

        // put entity type ID under the key "id", or "minecraft:empty" if no shape is equipped (or the shape entity type is invalid)
        data.putIntArray("uuid", UUIDUtil.uuidToIntArray(changed.getUUID()));
        data.putString("type", shape == null ? "minecraft:empty" : EntityType.getKey(shape.getType()).toString());
        data.put("entity_tag", entityTag);
        ModernNetworking.sendToPlayer(packetTarget, NetworkHandler.SHAPE_SYNC, data);
    }
}
