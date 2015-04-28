package anzac.peripherals.tile;

import static net.minecraftforge.fluids.FluidContainerRegistry.fillFluidContainer;
import static net.minecraftforge.fluids.FluidContainerRegistry.getFluidForFilledItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidContainerRegistry;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTank;
import net.minecraftforge.fluids.FluidTankInfo;
import net.minecraftforge.fluids.IFluidHandler;
import anzac.peripherals.annotations.Event;
import anzac.peripherals.annotations.Peripheral;
import anzac.peripherals.annotations.PeripheralMethod;
import anzac.peripherals.peripherals.PeripheralEvent;
import anzac.peripherals.utility.FluidUtils;
import anzac.peripherals.utility.Position;

@Peripheral(type = "fluidrouter")
public class FluidRouterTileEntity extends BaseTileEntity implements ISidedInventory, IFluidHandler {

	private static final String TANK_TAG = "tank";
	private final SimpleInventory inv = new SimpleFluidInventory(1, "Fluid Router");
	private final FluidTank tank = new FluidTank(FluidContainerRegistry.BUCKET_VOLUME * 16);

	public FluidRouterTileEntity() {
		inv.addListener(this);
	}

	protected SimpleInventory getInventory() {
		return inv;
	}

	/**
	 * Will return a table containing the uuid and count of each item in the internal cache.
	 * 
	 * @return A table of the internal contents.
	 * @throws Exception
	 */
	@PeripheralMethod
	public FluidTankInfo[] contents() throws Exception {
		return contents(ForgeDirection.UNKNOWN);
	}

	/**
	 * Will return a table containing the uuid and count of each item in the inventory connected to {@code direction}
	 * side of this block.
	 * 
	 * @param direction
	 *            which side of this block to examine the inventory of.
	 * @return A table of the contents of the connected inventory.
	 * @throws Exception
	 */
	@PeripheralMethod
	public FluidTankInfo[] contents(final ForgeDirection direction) throws Exception {
		return contents(direction, direction.getOpposite());
	}

	/**
	 * Will return a table containing the uuid and count of each item in the inventory connected to {@code direction}
	 * side of this block and limited the examined slot to those accessible from {@code side} side.
	 * 
	 * @param direction
	 *            which side of this block to examine the inventory of.
	 * @param dir
	 *            which side of the inventory to examine.
	 * @return A table of the contents of the connected inventory.
	 * @throws Exception
	 */
	@PeripheralMethod
	public FluidTankInfo[] contents(final ForgeDirection direction, final ForgeDirection dir) throws Exception {
		final TileEntity te;
		if (direction == ForgeDirection.UNKNOWN) {
			te = this;
		} else {
			final Position pos = new Position(xCoord, yCoord, zCoord, direction);
			pos.moveForwards(1);
			te = worldObj.getTileEntity(pos.x, pos.y, pos.z);
			if (te == null || !(te instanceof IFluidHandler)) {
				throw new Exception("fluid handler not found");
			}
		}
		final IFluidHandler handler = (IFluidHandler) te;
		return FluidUtils.contents(handler, dir);
	}

	/**
	 * Extract {@code amount} number of items with {@code uuid} from the inventory connected to {@code fromDir} side.
	 * 
	 * @param fromDir
	 *            which side of this block to extract from.
	 * @param fluidstack
	 *            the uuid of the items to extract.
	 * @return The actual number of items extracted.
	 * @throws Exception
	 */
	@PeripheralMethod
	public int extractFrom(final ForgeDirection fromDir, final FluidStack fluidstack) throws Exception {
		return extractFrom(fromDir, fluidstack, fromDir.getOpposite());
	}

	/**
	 * Extract {@code amount} number of items with {@code uuid} from the {@code side} side of the inventory connected to
	 * {@code fromDir} side.
	 * 
	 * @param fromDir
	 *            which side of this block to extract from.
	 * @param fluidstack
	 *            the uuid of the items to extract.
	 * @param extractSide
	 *            which side of the inventory to extract from.
	 * @return The actual number of items extracted.
	 * @throws Exception
	 */
	@PeripheralMethod
	public int extractFrom(final ForgeDirection fromDir, final FluidStack fluidstack, final ForgeDirection extractSide)
			throws Exception {
		if (getStackInSlot(0) != null) {
			throw new Exception("Internal cache is not empty");
		}
		final Position pos = new Position(xCoord, yCoord, zCoord, fromDir);
		pos.moveForwards(1);
		final TileEntity te = worldObj.getTileEntity(pos.x, pos.y, pos.z);
		if (te == null || !(te instanceof IFluidHandler)) {
			throw new Exception("Inventory not found");
		}
		final IFluidHandler inv = (IFluidHandler) te;
		final FluidStack drain = inv.drain(extractSide, fluidstack, true);
		if (drain != null && drain.amount > 0) {
			final int fill = fill(fromDir.getOpposite(), drain, true);
			return fill;
		}
		return 0;
	}

