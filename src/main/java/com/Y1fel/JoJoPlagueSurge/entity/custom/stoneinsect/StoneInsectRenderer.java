package com.Y1fel.JoJoPlagueSurge.entity.custom.stoneinsect;

import net.minecraft.client.renderer.entity.EntityRendererProvider;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class StoneInsectRenderer extends GeoEntityRenderer<StoneInsectEntity> {
    public StoneInsectRenderer(EntityRendererProvider.Context context) {
        super(context, new StoneInsectModel());
        this.shadowRadius = 0.25F;
    }
}
