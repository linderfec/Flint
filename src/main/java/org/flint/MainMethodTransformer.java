package org.flint;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;

import java.lang.instrument.ClassFileTransformer;
import java.security.ProtectionDomain;

/**
 * ClassFileTransformer 的实现
 * 在类加载时拦截目标类，修改其字节码
 */
public class MainMethodTransformer implements ClassFileTransformer {

    /** 要拦截的目标类（Minecraft 客户端主类） */
    private static final String TARGET_CLASS = "net/minecraft/client/main/Main";

    /**
     * 类加载时由 JVM 调用
     * @param loader              类加载器
     * @param className           类名（使用 / 分隔，如 net/minecraft/...）
     * @param classBeingRedefined 如果是重定义/重转换，则为被转换的类
     * @param protectionDomain    保护域
     * @param classfileBuffer     原始字节码
     * @return 修改后的字节码，返回 null 表示不修改
     */
    @Override
    public byte[] transform(ClassLoader loader, String className,
                            Class<?> classBeingRedefined,
                            ProtectionDomain protectionDomain,
                            byte[] classfileBuffer) {

        // 只处理目标类，其他类跳过
        if (!TARGET_CLASS.equals(className)) {
            return null;
        }

        System.out.println("[Flint] 拦截到目标类: " + className);

        try {
            // 读取原始字节码
            ClassReader cr = new ClassReader(classfileBuffer);
            // ClassWriter 用于生成修改后的字节码
            ClassWriter cw = new ClassWriter(cr, ClassWriter.COMPUTE_MAXS);

            // 使用自定义 ClassVisitor 遍历并修改字节码
            MainMethodClassVisitor cv = new MainMethodClassVisitor(cw);
            cr.accept(cv, ClassReader.EXPAND_FRAMES);

            // 返回修改后的字节码
            return cw.toByteArray();

        } catch (Exception e) {
            // 出错时打印堆栈，返回 null 让 JVM 使用原始字节码
            e.printStackTrace();
            return null;
        }
    }
}
