package tocraft.walkers.integrations;

import dev.architectury.platform.Platform;
import tocraft.walkers.integrations.friendsandfoes.FriendsAndFoesIntegration;

public class Integrations {
    public static void initialize() {
        // Friends and Foes
        if (Platform.getMod("friendsandfoes") != null)
            new FriendsAndFoesIntegration().initialize("friendsandfoes");
    }
}
