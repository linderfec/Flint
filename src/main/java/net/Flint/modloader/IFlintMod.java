package net.Flint.modloader;

import net.Flint.lifecycle.LifecycleEvent;

/**
 * Flint模组接口 - 所有Flint模组必须实现的接口
 * 继承LifecycleEvent接口，提供完整的生命周期回调方法
 */
public interface IFlintMod extends LifecycleEvent {
    // 继承LifecycleEvent的所有方法，包括onInitialize()
    // 模组可以选择性地实现需要的生命周期方法
}