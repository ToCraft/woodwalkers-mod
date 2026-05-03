package dev.tocraft.walkers;

import com.mojang.blaze3d.platform.InputConstants;
import dev.tocraft.craftedcore.event.client.ClientPlayerEvents;
import dev.tocraft.craftedcore.event.client.ClientTickEvents;
import dev.tocraft.craftedcore.event.client.RenderEvents;
import dev.tocraft.craftedcore.registration.KeyBindingRegistry;
import dev.tocraft.walkers.ability.AbilityOverlayRenderer;
import dev.tocraft.walkers.api.model.EntityArms;
import dev.tocraft.walkers.api.model.EntityUpdaters;
import dev.tocraft.walkers.api.platform.ApiLevel;
import dev.tocraft.walkers.eventhandler.ClientRespawnHandler;
import dev.tocraft.walkers.impl.tick.KeyPressHandler;
import dev.tocraft.walkers.network.ClientNetworking;
import dev.tocraft.walkers.screen.hud.OverlayEventHandler;
import dev.tocraft.walkers.screen.hud.VariantMenu;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.KeyMapping;
import net.minecraft.resources.Identifier;
import org.jetbrains.annotations.ApiStatus;
import org.lwjgl.glfw.GLFW;

@Environment(EnvType.CLIENT)
public class WalkersClient {
    public static boolean isRenderingVariantsMenu = false;
    @ApiStatus.Internal
    public static int variantOffset = 0;

    public static final KeyMapping.Category WALKERS_CATEGORY = KeyMapping.Category.register(Identifier.fromNamespaceAndPath("walkers", "key_categories"));
    public static final KeyMapping UNLOCK_KEY = new KeyMapping("key.walkers_unlock", InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_U, WALKERS_CATEGORY);
    public static final KeyMapping TRANSFORM_KEY = new KeyMapping("key.walkers", InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_G, WALKERS_CATEGORY);
    public static final KeyMapping ABILITY_KEY = new KeyMapping("key.walkers_ability", InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_R, WALKERS_CATEGORY);
    public static final KeyMapping VARIANTS_MENU_KEY = new KeyMapping("key.walkers_variants", InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_V, WALKERS_CATEGORY);


    public void initialize() {
        KeyBindingRegistry.register(ABILITY_KEY);
        KeyBindingRegistry.register(TRANSFORM_KEY);
        KeyBindingRegistry.register(UNLOCK_KEY);
        KeyBindingRegistry.register(VARIANTS_MENU_KEY);

        // Register client-side event handlers
        EntityUpdaters.init();
        AbilityOverlayRenderer.register();
        EntityArms.init();

        // Register event handlers
        ClientTickEvents.CLIENT_PRE.register(new KeyPressHandler());
        RenderEvents.HUD_RENDERING.register(new VariantMenu());
        ClientNetworking.registerPacketHandlers();

        OverlayEventHandler.initialize();

        ClientPlayerEvents.CLIENT_PLAYER_RESPAWN.register(new ClientRespawnHandler());

        ClientPlayerEvents.CLIENT_PLAYER_QUIT.register(player -> {
            if (player != null && ApiLevel.getClientLevel() != null) {
                ApiLevel.ON_API_LEVEL_CHANGE_EVENT.invoke().onApiLevelChange(ApiLevel.getClientLevel());
            }
        });
    }
}
