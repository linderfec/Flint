package org.flint.mixinservice;

import java.util.Collection;
import java.util.Collections;

import org.spongepowered.asm.launch.platform.MixinPlatformAgentAbstract;
import org.spongepowered.asm.launch.platform.container.IContainerHandle;
import org.spongepowered.asm.launch.platform.IMixinPlatformServiceAgent;
import org.spongepowered.asm.util.Constants;

/**
 * Agent 环境下的 Mixin 平台代理。
 *
 * <p>主要作用是告诉 Mixin：</p>
 * <ul>
 *   <li>当前是哪一侧（客户端 / 服务端），否则配置里 {@code "client"} 数组里的
 *       mixin 会因为 side 未知而被跳过；</li>
 *   <li>当前环境里有哪些 mixin 容器（就是 Agent 自己所在的 JAR）。</li>
 * </ul>
 */
public class AgentPlatform extends MixinPlatformAgentAbstract implements IMixinPlatformServiceAgent {

    @Override
    public void init() {
        // Agent 环境没有需要注入的 classloader
    }

    @Override
    public String getSideName() {
        ClassLoader classLoader = AgentPlatform.class.getClassLoader();

        if (classLoader.getResource("net/minecraft/client/Minecraft.class") != null) {
            return Constants.SIDE_CLIENT;
        }
        if (classLoader.getResource("net/minecraft/server/MinecraftServer.class") != null) {
            return Constants.SIDE_DEDICATEDSERVER;
        }

        // 无法判断时按客户端处理（Flint 目前是客户端整合包）
        return Constants.SIDE_CLIENT;
    }

    @Override
    public Collection<IContainerHandle> getMixinContainers() {
        return Collections.singletonList(AgentMixinService.getAgentContainer());
    }
}
