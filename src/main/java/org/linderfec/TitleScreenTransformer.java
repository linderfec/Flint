package org.linderfec;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

import java.lang.instrument.ClassFileTransformer;
import java.security.ProtectionDomain;

/**
 * ClassFileTransformer 的实现
 * 在类加载时拦截 TitleScreen 类，在标题画面左下角渲染 "Flint" 文字
 */
public class TitleScreenTransformer implements ClassFileTransformer {

    /** 要拦截的目标类（Minecraft 标题画面） */
    private static final String TARGET_CLASS = "net/minecraft/client/gui/screens/TitleScreen";

    /**
     * 类加载时由 JVM 调用
     * @param loader              类加载器
     * @param className           类名（使用 / 分隔）
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
        // 只处理 TitleScreen 类，其他跳过
        if (!TARGET_CLASS.equals(className)) {
            return null;
        }

        System.out.println("[Flint] 拦截到 TitleScreen: " + className);

        try {
            // 读取原始字节码
            ClassReader cr = new ClassReader(classfileBuffer);
            // ClassWriter 用于生成修改后的字节码
            ClassWriter cw = new ClassWriter(cr, ClassWriter.COMPUTE_MAXS);
            // 使用自定义 ClassVisitor 遍历并修改字节码
            cr.accept(new TitleScreenClassVisitor(cw), ClassReader.EXPAND_FRAMES);
            return cw.toByteArray();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 自定义 ClassVisitor
     * 在访问 TitleScreen 的方法时，拦截 extractRenderState 并注入绘制代码
     */
    private static class TitleScreenClassVisitor extends org.objectweb.asm.ClassVisitor {
        TitleScreenClassVisitor(ClassWriter cw) {
            super(Opcodes.ASM9, cw);
        }

        /**
         * 每当访问到一个方法时回调
         * 只拦截 extractRenderState(GuiGraphicsExtractor, int, int, float)
         */
        @Override
        public MethodVisitor visitMethod(int access, String name, String descriptor,
                                         String signature, String[] exceptions) {
            MethodVisitor mv = super.visitMethod(access, name, descriptor, signature, exceptions);
            if ("extractRenderState".equals(name) &&
                    "(Lnet/minecraft/client/gui/GuiGraphicsExtractor;IIF)V".equals(descriptor)) {
                System.out.println("[Flint] 找到 TitleScreen.extractRenderState，注入 Flint 文字");
                return new TitleScreenMethodVisitor(mv);
            }
            return mv;
        }
    }

    /**
     * 自定义 MethodVisitor
     * 在 extractRenderState 的 return 指令前插入绘制 "Flint" 的字节码
     */
    private static class TitleScreenMethodVisitor extends MethodVisitor {
        TitleScreenMethodVisitor(MethodVisitor mv) {
            super(Opcodes.ASM9, mv);
        }

        /**
         * 访问每一条指令，在 return 前插入 Flint 文字绘制
         */
        @Override
        public void visitInsn(int opcode) {
            if (opcode == Opcodes.RETURN) {
                injectFlintText();
            }
            super.visitInsn(opcode);
        }

        /**
         * 注入绘制 "Flint" 文字的字节码
         *
         * 生成的等效 Java 代码：
         *   extractor.text(this.getFont(), "Flint", 2, this.height - 22, -1);
         *
         * 栈操作说明：
         *   1. aload_1           — 压入 extractor（GuiGraphicsExtractor，第2个参数）
         *   2. aload_0           — 压入 this（TitleScreen 实例）
         *   3. invokevirtual     — 调用 this.getFont()，返回 Font，栈：[extractor, font]
         *   4. ldc "Flint"       — 压入字符串常量，栈：[extractor, font, "Flint"]
         *   5. iconst_2          — 压入 x=2，栈：[extractor, font, "Flint", 2]
         *   6. aload_0           — 压入 this
         *   7. getfield height   — 读取 this.height，栈：[extractor, font, "Flint", 2, height]
         *   8. bipush 22         — 压入 22
         *   9. isub              — height - 22，栈：[extractor, font, "Flint", 2, height-22]
         *  10. iconst_m1         — 压入 -1（白色），栈：[extractor, font, "Flint", 2, height-22, -1]
         *  11. invokevirtual     — 调用 extractor.text(font, "Flint", 2, height-22, -1)
         */
        private void injectFlintText() {
            // extractor（aload_1 = GuiGraphicsExtractor 参数）
            mv.visitVarInsn(Opcodes.ALOAD, 1);

            // this.getFont()
            mv.visitVarInsn(Opcodes.ALOAD, 0);
            mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL,
                    "net/minecraft/client/gui/screens/TitleScreen", "getFont",
                    "()Lnet/minecraft/client/gui/Font;", false);

            // 字符串 "Flint"
            mv.visitLdcInsn("Flint");

            // x = 2（左边距 2 像素）
            mv.visitInsn(Opcodes.ICONST_2);

            // this.height - 22（底部向上 22 像素，位于版权文字上方）
            mv.visitVarInsn(Opcodes.ALOAD, 0);
            mv.visitFieldInsn(Opcodes.GETFIELD,
                    "net/minecraft/client/gui/screens/Screen", "height", "I");
            mv.visitIntInsn(Opcodes.BIPUSH, 22);
            mv.visitInsn(Opcodes.ISUB);

            // 颜色 = -1（白色，0xFFFFFFFF）
            mv.visitInsn(Opcodes.ICONST_M1);

            // 调用 GuiGraphicsExtractor.text(Font, String, int, int, int)
            mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL,
                    "net/minecraft/client/gui/GuiGraphicsExtractor", "text",
                    "(Lnet/minecraft/client/gui/Font;Ljava/lang/String;III)V", false);
        }
    }
}
