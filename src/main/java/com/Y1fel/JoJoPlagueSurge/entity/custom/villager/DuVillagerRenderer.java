package com.Y1fel.JoJoPlagueSurge.entity.custom.villager;

import com.Y1fel.JoJoPlagueSurge.ModEntrance;
import net.minecraft.client.model.VillagerModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

public class DuVillagerRenderer<T extends BaseDuVillager> extends MobRenderer<T, VillagerModel<T>> {
    private final ResourceLocation texture;
    public DuVillagerRenderer(EntityRendererProvider.Context context, String name){
        super(context, new VillagerModel<>(context.bakeLayer(ModelLayers.VILLAGER)), 0.5F);
        this.texture = ResourceLocation.fromNamespaceAndPath(ModEntrance.MODID, "textures/entity/"+name+".png");
    }
    @Override
    @NotNull
    public ResourceLocation getTextureLocation(@NotNull T entity) {
        return texture;
    }
}
