package tocraft.walkers;

import com.mojang.blaze3d.platform.InputConstants;
import dev.architectury.event.events.client.ClientGuiEvent;
import dev.architectury.event.events.client.ClientTickEvent;
import dev.architectury.registry.client.keymappings.KeyMappingRegistry;
import net.minecraft.client.KeyMapping;
import org.lwjgl.glfw.GLFW;
import tocraft.walkers.ability.AbilityOverlayRenderer;
import tocraft.walkers.api.model.EntityArms;
import tocraft.walkers.api.model.EntityUpdaters;
import tocraft.walkers.impl.tick.KeyPressHandler;
import tocraft.walkers.network.ClientNetworking;
import tocraft.walkers.screen.hud.VariantMenu;

public class WalkersClient {
    public static boolean isRenderingVariantsMenu = false;
    public static int variantOffset = 0;

    public static final KeyMapping UNLOCK_KEY = new KeyMapping("key.walkers_unlock", InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_U, "key.categories.walkers");
    public static final KeyMapping TRANSFORM_KEY = new KeyMapping("key.walkers", InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_G, "key.categories.walkers");
    public static final KeyMapping ABILITY_KEY = new KeyMapping("key.walkers_ability", InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_R, "key.categories.walkers");
    public static final KeyMapping VARIANTS_MENU_KEY = new KeyMapping("key.walkers_variants", InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_V, "key.categories.walkers");


    public void initialize() {
        KeyMappingRegistry.register(ABILITY_KEY);
        KeyMappingRegistry.register(TRANSFORM_KEY);
        KeyMappingRegistry.register(UNLOCK_KEY);
        KeyMappingRegistry.register(VARIANTS_MENU_KEY);

        // Register client-side event handlers
        EntityUpdaters.init();
        AbilityOverlayRenderer.register();
        EntityArms.init();

        // Register event handlers
        ClientTickEvent.CLIENT_PRE.register(new KeyPressHandler());
        ClientGuiEvent.RENDER_HUD.register((guiGraphics, tickDelta) -> new VariantMenu().render(guiGraphics));
        ClientNetworking.registerPacketHandlers();
    }
}
