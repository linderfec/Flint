package org.spongepowered.asm.mixin;

import org.spongepowered.asm.mixin.MixinEnvironment.Phase;

/**
 * Agent 环境专用的阶段切换入口。
 *
 * <p>{@link MixinEnvironment#gotoPhase(Phase)} 是包私有的：launchwrapper 由
 * 同包的 {@code EnvironmentStateTweaker} 在游戏启动前把阶段推进到
 * {@link Phase#DEFAULT}，Agent 环境没有它，所以由这里代劳。</p>
 *
 * <p>阶段必须在类开始加载之前推进到 DEFAULT，否则注册进来的 mixin 配置
 * （绑定在 DEFAULT 环境上）永远不会被 {@code MixinProcessor} 选中，
 * mixin 就不会被应用。</p>
 */
public final class AgentPhaseHelper {

    private AgentPhaseHelper() {}

    /**
     * 进入 DEFAULT（运行期）阶段
     */
    public static void enterDefaultPhase() {
        MixinEnvironment.gotoPhase(Phase.DEFAULT);
    }
}
