package com.Y1fel.JoJoPlagueSurge.entity.custom.bluehawaii;

import net.minecraft.client.renderer.entity.EntityRendererProvider;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class BlueHawaiiRenderer extends GeoEntityRenderer<BlueHawaiiEntity> {
    public BlueHawaiiRenderer(EntityRendererProvider.Context context){
        super(context,new BlueHawaiiModel());

        this.shadowRadius = 0.5F;
    }
}
