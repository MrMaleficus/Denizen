package com.denizenscript.denizen.objects.properties.entity;

import com.denizenscript.denizen.objects.EntityTag;
import com.denizenscript.denizencore.objects.Mechanism;
import com.denizenscript.denizencore.objects.core.ElementTag;
import org.bukkit.entity.SkeletonHorse;

public class EntityTrapped extends EntityProperty<ElementTag> {

    // <--[property]
    // @object EntityTag
    // @name trapped
    // @input ElementTag(Boolean)
    // @description
    // Controls whether a skeleton horse is trapped.
    // A trapped skeleton horse will trigger the skeleton horse trap when the player is within 10 blocks of it.
    // -->

    public static boolean describes(EntityTag entity) {
        return entity.getBukkitEntity() instanceof SkeletonHorse;
    }

    @Override
    public ElementTag getPropertyValue() {
        return new ElementTag(as(SkeletonHorse.class).isTrapped());
    }

    @Override
    public void setPropertyValue(ElementTag param, Mechanism mechanism) {
        if (mechanism.requireBoolean()) {
            as(SkeletonHorse.class).setTrapped(param.asBoolean());
        }
    }

    @Override
    public String getPropertyId() {
        return "trapped";
    }

    public static void register() {
        autoRegister("trapped", EntityTrapped.class, ElementTag.class, false);
    }
}
