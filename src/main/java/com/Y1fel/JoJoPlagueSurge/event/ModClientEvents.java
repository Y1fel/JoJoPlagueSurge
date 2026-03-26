package com.Y1fel.JoJoPlagueSurge.event;

import com.Y1fel.JoJoPlagueSurge.ModEntrance;
import com.Y1fel.JoJoPlagueSurge.client.ModKeyMappings;
import com.Y1fel.JoJoPlagueSurge.entity.ModEntities;
import com.Y1fel.JoJoPlagueSurge.entity.custom.bluehawaii.BlueHawaiiRenderer;
import com.Y1fel.JoJoPlagueSurge.entity.custom.duvillager.DuVillagerRenderer;
import com.Y1fel.JoJoPlagueSurge.entity.custom.duwang.DuWangRenderer;
import com.Y1fel.JoJoPlagueSurge.entity.custom.trackingtornado.TrackingTornadoRenderer;
import com.mojang.logging.LogUtils;
import net.minecraft.client.Minecraft;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import org.slf4j.Logger;

@Mod.EventBusSubscriber(modid = ModEntrance.MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ModClientEvents {
    private static final Logger LOGGER = LogUtils.getLogger();

    @SubscribeEvent
    public static void onClientSetup(FMLClientSetupEvent event) {
        LOGGER.info("HELLO FROM CLIENT SETUP");
        LOGGER.info("MINECRAFT NAME >> {}", Minecraft.getInstance().getUser().getName());
    }

    @SubscribeEvent
    public static void registerRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer(ModEntities.BLUEHAWAII.get(), BlueHawaiiRenderer::new);
        event.registerEntityRenderer(ModEntities.DUVILLAGER.get(), DuVillagerRenderer::new);
        event.registerEntityRenderer(ModEntities.DUWANG.get(), DuWangRenderer::new);
        event.registerEntityRenderer(ModEntities.TRACKING_TORNADO.get(), TrackingTornadoRenderer::new);
    }

    @SubscribeEvent
    public static void registerKeyMappings(RegisterKeyMappingsEvent event) {
        event.register(ModKeyMappings.DUWANG_SKILL_1);
        event.register(ModKeyMappings.DUWANG_SKILL_2);
        event.register(ModKeyMappings.TOGGLE_STAND);
    }

}
