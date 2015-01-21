package anzac.peripherals.upgrades;

import net.minecraft.item.ItemStack;
import anzac.peripherals.annotations.UpgradeInfo;
import anzac.peripherals.init.ModItems;
import anzac.peripherals.peripherals.FurnaceUpgradePeripheral;
import dan200.computercraft.api.peripheral.IPeripheral;
import dan200.computercraft.api.turtle.ITurtleAccess;
import dan200.computercraft.api.turtle.TurtleSide;

/**
 * This Turtle Upgrade allows the turtle to smelt the currently selected item in it's inventory. <br/>
 * One piece of coal provides a turtle with 80 fuel and also allows 8 items to be smelted in a furnace, therefore for
 * each item smelted by the turtle 10 units of fuel are used.
 * 
 * @author Tony
 */
@UpgradeInfo(name = "furnace")
public class FurnaceUpgrade extends PeripheralUpgrade {

	public FurnaceUpgrade(final int upgradeId) {
		super(new ItemStack(ModItems.furnaceupgrade), upgradeId);
	}

	@Override
	public IPeripheral createPeripheral(final ITurtleAccess paramITurtleAccess, final TurtleSide paramTurtleSide) {
		return new FurnaceUpgradePeripheral(paramITurtleAccess, paramTurtleSide);
	}
}
