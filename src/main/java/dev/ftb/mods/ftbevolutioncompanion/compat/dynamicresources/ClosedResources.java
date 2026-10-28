package dev.ftb.mods.ftbevolutioncompanion.compat.dynamicresources;

public final class ClosedResources {
    private ClosedResources() {}

    public static boolean causedBy(Throwable error) {
        for (Throwable t = error; t != null; t = t.getCause()) {
            if (t instanceof IllegalStateException && "zip file closed".equals(t.getMessage())) {
                return true;
            }
            if (t.getCause() == t) {
                return false;
            }
        }
        return false;
    }
}