	/**
	 * Transfer {@code amount} number of items from the internal cache to the inventory connected on {@code toDir} side.
	 * 
	 * @param toDir
	 *            the side the inventory is connected to.
	 * @param amount
	 *            the number of items to transfer.
	 * @return the actual number of items transferred.
	 * @throws Exception
	 */
	@PeripheralMethod
	public int routeTo(final ForgeDirection toDir, final int amount) throws Exception {
		return routeTo(toDir, toDir.getOpposite(), amount);
	}

	/**
	 * Transfer {@code amount} number of items from the internal cache to the {@code side} side of the inventory
	 * connected on {@code toDir} side.
	 * 
	 * @param toDir
	 *            the side the inventory is connected to.
	 * @param insertDir
	 *            the side the inventory to insert the items from.
	 * @param amount
	 *            the number of items to transfer.
	 * @return the actual number of items transferred.
	 * @throws Exception
	 */
	@PeripheralMethod
	public int routeTo(final ForgeDirection toDir, final ForgeDirection insertDir, final int amount) throws Exception {
		final FluidStack drain = tank.drain(amount, false);
		final Position pos = new Position(xCoord, yCoord, zCoord, toDir);
		pos.moveForwards(1);
		final TileEntity te = worldObj.getTileEntity(pos.x, pos.y, pos.z);
		if (te == null || !(te instanceof IFluidHandler)) {
			throw new Exception("fluid handler not found");
		}
		final IFluidHandler inv = (IFluidHandler) te;
		final int fill = inv.fill(insertDir, drain, true);
		tank.drain(fill, true);
		return fill;
	}

	//
	// /**
	// * Transfer {@code amount} number of items from the internal cache to another connected peripheral with
	// * {@code label} label. The peripheral must be connected to the same computer.
	// *
	// * @param label
	// * the label of the peripheral.
	// * @param amount
	// * the number of items to transfer.
	// * @return the actual number of items transferred.
	// * @throws Exception
	// */
	// @PeripheralMethod
	// public int sendTo(final String label, final int amount) throws Exception {
	// return getEntity().sendTo(label, amount);
	// }
	//
	// /**
	// * Transfer {@code amount} amount of fluid from another connected peripheral with {@code label} label to the
	// * internal tank. The peripheral must be connected to the same computer.
	// *
	// * @param label
	// * the label of the peripheral.
	// * @param uuid
	// * the uuid of the fluid to transfer.
	// * @param amount
	// * the amount of fluid to transfer.
	// * @return the actual amount transferred.
	// * @throws Exception
	// */
	// @PeripheralMethod
	// public int requestFrom(final String label, final int uuid, final int amount) throws Exception {
	// return getEntity().requestFrom(label, uuid, amount);
	// }

	@Override
	public int[] getAccessibleSlotsFromSide(final int side) {
		return new int[0];
	}

	@Override
	public boolean canInsertItem(final int slot, final ItemStack stack, final int side) {
		return false;
	}

	@Override
	public boolean canExtractItem(final int slot, final ItemStack stack, final int side) {
		return false;
	}

	@Override
	public int getSizeInventory() {
		return getInventory().getSizeInventory();
	}

	@Override
	public ItemStack getStackInSlot(final int slot) {
		return getInventory().getStackInSlot(slot);
	}

	@Override
	public ItemStack decrStackSize(final int slot, final int amt) {
		return getInventory().decrStackSize(slot, amt);
	}

	@Override
	public ItemStack getStackInSlotOnClosing(final int slot) {
		return getInventory().getStackInSlotOnClosing(slot);
	}

	@Override
	public void setInventorySlotContents(final int slot, final ItemStack stack) {
		getInventory().setInventorySlotContents(slot, stack);
	}

