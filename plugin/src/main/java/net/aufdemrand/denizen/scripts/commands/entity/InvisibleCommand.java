package net.aufdemrand.denizen.scripts.commands.entity;

import net.aufdemrand.denizen.npc.traits.InvisibleTrait;
import net.aufdemrand.denizen.objects.dEntity;
import net.aufdemrand.denizen.utilities.Utilities;
import net.aufdemrand.denizen.utilities.debugging.dB;
import net.aufdemrand.denizencore.exceptions.InvalidArgumentsException;
import net.aufdemrand.denizencore.objects.Element;
import net.aufdemrand.denizencore.objects.aH;
import net.aufdemrand.denizencore.scripts.ScriptEntry;
import net.aufdemrand.denizencore.scripts.commands.AbstractCommand;
import net.citizensnpcs.api.npc.NPC;
import org.bukkit.entity.ArmorStand;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class InvisibleCommand extends AbstractCommand {

    // <--[command]
    // @Name Invisible
    // @Syntax invisible [<entity>] (state:true/false/toggle)
    // @Required 1
    // @Short Makes an NPC or entity go invisible
    // @Group entity
    //
    // @Description
    // For non-armor stand entities, applies a maximum duration invisibility potion.
    // For armor stands, toggles them invisible.
    // Applies the 'invisible' trait to NPCs.
    //
    // NPCs can't be made invisible if not added to the playerlist.
    // (The invisible trait adds the NPC to the playerlist when set)
    // See <@link language invisible trait>)
    //
    // @Tags
    // None
    //
    // @Usage
    // - invisible <player> state:true
    // Makes the player invisible
    //
    // @Usage
    // - invisible <npc> state:toggle
    // Makes the attached NPC visible if previously invisible, and invisible if not
    // -->

    enum Action {TRUE, FALSE, TOGGLE}

    @Override
    public void parseArgs(ScriptEntry scriptEntry) throws InvalidArgumentsException {

        for (aH.Argument arg : aH.interpretArguments(scriptEntry.aHArgs)) {
            if (!scriptEntry.hasObject("state")
                    && arg.matchesEnum(Action.values())) {
                scriptEntry.addObject("state", arg.asElement());
            }
            else if (!scriptEntry.hasObject("target")
                    && arg.matches("PLAYER")
                    && Utilities.entryHasPlayer(scriptEntry)) {
                scriptEntry.addObject("target", Utilities.getEntryPlayer(scriptEntry).getDenizenEntity());
            }
            else if (!scriptEntry.hasObject("target")
                    && arg.matches("NPC")
                    && Utilities.entryHasNPC(scriptEntry)) {
                scriptEntry.addObject("target", Utilities.getEntryNPC(scriptEntry).getDenizenEntity());
            }
            else if (!scriptEntry.hasObject("target")
                    && arg.matchesArgumentType(dEntity.class)) {
                scriptEntry.addObject("target", arg.asType(dEntity.class));
            }
            else {
                arg.reportUnhandled();
            }
        }

        if (!scriptEntry.hasObject("state")) {
            scriptEntry.addObject("state", new Element("TRUE"));
        }

        if (!scriptEntry.hasObject("target") || !((dEntity) scriptEntry.getdObject("target")).isValid()) {
            throw new InvalidArgumentsException("Must specify a valid target!");
        }
    }

    @Override
    public void execute(ScriptEntry scriptEntry) {
        // Get objects
        Element state = scriptEntry.getElement("state");
        dEntity target = (dEntity) scriptEntry.getObject("target");

        // Report to dB
        if (scriptEntry.dbCallShouldDebug()) {
            dB.report(scriptEntry, getName(), state.debug() + target.debug());
        }

        if (target.isCitizensNPC()) {
            NPC npc = target.getDenizenNPC().getCitizen();
            if (!npc.hasTrait(InvisibleTrait.class)) {
                npc.addTrait(InvisibleTrait.class);
            }
            InvisibleTrait trait = npc.getTrait(InvisibleTrait.class);
            switch (Action.valueOf(state.asString().toUpperCase())) {
                case FALSE:
                    trait.setInvisible(false);
                    break;
                case TRUE:
                    trait.setInvisible(true);
                    break;
                case TOGGLE:
                    trait.toggle();
                    break;
            }
        }
        else {
            switch (Action.valueOf(state.asString().toUpperCase())) {
                case FALSE:
                    if (target.getBukkitEntity() instanceof ArmorStand) {
                        ((ArmorStand) target.getBukkitEntity()).setVisible(true);
                    }
                    else {
                        target.getLivingEntity().removePotionEffect(PotionEffectType.INVISIBILITY);
                    }
                    break;
                case TRUE:
                    if (target.getBukkitEntity() instanceof ArmorStand) {
                        ((ArmorStand) target.getBukkitEntity()).setVisible(false);
                    }
                    else {
                        new PotionEffect(PotionEffectType.INVISIBILITY, Integer.MAX_VALUE, 1).apply(target.getLivingEntity());
                    }
                    break;
                case TOGGLE:
                    if (target.getBukkitEntity() instanceof ArmorStand) {
                        if (((ArmorStand) target.getBukkitEntity()).isVisible()) {
                            ((ArmorStand) target.getBukkitEntity()).setVisible(true);
                        }
                        else {
                            ((ArmorStand) target.getBukkitEntity()).setVisible(false);
                        }
                    }
                    else {
                        if (target.getLivingEntity().hasPotionEffect(PotionEffectType.INVISIBILITY)) {
                            target.getLivingEntity().removePotionEffect(PotionEffectType.INVISIBILITY);
                        }
                        else {
                            new PotionEffect(PotionEffectType.INVISIBILITY, Integer.MAX_VALUE, 1).apply(target.getLivingEntity());
                        }
                    }
                    break;
            }
        }
    }
}
