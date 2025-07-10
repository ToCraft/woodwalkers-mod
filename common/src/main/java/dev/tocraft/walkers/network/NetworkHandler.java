package dev.tocraft.walkers.network;

import dev.tocraft.walkers.Walkers;
import net.minecraft.resources.ResourceLocation;

public interface NetworkHandler {
    ResourceLocation SHAPE_REQUEST = Walkers.id("request");
    ResourceLocation UNLOCK_REQUEST = Walkers.id("unlock_request");
    ResourceLocation USE_ABILITY = Walkers.id("use_ability");
    ResourceLocation SHAPE_SYNC = Walkers.id("shape_sync");
    ResourceLocation ABILITY_SYNC = Walkers.id("ability_sync");
    ResourceLocation UNLOCK_SYNC = Walkers.id("unlock_sync");
    ResourceLocation VARIANT_REQUEST = Walkers.id("variant");
    ResourceLocation SYNC_API_LEVEL = Walkers.id("sync_api_level");
}
