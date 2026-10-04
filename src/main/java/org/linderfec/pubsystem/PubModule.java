package org.linderfec.pubsystem;

import java.lang.instrument.Instrumentation;

/**
 * pubSystem 模块接口
 * 所有放在 run/pubSystem/ 下的 jar 模块都需要实现此接口
 *
 * 模块可以在 onLoad 中通过 Instrumentation 注册自己的 ClassFileTransformer，
 * 从而拦截并修改任意游戏类的字节码，无需修改 Flint 本体代码。
 */
public interface PubModule {

    /** 模块名称 */
    String getName();

    /** 模块版本 */
    String getVersion();

    /**
     * 模块加载时调用
     * @param inst Instrumentation 实例，可用于注册 ClassFileTransformer 等
     */
    void onLoad(Instrumentation inst);

    /** 模块卸载时调用（释放资源） */
    void onUnload();
}
