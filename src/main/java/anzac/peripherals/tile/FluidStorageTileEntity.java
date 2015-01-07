package anzac.peripherals.tile;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.Constants.NBT;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidContainerRegistry;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTank;
import net.minecraftforge.fluids.FluidTankInfo;
import net.minecraftforge.fluids.IFluidHandler;
import anzac.peripherals.annotations.Peripheral;
import anzac.peripherals.annotations.PeripheralMethod;
import anzac.peripherals.handler.ConfigurationHandler;
import anzac.peripherals.utility.FluidUtils;
import anzac.peripherals.utility.Position;

@Peripheral(type = "fluidstorage")
public class FluidStorageTileEntity extends BaseTileEntity implements IFluidHandler {

	private static final String CAPACITY_TAG = "capacity";
	private static final String TANK_TAG = "tanks";
	private final List<FluidTank> tanks = new ArrayList<FluidTank>();
	private FluidTank emptyTank = new FluidTank(getMaxSize());

	public FluidStorageTileEntity() {
	}

	private int getMaxSize() {
		return ConfigurationHandler.hddSize * 1000 / 64 / 64;
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

	@PeripheralMethod
	public int routeTo(final int index, final ForgeDirection toDir, final int amount) throws Exception {
		return routeTo(index, toDir, toDir.getOpposite(), amount);
	}

	@PeripheralMethod
	public int routeTo(final int index, final ForgeDirection toDir, final ForgeDirection insertDir, final int amount)
			throws Exception {
		final FluidTank tank = tanks.get(index);
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
		if (tank.getFluidAmount() < tank.getCapacity() - FluidContainerRegistry.BUCKET_VOLUME) {
			tank.setCapacity(tank.getCapacity() - FluidContainerRegistry.BUCKET_VOLUME);
			emptyTank.setCapacity(emptyTank.getCapacity() + FluidContainerRegistry.BUCKET_VOLUME);
		}
		if (tank.getFluidAmount() == 0) {
			emptyTank.setCapacity(emptyTank.getCapacity() + tank.getCapacity());
			tanks.remove(tank);
		}
		return fill;
	}

	public void readFromNBT(final NBTTagCompound tagCompound) {
		super.readFromNBT(tagCompound);

		if (tagCompound.hasKey(TANK_TAG)) {
			final NBTTagList tagList = tagCompound.getTagList(TANK_TAG, NBT.TAG_COMPOUND);
			for (int i = 0; i < tagList.tagCount(); i++) {
				final NBTTagCompound tankTag = tagList.getCompoundTagAt(i);
				final int capacity = tankTag.getInteger(CAPACITY_TAG);
				final FluidTank tank = new FluidTank(capacity);
				tank.readFromNBT(tankTag);
				tanks.add(tank);
			}
		}
		if (tagCompound.hasKey("empty")) {
			emptyTank.setCapacity(tagCompound.getInteger("empty"));
		}
	}

	public void writeToNBT(final NBTTagCompound tagCompound) {
		super.writeToNBT(tagCompound);

		final NBTTagList tanksTag = new NBTTagList();
		for (final FluidTank tank : tanks) {
			final NBTTagCompound tankTag = new NBTTagCompound();
			tank.writeToNBT(tankTag);
			tankTag.setInteger(CAPACITY_TAG, tank.getCapacity());
			tanksTag.appendTag(tankTag);
		}
		tagCompound.setTag(TANK_TAG, tanksTag);

		tagCompound.setInteger("enpty", emptyTank.getCapacity());
	}

	public int getFluidAmount(final int index) {
		return tanks.get(index).getFluidAmount();
	}

	public void setFluidAmount(final int index, final int amount) {
		if (tanks.get(index).getFluid() != null) {
			tanks.get(index).getFluid().amount = amount;
		}
	}

	public int getFluid(final int index) {
		return tanks.get(index).getFluid() == null ? 0 : tanks.get(index).getFluid().fluidID;
	}

	public void setFluid(final int index, final int fluidId) {
		final Fluid fluid = FluidRegistry.getFluid(fluidId);
		if (fluid != null) {
			tanks.get(index).setFluid(new FluidStack(fluid, getFluidAmount(index)));
		}
	}

	public FluidTankInfo getInfo(final int index) {
		return tanks.get(index).getInfo();
	}

	@Override
	public int fill(final ForgeDirection from, FluidStack resource, final boolean doFill) {
		resource = resource.copy();
		int totalUsed = 0;

		FluidTank tank = null;
		for (FluidTank t : tanks) {
			final Fluid tankFluid = t.getFluid() != null ? t.getFluid().getFluid() : null;
			if (tankFluid == resource.getFluid()) {
				tank = t;
				break;
			}
		}
		if (tank == null) {
			tank = new FluidTank(FluidContainerRegistry.BUCKET_VOLUME);
			tanks.add(tank);
			if (doFill) {
				emptyTank.setCapacity(emptyTank.getCapacity() - FluidContainerRegistry.BUCKET_VOLUME);
			}
		}
		while (doFill && tank.getFluidAmount() + resource.amount > tank.getCapacity() && emptyTank.getCapacity() > 0) {
			tank.setCapacity(tank.getCapacity() + FluidContainerRegistry.BUCKET_VOLUME);
			emptyTank.setCapacity(emptyTank.getCapacity() - FluidContainerRegistry.BUCKET_VOLUME);
		}

		final int used = tank.fill(resource, doFill);
		resource.amount -= used;

		totalUsed += used;
		return totalUsed;
	}

	@Override
	public FluidStack drain(final ForgeDirection from, final FluidStack resource, final boolean doDrain) {
		return null;
	}

	@Override
	public FluidStack drain(final ForgeDirection from, final int maxDrain, final boolean doDrain) {
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
		info.add(emptyTank.getInfo());

		return info.toArray(new FluidTankInfo[info.size()]);
	}
}
