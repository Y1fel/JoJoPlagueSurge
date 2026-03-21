package com.Y1fel.JoJoPlagueSurge.entity.custom.villager;

import com.Y1fel.JoJoPlagueSurge.ModEntrance;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.model.VillagerModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.NotNull;

public class DuVillagerRenderer extends MobRenderer<DuVillagerEntity, PlayerModel<DuVillagerEntity>> {
    private static final ResourceLocation[] TEXTURES = new ResourceLocation[10];

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
    public ResourceLocation getTextureLocation(@NotNull DuVillagerEntity entity) {
        int variant = entity.getVariant();
        if (variant < 1 || variant > TEXTURES.length) {
            return TEXTURES[0];
        }

        return TEXTURES[variant - 1];
    }
}
