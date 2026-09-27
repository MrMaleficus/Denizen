package com.denizenscript.denizen.objects.properties.entity;

import com.denizenscript.denizen.objects.EntityTag;
import com.denizenscript.denizencore.objects.Mechanism;
import com.denizenscript.denizencore.objects.core.ElementTag;
import org.bukkit.entity.Axolotl;

public class EntityPlayingDead extends EntityProperty<ElementTag> {

    // <--[property]
    // @object EntityTag
    // @name playing_dead
    // @input ElementTag(Boolean)
    // @description
    // Controls whether an axolotl is playing dead.
    // Setting this won't be successful unless the entity is unaware of its surroundings. See <@link mechanism EntityTag.is_aware>.
    // -->

    public static boolean describes(EntityTag entity) {
        return entity.getBukkitEntity() instanceof Axolotl;
    }

    @Override
    public boolean isDefaultValue(ElementTag val) {
        return !val.asBoolean();
    }

    @Override
    public ElementTag getPropertyValue() {
        return new ElementTag(as(Axolotl.class).isPlayingDead());
    }

    @Override
    public void setPropertyValue(ElementTag param, Mechanism mechanism) {
        if (mechanism.requireBoolean()) {
            as(Axolotl.class).setPlayingDead(param.asBoolean());
        }
    }

    @Override
    public String getPropertyId() {
        return "playing_dead";
    }

    public static void register() {
        autoRegister("playing_dead", EntityPlayingDead.class, ElementTag.class, false);
    }
}
