package anzac.peripherals.upgrades;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Facing;
import net.minecraft.util.IIcon;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.common.util.ForgeDirection;
import anzac.peripherals.Peripherals;
import anzac.peripherals.utility.InvUtils;
import anzac.peripherals.utility.Position;
import anzac.peripherals.utility.UpgradeUtils;
import dan200.computercraft.api.peripheral.IPeripheral;
import dan200.computercraft.api.turtle.ITurtleAccess;
import dan200.computercraft.api.turtle.TurtleCommandResult;
import dan200.computercraft.api.turtle.TurtleSide;
import dan200.computercraft.api.turtle.TurtleUpgradeType;
import dan200.computercraft.api.turtle.TurtleVerb;

public abstract class ToolUpgrade extends BaseUpgrade {

	public ToolUpgrade(final ItemStack toolStack, final int upgradeId) {
		super(toolStack, upgradeId);
	}

	@Override
	public final TurtleUpgradeType getType() {
		return TurtleUpgradeType.Tool;
	}

	@Override
	public final IPeripheral createPeripheral(final ITurtleAccess turtle, final TurtleSide side) {
		return null;
	}

	@Override
	public final TurtleCommandResult useTool(final ITurtleAccess turtle, final TurtleSide side, final TurtleVerb verb,
			final int direction) {
		switch (verb) {
		case Attack:
			return attack(turtle, direction);
		case Dig:
			return dig(turtle, ForgeDirection.values()[direction]);
		}
		return TurtleCommandResult.failure("Unsupported action");
	}

	protected TurtleCommandResult attack(final ITurtleAccess turtle, final int direction) {
		final World world = turtle.getWorld();

		final EntityPlayer turtlePlayer = UpgradeUtils.createPlayer(world, turtle, direction);

		final Vec3 turtlePos = Vec3.createVectorHelper(turtlePlayer.posX, turtlePlayer.posY, turtlePlayer.posZ);
		final Vec3 rayDir = turtlePlayer.getLook(1.0F);
		final Vec3 rayStart = turtlePos.addVector(rayDir.xCoord * 0.4D, rayDir.yCoord * 0.4D, rayDir.zCoord * 0.4D);
		final Entity hitEntity = UpgradeUtils.findEntity(world, rayStart, rayDir, 1.1D);
		if (hitEntity == null) {
			return TurtleCommandResult.failure("Nothing to attack here");
		}
		if (!canAttackEntity(turtle, hitEntity)) {
			return TurtleCommandResult.failure();
		}

		return attack(turtle, hitEntity);
	}

	protected TurtleCommandResult dig(final ITurtleAccess turtle, final ForgeDirection direction) {
		final World world = turtle.getWorld();
		final Position pos = new Position(turtle.getPosition());
		pos.orientation = direction;
		final ForgeDirection oppositeDir = direction.getOpposite();
		final int opposite = oppositeDir.ordinal();
		final Position newPos = new Position(pos);
		final EntityPlayer turtlePlayer = UpgradeUtils.createPlayer(world, turtle, turtle.getDirection());
		final ItemStack craftingItem = getCraftingItem().copy();
		turtlePlayer.setCurrentItemOrArmor(0, craftingItem);

		// try using the item first
		newPos.moveForwards(1);
		final Item item = craftingItem.getItem();
		if (item.onItemUse(craftingItem, turtlePlayer, world, newPos.x, newPos.y, newPos.z, opposite, 0.0F, 0.0F, 0.0F)) {
			return TurtleCommandResult.success();
		}
		newPos.moveForwards(1);
		if (item.onItemUse(craftingItem, turtlePlayer, world, newPos.x, newPos.y, newPos.z, opposite, 0.0F, 0.0F, 0.0F)) {
			return TurtleCommandResult.success();
		}
		if (direction.ordinal() >= 2) {
			newPos.orientation = ForgeDirection.DOWN;
			newPos.moveForwards(1);
			if (item.onItemUse(craftingItem, turtlePlayer, world, newPos.x, newPos.y, newPos.z, 0, 0.0F, 0.0F, 0.0F)) {
				return TurtleCommandResult.success();
			}
		}

		// try breaking the block
		pos.moveForwards(1);
		final int x = pos.x;
		final int y = pos.y;
		final int z = pos.z;

		final Block block = world.getBlock(x, y, z);
		if (block == null || world.isAirBlock(x, y, z)) {
			return TurtleCommandResult.failure();
		}

		if (!canDigBlock(world, x, y, z, oppositeDir)) {
			return TurtleCommandResult.failure();
		}

		return dig(turtle, x, y, z, oppositeDir);
	}

