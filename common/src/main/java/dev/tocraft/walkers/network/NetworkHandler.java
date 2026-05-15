package dev.tocraft.walkers.network;

import dev.tocraft.walkers.Walkers;
import net.minecraft.resources.Identifier;

public interface NetworkHandler {
    Identifier SHAPE_REQUEST = Walkers.id("request");
    Identifier UNLOCK_REQUEST = Walkers.id("unlock_request");
    Identifier USE_ABILITY = Walkers.id("use_ability");
    Identifier SHAPE_SYNC = Walkers.id("shape_sync");
    Identifier ABILITY_SYNC = Walkers.id("ability_sync");
    Identifier UNLOCK_SYNC = Walkers.id("unlock_sync");
    Identifier VARIANT_REQUEST = Walkers.id("variant");
    Identifier SYNC_API_LEVEL = Walkers.id("sync_api_level");
}
