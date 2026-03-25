package com.Y1fel.JoJoPlagueSurge.entity.custom.trackingtornado;

import net.minecraft.client.renderer.entity.EntityRendererProvider;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class TrackingTornadoRenderer extends GeoEntityRenderer<TrackingTornadoEntity> {
    public TrackingTornadoRenderer(EntityRendererProvider.Context context) {
        super(context, new TrackingTornadoModel());
        this.shadowRadius = 0.2F;
    }
}
