package com.myththewolf.modbot.core.lib.invocation.impl;

import com.myththewolf.modbot.core.lib.logging.Loggable;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;

/**
 * This is a custom  URL Class Loader so we can load plugins and keep them secure.
 */
public class PluginClassLoader extends URLClassLoader implements Loggable {
    public PluginClassLoader(ClassLoader parent) {
        super(new URL[0], parent);
    }

    @Override
    protected void addURL(URL url) {
        super.addURL(url);
    }

    public void loadJarFile(File jar) {
        try {
            addURL(jar.toURI().toURL());
        } catch (MalformedURLException exception) {
            getLogger().error("Error while importing jar file from PluginClassLoader: {}", exception);
        }
    }

    public Class<?> getClassByName(String clazz) {

        try {
            return loadClass(clazz);
        } catch (ClassNotFoundException exception) {
            getLogger().error("Error while importing jar file from PluginClassLoader: {}", exception);
        }
        return null;
    }
}
