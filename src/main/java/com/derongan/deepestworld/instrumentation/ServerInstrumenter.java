package com.derongan.deepestworld.instrumentation;

import net.bytebuddy.ByteBuddy;
import net.bytebuddy.agent.ByteBuddyAgent;
import net.bytebuddy.asm.Advice;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.dynamic.DynamicType;
import net.bytebuddy.dynamic.loading.ClassReloadingStrategy;
import net.bytebuddy.implementation.StubMethod;
import net.bytebuddy.matcher.ElementMatchers;
import net.minecraft.server.v1_15_R1.ChunkCoordIntPair;
import net.minecraft.server.v1_15_R1.PlayerChunkMap;
import org.bukkit.plugin.Plugin;

public class ServerInstrumenter {
    private final TypeDescription chunkCoordIntPairType = TypeDescription.ForLoadedType.of(ChunkCoordIntPair.class);
    private final TypeDescription primitiveIntType = TypeDescription.ForLoadedType.of(Integer.class).asUnboxed();

    private Plugin plugin;


    public ServerInstrumenter(Plugin plugin) {
        this.plugin = plugin;
    }

    public void instrument() {
        ByteBuddyAgent.install();

        DynamicType.Unloaded<PlayerChunkMap> type = new ByteBuddy()
                .redefine(PlayerChunkMap.class)
                .method(ElementMatchers.named("a")
                        .and(ElementMatchers.isStatic())
                        .and(ElementMatchers.takesArguments(chunkCoordIntPairType, primitiveIntType, primitiveIntType)))
                .intercept(Advice.to(WindowAwareInterceptor.class).wrap(StubMethod.INSTANCE))
                .make();

//        try {
//            type.saveIn(Paths.get("/home/derongan/projects/minecraft/DeepestWorld/redefinitions").toFile());
//        } catch (IOException e) {
//            e.printStackTrace();
//        }

        type.load(PlayerChunkMap.class.getClassLoader(), ClassReloadingStrategy.fromInstalledAgent());
    }

    public static class WindowAwareInterceptor {
        @Advice.OnMethodExit
        public static void forceChunkSend(@Advice.Argument(0) ChunkCoordIntPair chunkCoordIntPair,
                                          @Advice.Argument(1) int x,
                                          @Advice.Argument(2) int z,
                                          @Advice.Return(readOnly = false) int returned) {

            int k = chunkCoordIntPair.x - x;
            int l = chunkCoordIntPair.z - z;
            returned = Math.max(Math.abs(k), Math.abs(l));
        }
    }
}
