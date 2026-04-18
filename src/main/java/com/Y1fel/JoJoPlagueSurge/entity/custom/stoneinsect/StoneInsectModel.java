package com.Y1fel.JoJoPlagueSurge.entity.custom.stoneinsect;

import com.Y1fel.JoJoPlagueSurge.ModEntrance;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class StoneInsectModel extends GeoModel<StoneInsectEntity> {
    @Override
    public ResourceLocation getModelResource(StoneInsectEntity animatable) {
        return ResourceLocation.fromNamespaceAndPath(ModEntrance.MODID, "geo/stoneinsect.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(StoneInsectEntity animatable) {
        return ResourceLocation.fromNamespaceAndPath(ModEntrance.MODID, "textures/entity/stoneinsect.png");
    }

    @Override
    public ResourceLocation getAnimationResource(StoneInsectEntity animatable) {
        return ResourceLocation.fromNamespaceAndPath(ModEntrance.MODID, "animations/stoneinsect.animation.json");
    }
}
