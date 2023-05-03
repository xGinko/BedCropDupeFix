package me.xginko.bedcropdupefix;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.Vector;

import java.util.HashSet;

public final class BedCropDupeFix extends JavaPlugin implements Listener {

    @Override
    public void onEnable() {
        getServer().getPluginManager().registerEvents(this, this);
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.LOW)
    private void onPlayerInteract(PlayerInteractEvent event) {
        if (!event.getAction().equals(Action.RIGHT_CLICK_BLOCK)) return;

        PlayerInventory inventory = event.getPlayer().getInventory();
        ItemStack heldItem = inventory.getItem(inventory.getHeldItemSlot());
        if (heldItem == null) return;

        // Check if player is holding a bed
        String heldItemTypeName = heldItem.getType().name();
        if (!heldItemTypeName.endsWith("_BED") && !heldItemTypeName.equals("BED")) return;

        Block clickedBlock = event.getClickedBlock();
        if (clickedBlock == null) return; // Shouldn't be null since we're making sure the action is right click block but just in case

        for (Block block : getAdjacentBlocks(clickedBlock.getLocation(), event.getBlockFace())) {
            String blockTypeName = block.getType().name();
            if (
                    blockTypeName.equals("WHEAT") || blockTypeName.equals("POTATOES")
                    || blockTypeName.equals("POTATO") || blockTypeName.equals("CARROTS")
                    || blockTypeName.equals("CARROT") || blockTypeName.equals("NETHER_WARTS")
                    || blockTypeName.equals("BEETROOTS")
            ) {
                event.setCancelled(true);
                getLogger().info("Player " + event.getPlayer().getName() + " failed duplicating crops with beds.");
                return;
            }
        }
    }

    private HashSet<Block> getAdjacentBlocks(Location clickedBlockLocation, BlockFace blockFace) {
        HashSet<Block> adjacentBlocks = new HashSet<>();

        Location searchLocation = clickedBlockLocation.add(getDirection(blockFace));

        adjacentBlocks.add(searchLocation.add(0, -1, 0).getBlock()); // Down
        adjacentBlocks.add(searchLocation.add(0, 2, 0).getBlock()); // Up
        adjacentBlocks.add(searchLocation.add(-1, -1, 0).getBlock()); // Left
        adjacentBlocks.add(searchLocation.add(2, 0, 0).getBlock()); // Right
        adjacentBlocks.add(searchLocation.add(-1, 0, 1).getBlock()); // Forward
        adjacentBlocks.add(searchLocation.add(0, 0, -2).getBlock()); // Downward

        return adjacentBlocks;
    }

    private Vector getDirection(BlockFace blockFace) {
        int modX = blockFace.getModX();
        int modY = blockFace.getModY();
        int modZ = blockFace.getModZ();
        Vector direction = new Vector(modX, modY, modZ);
        if (modX != 0 || modY != 0 || modZ != 0) {
            direction.normalize();
        }
        return direction;
    }
}
