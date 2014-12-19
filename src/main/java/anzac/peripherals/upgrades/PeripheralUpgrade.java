package anzac.peripherals.upgrades;

import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import dan200.computercraft.api.turtle.ITurtleAccess;
import dan200.computercraft.api.turtle.TurtleCommandResult;
import dan200.computercraft.api.turtle.TurtleSide;
import dan200.computercraft.api.turtle.TurtleUpgradeType;
import dan200.computercraft.api.turtle.TurtleVerb;

public abstract class PeripheralUpgrade extends BaseUpgrade {

	@SideOnly(Side.CLIENT)
	protected IIcon icon;

	public PeripheralUpgrade(final ItemStack itemStack, final int upgradeId) {
		super(itemStack, upgradeId);
	}

	@Override
	public TurtleUpgradeType getType() {
		return TurtleUpgradeType.Peripheral;
	}

	@Override
	public TurtleCommandResult useTool(final ITurtleAccess paramITurtleAccess, final TurtleSide paramTurtleSide,
			final TurtleVerb paramTurtleVerb, final int paramInt) {
		return null;
	}

	@Override
	public void update(final ITurtleAccess paramITurtleAccess, final TurtleSide paramTurtleSide) {
	}

	@Override
	public IIcon getIcon(final ITurtleAccess paramITurtleAccess, final TurtleSide paramTurtleSide) {
		return icon != null ? icon : itemStack.getIconIndex();
	}

	@SideOnly(Side.CLIENT)
	public abstract void registerIcons(final IIconRegister par1IconRegister);

}
