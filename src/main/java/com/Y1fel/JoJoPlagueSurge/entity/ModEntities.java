package com.Y1fel.JoJoPlagueSurge.entity;

import com.Y1fel.JoJoPlagueSurge.ModEntrance;
import com.Y1fel.JoJoPlagueSurge.entity.custom.bluehawaii.BlueHawaiiEntity;
import com.Y1fel.JoJoPlagueSurge.entity.custom.duvillager.DuVillagerEntity;
import com.Y1fel.JoJoPlagueSurge.entity.custom.duwang.DuWangEntity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModEntities {
    public static final DeferredRegister<EntityType<?>> ENTITY_TYPES =
            DeferredRegister.create(ForgeRegistries.ENTITY_TYPES, ModEntrance.MODID);
    public static final RegistryObject<EntityType<BlueHawaiiEntity>> BLUEHAWAII =
            ENTITY_TYPES.register("bluehawaii",
                    ()->EntityType.Builder.of(BlueHawaiiEntity::new, MobCategory.MONSTER)
                            .sized(0.6F, 1.8F)
                            .build("bluehawaii"));
    public static final RegistryObject<EntityType<DuVillagerEntity>> DUVILLAGER =
            ENTITY_TYPES.register("duvillager",
                    ()->EntityType.Builder.of(DuVillagerEntity::new,MobCategory.MONSTER)
                            .sized(0.6F, 1.8F)
                            .build("duvillager"));
    public static final RegistryObject<EntityType<DuWangEntity>> DUWANG =
            ENTITY_TYPES.register("duwang",
                    ()->EntityType.Builder.of(DuWangEntity::new,MobCategory.MONSTER)
                            .sized(0.6F, 1.8F)
                            .build("duwang"));
    public static void register(IEventBus eventBus) {
        ENTITY_TYPES.register(eventBus);
    }
}
