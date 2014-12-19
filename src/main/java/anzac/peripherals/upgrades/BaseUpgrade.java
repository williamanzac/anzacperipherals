package anzac.peripherals.upgrades;

import net.minecraft.item.ItemStack;
import anzac.peripherals.annotations.UpgradeInfo;
import anzac.peripherals.reference.Names;
import dan200.computercraft.api.turtle.ITurtleUpgrade;

public abstract class BaseUpgrade implements ITurtleUpgrade {

	protected final ItemStack itemStack;
	protected final int upgradeId;
	private String adjective;

	protected BaseUpgrade(final ItemStack itemStack, final int upgradeId) {
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
		if (adjective == null) {
			final UpgradeInfo info = getClass().getAnnotation(UpgradeInfo.class);
			if (info != null) {
				adjective = Names.getUpgradeKey(info.name());
			} else {
				adjective = Names.getUpgradeKey(itemStack.getUnlocalizedName());
			}
		}
		return adjective;
	}

	@Override
	public ItemStack getCraftingItem() {
		return itemStack.copy();
	}

}
