package top.fur.furrybohe.handler;

public class ShaderHandler {

    public static void setActive(boolean active) {
        TintPostProcessor.INSTANCE.setActive(active);
    }

    public static boolean isActive() {
        return TintPostProcessor.INSTANCE.isActive();
    }
}