package dev.ftb.mods.ftbevolutioncompanion.skills;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import net.minecraft.world.entity.Entity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class DamageXpRemainder {
    private static final Logger LOGGER = LoggerFactory.getLogger(DamageXpRemainder.class);
    private static final String CALCULATION = "net.puffish.skillsmod.api.calculation.Calculation";

    private static volatile boolean failureLogged;

    private DamageXpRemainder() {}

    public static Object wrap(Object calculation) {
        if (calculation == null || Proxy.isProxyClass(calculation.getClass())) {
            return calculation;
        }
        try {
            Class<?> type =
                    Class.forName(CALCULATION, false, calculation.getClass().getClassLoader());
            return Proxy.newProxyInstance(type.getClassLoader(), new Class<?>[] {type}, new Carry(calculation));
        } catch (ReflectiveOperationException | RuntimeException e) {
            logFailure(e);
            return calculation;
        }
    }

    private static void logFailure(Exception e) {
        if (!failureLogged) {
            failureLogged = true;
            LOGGER.warn("Could not carry damage XP fractions", e);
        }
    }

    private static final class Carry implements InvocationHandler {
        private final Object delegate;
        private final Map<UUID, Double> remainders = new ConcurrentHashMap<>();
        private volatile Method playerAccessor;

        private Carry(Object delegate) {
            this.delegate = delegate;
        }

        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            if (method.getDeclaringClass() == Object.class) {
                return switch (method.getName()) {
                    case "equals" -> proxy == args[0];
                    case "hashCode" -> System.identityHashCode(proxy);
                    case "toString" -> "DamageXpRemainder[" + delegate + "]";
                    default -> call(method, args);
                };
            }
            Object result = call(method, args);
            if (!"evaluate".equals(method.getName()) || args == null || args.length != 1) {
                return result;
            }
            if (!(result instanceof Double value) || !(value > 0.0) || value.isInfinite()) {
                return result;
            }
            UUID player = player(args[0]);
            if (player == null) {
                return result;
            }
            double total = value + remainders.getOrDefault(player, 0.0);
            double whole = Math.floor(total);
            remainders.put(player, total - whole);
            return whole;
        }

        private Object call(Method method, Object[] args) throws Throwable {
            try {
                return method.invoke(delegate, args);
            } catch (InvocationTargetException e) {
                throw e.getCause();
            }
        }

        private UUID player(Object data) {
            try {
                Method accessor = playerAccessor;
                if (accessor == null) {
                    accessor = data.getClass().getMethod("player");
                    playerAccessor = accessor;
                }
                return accessor.invoke(data) instanceof Entity entity ? entity.getUUID() : null;
            } catch (ReflectiveOperationException | RuntimeException e) {
                logFailure(e);
                return null;
            }
        }
    }
}
