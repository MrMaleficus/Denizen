package com.denizenscript.denizen.objects.properties.entity;

import com.denizenscript.denizen.objects.EntityTag;
import com.denizenscript.denizencore.objects.Mechanism;
import com.denizenscript.denizencore.objects.core.ElementTag;
import org.bukkit.entity.AbstractArrow;

public class EntityCritical extends EntityProperty<ElementTag> {

    // <--[property]
    // @object EntityTag
    // @name critical
    // @input ElementTag(Boolean)
    // @description
    // Controls whether an arrow or trident is critical.
    // -->

    public static boolean describes(EntityTag entity) {
        return entity.getBukkitEntity() instanceof AbstractArrow;
    }

    @Override
    public boolean isDefaultValue(ElementTag val) {
        return !val.asBoolean();
    }

    @Override
    public ElementTag getPropertyValue() {
        return new ElementTag(as(AbstractArrow.class).isCritical());
    }

    @Override
    public void setPropertyValue(ElementTag param, Mechanism mechanism) {
        if (mechanism.requireBoolean()) {
            as(AbstractArrow.class).setCritical(param.asBoolean());
        }
    }

    @Override
    public String getPropertyId() {
        return "critical";
    }

    public static void register() {
        autoRegister("critical", EntityCritical.class, ElementTag.class, false);
    }
}
