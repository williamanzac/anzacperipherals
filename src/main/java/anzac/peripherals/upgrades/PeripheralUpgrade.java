package anzac.peripherals.upgrades;

import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import anzac.peripherals.reference.Names;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import dan200.computercraft.api.turtle.ITurtleAccess;
import dan200.computercraft.api.turtle.ITurtleUpgrade;
import dan200.computercraft.api.turtle.TurtleCommandResult;
import dan200.computercraft.api.turtle.TurtleSide;
import dan200.computercraft.api.turtle.TurtleUpgradeType;
import dan200.computercraft.api.turtle.TurtleVerb;

public abstract class PeripheralUpgrade implements ITurtleUpgrade {

	private final ItemStack itemStack;
	private final int upgradeId;

	@SideOnly(Side.CLIENT)
	protected IIcon icon;

	public PeripheralUpgrade(final ItemStack itemStack, final int upgradeId) {
		super();
		this.itemStack = itemStack;
		this.upgradeId = upgradeId;
	}

	@Override
	public int getUpgradeID() {
		return upgradeId;
	}

	@Override
	public String getUnlocalisedAdjective() {
		return Names.getUpgradeKey(itemStack.getUnlocalizedName());
	}

	@Override
	public TurtleUpgradeType getType() {
		return TurtleUpgradeType.Peripheral;
	}

	@Override
	public ItemStack getCraftingItem() {
		return itemStack.copy();
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
