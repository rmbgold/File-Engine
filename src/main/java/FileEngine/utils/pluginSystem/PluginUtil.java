package FileEngine.utils.pluginSystem;

import FileEngine.IsDebug;
import FileEngine.annotation.EventRegister;
import FileEngine.configs.AllConfigs;
import FileEngine.utils.EventUtil;
import FileEngine.eventHandler.Event;
import FileEngine.eventHandler.EventHandler;
import FileEngine.eventHandler.impl.plugin.*;
import FileEngine.eventHandler.impl.taskbar.ShowTaskBarMessageEvent;
import FileEngine.utils.CachedThreadPoolUtil;
import com.alibaba.fastjson.JSONObject;

import java.io.*;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class PluginUtil {
    private final Set<PluginInfo> pluginInfoSet = ConcurrentHashMap.newKeySet();
    private final Set<String> NOT_LATEST_PLUGINS = ConcurrentHashMap.newKeySet();
    private final HashSet<String> OLD_API_PLUGINS = new HashSet<>();
    private final HashSet<String> REPEAT_PLUGINS = new HashSet<>();
    private final HashSet<String> LOAD_ERROR_PLUGINS = new HashSet<>();
    private final PluginInfo nullPluginInfo = new PluginInfo(null, "", "");

    private static volatile PluginUtil INSTANCE = null;

    public static PluginUtil getInstance() {
        if (INSTANCE == null) {
            synchronized (PluginUtil.class) {
                if (INSTANCE == null) {
                    INSTANCE = new PluginUtil();
                }
            }
        }
        return INSTANCE;
    }

    private void checkPluginMessageThread() {
        CachedThreadPoolUtil.getInstance().executeTask(() -> {
            try {
                String[] message;
                Plugin plugin;
                EventUtil eventUtil = EventUtil.getInstance();
                while (eventUtil.isNotMainExit()) {
                    for (PluginInfo each : pluginInfoSet) {
                        plugin = each.plugin;
                        message = plugin.getMessage();
                        if (message != null) {
                            eventUtil.putEvent(new ShowTaskBarMessageEvent(message[0], message[1]));
                        }
                    }
                    TimeUnit.MILLISECONDS.sleep(50);
                }
            } catch (InterruptedException ignored) {
            }
        });
    }

    private PluginUtil() {
        checkPluginMessageThread();
    }

    public boolean isPluginTooOld() {
        return !OLD_API_PLUGINS.isEmpty();
    }

    public boolean isPluginRepeat() {
        return !REPEAT_PLUGINS.isEmpty();
    }

    public boolean isPluginLoadError() {
        return !LOAD_ERROR_PLUGINS.isEmpty();
    }

    public boolean isPluginsNotLatest(String pluginName) {
        return NOT_LATEST_PLUGINS.contains(pluginName);
    }

    private void removeFromPluginsCanUpdate(String pluginName) {
        NOT_LATEST_PLUGINS.remove(pluginName);
    }

    private void addPluginsCanUpdate(String pluginName) {
        NOT_LATEST_PLUGINS.add(pluginName);
    }

    public String getLoadingErrorPlugins() {
        StringBuilder strb = new StringBuilder();
        for (String each : LOAD_ERROR_PLUGINS) {
            strb.append(each).append(",");
        }
        return strb.substring(0, strb.length() - 1);
    }

    public String getRepeatPlugins() {
        StringBuilder strb = new StringBuilder();
        for (String repeatPlugins : REPEAT_PLUGINS) {
            strb.append(repeatPlugins).append(",");
        }
        return strb.substring(0, strb.length() - 1);
    }

    public PluginInfo getPluginInfoByName(String name) {
        for (PluginInfo each : pluginInfoSet) {
            if (each.name.equals(name)) {
                return each;
            }
        }
        return nullPluginInfo;
    }

    public PluginInfo getPluginInfoByIdentifier(String identifier) {
        for (PluginInfo each : pluginInfoSet) {
            if (each.identifier.equals(identifier)) {
                return each;
            }
        }
        return nullPluginInfo;
    }

    public int getInstalledPluginNum() {
        return pluginInfoSet.size();
    }

    public String getAllOldPluginsName() {
        StringBuilder strb = new StringBuilder();
        for (String oldPlugin : OLD_API_PLUGINS) {
            strb.append(oldPlugin).append(",");
        }
        return strb.substring(0, strb.length() - 1);
    }

    private String readAll(InputStream inputStream) {
        StringBuilder strb = new StringBuilder();
        String line;
        try (BufferedReader buffr = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {
            while ((line = buffr.readLine()) != null) {
                strb.append(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return strb.toString();
    }

    private void unloadPlugin(Plugin plugin) {
        if (plugin != null) {
            plugin.unloadPlugin();
        }
    }

    private boolean loadPlugin(File pluginFile, String className, String identifier, String pluginName) throws Exception {
        boolean isTooOld = false;
        URLClassLoader classLoader = new URLClassLoader(
                new URL[]{pluginFile.toURI().toURL()},
                PluginUtil.class.getClassLoader()
        );
        Class<?> c = Class.forName(className, true, classLoader);
        Object instance = c.getDeclaredConstructor().newInstance();
        PluginClassAndInstanceInfo pluginClassAndInstanceInfo = new PluginClassAndInstanceInfo(c, instance);
        Plugin plugin = new Plugin(pluginClassAndInstanceInfo);
        plugin.loadPlugin();
        plugin.setCurrentTheme(AllConfigs.getInstance().getDefaultBackgroundColor(), AllConfigs.getInstance().getLabelColor(), AllConfigs.getInstance().getBorderColor());
        if (Plugin.getLatestApiVersion() - plugin.getApiVersion() >= 2) {
            isTooOld = true;
        }
        pluginInfoSet.add(new PluginInfo(plugin, pluginName, identifier));
        return isTooOld;
    }

    private void unloadAllPlugins() {
        for (PluginInfo each : pluginInfoSet) {
            unloadPlugin(each.plugin);
        }
    }

    private void setCurrentTheme(int defaultColor, int chosenLabelColor, int borderColor) {
        for (PluginInfo each : pluginInfoSet) {
            each.plugin.setCurrentTheme(defaultColor, chosenLabelColor, borderColor);
        }
    }

    public boolean hasPlugin(String pluginName) {
        for (PluginInfo each : pluginInfoSet) {
            if (each.name.equals(pluginName)) {
                return true;
            }
        }
        return false;
    }

    public Object[] getPluginNameArray() {
        ArrayList<String> list = new ArrayList<>();
        for (PluginInfo each : pluginInfoSet) {
            list.add(each.name);
        }
        return list.toArray();
    }

    private void loadAllPlugins(String pluginPath) {
        FilenameFilter filter = (dir, name) -> name.endsWith(".jar");
        File[] files = new File(pluginPath).listFiles(filter);
        if (files == null || files.length == 0) {
            return;
        }
        try {
            for (File jar : files) {
                JarFile jarFile = new JarFile(jar);
                Enumeration<?> enu = jarFile.entries();
                while (enu.hasMoreElements()) {
                    JarEntry element = (JarEntry) enu.nextElement();
                    String name = element.getName();
                    if ("PluginInfo.json".equals(name)) {
                        String pluginInfo = readAll(jarFile.getInputStream(element));
                        JSONObject json = JSONObject.parseObject(pluginInfo);
                        String className = json.getString("className");
                        String identifier = json.getString("identifier");
                        String pluginName = json.getString("name");
                        if (!hasPlugin(pluginName)) {
                            try {
                                boolean isPluginApiTooOld = loadPlugin(jar, className, identifier, pluginName);
                                if (isPluginApiTooOld) {
                                    OLD_API_PLUGINS.add(pluginName);
                                }
                            } catch (Exception e) {
                                LOAD_ERROR_PLUGINS.add(pluginName);
                                e.printStackTrace();
                            }
                        } else {
                            REPEAT_PLUGINS.add(jar.getName());
                        }
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void checkAllPluginsVersion(StringBuilder oldPlugins) {
        boolean isLatest;
        for (PluginInfo each : pluginInfoSet) {
            try {
                isLatest = isPluginLatest(each.plugin);
                if (IsDebug.isDebug()) {
                    System.out.println("++++++++++++++++++++++++++++++++++++++++++++");
                    System.out.println("插件：" + each.name + "已检查完毕，结果：" + isLatest);
                    System.out.println("++++++++++++++++++++++++++++++++++++++++++++");
                }
                if (!isLatest) {
                    oldPlugins.append(each.name).append(" ");
                    addPluginsCanUpdate(each.name);
                }
            } catch (Exception e) {
                if (IsDebug.isDebug()) {
                    System.err.println("++++++++++++++++++++++++++++++++++++++++++++");
                    System.err.println("插件：" + each + "检查更新失败");
                    System.err.println("++++++++++++++++++++++++++++++++++++++++++++");
                }
            }
        }
    }

    private boolean isPluginLatest(Plugin plugin) throws InterruptedException {
        AtomicLong startCheckTime = new AtomicLong();
        AtomicBoolean isVersionLatest = new AtomicBoolean();
        Thread checkUpdateThread = new Thread(() -> {
            startCheckTime.set(System.currentTimeMillis());
            try {
                isVersionLatest.set(plugin.isLatest());
                if (!Thread.interrupted()) {
                    startCheckTime.set(0x100L); //表示检查成功
                }
            } catch (Exception e) {
                e.printStackTrace();
                startCheckTime.set(0xFFFL);
            }
        });
        CachedThreadPoolUtil.getInstance().executeTask(checkUpdateThread);
        //等待获取插件更新信息
        try {
            while (startCheckTime.get() != 0x100L) {
                TimeUnit.MILLISECONDS.sleep(200);
                if ((System.currentTimeMillis() - startCheckTime.get() > 5000L && startCheckTime.get() != 0x100L) || startCheckTime.get() == 0xFFFL) {
                    checkUpdateThread.interrupt();
                    throw new InterruptedException("check update failed.");
                }
                if (!EventUtil.getInstance().isNotMainExit()) {
                    break;
                }
            }
            return isVersionLatest.get();
        } catch (InterruptedException e) {
            throw new InterruptedException("check update failed.");
        }
    }

    @EventRegister
    public static void registerEventHandler() {
        EventUtil eventUtil = EventUtil.getInstance();
        eventUtil.register(AddPluginsCanUpdateEvent.class, new EventHandler() {
            @Override
            public void todo(Event event) {
                getInstance().addPluginsCanUpdate(((AddPluginsCanUpdateEvent) event).pluginName);
            }
        });

        eventUtil.register(LoadAllPluginsEvent.class, new EventHandler() {
            @Override
            public void todo(Event event) {
                getInstance().loadAllPlugins(((LoadAllPluginsEvent) event).pluginDirPath);
            }
        });

        eventUtil.register(RemoveFromPluginsCanUpdateEvent.class, new EventHandler() {
            @Override
            public void todo(Event event) {
                getInstance().removeFromPluginsCanUpdate(((RemoveFromPluginsCanUpdateEvent) event).pluginName);
            }
        });

        eventUtil.register(SetPluginsCurrentThemeEvent.class, new EventHandler() {
            @Override
            public void todo(Event event) {
                SetPluginsCurrentThemeEvent task1 = (SetPluginsCurrentThemeEvent) event;
                getInstance().setCurrentTheme(task1.defaultColor, task1.chosenColor, task1.borderColor);
            }
        });

        eventUtil.register(UnloadAllPluginsEvent.class, new EventHandler() {
            @Override
            public void todo(Event event) {
                getInstance().unloadAllPlugins();
            }
        });
    }

    public static class PluginInfo {
        public final Plugin plugin;
        public final String name;
        public final String identifier;

        private PluginInfo(Plugin plugin, String name, String identifier) {
            this.plugin = plugin;
            this.name = name;
            this.identifier = identifier;
        }

        @Override
        public String toString() {
            return name;
        }
    }
}
