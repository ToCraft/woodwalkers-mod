package tocraft.walkers.network;

import tocraft.walkers.Walkers;
import net.minecraft.util.Identifier;

public interface NetworkHandler {
    Identifier SHAPE_REQUEST = Walkers.id("request");
    Identifier DEV_SHAPE_REQUEST = Walkers.id("dev_request");
    Identifier USE_ABILITY = Walkers.id("use_ability");
    Identifier SHAPE_SYNC = Walkers.id("shape_sync");
    Identifier ABILITY_SYNC = Walkers.id("ability_sync");
    Identifier CONFIG_SYNC = Walkers.id("config_sync");
    Identifier UNLOCK_SYNC = Walkers.id("unlock_sync");
}
