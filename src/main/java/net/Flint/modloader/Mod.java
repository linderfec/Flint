package net.Flint.modloader;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 模组注解 - 用于标识和配置Flint模组
 * 此注解应用于模组主类，提供模组的元数据信息
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface Mod {
    /**
     * 模组的唯一标识符
     * 
     * @return 模组ID字符串
     */
    String value();
    
    /**
     * 模组名称
     * 
     * @return 模组显示名称，默认为空字符串
     */
    String name() default "";
    
    /**
     * 模组版本
     * 
     * @return 模组版本字符串，默认为空字符串
     */
    String version() default "";
    
    /**
     * 模组描述
     * 
     * @return 模组描述信息，默认为空字符串
     */
    String description() default "";
}
