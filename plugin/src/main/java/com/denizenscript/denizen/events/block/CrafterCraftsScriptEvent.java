package com.denizenscript.denizen.events.block;

import com.denizenscript.denizen.objects.ItemTag;
import com.denizenscript.denizen.objects.LocationTag;
import com.denizenscript.denizen.events.BukkitScriptEvent;
import com.denizenscript.denizencore.objects.ObjectTag;
import com.denizenscript.denizencore.objects.core.ElementTag;
import org.bukkit.Keyed;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.CrafterCraftEvent;
import org.bukkit.inventory.ItemStack;

public class CrafterCraftsScriptEvent extends BukkitScriptEvent implements Listener {

    // <--[event]
    // @Events
    // crafter crafts <item>
    //
    // @Group Block
    //
    // @Location true
    //
    // @Cancellable true
    //
    // @Triggers when a crafter crafts an item.
    //
    // @Context
    // <context.location> returns the LocationTag of the crafter.
    // <context.item> returns the ItemTag of the item being crafted.
    // <context.recipe_id> returns the ElementTag of the recipe ID formed.
    //
    // @Determine
    // "ITEM:<ItemTag>" to set the item being crafted. Determinations still consume ingredients.
    //
    // -->

    public CrafterCraftsScriptEvent() {
        registerCouldMatcher("crafter crafts <item>");
        this.<CrafterCraftsScriptEvent, ItemTag>registerDetermination("item", ItemTag.class, (evt, context, result) -> {
            event.setResult(result.getItemStack());
        });
    }

    public LocationTag location;
    public ItemTag result;
    public CrafterCraftEvent event;

    @Override
    public boolean matches(ScriptPath path) {
        if (!runInCheck(path, location)) {
            return false;
        }
        if  (!path.tryArgObject(2, result)) {
            return false;
        }
        return super.matches(path);
    }

    @Override
    public ObjectTag getContext(String name) {
        switch (name) {
            case "item": return result;
            case "location": return location;
            case "recipe_id": return new ElementTag(((Keyed) event.getRecipe()).getKey().toString());
        }
        return super.getContext(name);
    }

    @EventHandler
    public void onCrafterCrafts(CrafterCraftEvent event) {
        location = new LocationTag(event.getBlock().getLocation());
        result = new ItemTag(event.getResult());
        this.event = event;
        fire(event);
    }
}
