package tocraft.walkers.network;

import net.minecraft.resources.ResourceLocation;
import tocraft.walkers.Walkers;

public interface NetworkHandler {
    ResourceLocation SHAPE_REQUEST = Walkers.id("request");
    ResourceLocation UNLOCK_REQUEST = Walkers.id("unlock_request");
    ResourceLocation SPECIAL_SHAPE_REQUEST = Walkers.id("special_request");
    ResourceLocation USE_ABILITY = Walkers.id("use_ability");
    ResourceLocation SHAPE_SYNC = Walkers.id("shape_sync");
    ResourceLocation ABILITY_SYNC = Walkers.id("ability_sync");
    ResourceLocation UNLOCK_SYNC = Walkers.id("unlock_sync");
}
