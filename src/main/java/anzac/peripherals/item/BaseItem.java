package anzac.peripherals.item;

import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import anzac.peripherals.creativetab.CreativeTab;
import anzac.peripherals.reference.Names;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public abstract class BaseItem extends Item {
	public BaseItem() {
		setCreativeTab(CreativeTab.peripheralTab);
	}

	@Override
	public String getUnlocalizedName() {
		return Names.getItemKey(super.getUnlocalizedName());
	}

	@Override
	public String getUnlocalizedName(final ItemStack stack) {
		return Names.getItemKey(super.getUnlocalizedName());
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerIcons(final IIconRegister iconRegister) {
		itemIcon = iconRegister.registerIcon(Names.unwrapUnlocalizedName(getUnlocalizedName()));
	}
}
