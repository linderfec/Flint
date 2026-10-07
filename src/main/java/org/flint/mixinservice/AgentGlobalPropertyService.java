package org.flint.mixinservice;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.spongepowered.asm.service.IGlobalPropertyService;
import org.spongepowered.asm.service.IPropertyKey;

/**
 * Agent 环境下的 Mixin 全局属性服务（blackboard）。
 *
 * <p>Mixin 启动时会用它保存 {@code mixin.initialised}、{@code mixin.platform}
 * 等全局状态。内置实现需要 launchwrapper 的 {@code Launch.blackboard}，
 * 这里换成一个简单的并发 Map。</p>
 */
public class AgentGlobalPropertyService implements IGlobalPropertyService {

    private final Map<String, Object> store = new ConcurrentHashMap<String, Object>();

    private final Map<String, IPropertyKey> keys = new ConcurrentHashMap<String, IPropertyKey>();

    @Override
    public IPropertyKey resolveKey(String name) {
        IPropertyKey key = this.keys.get(name);
        if (key == null) {
            IPropertyKey created = new AgentPropertyKey(name);
            key = this.keys.putIfAbsent(name, created);
            if (key == null) {
                key = created;
            }
        }
        return key;
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T getProperty(IPropertyKey key) {
        return (T)this.store.get(key.toString());
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T getProperty(IPropertyKey key, T defaultValue) {
        T value = (T)this.store.get(key.toString());
        return value != null ? value : defaultValue;
    }

    @Override
    public String getPropertyString(IPropertyKey key, String defaultValue) {
        Object value = this.store.get(key.toString());
        return value != null ? value.toString() : defaultValue;
    }

    @Override
    public void setProperty(IPropertyKey key, Object value) {
        this.store.put(key.toString(), value);
    }

    private static final class AgentPropertyKey implements IPropertyKey {

        private final String name;

        AgentPropertyKey(String name) {
            this.name = name;
        }

        @Override
        public String toString() {
            return this.name;
        }

        @Override
        public boolean equals(Object other) {
            return other instanceof AgentPropertyKey && this.name.equals(((AgentPropertyKey)other).name);
        }

        @Override
        public int hashCode() {
            return this.name.hashCode();
        }
    }
}
