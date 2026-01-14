package net.Flint.modloader;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;

public class ModLoader extends URLClassLoader {
    public ModLoader(File modJarFile) throws IOException {
        super(new URL[] {modJarFile.toURI().toURL()}, ModLoader.class.getClassLoader());
    }

    @Override
    public Class<?> loadClass(String name) throws ClassNotFoundException {
        try {
            return findClass(name);
        }catch (ClassNotFoundException e) {
            return super.loadClass(name);
        }
    }
}
