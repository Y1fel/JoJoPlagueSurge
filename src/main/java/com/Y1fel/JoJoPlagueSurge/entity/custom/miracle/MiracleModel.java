package com.Y1fel.JoJoPlagueSurge.entity.custom.miracle;

import com.Y1fel.JoJoPlagueSurge.ModEntrance;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class MiracleModel extends GeoModel<MiracleEntity> {
    @Override
    public ResourceLocation getModelResource(MiracleEntity animatable) {
        return ResourceLocation.fromNamespaceAndPath(ModEntrance.MODID, "geo/miracle.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(MiracleEntity animatable) {
        return ResourceLocation.fromNamespaceAndPath(ModEntrance.MODID, "textures/entity/miracle.png");
    }

    @Override
    public ResourceLocation getAnimationResource(MiracleEntity animatable) {
        return ResourceLocation.fromNamespaceAndPath(ModEntrance.MODID, "animations/miracle.animation.json");
    }
}
