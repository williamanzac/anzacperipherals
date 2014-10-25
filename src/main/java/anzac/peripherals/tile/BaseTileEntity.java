package anzac.peripherals.tile;

import static anzac.peripherals.utility.ClassUtils.callPeripheralMethod;

import java.util.HashSet;
import java.util.Set;

import net.minecraft.tileentity.TileEntity;

import org.apache.commons.lang3.ArrayUtils;

import anzac.peripherals.peripherals.PeripheralEvent;
import anzac.peripherals.utility.ClassUtils;
import dan200.computercraft.api.lua.ILuaContext;
import dan200.computercraft.api.lua.LuaException;
import dan200.computercraft.api.peripheral.IComputerAccess;
import dan200.computercraft.api.peripheral.IPeripheral;

public class BaseTileEntity extends TileEntity implements IPeripheral {
	private String[] methodNames;

	protected final Set<IComputerAccess> computers = new HashSet<IComputerAccess>();

	@Override
	public final String getType() {
		return ClassUtils.getType(getClass());
	}

	@Override
	public final String[] getMethodNames() {
		if (methodNames == null) {
			methodNames = ClassUtils.getMethodNames(getClass());
		}
		return methodNames;
	}

	@Override
	public final Object[] callMethod(final IComputerAccess computer, final ILuaContext context, final int method,
			final Object[] arguments) throws LuaException, InterruptedException {
		final String methodName = getMethodNames()[method];
		return callPeripheralMethod(this, methodName, arguments);
	}

	@Override
	public void attach(final IComputerAccess computer) {
		computers.add(computer);
	}

	@Override
	public void detach(final IComputerAccess computer) {
		computers.remove(computer);
	}

	public boolean isConnected() {
		return !computers.isEmpty();
	}

	@Override
	public boolean equals(final IPeripheral other) {
		if (getClass() != other.getClass()) {
			return false;
		}
		return true;
	}

	public void fireEvent(final PeripheralEvent event, final Object... values) {
		for (final IComputerAccess computer : computers) {
			final Object[] clone = ArrayUtils.clone(values);
			ArrayUtils.add(clone, 0, computer.getAttachmentName());
			computer.queueEvent(event.name(), clone);
		}
	}

}
