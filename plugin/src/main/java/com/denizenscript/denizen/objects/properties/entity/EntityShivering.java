package com.denizenscript.denizen.objects.properties.entity;

import com.denizenscript.denizen.objects.EntityTag;
import com.denizenscript.denizencore.objects.Mechanism;
import com.denizenscript.denizencore.objects.core.ElementTag;
import org.bukkit.entity.Strider;

public class EntityShivering extends EntityProperty<ElementTag> {

    // <--[property]
    // @object EntityTag
    // @name shivering
    // @input ElementTag(Boolean)
    // @description
    // Controls whether a strider is shivering.
    // -->

    public static boolean describes(EntityTag entity) {
        return entity.getBukkitEntity() instanceof Strider;
    }

    @Override
    public ElementTag getPropertyValue() {
        return new ElementTag(as(Strider.class).isShivering());
    }

    @Override
    public void setPropertyValue(ElementTag param, Mechanism mechanism) {
        if (mechanism.requireBoolean()) {
            as(Strider.class).setShivering(param.asBoolean());
        }
    }

    @Override
    public String getPropertyId() {
        return "shivering";
    }

    public static void register() {
        autoRegister("shivering", EntityShivering.class, ElementTag.class, false);
    }
}
