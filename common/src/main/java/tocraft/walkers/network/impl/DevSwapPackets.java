package tocraft.walkers.network.impl;

import dev.architectury.networking.NetworkManager;
import tocraft.walkers.api.PlayerShape;
import tocraft.walkers.api.platform.WalkersConfig;
import tocraft.walkers.api.variant.ShapeType;
import tocraft.walkers.impl.PlayerDataProvider;
import tocraft.walkers.network.ClientNetworking;
import tocraft.walkers.network.NetworkHandler;
import io.netty.buffer.Unpooled;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

public class DevSwapPackets {

    public static void registerDevRequestPacketHandler() {
        NetworkManager.registerReceiver(NetworkManager.Side.C2S, NetworkHandler.DEV_REQUEST, (buf, context) -> {
            Identifier id = buf.readIdentifier();

            context.getPlayer().getServer().execute(() -> {
                NbtCompound nbt = new NbtCompound();
                nbt.putBoolean("isDev", true);
                nbt.putString("id", id.toString());
                ServerWorld serverWorld = ((ServerPlayerEntity) context.getPlayer()).getServerWorld();
                Entity created = EntityType.loadEntityWithPassengers(nbt, serverWorld, it -> it);
        
                if(created instanceof LivingEntity living) {
                    @Nullable ShapeType<?> defaultType = ShapeType.from(living);
        
                    if(defaultType != null) {
                        if(defaultType.getEntityType() == ((PlayerDataProvider)context.getPlayer()).get2ndShape().getEntityType())
                            PlayerShape.updateShapes((ServerPlayerEntity) context.getPlayer(), defaultType, (LivingEntity) created);
                        else if (WalkersConfig.getInstance().devShapeIsThirdShape())
                            PlayerShape.updateShapes((ServerPlayerEntity) context.getPlayer(), defaultType, (LivingEntity) created);
                    }
                }
                
                // Refresh player dimensions
                context.getPlayer().calculateDimensions();
            });
        });
    }

    public static void sendDevSwapRequest(Identifier id) {
        PacketByteBuf packet = new PacketByteBuf(Unpooled.buffer());

        packet.writeIdentifier(id);

        NetworkManager.sendToServer(ClientNetworking.DEV_REQUEST, packet);
    }
}
