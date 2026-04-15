package com.Y1fel.JoJoPlagueSurge.entity.custom.duvillager;

import com.Y1fel.JoJoPlagueSurge.ModEntrance;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

public class DuVillagerRenderer<T extends DuVillagerEntity> extends MobRenderer<T, PlayerModel<T>> {
    private static final ResourceLocation[] TEXTURES = new ResourceLocation[DuVillagerEntity.NORMAL_VARIANT_COUNT];

    static {
        for (int i = 0; i < TEXTURES.length; i++) {
            TEXTURES[i] = ResourceLocation.fromNamespaceAndPath(
                    ModEntrance.MODID,
                    "textures/entity/duvillager" + (i + 1) + ".png"
            );
        }
    }

    public DuVillagerRenderer(EntityRendererProvider.Context context) {
        super(context, new PlayerModel<>(context.bakeLayer(ModelLayers.PLAYER),false), 0.5F);
    }

    @Override
    @NotNull
    public ResourceLocation getTextureLocation(@NotNull T entity) {
        int variant = entity.getVariant();
        if (variant < 1 || variant > TEXTURES.length) {
            return TEXTURES[0];
        }

        return TEXTURES[variant - 1];
    }
}
