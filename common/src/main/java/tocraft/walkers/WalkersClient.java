package tocraft.walkers;

import com.mojang.blaze3d.platform.InputConstants;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.KeyMapping;
import org.jetbrains.annotations.ApiStatus;
import org.lwjgl.glfw.GLFW;
import tocraft.craftedcore.event.client.ClientPlayerEvents;
import tocraft.craftedcore.event.client.ClientTickEvents;
import tocraft.craftedcore.event.client.RenderEvents;
import tocraft.craftedcore.registration.KeyBindingRegistry;
import tocraft.walkers.ability.AbilityOverlayRenderer;
import tocraft.walkers.api.model.EntityArms;
import tocraft.walkers.api.model.EntityUpdaters;
import tocraft.walkers.api.platform.ApiLevel;
import tocraft.walkers.eventhandler.ClientRespawnHandler;
import tocraft.walkers.impl.tick.KeyPressHandler;
import tocraft.walkers.network.ClientNetworking;
import tocraft.walkers.screen.hud.OverlayEventHandler;
import tocraft.walkers.screen.hud.VariantMenu;

@Environment(EnvType.CLIENT)
public class WalkersClient {
    public static boolean isRenderingVariantsMenu = false;
    @ApiStatus.Internal
    public static int variantOffset = 0;
    private final VariantMenu variantMenu = new VariantMenu();

    public static final KeyMapping UNLOCK_KEY = new KeyMapping("key.walkers_unlock", InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_U, "key.categories.walkers");
    public static final KeyMapping TRANSFORM_KEY = new KeyMapping("key.walkers", InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_G, "key.categories.walkers");
    public static final KeyMapping ABILITY_KEY = new KeyMapping("key.walkers_ability", InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_R, "key.categories.walkers");
    public static final KeyMapping VARIANTS_MENU_KEY = new KeyMapping("key.walkers_variants", InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_V, "key.categories.walkers");


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
        RenderEvents.HUD_RENDERING.register((guiGraphics, tickDelta) -> variantMenu.render(guiGraphics));
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
