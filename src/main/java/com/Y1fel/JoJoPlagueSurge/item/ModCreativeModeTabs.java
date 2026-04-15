package com.Y1fel.JoJoPlagueSurge.item;

import com.Y1fel.JoJoPlagueSurge.ModEntrance;
import com.Y1fel.JoJoPlagueSurge.block.ModBlocks;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public class ModCreativeModeTabs {
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS =
            DeferredRegister.create(Registries.CREATIVE_MODE_TAB, ModEntrance.MODID);

    public static final RegistryObject<CreativeModeTab> TEST_TAB =
            CREATIVE_MODE_TABS.register("jojo_tab", () -> CreativeModeTab.builder()
                    .icon(()->new ItemStack(ModItems.BLUEHAWAII_SPAWN_EGG.get()))
                    .title(Component.translatable("itemGroup.jojo_tab"))
                    .displayItems((pParameters, pOutput) -> {
                        pOutput.accept(ModBlocks.OZONE.get());
                        pOutput.accept(ModItems.BLUEHAWAII_SPAWN_EGG.get());
                        pOutput.accept(ModItems.DUVILLAGER_SPAWN_EGG.get());
                        pOutput.accept(ModItems.CRIMINAL_DUVILLAGER_SPAWN_EGG.get());
                        pOutput.accept(ModItems.DUWANG_SPAWN_EGG.get());
                        pOutput.accept(ModItems.MIRACLE_SPAWN_EGG.get());
                        pOutput.accept(ModItems.BLOODY_TOOTH.get());
                        pOutput.accept(ModItems.STAND_DISC.get());
                        pOutput.accept(ModItems.BLUEHAWAII_STAND_DISC.get());
                        pOutput.accept(ModItems.SPIRIT_VISION_POTION.get());
                    }).build());
    public static void register(IEventBus eventBus) {
        CREATIVE_MODE_TABS.register(eventBus);
    }
}
