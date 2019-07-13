package com.denizenscript.denizen.events.world;

import com.denizenscript.denizen.objects.LocationTag;
import com.denizenscript.denizen.objects.MaterialTag;
import com.denizenscript.denizen.events.BukkitScriptEvent;
import com.denizenscript.denizencore.objects.core.ElementTag;
import com.denizenscript.denizencore.objects.core.ListTag;
import com.denizenscript.denizencore.objects.ObjectTag;
import com.denizenscript.denizencore.scripts.containers.ScriptContainer;
import com.denizenscript.denizencore.utilities.CoreUtilities;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPistonExtendEvent;

public class PistonExtendsScriptEvent extends BukkitScriptEvent implements Listener {

    // <--[event]
    // @Events
    // piston extends
    // <block> extends
    //
    // @Regex ^on [^\s]+ extends$
    // @Switch in <area>
    //
    // @Cancellable true
    //
    // @Triggers when a piston extends.
    //
    // @Context
    // <context.location> returns the LocationTag of the piston.
    // <context.material> returns the MaterialTag of the piston.
    // <context.length> returns an ElementTag of the number of blocks that will be moved by the piston.
    // <context.blocks> returns a ListTag of all block locations about to be moved.
    // <context.sticky> returns an ElementTag of whether the piston is sticky.
    // <context.relative> returns a LocationTag of the block in front of the piston.
    //
    // -->

    public PistonExtendsScriptEvent() {
        instance = this;
    }

    public static PistonExtendsScriptEvent instance;
    public LocationTag location;
    public MaterialTag material;
    public ElementTag length;
    public ListTag blocks;
    public ElementTag sticky;
    public LocationTag relative;
    public BlockPistonExtendEvent event;

    @Override
    public boolean couldMatch(ScriptContainer scriptContainer, String s) {
        String lower = CoreUtilities.toLowerCase(s);
        String cmd = CoreUtilities.getXthArg(1, lower);
        return cmd.equals("extends");
    }

    @Override
    public boolean matches(ScriptPath path) {
        String mat = path.eventArgLowerAt(0);
        return (mat.equals("piston") || tryMaterial(material, mat))
                && runInCheck(path, location);
    }

    @Override
    public String getName() {
        return "PistonExtends";
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
        else if (name.equals("sticky")) {
            return sticky;
        }
        else if (name.equals("relative")) {
            return relative;
        }
        else if (name.equals("blocks")) {
            return blocks;
        }
        else if (name.equals("length")) {
            return length;
        }
        return super.getContext(name);
    }

    @EventHandler
    public void onPistonExtends(BlockPistonExtendEvent event) {
        location = new LocationTag(event.getBlock().getLocation());
        material = new MaterialTag(event.getBlock());
        sticky = new ElementTag(event.isSticky() ? "true" : "false");
        relative = new LocationTag(event.getBlock().getRelative(event.getDirection()).getLocation());
        blocks = new ListTag();
        for (Block block : event.getBlocks()) {
            blocks.add(new LocationTag(block.getLocation()).identify());
        }
        length = new ElementTag(blocks.size());
        this.event = event;
        fire(event);
    }
}
