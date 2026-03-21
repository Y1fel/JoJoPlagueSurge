package com.Y1fel.JoJoPlagueSurge.entity.custom.duwang;

import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.world.entity.monster.Monster;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.renderer.GeoEntityRenderer;
import software.bernie.geckolib.util.GeckoLibUtil;

public class DuWangRenderer extends GeoEntityRenderer<DuWangEntity>{
    public DuWangRenderer(EntityRendererProvider.Context context) {
        super(context, new DuWangModel());
        this.shadowRadius = 0.2F;
    }
}
