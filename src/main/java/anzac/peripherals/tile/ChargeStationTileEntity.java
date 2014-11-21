package anzac.peripherals.tile;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityFurnace;
import net.minecraftforge.common.util.ForgeDirection;
import anzac.peripherals.annotations.Peripheral;
import anzac.peripherals.annotations.PeripheralMethod;
import anzac.peripherals.handler.ConfigurationHandler;
import anzac.peripherals.utility.ClassUtils;
import anzac.peripherals.utility.InvUtils;
import anzac.peripherals.utility.Position;
import cofh.api.energy.EnergyStorage;
import cofh.api.energy.IEnergyHandler;
import dan200.computercraft.api.turtle.ITurtleAccess;

/**
 * This peripheral will transfer it's internal power to any turtles parked next to it. It can accept either MJ or RF
 * power. An Iron Charge Station can store a maximum of 25 units of fuel. A Gold Charge Station can store a maximum of
 * 250 units of fuel. A Diamond Charge Station can store up to 2500 units of fuel. By default 1 unit of fuel is equal to
 * 20 MJ.
 * 
 * @author Tony
 */
@Peripheral(type = "chargestation")
public abstract class ChargeStationTileEntity extends BaseTileEntity implements IEnergyHandler, IInventory {

	private final EnergyStorage storage;
	private final SimpleInventory inv = new SimpleInventory(1, "Charge Station", 64);

	private static final float MAX_OUTPUT = 10;
	// private static final float MIN_OUTPUT = MAX_OUTPUT / 3;
	private static final float TARGET_OUTPUT = .375f;
	private final float kp = 1f;
	private final float ki = 0.05f;
	// private final double eLimit = (MAX_OUTPUT - MIN_OUTPUT) / ki;
	private int burnTime = 0;
	private int totalBurnTime = 0;
	private double esum = 0;
	private int currentOutput = 0;

	protected ChargeStationTileEntity() {
		super();
		this.storage = new EnergyStorage(250 * maxNumTurtles() * ConfigurationHandler.rfMultiplier, maxNumTurtles()
				* ConfigurationHandler.rfMultiplier);
		inv.addListener(this);
	}

	/**
	 * Get the amount of stored energy inside of this peripheral.
	 * 
	 * @return Returns how many units of fuel are currently stored in it.
	 */
	@PeripheralMethod
	public int getEnergyStored() {
		return storage.getEnergyStored();
	}

	public void setEnergyStored(final int energy) {
		storage.setEnergyStored(energy);
	}

	/**
	 * Get the maximum amount of energy that can be stored inside of this peripheral.
	 * 
	 * @return Returns how many units of fuel can be stored in it.
	 */
	@PeripheralMethod
	public int getMaxEnergyStored() {
		return storage.getMaxEnergyStored();
	}

	@Override
	public void updateEntity() {
		super.updateEntity();

		if (worldObj.isRemote) {
			return;
		}

		burn();

		if (worldObj.getTotalWorldTime() % 10 == 0) {
			// AnzacPeripheralsCore.logger.info("update; stored: " + handler.getEnergyStored());
			transferPower();
		}
	}

	public void burn() {
		if (burnTime > 0) {
			burnTime--;
			currentOutput = calculateCurrentOutput();
			storage.receiveEnergy(currentOutput, false);
		} else {
			currentOutput = 0;
		}

		if (burnTime == 0) {
			burnTime = totalBurnTime = getItemBurnTime(getStackInSlot(0));
			if (burnTime > 0) {
				setInventorySlotContents(0, InvUtils.consumeItem(getStackInSlot(0)));
			}
		}
	}

	private int getItemBurnTime(final ItemStack itemstack) {
		if (itemstack == null) {
			return 0;
		} else {
			return TileEntityFurnace.getItemBurnTime(itemstack);
		}
	}

	public int getBurnTime() {
		return burnTime;
	}

	public void setBurnTime(final int burnTime) {
		this.burnTime = burnTime;
	}

	public int getTotalBurnTime() {
		return totalBurnTime;
	}

	public void setTotalBurnTime(final int totalBurnTime) {
		this.totalBurnTime = totalBurnTime;
	}

	protected float getMaxOutput() {
		return maxNumTurtles() * MAX_OUTPUT;
	}

	private float getMinOutput() {
		return getMaxOutput() / 3;
	}

	private float getLimit() {
		return (getMaxOutput() - getMinOutput()) / ki;
	}

	public int calculateCurrentOutput() {
		final double e = TARGET_OUTPUT * storage.getMaxEnergyStored() - storage.getEnergyStored();
		final float eLimit = getLimit();
		esum = Math.max(Math.min(esum + e, eLimit), -eLimit);
		return (int) Math.round(Math.max(Math.min(e * kp + esum * ki, getMaxOutput()), getMinOutput()));
	}

