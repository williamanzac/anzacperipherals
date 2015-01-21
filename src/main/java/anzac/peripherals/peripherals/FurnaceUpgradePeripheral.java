package anzac.peripherals.peripherals;

import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.FurnaceRecipes;
import net.minecraft.util.Facing;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import anzac.peripherals.annotations.Peripheral;
import anzac.peripherals.annotations.PeripheralMethod;
import anzac.peripherals.utility.InvUtils;
import anzac.peripherals.utility.Position;
import dan200.computercraft.api.turtle.ITurtleAccess;
import dan200.computercraft.api.turtle.TurtleSide;

@Peripheral(type = "furnace")
public class FurnaceUpgradePeripheral extends BasePeripheral {

	private static final int FUEL_TO_CONSUME = 10;

	private final ITurtleAccess turtle;

	// private final TurtleSide side;
	// private final NBTTagCompound nbt;

	public FurnaceUpgradePeripheral(final ITurtleAccess paramITurtleAccess, final TurtleSide side) {
		turtle = paramITurtleAccess;
		// this.side = side;
		// nbt = turtle.getUpgradeNBTData(side);
	}

	/**
	 * Smelt the currently selected item from the turtle's inventory. The output will be put in to the first available
	 * slot or it will be dropped on to the ground.
	 * 
	 * @param amount
	 *            How many of the item to smelt.
	 * @throws Exception
	 *             Return an error if the currently selected slot is empty, if there is not enough fuel left to smelt an
	 *             item or if the current item cannot be smelted.
	 */
	@PeripheralMethod
	public void smelt(final int amount) throws Exception {
		final int selectedSlot = turtle.getSelectedSlot();
		for (int i = 0; i < amount; i++) {
			final ItemStack stackInSlot = turtle.getInventory().getStackInSlot(selectedSlot);
			if (stackInSlot == null || stackInSlot.stackSize <= 0) {
				throw new Exception("Selected slot is empty");
			}
			final ItemStack itemstack = FurnaceRecipes.smelting().getSmeltingResult(stackInSlot);
			if (itemstack == null) {
				throw new Exception("No Smelting recipe found");
			}
			if (!turtle.consumeFuel(FUEL_TO_CONSUME)) {
				throw new Exception("Not enough fuel");
			}
			storeOrDrop(itemstack.copy());
			final ItemStack consumeItem = InvUtils.consumeItem(stackInSlot);
			turtle.getInventory().setInventorySlotContents(selectedSlot, consumeItem);
		}
	}

	protected void storeOrDrop(final ItemStack stack) {
		stack.stackSize -= InvUtils.addItem(turtle.getInventory(), stack, ForgeDirection.UNKNOWN);
		if (stack.stackSize > 0) {
			dropItemStack(stack);
		}
	}

	protected void dropItemStack(final ItemStack stack) {
		final World world = turtle.getWorld();
		final Position pos = new Position(turtle.getPosition());
		pos.orientation = ForgeDirection.values()[Facing.oppositeSide[turtle.getDirection()]];
		final EntityItem entityItem = new EntityItem(world, pos.x, pos.y, pos.z, stack.copy());
		entityItem.motionX = (pos.orientation.offsetX * 0.7D + world.rand.nextFloat() * 0.2D - 0.1D);
		entityItem.motionY = (pos.orientation.offsetY * 0.7D + world.rand.nextFloat() * 0.2D - 0.1D);
		entityItem.motionZ = (pos.orientation.offsetZ * 0.7D + world.rand.nextFloat() * 0.2D - 0.1D);
		entityItem.delayBeforeCanPickup = 30;
		world.spawnEntityInWorld(entityItem);
	}
}
