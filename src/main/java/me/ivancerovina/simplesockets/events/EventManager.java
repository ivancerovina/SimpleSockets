package me.ivancerovina.simplesockets.events;

import me.ivancerovina.simplesockets.packet.Packet;
import me.ivancerovina.simplesockets.server.Client;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public class EventManager {
    private final HashMap<Class<?>, HashMap<Object, List<Method>>> callables = new HashMap<>();

    public void registerListener(SimpleSocketEventListener listener) {
        var clazz = listener.getClass();
        var methods = clazz.getMethods();

        for (Method method : methods) {
            if (method.isAnnotationPresent(EventHandler.class)) {

                if (!method.canAccess(listener)) {
                    throw new EventException("Cannot access method " + method.getName() + " in class " + clazz.getName());
                }

                if (method.getParameterCount() != 1) {
                    throw new EventException("Parameter count of method " + method.getName() + " must be 1");
                }

                var paramClass = method.getParameters()[0].getType();

                callables.computeIfAbsent(paramClass, k -> new HashMap<>())
                        .computeIfAbsent(listener, k -> new ArrayList<>())
                        .add(method);
            } else if (method.isAnnotationPresent(PacketHandler.class)) {
                // TODO Make a separate packet handler
            }
        }
    }

    public boolean callEvent(Object event) {
        var clazz = event.getClass();

        if (!callables.containsKey(clazz)) {
            return false;
        }

        var events = callables.get(clazz);

        events.forEach((object, methodList) ->
                methodList.forEach(method -> {
                    try {
                        method.invoke(object, event);
                    } catch (InvocationTargetException e) {
                        throw new EventException("Exception while invoking event", e.getCause());
                    } catch (IllegalAccessException ignored) {
                    }
                })
        );

        return events.size() > 0;
    }
}
