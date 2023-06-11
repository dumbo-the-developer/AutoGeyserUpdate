package in.godspunky.autogeyserupdate;

import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.plugin.Plugin;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.concurrent.TimeUnit;

public class AutoUpdate extends Plugin implements IPlugin {

    @Override
    public ProxyServer getProxy() {
        return super.getProxy();
    }


    @Override
    public void onEnable() {
        new AutoGeyserUpdate(this);
    }

    @Override
    public void broadcastMessage(String message) {
        getProxy().broadcast(message);
    }

    @Override
    public void runTaskLaterAsync(Runnable runnable, long seconds) {
        long ticks = seconds*60;
        getProxy().getScheduler().schedule(this, runnable, ticks, TimeUnit.SECONDS);
    }

    @Override
    public void restart() {
        try {
            // Use spigot's restart() method if it exists
            if (getProxy().getClass().getMethod("stop") != null) {
                getProxy().stop();
                return;
            }
        } catch (NoSuchMethodException expected) {}

        // Otherwise assume the server knows how to restart itself
        getProxy().stop();
    }

    @Override
    public File getGeyserJar() {
        Plugin geyser = getProxy().getPluginManager().getPlugin("Geyser-BungeeCord");
        if (geyser == null || !(geyser instanceof Plugin)) {
            return null;
        } else {
            // getFile() is only in JavaPlugins
            Plugin jGeyser = (Plugin) geyser;
            try {
                // getFile() method is protected in bukkit, force access to it
                Method m = jGeyser.getClass().getSuperclass().getDeclaredMethod("getFile");
                m.setAccessible(true);
                return (File) m.invoke(jGeyser);
            } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
                e.printStackTrace();
                return null;
            }
        }
    }

    @Override
    public File getPluginsDirectory() {
        // Assume the directory that this plugin is in is the plugins directory
        return getFile().getParentFile();
    }
}