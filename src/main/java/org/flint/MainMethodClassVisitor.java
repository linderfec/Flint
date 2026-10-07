package org.flint;

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

/**
 * 自定义的 ClassVisitor
 * 在访问类的方法时，拦截目标 main 方法并注入自定义字节码
 */
public class MainMethodClassVisitor extends ClassVisitor {

    /**
     * @param cv 委托的下一个 ClassVisitor（通常是 ClassWriter）
     */
    public MainMethodClassVisitor(ClassVisitor cv) {
        super(Opcodes.ASM9, cv);
    }

    /**
     * 每当访问到一个方法时回调
     * @param access     方法的访问标志（public/static 等）
     * @param name       方法名
     * @param descriptor 方法描述符（参数类型和返回值类型）
     * @param signature  泛型签名（可能为 null）
     * @param exceptions 抛出的异常类型（可能为 null）
     * @return 自定义的 MethodVisitor，用于修改该方法字节码
     */
    @Override
    public MethodVisitor visitMethod(int access, String name, String descriptor,
                                     String signature, String[] exceptions) {

        // 匹配 main 方法: public static void main(String[])
        if ("main".equals(name) && "([Ljava/lang/String;)V".equals(descriptor)) {
            System.out.println("[Flint] 找到 main 方法，开始修改字节码");

            // 获取原始的 MethodVisitor
            MethodVisitor mv = super.visitMethod(access, name, descriptor, signature, exceptions);
            // 返回我们自定义的 MethodVisitor，在原始逻辑前后注入代码
            return new MainMethodVisitor(mv, access, name, descriptor);
        }

        // 非目标方法，不做修改
        return super.visitMethod(access, name, descriptor, signature, exceptions);
    }
}
