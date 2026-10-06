package dev.ftb.mods.ftbevolutioncompanion.compat.starcatcher;

import com.wdiscute.starcatcher.data.network.CBPlayerStructuresPayload;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;

public final class StarcatcherPayloads {
    private StarcatcherPayloads() {}

    public static void register(RegisterPayloadHandlersEvent event) {
        event.registrar("1")
                .playToClient(
                        CBPlayerStructuresPayload.TYPE,
                        CBPlayerStructuresPayload.STREAM_CODEC,
                        (payload, context) -> payload.handle(context));
    }
}
