package top.fur.furrybohe.register;

import net.minecraft.world.entity.player.Player;
import net.minecraftforge.eventbus.api.Event;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;

public class RegisterEventHandlers {
    private final Map<Class<? extends Event>, BiConsumer<Player, Event>> handlers = new HashMap<>();

    public <T extends Event> void register(Class<T> eventClass, BiConsumer<Player, T> handler) {
        handlers.put(eventClass, (player, event) -> handler.accept(player, (T) event));
    }

    @SuppressWarnings("unchecked")
    public <T extends Event> void handleEvent(Player player, T event) {
        BiConsumer<Player, Event> handler = handlers.get(event.getClass());
        if (handler != null) {
            handler.accept(player, event);
        }
    }

    public boolean hasHandler(Class<? extends Event> eventClass) {
        return handlers.containsKey(eventClass);
    }
}
