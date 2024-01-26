package tocraft.walkers.impl.variant;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.animal.Panda;
import net.minecraft.world.level.Level;
import tocraft.walkers.api.variant.TypeProvider;

public class PandaTypeProvider extends TypeProvider<Panda> {

    @Override
    public int getVariantData(Panda entity) {
        return entity.getMainGene().getId();
    }

    @Override
    public Panda create(EntityType<Panda> type, Level level, int data) {
        Panda panda = new Panda(type, level);
        panda.setMainGene(Panda.Gene.byId(data));
        return panda;
    }

    @Override
    public int getFallbackData() {
        return Panda.Gene.NORMAL.getId();
    }

    @Override
    public int getRange() {
        return Panda.Gene.values().length - 1;
    }

    @Override
    public Component modifyText(Panda entity, MutableComponent text) {
        Panda.Gene gene = entity.getMainGene();
        if (gene.equals(Panda.Gene.NORMAL)) return text;
        else return Component.literal(formatTypePrefix(gene.getSerializedName()) + " ").append(text);
    }
}