package net.aufdemrand.denizen.scripts.commands.item;

import net.aufdemrand.denizen.nms.NMSHandler;
import net.aufdemrand.denizen.nms.interfaces.PacketHelper;
import net.aufdemrand.denizen.objects.dItem;
import net.aufdemrand.denizen.objects.dPlayer;
import net.aufdemrand.denizen.utilities.Utilities;
import net.aufdemrand.denizen.utilities.debugging.dB;
import net.aufdemrand.denizen.utilities.inventory.SlotHelper;
import net.aufdemrand.denizencore.DenizenCore;
import net.aufdemrand.denizencore.exceptions.InvalidArgumentsException;
import net.aufdemrand.denizencore.objects.Duration;
import net.aufdemrand.denizencore.objects.Element;
import net.aufdemrand.denizencore.objects.aH;
import net.aufdemrand.denizencore.objects.dList;
import net.aufdemrand.denizencore.scripts.ScriptEntry;
import net.aufdemrand.denizencore.scripts.commands.AbstractCommand;
import net.aufdemrand.denizencore.utilities.scheduling.OneTimeSchedulable;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.List;

public class FakeItemCommand extends AbstractCommand {

    // <--[command]
    // @Name FakeItem
    // @Syntax fakeitem [<item>|...] [slot:<slot>] (duration:<duration>) (players:<player>|...) (player_only)
    // @Required 2
    // @Short Show a fake item in a player's inventory.
    // @Group item
    //
    // @Description
    // This command allows you to display an item in an inventory that is not really there.
    // To make it automatically disappear at a specific time, use the 'duration:' argument.
    // By default, it will use any inventory the player currently has open. To force it to use only the player's
    // inventory, use the 'player_only' argument.
    //
    // @Tags
    // None
    //
    // @Usage
    // Use to show a clientside-only pumpkin on the player's head.
    // - fakeitem i@pumpkin slot:head
    //
    // @Usage
    // Use to show a fake book in the player's hand for 1 tick.
    // - fakeitem "i@written_book[book=author|Morphan1|title|My Book|pages|This is my book!]" slot:<player.item_in_hand.slot> duration:1t
    // -->

    @Override
    public void parseArgs(ScriptEntry scriptEntry) throws InvalidArgumentsException {

        /* Match arguments to expected variables */
        for (aH.Argument arg : aH.interpretArguments(scriptEntry.aHArgs)) {

            if (!scriptEntry.hasObject("slot")
                    && arg.matchesPrefix("slot")) {
                scriptEntry.addObject("slot", arg.asElement());
            }
            else if (!scriptEntry.hasObject("duration")
                    && arg.matchesPrefix("duration")
                    && arg.matchesArgumentType(Duration.class)) {
                scriptEntry.addObject("duration", arg.asType(Duration.class));
            }
            else if (!scriptEntry.hasObject("item")
                    && arg.matchesArgumentList(dItem.class)) {
                scriptEntry.addObject("item", arg.asType(dList.class).filter(dItem.class, scriptEntry));
            }
            else if (!scriptEntry.hasObject("players")
                    && arg.matchesArgumentList(dPlayer.class)
                    && arg.matchesPrefix("players")) {
                scriptEntry.addObject("players", arg.asType(dList.class).filter(dPlayer.class, scriptEntry));
            }
            else if (!scriptEntry.hasObject("player_only")
                    && arg.matches("player_only")) {
                scriptEntry.addObject("player_only", new Element(true));
            }
            else {
                arg.reportUnhandled();
            }

        }

        if (!scriptEntry.hasObject("item")) {
            throw new InvalidArgumentsException("Must specify a valid item to fake!");
        }

        if (!scriptEntry.hasObject("slot")) {
            throw new InvalidArgumentsException("Must specify a valid slot!");
        }

        scriptEntry.defaultObject("duration", Duration.ZERO).defaultObject("player_only", new Element(false))
                .defaultObject("players", Arrays.asList(Utilities.getEntryPlayer(scriptEntry)));

    }

    @Override
    public void execute(ScriptEntry scriptEntry) {

        List<dItem> items = (List<dItem>) scriptEntry.getObject("item");
        final Element elSlot = scriptEntry.getElement("slot");
        Duration duration = scriptEntry.getdObject("duration");
        final List<dPlayer> players = (List<dPlayer>) scriptEntry.getObject("players");
        final Element player_only = scriptEntry.getElement("player_only");

        if (scriptEntry.dbCallShouldDebug()) {
            dB.report(scriptEntry, getName(), aH.debugList("items", items) + elSlot.debug() + duration.debug()
                    + aH.debugList("players", players) + player_only.debug());
        }

        int slot = SlotHelper.nameToIndex(elSlot.asString());
        if (slot == -1) {
            dB.echoError(scriptEntry.getResidingQueue(), "The input '" + elSlot.asString() + "' is not a valid slot!");
            return;
        }
        final boolean playerOnly = player_only.asBoolean();

        final PacketHelper packetHelper = NMSHandler.getInstance().getPacketHelper();

        for (dItem item : items) {
            if (item == null) {
                slot++;
                continue;
            }

            for (dPlayer player : players) {
                Player ent = player.getPlayerEntity();
                packetHelper.setSlot(ent, translateSlot(ent, slot, playerOnly), item.getItemStack(), playerOnly);
            }

            final int slotSnapshot = slot;
            slot++;

            if (duration.getSeconds() > 0) {
                DenizenCore.schedule(new OneTimeSchedulable(new Runnable() {
                    @Override
                    public void run() {
                        for (dPlayer player : players) {
                            Player ent = player.getPlayerEntity();
                            int translated = translateSlot(ent, slotSnapshot, playerOnly);
                            ItemStack original = ent.getOpenInventory().getItem(translated);
                            packetHelper.setSlot(ent, translated, original, playerOnly);
                        }
                    }
                }, (float) duration.getSeconds()));
            }
        }
    }

    static int translateSlot(Player player, int slot, boolean player_only) {
        // This is (probably?) a translation from standard player inventory slots to ones that work with the full crafting inventory system
        if (slot < 0) {
            return 0;
        }
        int total = player_only ? 46 : player.getOpenInventory().countSlots();
        if (total == 46) {
            if (slot == 45) {
                return slot;
            }
            else if (slot > 35) {
                slot = 8 - (slot - 36);
                return slot;
            }
            total -= 1;
        }
        if (slot > total) {
            return total;
        }
        return (int) (slot + (total - 9) - (9 * (2 * Math.floor(slot / 9.0))));
    }
}
