package com.Y1fel.JoJoPlagueSurge.entity.custom;

import com.Y1fel.JoJoPlagueSurge.ModEntrance;
import com.Y1fel.JoJoPlagueSurge.entity.client.BlueHawaiiEntity;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class BlueHawaiiModel extends GeoModel<BlueHawaiiEntity> {
    @Override
    public ResourceLocation getModelResource(BlueHawaiiEntity animatable) {
        return new ResourceLocation(ModEntrance.MODID, "geo/blue_hawaii.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(BlueHawaiiEntity animatable) {
        return new ResourceLocation(ModEntrance.MODID, "textures/entity/blue_hawaii.png");
    }

    @Override
    public ResourceLocation getAnimationResource(BlueHawaiiEntity animatable) {
        return new ResourceLocation(ModEntrance.MODID, "animations/blue_hawaii.animation.json");
    }
}
