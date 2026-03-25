package com.Y1fel.JoJoPlagueSurge.entity.custom.trackingtornado;

import com.Y1fel.JoJoPlagueSurge.ModEntrance;
import net.minecraft.client.model.EntityModel;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class TrackingTornadoModel extends GeoModel<TrackingTornadoEntity> {
    @Override
    public ResourceLocation getModelResource(TrackingTornadoEntity animatable) {
        return ResourceLocation.fromNamespaceAndPath(ModEntrance.MODID, "geo/tracking_tornado.geo.json");
    }
    @Override
    public ResourceLocation getTextureResource(TrackingTornadoEntity animatable) {
        return ResourceLocation.fromNamespaceAndPath(ModEntrance.MODID, "textures/entity/tracking_tornado.png");
    }
    @Override
    public ResourceLocation getAnimationResource(TrackingTornadoEntity animatable) {
        return ResourceLocation.fromNamespaceAndPath(ModEntrance.MODID, "animations/tracking_tornado.animation.json");
    }
}
