package com.denizenscript.denizen.events.world;

import com.denizenscript.denizen.objects.LocationTag;
import com.denizenscript.denizen.objects.MaterialTag;
import com.denizenscript.denizen.events.BukkitScriptEvent;
import com.denizenscript.denizencore.objects.ObjectTag;
import com.denizenscript.denizencore.scripts.containers.ScriptContainer;
import com.denizenscript.denizencore.utilities.CoreUtilities;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.LeavesDecayEvent;

public class LeafDecaysScriptEvent extends BukkitScriptEvent implements Listener {

    // <--[event]
    // @Events
    // leaves decay
    // <block> decay
    //
    // @Regex ^on [^\s]+ decay$
    // @Switch in <area>
    //
    // @Cancellable true
    //
    // @Triggers when leaves decay.
    //
    // @Context
    // <context.location> returns the LocationTag of the leaves.
    // <context.material> returns the MaterialTag of the leaves.
    //
    // -->

    public LeafDecaysScriptEvent() {
        instance = this;
    }

    public static LeafDecaysScriptEvent instance;
    public LocationTag location;
    public MaterialTag material;
    public LeavesDecayEvent event;

    @Override
    public boolean couldMatch(ScriptContainer scriptContainer, String s) {
        String lower = CoreUtilities.toLowerCase(s);
        return CoreUtilities.getXthArg(1, lower).equals("decay");
    }

    @Override
    public boolean matches(ScriptPath path) {
        String mat = path.eventArgLowerAt(0);
        return (mat.equals("leaves") || (tryMaterial(material, mat)))
                && runInCheck(path, location);
    }

    @Override
    public String getName() {
        return "LeafDecays";
    }

    @Override
    public boolean applyDetermination(ScriptContainer container, String determination) {
        return super.applyDetermination(container, determination);
    }

    @Override
    public ObjectTag getContext(String name) {
        if (name.equals("location")) {
            return location;
        }
        else if (name.equals("material")) {
            return material;
        }
        return super.getContext(name);
    }

    @EventHandler
    public void onLeafDecays(LeavesDecayEvent event) {
        location = new LocationTag(event.getBlock().getLocation());
        material = new MaterialTag(event.getBlock());
        this.event = event;
        fire(event);
    }
}
