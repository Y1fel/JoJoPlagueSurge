package com.Y1fel.JoJoPlagueSurge.entity.custom.miracle;

import net.minecraft.client.renderer.entity.EntityRendererProvider;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class MiracleRenderer extends GeoEntityRenderer<MiracleEntity> {
    public MiracleRenderer(EntityRendererProvider.Context context) {
        super(context, new MiracleModel());
        this.shadowRadius = 0.35F;
    }
}
