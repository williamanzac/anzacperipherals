package anzac.peripherals.upgrades;

import net.minecraft.item.ItemStack;
import anzac.peripherals.annotations.UpgradeInfo;
import anzac.peripherals.init.ModItems;
import anzac.peripherals.peripherals.EnderChestUpgradePeripheral;
import dan200.computercraft.api.peripheral.IPeripheral;
import dan200.computercraft.api.turtle.ITurtleAccess;
import dan200.computercraft.api.turtle.TurtleSide;

/**
 * @author Tony
 */
@UpgradeInfo(name = "enderchest")
public class ChestUpgrade extends PeripheralUpgrade {

	public ChestUpgrade(final int upgradeId) {
		super(new ItemStack(ModItems.enderchestupgrade), upgradeId);
	}

	@Override
	public IPeripheral createPeripheral(final ITurtleAccess paramITurtleAccess, final TurtleSide paramTurtleSide) {
		return new EnderChestUpgradePeripheral(paramITurtleAccess, paramTurtleSide);
	}
}
