package tocraft.walkers.ability.impl;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.NeutralMob;
import net.minecraft.world.entity.animal.Pufferfish;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import tocraft.walkers.ability.ShapeAbility;
import tocraft.walkers.api.PlayerShape;
import tocraft.walkers.mixin.EntityTrackerAccessor;
import tocraft.walkers.mixin.ThreadedAnvilChunkStorageAccessor;

public class PufferfishAbility extends ShapeAbility<Pufferfish> {
    @Override
    public void onUse(Player player, Pufferfish shape, Level world) {
            if (!world.isClientSide()) {
                if (shape.getPuffState() == 0) {
                    shape.inflateCounter = 1;
                    shape.deflateTimer = 0;
                }
                else {
                    shape.inflateCounter = 0;
                }
            }
    }

    @Override
    public Item getIcon() {
        return Items.PUFFERFISH;
    }
}
