package com.denizenscript.denizen.events.entity;

import com.denizenscript.denizen.objects.EntityTag;
import com.denizenscript.denizen.objects.LocationTag;
import com.denizenscript.denizen.utilities.debugging.Debug;
import com.denizenscript.denizen.BukkitScriptEntryData;
import com.denizenscript.denizen.events.BukkitScriptEvent;
import com.denizenscript.denizencore.objects.core.ElementTag;
import com.denizenscript.denizencore.objects.ArgumentHelper;
import com.denizenscript.denizencore.objects.core.ListTag;
import com.denizenscript.denizencore.objects.ObjectTag;
import com.denizenscript.denizencore.scripts.ScriptEntryData;
import com.denizenscript.denizencore.scripts.containers.ScriptContainer;
import com.denizenscript.denizencore.utilities.CoreUtilities;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityExplodeEvent;

public class EntityExplodesScriptEvent extends BukkitScriptEvent implements Listener {

    // <--[event]
    // @Events
    // entity explodes
    // <entity> explodes
    //
    // @Regex ^on [^\s]+ explodes$
    // @Switch in <area>
    //
    // @Cancellable true
    //
    // @Triggers when an entity explodes.
    //
    // @Context
    // <context.blocks> returns a ListTag of blocks that the entity blew up.
    // <context.entity> returns the EntityTag that exploded.
    // <context.location> returns the LocationTag the entity blew up at.
    // <context.strength> returns an Element(Decimal) of the strength of the explosion.
    //
    // @Determine
    // ListTag(LocationTag) to set a new lists of blocks that are to be affected by the explosion.
    // Element(Decimal) to change the strength of the explosion.
    //
    // -->

    public EntityExplodesScriptEvent() {
        instance = this;
    }

    public static EntityExplodesScriptEvent instance;
    public EntityTag entity;
    public ListTag blocks;
    public LocationTag location;
    public float strength;
    private Boolean blockSet;
    public EntityExplodeEvent event;

    @Override
    public boolean couldMatch(ScriptContainer scriptContainer, String s) {
        return CoreUtilities.getXthArg(1, CoreUtilities.toLowerCase(s)).equals("explodes");
    }

    @Override
    public boolean matches(ScriptPath path) {
        String target = path.eventArgLowerAt(0);

        if (!tryEntity(entity, target)) {
            return false;
        }

        if (!runInCheck(path, entity.getLocation())) {
            return false;
        }

        return true;
    }

    @Override
    public String getName() {
        return "EntityExplodes";
    }

    @Override
    public boolean applyDetermination(ScriptContainer container, String determination) {
        if (ArgumentHelper.matchesDouble(determination)) {
            strength = ArgumentHelper.getFloatFrom(determination);
            return true;
        }
        if (ListTag.matches(determination)) {
            blocks = new ListTag();
            blockSet = true;
            for (String loc : ListTag.valueOf(determination)) {
                LocationTag location = LocationTag.valueOf(loc);
                if (location == null) {
                    Debug.echoError("Invalid location '" + loc + "' check [" + getName() + "]: '  for " + container.getName());
                }
                else {
                    blocks.add(location.identifySimple());
                }
            }
            return true;
        }
        return super.applyDetermination(container, determination);
    }

    @Override
    public ScriptEntryData getScriptEntryData() {
        return new BukkitScriptEntryData(entity.isPlayer() ? EntityTag.getPlayerFrom(event.getEntity()) : null,
                entity.isCitizensNPC() ? EntityTag.getNPCFrom(event.getEntity()) : null);
    }

    @Override
    public ObjectTag getContext(String name) {
        if (name.equals("entity")) {
            return entity;
        }
        else if (name.equals("location")) {
            return location;
        }
        else if (name.equals("blocks")) {
            return blocks;
        }
        else if (name.equals("strength")) {
            return new ElementTag(strength);
        }
        return super.getContext(name);
    }

    @EventHandler
    public void onEntityExplodes(EntityExplodeEvent event) {
        entity = new EntityTag(event.getEntity());
        location = new LocationTag(event.getLocation());
        strength = event.getYield();
        blocks = new ListTag();
        blockSet = false;
        for (Block block : event.blockList()) {
            blocks.add(new LocationTag(block.getLocation()).identify());
        }
        this.event = event;
        fire(event);
        if (blockSet) {
            event.blockList().clear();
            if (blocks.size() > 0) {
                event.blockList().clear();
                for (String loc : blocks) {
                    LocationTag location = LocationTag.valueOf(loc);
                    event.blockList().add(location.getWorld().getBlockAt(location));
                }
            }
        }
        event.setYield(strength);
    }
}
