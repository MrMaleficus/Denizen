package com.denizenscript.denizen.objects.properties.entity;

import com.denizenscript.denizen.objects.EntityTag;
import com.denizenscript.denizencore.objects.Mechanism;
import com.denizenscript.denizencore.objects.core.ElementTag;
import org.bukkit.entity.Raider;

public class EntityPatrolLeader extends EntityProperty<ElementTag> {

    // <--[property]
    // @object EntityTag
    // @name is_patrol_leader
    // @input ElementTag(Boolean)
    // @description
    // Controls whether a raider mob (like a pillager) is a patrol leader.
    // -->

    public static boolean describes(EntityTag entity) {
        return entity.getBukkitEntity() instanceof Raider;
    }

    @Override
    public ElementTag getPropertyValue() {
        return new ElementTag(as(Raider.class).isPatrolLeader());
    }

    @Override
    public void setPropertyValue(ElementTag param, Mechanism mechanism) {
        if (mechanism.requireBoolean()) {
            as(Raider.class).setPatrolLeader(param.asBoolean());
        }
    }

    @Override
    public String getPropertyId() {
        return "is_patrol_leader";
    }

    public static void register() {
        autoRegister("is_patrol_leader", EntityPatrolLeader.class, ElementTag.class, false);
    }
}