	/**
	 * @param name
	 * @param stackSize
	 */
	@Event(PeripheralEvent.fluid_route)
	private void fireRouteEvent(final String name, final int stackSize) {
		fireEvent(PeripheralEvent.fluid_route, name, stackSize);
	}

	@Override
	public String getInventoryName() {
		return getInventory().getInventoryName();
	}

	@Override
	public boolean hasCustomInventoryName() {
		return getInventory().hasCustomInventoryName();
	}

	@Override
	public int getInventoryStackLimit() {
		return getInventory().getInventoryStackLimit();
	}

	@Override
	public boolean isUseableByPlayer(final EntityPlayer player) {
		return isConnected() && worldObj.getTileEntity(xCoord, yCoord, zCoord) == this
				&& player.getDistanceSq(xCoord + 0.5D, yCoord + 0.5D, zCoord + 0.5D) <= 64.0D;
	}

	@Override
	public void openInventory() {
		getInventory().openInventory();
	}

	@Override
	public void closeInventory() {
		getInventory().closeInventory();
	}

	@Override
	public boolean isItemValidForSlot(final int i, final ItemStack itemstack) {
		return getInventory().isItemValidForSlot(i, itemstack);
	}

	public void readFromNBT(final NBTTagCompound tagCompound) {
		super.readFromNBT(tagCompound);

		getInventory().readFromNBT(tagCompound);
		if (tagCompound.hasKey(TANK_TAG)) {
			tank.readFromNBT(tagCompound.getCompoundTag(TANK_TAG));
		}
	}

	public void writeToNBT(final NBTTagCompound tagCompound) {
		super.writeToNBT(tagCompound);

		getInventory().writeToNBT(tagCompound);
		final NBTTagCompound tankTag = new NBTTagCompound();
		tank.writeToNBT(tankTag);
		tagCompound.setTag(TANK_TAG, tankTag);
	}

	public int getFluidAmount() {
		return tank.getFluidAmount();
	}

	public void setFluidAmount(final int amount) {
		if (tank.getFluid() != null) {
			tank.getFluid().amount = amount;
		}
	}

	public int getFluid() {
		return tank.getFluid() == null ? 0 : tank.getFluid().getFluidID();
	}

	public void setFluid(final int fluidId) {
		final Fluid fluid = FluidRegistry.getFluid(fluidId);
		if (fluid != null) {
			tank.setFluid(new FluidStack(fluid, getFluidAmount()));
		}
	}

	public FluidTankInfo getInfo() {
		return tank.getInfo();
	}

	@Override
	public int fill(final ForgeDirection from, final FluidStack resource, final boolean doFill) {
		final int fill = tank.fill(resource, doFill);
		if (fill > 0) {
			fireRouteEvent(resource.getLocalizedName(), fill);
		}
		return fill;
	}

	@Override
	public FluidStack drain(final ForgeDirection from, final FluidStack resource, final boolean doDrain) {
		// do not allow drain
		return null;
	}

	@Override
	public FluidStack drain(final ForgeDirection from, final int maxDrain, final boolean doDrain) {
		// do not allow drain
		return null;
	}

	@Override
	public boolean canFill(final ForgeDirection from, final Fluid fluid) {
		final Fluid tankFluid = tank.getFluid() != null ? tank.getFluid().getFluid() : null;
		return tankFluid == null || tankFluid == fluid;
	}

	@Override
	public boolean canDrain(final ForgeDirection from, final Fluid fluid) {
		// do not allow drain
		return false;
	}

	@Override
	public FluidTankInfo[] getTankInfo(final ForgeDirection from) {
		return new FluidTankInfo[] { tank.getInfo() };
	}

	@Override
	public void updateEntity() {
		super.updateEntity();

		final ItemStack current = inv.getStackInSlot(0);
		if (current != null && worldObj.getTotalWorldTime() % 10 == 0) {
			final FluidStack available = tank.getFluid();

			if (available != null) {
				final ItemStack filled = fillFluidContainer(available, current);

				final FluidStack liquid = getFluidForFilledItem(filled);

				if (liquid != null) {
					inv.setInventorySlotContents(0, null);
					inv.setInventorySlotContents(0, filled);

					tank.drain(liquid.amount, true);
				}
			}
		}
	}
}
