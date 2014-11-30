package anzac.peripherals.tile;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraftforge.common.util.Constants.NBT;
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

@Peripheral(type = "fluidstorage")
public class FluidStorageTileEntity extends BaseTileEntity implements ISidedInventory, IFluidHandler {

	private static final String TANK_TAG = "tanks";
	private final SimpleInventory inv = new SimpleFluidInventory(1, "Fluid Storage");
	private final FluidTank[] tanks = new FluidTank[8];

	public FluidStorageTileEntity() {
		inv.addListener(this);
		for (int i = 0; i < tanks.length; i++) {
			tanks[i] = new FluidTank(FluidContainerRegistry.BUCKET_VOLUME * 16);
		}
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
		return FluidUtils.contents(this, ForgeDirection.UNKNOWN);
	}

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
			final NBTTagList tagList = tagCompound.getTagList(TANK_TAG, NBT.TAG_COMPOUND);
			for (int i = 0; i < tagList.tagCount(); i++) {
				tanks[i].readFromNBT(tagList.getCompoundTagAt(i));
			}
		}
	}

	public void writeToNBT(final NBTTagCompound tagCompound) {
		super.writeToNBT(tagCompound);

		getInventory().writeToNBT(tagCompound);
		final NBTTagList tanksTag = new NBTTagList();
		for (int i = 0; i < tanks.length; i++) {
			final NBTTagCompound tankTag = new NBTTagCompound();
			tanks[i].writeToNBT(tankTag);
			tanksTag.appendTag(tankTag);
		}
		tagCompound.setTag(TANK_TAG, tanksTag);
	}

	public int getFluidAmount(final int index) {
		return tanks[index].getFluidAmount();
	}

	public void setFluidAmount(final int index, final int amount) {
		if (tanks[index].getFluid() != null) {
			tanks[index].getFluid().amount = amount;
		}
	}

	public int getFluid(final int index) {
		return tanks[index].getFluid() == null ? 0 : tanks[index].getFluid().fluidID;
	}

	public void setFluid(final int index, final int fluidId) {
		final Fluid fluid = FluidRegistry.getFluid(fluidId);
		if (fluid != null) {
			tanks[index].setFluid(new FluidStack(fluid, getFluidAmount(index)));
		}
	}

	public FluidTankInfo getInfo(final int index) {
		return tanks[index].getInfo();
	}

	@Override
	public int fill(final ForgeDirection from, FluidStack resource, final boolean doFill) {
		resource = resource.copy();
		int totalUsed = 0;

		for (final FluidTank tank : tanks) {
			final int used = tank.fill(resource, doFill);
			resource.amount -= used;

			totalUsed += used;
			if (resource.amount <= 0) {
				break;
			}
		}
		return totalUsed;
	}

	@Override
	public FluidStack drain(final ForgeDirection from, final FluidStack resource, final boolean doDrain) {
		for (final FluidTank tank : tanks) {
			final Fluid tankFluid = tank.getFluid() != null ? tank.getFluid().getFluid() : null;
			if (tankFluid != null && tankFluid.equals(resource.getFluid())) {
				FluidStack drain = tank.drain(resource.amount, doDrain);
				return drain;
			}
		}
		return null;
	}

	@Override
	public FluidStack drain(final ForgeDirection from, final int maxDrain, final boolean doDrain) {
		for (final FluidTank tank : tanks) {
			final Fluid tankFluid = tank.getFluid() != null ? tank.getFluid().getFluid() : null;
			if (tankFluid != null) {
				FluidStack drain = tank.drain(maxDrain, doDrain);
				return drain;
			}
		}
		return null;
	}

	@Override
	public boolean canFill(final ForgeDirection from, final Fluid fluid) {
		for (final FluidTank tank : tanks) {
			final Fluid tankFluid = tank.getFluid() != null ? tank.getFluid().getFluid() : null;
			if (tankFluid == null || tankFluid == fluid) {
				return true;
			}
		}
		return false;
	}

	@Override
	public boolean canDrain(final ForgeDirection from, final Fluid fluid) {
		for (final FluidTank tank : tanks) {
			final Fluid tankFluid = tank.getFluid() != null ? tank.getFluid().getFluid() : null;
			if (tankFluid == fluid) {
				return true;
			}
		}
		return false;
	}

	@Override
	public FluidTankInfo[] getTankInfo(final ForgeDirection from) {
		final List<FluidTankInfo> info = new ArrayList<FluidTankInfo>();
		for (final FluidTank tank : tanks) {
			if (tank != null) {
				info.add(tank.getInfo());
			}
		}

		return info.toArray(new FluidTankInfo[info.size()]);
	}

	@Override
	public void updateEntity() {
		super.updateEntity();

		// final ItemStack current = inv.getStackInSlot(0);
		// if (current != null && worldObj.getTotalWorldTime() % 10 == 0) {
		// final FluidStack available = tank.getFluid();
		//
		// if (available != null) {
		// final ItemStack filled = fillFluidContainer(available, current);
		//
		// final FluidStack liquid = getFluidForFilledItem(filled);
		//
		// if (liquid != null) {
		// inv.setInventorySlotContents(0, null);
		// inv.setInventorySlotContents(0, filled);
		//
		// tank.drain(liquid.amount, true);
		// }
		// }
		// }
	}
}
