package org.flint;

import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

/**
 * 自定义的 MethodVisitor
 * 在方法入口和出口处注入 System.out.println 调用
 */
public class MainMethodVisitor extends MethodVisitor {

    private final String methodName;
    private final String descriptor;

    /**
     * @param mv         委托的原始 MethodVisitor
     * @param access     方法访问标志
     * @param name       方法名
     * @param descriptor 方法描述符
     */
    public MainMethodVisitor(MethodVisitor mv, int access, String name, String descriptor) {
        super(Opcodes.ASM9, mv);
        this.methodName = name;
        this.descriptor = descriptor;
    }

    /**
     * 在方法字节码的开头被调用
     * 在这里插入方法入口的打印逻辑
     */
    @Override
    public void visitCode() {
        // 在方法开头插入: System.out.println("方法被拦截！原main已被ASM修改");
        // 1. 获取 System.out 静态字段
        mv.visitFieldInsn(Opcodes.GETSTATIC, "java/lang/System", "out", "Ljava/io/PrintStream;");
        // 2. 将待打印的字符串常量压入栈
        mv.visitLdcInsn("方法被拦截！原main已被ASM修改");
        // 3. 调用 PrintStream.println(String) 方法
        mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/io/PrintStream", "println",
                "(Ljava/lang/String;)V", false);

        // 调用父类方法，继续处理原始方法字节码（必须调用，否则原逻辑丢失）
        super.visitCode();
    }

    /**
     * 访问每一条指令
     * 在 return 指令前插入方法结束的打印逻辑
     */
    @Override
    public void visitInsn(int opcode) {
        // 判断是否为返回指令（包括 void、int、long、float、double、引用类型的返回）
        if (opcode == Opcodes.RETURN || opcode == Opcodes.IRETURN ||
                opcode == Opcodes.LRETURN || opcode == Opcodes.FRETURN ||
                opcode == Opcodes.DRETURN || opcode == Opcodes.ARETURN) {

            // 在 return 之前插入: System.out.println("main方法执行完毕");
            mv.visitFieldInsn(Opcodes.GETSTATIC, "java/lang/System", "out", "Ljava/io/PrintStream;");
            mv.visitLdcInsn("main方法执行完毕");
            mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/io/PrintStream", "println",
                    "(Ljava/lang/String;)V", false);
        }

        // 继续处理原始指令
        super.visitInsn(opcode);
    }
}
