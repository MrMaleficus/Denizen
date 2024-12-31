package com.denizenscript.denizen.events.player;

import com.denizenscript.denizen.Denizen;
import com.denizenscript.denizen.events.BukkitScriptEvent;
import com.denizenscript.denizen.objects.EntityTag;
import com.denizenscript.denizen.objects.ItemTag;
import com.denizenscript.denizen.objects.LocationTag;
import com.denizenscript.denizen.utilities.implementation.BukkitScriptEntryData;
import com.denizenscript.denizen.utilities.inventory.SlotHelper;
import com.denizenscript.denizencore.objects.ObjectTag;
import com.denizenscript.denizencore.objects.core.ElementTag;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerItemDamageEvent;

public class PlayerItemTakesDamageScriptEvent extends BukkitScriptEvent implements Listener {

    // <--[event]
    // @Events
    // player item takes damage
    // player <item> takes damage
    //
    // @Synonyms item durability changes
    //
    // @Group Player
    //
    // @Location true
    //
    // @Cancellable true
    //
    // @Triggers when the player damages an item.
    //
    // @Context
    // <context.damage> returns the amount of damage the item has taken.
    // <context.original_damage> returns the original amount of damage the item would have taken, before any modifications such as the unbreaking enchantment (only on Paper).
    // <context.item> returns the item that has taken damage.
    // <context.slot> returns the slot of the item that has taken damage. This value is a bit of a hack and is not reliable.
    //
    // @Determine
    // ElementTag(Number) to set the amount of damage the item will take.
    //
    // @Player Always.
    //
    // -->

    public PlayerItemTakesDamageScriptEvent() {
        registerCouldMatcher("player <item> takes damage");
        this.<PlayerItemTakesDamageScriptEvent, ElementTag>registerDetermination(null, ElementTag.class, (evt, context, amount) -> {
            if (amount.asElement().isInt()) {
                evt.event.setDamage(amount.asInt());
            }
        });
    }

    public PlayerItemDamageEvent event;
    ItemTag item;
    LocationTag location;

    @Override
    public boolean matches(ScriptPath path) {
        if (!path.tryArgObject(1, item)) {
            return false;
        }
        if (!runInCheck(path, location)) {
            return false;
        }
        return super.matches(path);
    }

    @Override
    public ObjectTag getContext(String name) {
        return switch (name) {
            case "item" -> item;
            case "damage" -> new ElementTag(event.getDamage());
            case "slot" -> new ElementTag(SlotHelper.slotForItem(event.getPlayer().getInventory(), item.getItemStack()) + 1);
            default -> super.getContext(name);
        };
    }

    @Override
    public BukkitScriptEntryData getScriptEntryData() {
        return new BukkitScriptEntryData(event.getPlayer());
    }

    @Override
    public void cancellationChanged() {
        if (cancelled) {
            final Player p = event.getPlayer();
            Bukkit.getScheduler().scheduleSyncDelayedTask(Denizen.getInstance(), p::updateInventory, 1);
        }
        super.cancellationChanged();
    }

    @EventHandler
    public void onPlayerItemTakesDamage(PlayerItemDamageEvent event) {
        if (EntityTag.isNPC(event.getPlayer())) {
            return;
        }
        item = new ItemTag(event.getItem());
        location = new LocationTag(event.getPlayer().getLocation());
        this.event = event;
        fire(event);
    }
}