	private void transferPower() {
		final List<ITurtleAccess> turtles = findTurtles();
		// AnzacPeripheralsCore.logger.info("found turtles: " + turtles);
		// AnzacPeripheralsCore.logger.info("has turtle; stored: " + workProvider.getEnergyStored());
		for (final ITurtleAccess turtle : turtles) {
			final int extractEnergy = storage.extractEnergy(ConfigurationHandler.rfMultiplier, true);
			if (extractEnergy < ConfigurationHandler.rfMultiplier) {
				continue;
			}
			final int fuelLevel = turtle.getFuelLevel();
			// AnzacPeripheralsCore.logger.info("fuelLevel: " + fuelLevel);
			final int fuelLimit = turtle.getFuelLimit();
			// AnzacPeripheralsCore.logger.info("fuelLimit: " + fuelLimit);
			if (fuelLevel + 1 <= fuelLimit) {
				// AnzacPeripheralsCore.logger.info("amount: " + amount);
				turtle.addFuel(1);
				storage.extractEnergy(ConfigurationHandler.rfMultiplier, false);
			}
		}
	}

	protected abstract int maxNumTurtles();

	private List<ITurtleAccess> findTurtles() {
		final int maxNumTurtles = maxNumTurtles();
		final List<ITurtleAccess> turtles = new ArrayList<ITurtleAccess>(maxNumTurtles);
		final List<ForgeDirection> directions = new ArrayList<ForgeDirection>();
		directions.addAll(Arrays.asList(ForgeDirection.values()));
		Collections.shuffle(directions);
		for (final ForgeDirection direction : directions) {
			if (turtles.size() >= maxNumTurtles) {
				break;
			}
			final Position position = new Position(xCoord, yCoord, zCoord, direction);
			position.moveForwards(1);
			final TileEntity entity = worldObj.getTileEntity(position.x, position.y, position.z);
			if (ClassUtils.instanceOf(entity, "dan200.computercraft.shared.turtle.blocks.ITurtleTile")) {
				// AnzacPeripheralsCore.logger.info("found turtle");
				turtles.add((ITurtleAccess) ClassUtils.callMethod(entity, "getAccess", null));
			}
		}

		return turtles;
	}

	@Override
	public boolean canConnectEnergy(final ForgeDirection from) {
		return true;
	}

	@Override
	public int receiveEnergy(final ForgeDirection from, final int maxReceive, final boolean simulate) {
		return storage.receiveEnergy(maxReceive, simulate);
	}

	@Override
	public int extractEnergy(final ForgeDirection from, final int maxExtract, final boolean simulate) {
		return 0; // do not extract
	}

	@Override
	public int getEnergyStored(final ForgeDirection from) {
		return storage.getEnergyStored();
	}

	@Override
	public int getMaxEnergyStored(final ForgeDirection from) {
		return storage.getMaxEnergyStored();
	}

	@Override
	public void readFromNBT(final NBTTagCompound tagCompound) {
		super.readFromNBT(tagCompound);
		storage.readFromNBT(tagCompound);
		inv.readFromNBT(tagCompound);
		burnTime = tagCompound.getInteger("burnTime");
		totalBurnTime = tagCompound.getInteger("totalBurnTime");
	}

	@Override
	public void writeToNBT(final NBTTagCompound tagCompound) {
		super.writeToNBT(tagCompound);
		storage.writeToNBT(tagCompound);
		inv.writeToNBT(tagCompound);
		tagCompound.setInteger("burnTime", burnTime);
		tagCompound.setInteger("totalBurnTime", totalBurnTime);
	}

	public int getSizeInventory() {
		return inv.getSizeInventory();
	}

	public ItemStack getStackInSlot(final int slotId) {
		return inv.getStackInSlot(slotId);
	}

	public ItemStack decrStackSize(final int slotId, final int count) {
		return inv.decrStackSize(slotId, count);
	}

	public void setInventorySlotContents(final int slotId, final ItemStack itemstack) {
		inv.setInventorySlotContents(slotId, itemstack);
	}

	public String getInventoryName() {
		return inv.getInventoryName();
	}

	public int getInventoryStackLimit() {
		return inv.getInventoryStackLimit();
	}

	public boolean isUseableByPlayer(final EntityPlayer entityplayer) {
		return inv.isUseableByPlayer(entityplayer);
	}

	public void openInventory() {
		inv.openInventory();
	}

	public void closeInventory() {
		inv.closeInventory();
	}

	public ItemStack getStackInSlotOnClosing(final int slotId) {
		return inv.getStackInSlotOnClosing(slotId);
	}

	public boolean isItemValidForSlot(final int i, final ItemStack itemstack) {
		return inv.isItemValidForSlot(i, itemstack);
	}

	public boolean hasCustomInventoryName() {
		return inv.hasCustomInventoryName();
	}
}
