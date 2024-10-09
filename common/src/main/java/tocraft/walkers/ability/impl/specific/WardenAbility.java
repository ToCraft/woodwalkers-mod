//#if MC>1182
package tocraft.walkers.ability.impl.specific;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import tocraft.walkers.Walkers;
import tocraft.walkers.ability.ShapeAbility;
import tocraft.walkers.impl.SonicBoomUser;

public class WardenAbility<T extends LivingEntity> extends ShapeAbility<T> {
    public static final ResourceLocation ID = Walkers.id("warden");

    @Override
    public ResourceLocation getId() {
        return ID;
    }

    @Override
    public void onUse(Player player, T shape, Level world) {
        super.onUse(player, shape, world);
        ((SonicBoomUser) player).shape$ability_startSonicBoom();
    }

    @Override
    public Item getIcon() {
        return Items.ECHO_SHARD;
    }

    @Override
    public int getDefaultCooldown() {
        return 200;
    }
}
//#endif