	protected void storeOrDrop(final ITurtleAccess turtle, final ItemStack stack) {
		stack.stackSize -= InvUtils.addItem(turtle.getInventory(), stack, true, ForgeDirection.UNKNOWN);
		if (stack.stackSize > 0) {
			dropItemStack(turtle, stack);
		}
	}

	protected void dropItemStack(final ITurtleAccess turtle, final ItemStack stack) {
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

	protected List<ItemStack> getBlockDrops(final World world, final int x, final int y, final int z,
			final ForgeDirection opposite) {
		final Block block = world.getBlock(x, y, z);
		final int meta = world.getBlockMetadata(x, y, z);
		if (block == null || !ForgeHooks.canToolHarvestBlock(block, meta, getCraftingItem())) {
			return new ArrayList<ItemStack>();
		}
		return block.getDrops(world, x, y, z, meta, 0);
	}

	protected boolean canAttackEntity(final ITurtleAccess turtle, final Entity entityHit) {
		final EntityPlayer turtlePlayer = UpgradeUtils.createPlayer(turtle.getWorld(), turtle, turtle.getDirection());
		return entityHit.canAttackWithItem() && !entityHit.hitByEntity(turtlePlayer);
	}

	protected boolean canDigBlock(final World world, final int x, final int y, final int z,
			final ForgeDirection opposite) {
		final Block block = world.getBlock(x, y, z);
		final int meta = world.getBlockMetadata(x, y, z);

		return ForgeHooks.canToolHarvestBlock(block, meta, getCraftingItem());
	}

	@Override
	public final IIcon getIcon(final ITurtleAccess turtle, final TurtleSide side) {
		return getCraftingItem().getIconIndex();
	}

	@Override
	public final void update(final ITurtleAccess turtle, final TurtleSide side) {
		// nothing to do for a tool
	}

	protected TurtleCommandResult attack(final ITurtleAccess turtle, final Entity entityHit) {
		final EntityPlayer turtlePlayer = UpgradeUtils.createPlayer(turtle.getWorld(), turtle, turtle.getDirection());
		turtlePlayer.setCurrentItemOrArmor(0, getCraftingItem().copy());

		Peripherals.proxy.setEntityDropConsumer(entityHit, new DropConsumer() {
			@Override
			public void consumeDrop(final Entity entity, final ItemStack drop) {
				storeOrDrop(turtle, drop);
			}
		});
		turtlePlayer.attackTargetEntityWithCurrentItem(entityHit);

		Peripherals.proxy.clearEntityDropConsumer(entityHit);
		return TurtleCommandResult.success();
	}

	private TurtleCommandResult dig(final ITurtleAccess turtle, final int x, final int y, final int z,
			final ForgeDirection opposite) {
		final World world = turtle.getWorld();
		for (final ItemStack stack : getBlockDrops(world, x, y, z, opposite)) {
			storeOrDrop(turtle, stack);
		}

		final Block block = world.getBlock(x, y, z);
		if (block != null) {
			world.playSoundEffect(x + 0.5d, y + 0.5d, z + 0.5d, block.stepSound.getBreakSound(),
					(block.stepSound.getVolume() + 1.0f) / 2.0f, block.stepSound.getPitch() * 0.8f);
		}
		world.setBlockToAir(x, y, z);
		return TurtleCommandResult.success();
	}
}