package tocraft.walkers.impl.variant;

import net.minecraft.core.Registry;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.npc.VillagerProfession;
import net.minecraft.world.entity.npc.VillagerType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import tocraft.craftedcore.patched.CRegistries;
import tocraft.craftedcore.patched.Identifier;
import tocraft.craftedcore.patched.TComponent;
import tocraft.walkers.Walkers;
import tocraft.walkers.api.variant.TypeProvider;

import java.util.Random;

public class VillagerTypeProvider extends TypeProvider<Villager> {

    @Override
    public int getVariantData(Villager entity) {
        //noinspection unchecked
        return ((Registry<VillagerProfession>) CRegistries.getRegistry(Identifier.parse("villager_profession"))).getId(entity.getVillagerData().getProfession());
    }

    @Override
    public Villager create(EntityType<Villager> type, Level level, int data) {
        Villager villager = new Villager(type, level);
        villager.setVillagerData(villager.getVillagerData().setProfession(((Registry<VillagerProfession>) CRegistries.getRegistry(Identifier.parse("villager_profession"))).byId(data)));
        return villager;
    }

    @Override
    public Villager create(EntityType<Villager> type, Level level, int data, Player player) {
        if (player != null && Walkers.CONFIG.multiVectorVariants > 0) {
            Villager villager = new Villager(type, level);
            VillagerType villagerType;
            if (Walkers.CONFIG.multiVectorVariants == 2) {
                villagerType = VillagerType.byBiome(level.getBiome(player.blockPosition()));
            } else {
                villagerType = ((Registry<VillagerType>) CRegistries.getRegistry(Identifier.parse("villager_type"))).byId(new Random().nextInt(0, ((Registry<VillagerType>) CRegistries.getRegistry(Identifier.parse("villager_type"))).size() - 1));
            }
            //#if MC>1182
            villager.setVariant(villagerType);
            //#else
            //$$ villager.setVillagerData(villager.getVillagerData().setType(villagerType));
            //#endif
            villager.setVillagerData(villager.getVillagerData().setProfession(((Registry<VillagerProfession>) CRegistries.getRegistry(Identifier.parse("villager_profession"))).byId(data)));
            return villager;
        } else {
            return create(type, level, data);
        }
    }

    @Override
    public int getFallbackData() {
        return 0;
    }

    @Override
    public int getRange() {
        return CRegistries.getRegistry(Identifier.parse("villager_profession")).size() - 1;
    }

    @Override
    public Component modifyText(Villager entity, MutableComponent text) {
        return TComponent.literal(formatTypePrefix(entity.getVillagerData().getProfession().toString()) + " ").append(text);
    }
}
