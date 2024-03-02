package tocraft.walkers.impl.variant;

import net.minecraft.core.Registry;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.monster.ZombieVillager;
import net.minecraft.world.level.Level;
import tocraft.walkers.api.variant.TypeProvider;

public class ZombieVillagerTypeProvider extends TypeProvider<ZombieVillager> {

    @Override
    public int getVariantData(ZombieVillager entity) {
        return Registry.VILLAGER_TYPE.getId(entity.getVillagerData().getType());
    }

    @Override
    public ZombieVillager create(EntityType<ZombieVillager> type, Level level, int data) {
        ZombieVillager villager = new ZombieVillager(type, level);
        villager.getVillagerData().setType(Registry.VILLAGER_TYPE.byId(data));
        return villager;
    }

    @Override
    public int getFallbackData() {
        return 0;
    }

    @Override
    public int getRange() {
        return Registry.VILLAGER_TYPE.size() - 1;
    }

    @Override
    public Component modifyText(ZombieVillager entity, MutableComponent text) {
        return new TextComponent(formatTypePrefix(entity.getVillagerData().getType().toString()) + " ").append(text);
    }
}