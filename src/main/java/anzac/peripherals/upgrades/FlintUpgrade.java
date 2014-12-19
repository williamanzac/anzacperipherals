package anzac.peripherals.upgrades;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import anzac.peripherals.annotations.UpgradeInfo;
import anzac.peripherals.utility.Position;
import anzac.peripherals.utility.UpgradeUtils;
import dan200.computercraft.api.turtle.ITurtleAccess;
import dan200.computercraft.api.turtle.TurtleCommandResult;

@UpgradeInfo(name = "flint")
public class FlintUpgrade extends ToolUpgrade {

	public FlintUpgrade(final Item tool, final int upgradeId) {
		super(new ItemStack(tool), upgradeId);
	}

	@Override
	protected TurtleCommandResult dig(final ITurtleAccess turtle, final ForgeDirection direction) {
		final World world = turtle.getWorld();
		final Position pos = new Position(turtle.getPosition());
		pos.orientation = direction;
		pos.moveForwards(1);
		if (direction != ForgeDirection.UP) {
			pos.orientation = ForgeDirection.DOWN;
			pos.moveForwards(1);
		}
		final EntityPlayer turtlePlayer = UpgradeUtils.createPlayer(world, turtle, turtle.getDirection());
		final ItemStack craftingItem = getCraftingItem().copy();
		turtlePlayer.setCurrentItemOrArmor(0, craftingItem);

		if (craftingItem.getItem().onItemUse(craftingItem, turtlePlayer, world, pos.x, pos.y, pos.z,
				ForgeDirection.UP.ordinal(), 0.0F, 0.0F, 0.0F)) {
			return TurtleCommandResult.success();
		}
		return TurtleCommandResult.failure();
	}
}
