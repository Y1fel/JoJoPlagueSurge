package com.Y1fel.JoJoPlagueSurge.entity.custom.bluehawaii;

import com.Y1fel.JoJoPlagueSurge.ModEntrance;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class BlueHawaiiModel extends GeoModel<BlueHawaiiEntity> {
    @Override
    public ResourceLocation getModelResource(BlueHawaiiEntity animatable) {
        return ResourceLocation.fromNamespaceAndPath(ModEntrance.MODID, "geo/blue_hawaii.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(BlueHawaiiEntity animatable) {
        return ResourceLocation.fromNamespaceAndPath(ModEntrance.MODID, "textures/entity/blue_hawaii.png");
    }

    @Override
    public ResourceLocation getAnimationResource(BlueHawaiiEntity animatable) {
        return ResourceLocation.fromNamespaceAndPath(ModEntrance.MODID, "animations/blue_hawaii.animation.json");
    }
}
