package anzac.peripherals.tile;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import anzac.peripherals.annotations.Peripheral;
import anzac.peripherals.annotations.PeripheralMethod;
import anzac.peripherals.peripherals.Target;
import anzac.peripherals.utility.ClassUtils;
import anzac.peripherals.utility.LogHelper;
import anzac.peripherals.utility.Position;
import dan200.computercraft.api.turtle.ITurtleAccess;

/**
 * This peripheral can use it internal power to teleport any turtle next to it. It can accept either MJ or RF power. A
 * memory card must be placed inside with the desired destination. It can even be in another dimension. An Iron
 * Teleporter can only have 1 destination and can store up to 250 fuel units. A Gold Teleporter can have 2 destinations
 * and stores up to 2500 units. A Diamond Teleporter can have 4 destination and stores up to 25000 units.
 * 
 * @author Tony
 */
@Peripheral(type = "turtleteleporter")
public abstract class TurtleTeleporterTileEntity extends BaseTileEntity implements IInventory {

	private final SimpleTargetInventory inv = new SimpleTargetInventory(maxNumTargets(), "Turtle Teleporter");

	protected TurtleTeleporterTileEntity() {
		super();
		inv.addListener(this);
	}

	/**
	 * Get a list of the currently configured targets.
	 * 
	 * @return Returns an array of teleporter targets.
	 */
	@PeripheralMethod
	public List<Target> getTargets() {
		final List<Target> targets = new ArrayList<Target>();
		for (int i = 0; i < getSizeInventory(); i++) {
			final ItemStack stack = getStackInSlot(i);
			if (stack != null && stack.hasTagCompound()) {
				final NBTTagCompound tagCompound = stack.getTagCompound();
				final Target target = new Target();
				final int x = tagCompound.getInteger("linkx");
				final int y = tagCompound.getInteger("linky");
				final int z = tagCompound.getInteger("linkz");
				final int d = tagCompound.getInteger("linkd");
				target.position = new Position(x, y, z);
				target.dimension = d;
				targets.add(target);
			}
		}
		return targets;
	}

	/**
	 * Teleport a turtle next to this peripheral to the specified target.
	 * 
	 * @param index
	 *            The numerical index of the target in the array.
	 * @throws Exception
	 *             Returns an error if the destination is blocked or invalid, there are no turtles next to it or if
	 *             there is not enough power.
	 */
	@PeripheralMethod
	public void teleport(final int index) throws Exception {
		// AnzacPeripheralsCore.logger.info("targets: " + targets + "isRemote: " + worldObj.isRemote);
		final Target target = getTargets().get(index);
		final List<ITurtleAccess> turtles = findTurtles();
		if (turtles == null || turtles.isEmpty()) {
			throw new Exception("No turtles found");
		}
		// check destination
		final World destWorld = MinecraftServer.getServer().worldServerForDimension(target.dimension);
		if (destWorld == null) {
			throw new Exception("Destination world does not exist");
		}
		Position pos = target.position;
		final TileEntity entity = destWorld.getTileEntity(pos.x, pos.y, pos.z);
		if (!(entity instanceof TurtleTeleporterTileEntity)) {
			throw new Exception("Destination is not a Turtle Teleporter");
		}
		validateTarget(target);
		for (final ITurtleAccess turtle : turtles) {
			LogHelper.info(turtle.getPosition() + ", " + turtle.getDirection());
			pos = target.position;
			pos.orientation = ForgeDirection.getOrientation(turtle.getDirection());
			pos.moveForwards(1);
			if (!canPlaceBlockAt(destWorld, pos)) {
				throw new Exception("Destination is blocked");
			}
			final float required = (float) requiredPower(target);
			final float useEnergy = turtle.getFuelLevel();
			if (useEnergy < required) {
				throw new Exception("Not enough power, requires " + required);
			}
			// AnzacPeripheralsCore.logger.info("teleporting");
			if (turtle.teleportTo(destWorld, pos.x, pos.y, pos.z)) {
				turtle.consumeFuel((int) required);
				onTeleport();
				((TurtleTeleporterTileEntity) entity).onTeleport();
			}
		}
	}

	protected void validateTarget(final Target target) throws Exception {
		final double power = requiredPower(target);
		final double max = 50 * Math.pow(10, maxNumTargets());
		final double samed = Math.abs(worldObj.provider.dimensionId - target.dimension);
		switch (maxNumTargets()) {
		case 1:
			if (samed != 0) {
				throw new Exception("The target is in another dimension");
			}
			break;
		case 2:
			if (samed > 4) {
				throw new Exception("The target dimension is too far away");
			}
			break;
		default:
			break;
		}
		if (power > max) {
			throw new Exception("The required power is greater than " + max);
		}
	}

	private double requiredPower(final Target target) {
		final double samed = Math.abs(worldObj.provider.dimensionId - target.dimension) + 1;
		final Position position = target.position;
		final double dist = Math.sqrt(getDistanceFrom(position.x, position.y, position.z));
		return dist * samed * 2;
	}

	public void onTeleport() {
		worldObj.playSoundEffect(xCoord + 0.5d, yCoord + 0.5d, zCoord + 0.5d, "mob.endermen.portal", 1f, 1f);
	}

	private boolean canPlaceBlockAt(final World par1World, final Position position) {
		final int par2 = position.x;
		final int par3 = position.y;
		final int par4 = position.z;
		final Block block = par1World.getBlock(par2, par3, par4);
		// AnzacPeripheralsCore.logger.info("block at; x:" + par2 + ", y:" + par3 + ", z:" + par4 + ", l:" + l);
		LogHelper.info("blocak at " + position + ", " + block);
		return block == null || block.isReplaceable(par1World, par2, par3, par4);
	}

	protected abstract int maxNumTargets();

	private List<ITurtleAccess> findTurtles() {
		final List<ITurtleAccess> turtles = new ArrayList<ITurtleAccess>();
		for (final ForgeDirection direction : ForgeDirection.values()) {
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
	public void readFromNBT(final NBTTagCompound tagCompound) {
		super.readFromNBT(tagCompound);
		inv.readFromNBT(tagCompound);
	}

	@Override
	public void writeToNBT(final NBTTagCompound tagCompound) {
		super.writeToNBT(tagCompound);
		inv.writeToNBT(tagCompound);
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
