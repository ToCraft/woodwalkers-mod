package tocraft.walkers.impl.tick;

import org.jetbrains.annotations.Nullable;

import dev.architectury.event.events.client.ClientTickEvent;
import tocraft.walkers.WalkersClient;
import tocraft.walkers.api.platform.SyncedVars;
import tocraft.walkers.api.variant.ShapeType;
import tocraft.walkers.impl.PlayerDataProvider;
import tocraft.walkers.network.impl.SwapPackets;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.text.Text;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;

public class UnlockKeyPressHandler implements ClientTickEvent.Client {
    private float currentTimer = 0f;

    @Override
    public void tick(MinecraftClient client) {
        assert client.player != null;

        if(WalkersClient.UNLOCK_KEY.isPressed()) {
            if (SyncedVars.getEnableUnlockSystem()) {
                HitResult hit = client.crosshairTarget;
                if ((((PlayerDataProvider)client.player).get2ndShape() == null || SyncedVars.getUnlockOveridesCurrentShape()) && hit instanceof EntityHitResult) {
                    if (currentTimer <= 0) {
                        Entity entityHit = ((EntityHitResult) hit).getEntity();
                        if(entityHit instanceof LivingEntity living) {
                            @Nullable ShapeType<?> type = ShapeType.from(living);
                            // unlock shape
                            SwapPackets.sendSwapRequest(type, true);
                            // send unlock message
                            Text name = Text.translatable(type.getEntityType().getTranslationKey());
                            client.player.sendMessage(Text.translatable("walkers.unlock_entity", name), true);

                        }
                        currentTimer = SyncedVars.getUnlockTimer();
                    }
                    else {
                        client.player.sendMessage(Text.translatable("walkers.unlock_progress"), true);
                        currentTimer -= 1;
                    }
                }
                else
                    currentTimer = SyncedVars.getUnlockTimer();
            }
        }
        else if (currentTimer != SyncedVars.getUnlockTimer())
            currentTimer = SyncedVars.getUnlockTimer();
    }
}
