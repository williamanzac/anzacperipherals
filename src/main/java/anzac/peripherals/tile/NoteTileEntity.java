package anzac.peripherals.tile;

import net.minecraft.block.material.Material;
import anzac.peripherals.annotations.Peripheral;
import anzac.peripherals.annotations.PeripheralMethod;
import anzac.peripherals.init.ModBlocks;

@Peripheral(type = "note")
public class NoteTileEntity extends BaseTileEntity {

	public static enum Instrument {
		harp, bd, snare, hat, bassattack
	}

	/**
	 * @param instrument
	 * @param note
	 * @throws Exception
	 */
	@PeripheralMethod
	public void playNote(final Instrument instrument, final int note) throws Exception {
		if (note < 0 || note > 24) {
			throw new Exception("note must be between: 0-24");
		}
		if (worldObj.getBlock(xCoord, yCoord + 1, zCoord).getMaterial() == Material.air) {
			worldObj.addBlockEvent(xCoord, yCoord, zCoord, ModBlocks.note, instrument.ordinal(), note);
		}
	}

	@Override
	public boolean receiveClientEvent(final int instrument, final int note) {
		final float inflate = (float) Math.pow(2.0D, (note - 12) / 12.0D);
		playSound("note." + Instrument.values()[instrument], 3.0F, inflate);
		worldObj.spawnParticle("note", xCoord + 0.5D, yCoord + 1.2D, zCoord + 0.5D, note / 24.0D, 0.0D, 0.0D);
		return true;
	}

	/**
	 * @param sound
	 */
	@PeripheralMethod
	public void playSound(final String sound) {
		playSound(sound, 1f, 1f);
	}

	/**
	 * @param sound
	 * @param volume
	 */
	@PeripheralMethod
	public void playSound(final String sound, final float volume) {
		playSound(sound, volume, 1f);
	}

	/**
	 * @param sound
	 * @param volume
	 * @param pitch
	 */
	@PeripheralMethod
	public void playSound(final String sound, final float volume, final float pitch) {
		worldObj.playSoundEffect(xCoord + 0.5D, yCoord + 0.5D, zCoord + 0.5D, sound, volume, pitch);
	}
}
