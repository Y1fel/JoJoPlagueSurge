package com.Y1fel.JoJoPlagueSurge.entity.custom.stand;

import com.Y1fel.JoJoPlagueSurge.entity.ModEntities;
import com.Y1fel.JoJoPlagueSurge.entity.custom.bluehawaii.BlueHawaiiEntity;
import com.Y1fel.JoJoPlagueSurge.entity.custom.duwang.DuWangEntity;
import net.minecraft.world.entity.EntityType;

import javax.annotation.Nullable;
import java.util.function.Supplier;

public enum StandType {
    DUWANG("duwang", DuWangEntity.class, () -> ModEntities.DUWANG.get(), StandFollowProfile.DEFAULT_LEFT_BACK),
    BLUE_HAWAII("bluehawaii", BlueHawaiiEntity.class, () -> ModEntities.BLUEHAWAII.get(), StandFollowProfile.DEFAULT_LEFT_BACK);

    private final String id;
    private final Class<? extends StandEntity> standClass;
    private final Supplier<EntityType<? extends StandEntity>> entityTypeSupplier;
    private final StandFollowProfile followProfile;

    StandType(
            String id,
            Class<? extends StandEntity> standClass,
            Supplier<EntityType<? extends StandEntity>> entityTypeSupplier,
            StandFollowProfile followProfile
    ) {
        this.id = id;
        this.standClass = standClass;
        this.entityTypeSupplier = entityTypeSupplier;
        this.followProfile = followProfile;
    }

    public String getId() {
        return id;
    }

    public Class<? extends StandEntity> getStandClass() {
        return standClass;
    }

    public EntityType<? extends StandEntity> getEntityType() {
        return entityTypeSupplier.get();
    }

    public StandFollowProfile getFollowProfile() {
        return followProfile;
    }

    public boolean matches(StandEntity stand) {
        return standClass.isInstance(stand);
    }

    @Nullable
    public static StandType byId(@Nullable String id) {
        if (id == null || id.isBlank()) {
            return null;
        }

        for (StandType type : values()) {
            if (type.id.equals(id)) {
                return type;
            }
        }
        return null;
    }
}
