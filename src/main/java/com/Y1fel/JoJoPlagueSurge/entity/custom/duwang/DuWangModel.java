package com.Y1fel.JoJoPlagueSurge.entity.custom.duwang;

import com.Y1fel.JoJoPlagueSurge.ModEntrance;
import com.Y1fel.JoJoPlagueSurge.entity.custom.bluehawaii.BlueHawaiiEntity;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class DuWangModel extends GeoModel<DuWangEntity> {
    @Override
    public ResourceLocation getModelResource(DuWangEntity animatable) {
        return ResourceLocation.fromNamespaceAndPath(ModEntrance.MODID, "geo/duwang.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(DuWangEntity animatable) {
        return ResourceLocation.fromNamespaceAndPath(ModEntrance.MODID, "textures/entity/duwang.png");
    }

    @Override
    public ResourceLocation getAnimationResource(DuWangEntity animatable) {
        return ResourceLocation.fromNamespaceAndPath(ModEntrance.MODID, "animations/duwang.animation.json");
    }
}
