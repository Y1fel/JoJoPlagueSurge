package com.Y1fel.JoJoPlagueSurge.effect;

import com.Y1fel.JoJoPlagueSurge.ModEntrance;
import net.minecraft.world.effect.MobEffect;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModEffects {
    public static final DeferredRegister<MobEffect> MOB_EFFECTS =
            DeferredRegister.create(ForgeRegistries.MOB_EFFECTS, ModEntrance.MODID);

    public static final RegistryObject<MobEffect> SPIRIT_VISION =
            MOB_EFFECTS.register("spirit_vision", SpiritVisionEffect::new);

    private ModEffects() {
    }

    public static void register(IEventBus eventBus) {
        MOB_EFFECTS.register(eventBus);
    }
}
