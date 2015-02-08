package anzac.peripherals.item;

import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import anzac.peripherals.annotations.ItemInfo;
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
		final ItemInfo itemInfo = getClass().getAnnotation(ItemInfo.class);
		final String key = itemInfo.name();
		return Names.getItemKey(key);
	}

	@Override
	public String getUnlocalizedName(final ItemStack stack) {
		return getUnlocalizedName();
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerIcons(final IIconRegister iconRegister) {
		itemIcon = iconRegister.registerIcon(Names.unwrapUnlocalizedName(getUnlocalizedName()));
	}
}
