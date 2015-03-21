package anzac.peripherals.peripherals;

import static anzac.peripherals.utility.ClassUtils.callPeripheralMethod;

import java.util.HashSet;
import java.util.Set;

import anzac.peripherals.utility.ClassUtils;
import dan200.computercraft.api.lua.ILuaContext;
import dan200.computercraft.api.lua.LuaException;
import dan200.computercraft.api.peripheral.IComputerAccess;
import dan200.computercraft.api.peripheral.IPeripheral;

public abstract class BasePeripheral implements IPeripheral {

	private static String[] methodNames;

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
		return callPeripheralMethod(this, context, methodName, arguments);
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
		// if (other instanceof BasePeripheral) {
		// return Objects.equals(entity, ((BasePeripheral) other).entity)
		// && Objects.equals(turtle, ((BasePeripheral) other).turtle);
		// }
		return false;
	}

	@Override
	public int hashCode() {
		// final int prime = 31;
		final int result = 1;
		// result = prime * result + (entity == null ? 0 : entity.hashCode());
		// result = prime * result + (turtle == null ? 0 : turtle.hashCode());
		return result;
	}

	@Override
	public boolean equals(final Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		// final BasePeripheral other = (BasePeripheral) obj;
		// if (entity == null) {
		// if (other.entity != null) {
		// return false;
		// }
		// } else if (!entity.equals(other.entity)) {
		// return false;
		// }
		// if (turtle == null) {
		// if (other.turtle != null) {
		// return false;
		// }
		// } else if (!turtle.equals(other.turtle)) {
		// return false;
		// }
		return true;
	}
}
