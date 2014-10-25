package anzac.peripherals.tile;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.util.ForgeDirection;
import anzac.peripherals.annotations.Event;
import anzac.peripherals.annotations.Peripheral;
import anzac.peripherals.annotations.PeripheralMethod;
import anzac.peripherals.peripherals.PeripheralEvent;
import anzac.peripherals.utility.Position;

@Peripheral(type = "redstone")
public class RedstoneTileEntity extends BaseTileEntity {

	private final int[] input = new int[6];
	private int[] output = new int[6];
	private final int[] bundledInput = new int[6];
	private int[] bundledOutput = new int[6];

	/**
	 * @param side
	 * @param on
	 */
	@PeripheralMethod
	public void setOutput(final ForgeDirection side, final boolean on) {
		setAnalogOutput(side, on ? 15 : 0);
	}

	/**
	 * @param side
	 * @return
	 */
	@PeripheralMethod
	public boolean getOutput(final ForgeDirection side) {
		return getAnalogInput(side) > 0;
	}

	/**
	 * @param side
	 * @return
	 */
	@PeripheralMethod
	public boolean getInput(final ForgeDirection side) {
		return getAnalogInput(side) > 0;
	}

	/**
	 * @param side
	 * @param value
	 */
	@PeripheralMethod
	public void setBundledOutput(final ForgeDirection side, final int value) {
		if (bundledOutput[side.ordinal()] != value) {
			bundledOutput[side.ordinal()] = value;
			worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
		}
	}

	/**
	 * @param side
	 * @return
	 */
	@PeripheralMethod
	public int getBundledOutput(final ForgeDirection side) {
		return bundledOutput[side.ordinal()];
	}

	/**
	 * @param side
	 * @return
	 */
	@PeripheralMethod
	public int getBundledInput(final ForgeDirection side) {
		return bundledInput[side.ordinal()];
	}

	/**
	 * @param side
	 * @param mask
	 * @return
	 */
	@PeripheralMethod
	public boolean testBundledInput(final ForgeDirection side, final int mask) {
		return (input[side.ordinal()] & mask) == mask;
	}

	/**
	 * @param side
	 * @param value
	 */
	@PeripheralMethod
	public void setAnalogOutput(final ForgeDirection side, final int value) {
		if (output[side.ordinal()] != value) {
			output[side.ordinal()] = value;
			final Position p = new Position(xCoord, yCoord, zCoord, side);
			p.moveForwards(1);
			// worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
			// worldObj.notifyBlockChange(xCoord, yCoord, zCoord, getBlockType());
			worldObj.notifyBlocksOfNeighborChange(xCoord, yCoord, zCoord, getBlockType());
			worldObj.notifyBlocksOfNeighborChange(p.x, p.y, p.z, getBlockType());
			worldObj.notifyBlockOfNeighborChange(p.x, p.y, p.z, getBlockType());
			worldObj.func_147453_f(xCoord, yCoord, zCoord, getBlockType());
		}
	}

	/**
	 * @param side
	 * @param value
	 */
	@PeripheralMethod
	public void setAnalogueOutput(final ForgeDirection side, final int value) {
		setAnalogOutput(side, value);
	}

	/**
	 * @param side
	 * @return
	 */
	@PeripheralMethod
	public int getAnalogOutput(final ForgeDirection side) {
		return output[side.ordinal()];
	}

	/**
	 * @param side
	 * @return
	 */
	@PeripheralMethod
	public int getAnalogueOutput(final ForgeDirection side) {
		return getAnalogOutput(side);
	}

	/**
	 * @param side
	 * @return
	 */
	@PeripheralMethod
	public int getAnalogInput(final ForgeDirection side) {
		return input[side.ordinal()];
	}

	/**
	 * @param side
	 * @param value
	 * @return
	 */
	@PeripheralMethod
	public int getAnalogueInput(final ForgeDirection side, final int value) {
		return getAnalogInput(side);
	}

	// used by PeripheralBlock
	public int getOutput(final int side) {
		return output[side];
	}

	// used by PeripheralBlock
	public void setInput(final int side, final int inputStrength) {
		if (input[side] != inputStrength) {
			input[side] = inputStrength;
			fireRedstoneEvent(side);
		}
	}

	// used by PeripheralBlock
	public void setBundledInput(final int side, final int combination) {
		if (bundledInput[side] != combination) {
			bundledInput[side] = combination;
			fireRedstoneEvent(side);
		}
	}

	/**
	 * @param side
	 */
	@Event(PeripheralEvent.redstone)
	private void fireRedstoneEvent(final int side) {
		fireEvent(PeripheralEvent.redstone, side);
	}

	@Override
	public void readFromNBT(final NBTTagCompound nbtTagCompound) {
		super.readFromNBT(nbtTagCompound);

		if (nbtTagCompound.hasKey("output")) {
			output = nbtTagCompound.getIntArray("output");
		}
		if (nbtTagCompound.hasKey("bundledOutput")) {
			bundledOutput = nbtTagCompound.getIntArray("bundledOutput");
		}
	}

	@Override
	public void writeToNBT(final NBTTagCompound nbtTagCompound) {
		super.writeToNBT(nbtTagCompound);

		nbtTagCompound.setIntArray("output", output);
		nbtTagCompound.setIntArray("bundledOutput", bundledOutput);
	}
}
