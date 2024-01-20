package tocraft.walkers.impl.variant;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.monster.ZombieVillager;
import net.minecraft.world.level.Level;
import tocraft.walkers.api.variant.TypeProvider;

public class ZombieVillagerTypeProvider extends TypeProvider<ZombieVillager> {

    @Override
    public int getVariantData(ZombieVillager entity) {
        return BuiltInRegistries.VILLAGER_TYPE.getId(entity.getVariant());
    }

    @Override
    public ZombieVillager create(EntityType<ZombieVillager> type, Level level, int data) {
        ZombieVillager villager = new ZombieVillager(type, level);
        villager.setVariant(BuiltInRegistries.VILLAGER_TYPE.byId(data));
        return villager;
    }

    @Override
    public int getFallbackData() {
        return 0;
    }

    @Override
    public int getRange() {
        return BuiltInRegistries.VILLAGER_TYPE.size() - 1;
    }

    @Override
    public Component modifyText(ZombieVillager entity, MutableComponent text) {
        return Component.literal(formatTypePrefix(entity.getVariant().toString()) + " ").append(text);
    }
}
