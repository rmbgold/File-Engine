package FileEngine.frames;

import FileEngine.IsDebug;
import FileEngine.annotation.EventRegister;
import FileEngine.configs.AllConfigs;
import FileEngine.configs.ConfigEntity;
import FileEngine.configs.Enums;
import FileEngine.eventHandler.Event;
import FileEngine.eventHandler.EventHandler;
import FileEngine.eventHandler.impl.SetSwingLaf;
import FileEngine.eventHandler.impl.configs.AddCmdEvent;
import FileEngine.eventHandler.impl.configs.DeleteCmdEvent;
import FileEngine.eventHandler.impl.configs.SaveConfigsEvent;
import FileEngine.eventHandler.impl.configs.SetConfigsEvent;
import FileEngine.eventHandler.impl.database.*;
import FileEngine.eventHandler.impl.download.StartDownloadEvent;
import FileEngine.eventHandler.impl.download.StopDownloadEvent;
import FileEngine.eventHandler.impl.frame.pluginMarket.ShowPluginMarket;
import FileEngine.eventHandler.impl.frame.searchBar.HideSearchBarEvent;
import FileEngine.eventHandler.impl.frame.searchBar.PreviewSearchBarEvent;
import FileEngine.eventHandler.impl.frame.settingsFrame.HideSettingsFrameEvent;
import FileEngine.eventHandler.impl.frame.settingsFrame.ShowSettingsFrameEvent;
import FileEngine.eventHandler.impl.hotkey.ResponseCtrlEvent;
import FileEngine.eventHandler.impl.plugin.AddPluginsCanUpdateEvent;
import FileEngine.utils.*;
import FileEngine.utils.database.DatabaseUtil;
import FileEngine.utils.database.SQLiteUtil;
import FileEngine.utils.download.DownloadManager;
import FileEngine.utils.moveFiles.MoveDesktopFiles;
import FileEngine.utils.pluginSystem.Plugin;
import FileEngine.utils.pluginSystem.PluginUtil;
import com.alibaba.fastjson.JSONObject;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.filechooser.FileSystemView;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;


@SuppressWarnings("unused")
public class SettingsFrame {
    private final Set<String> cacheSet = ConcurrentHashMap.newKeySet();
    private static volatile int tmp_copyPathKeyCode;
    private static volatile int tmp_runAsAdminKeyCode;
    private static volatile int tmp_openLastFolderKeyCode;
    private static volatile boolean isStartupChanged = false;
    private static final ImageIcon frameIcon = new ImageIcon(SettingsFrame.class.getResource("/icons/frame.png"));
    private static final JFrame frame = new JFrame("Settings");
    private static final TranslateUtil translateUtil = TranslateUtil.getInstance();
    private static final EventUtil eventUtil = EventUtil.getInstance();
    private static final AllConfigs allConfigs = AllConfigs.getInstance();
    private static final CachedThreadPoolUtil cachedThreadPoolUtil = CachedThreadPoolUtil.getInstance();
    private final HashMap<String, Integer> tabComponentIndexMap = new HashMap<>();
    private final HashMap<String, Integer> suffixMap = new HashMap<>();
    private JTextField textFieldUpdateInterval;
    private JTextField textFieldCacheNum;
    private JTextArea textAreaIgnorePath;
    private JCheckBox checkBoxAddToStartup;
    private JLabel labelSetIgnorePathTip;
    private JLabel labelUpdateInterval;
    private JLabel labelOpenSearchBarHotKey;
    private JLabel labelMaxCacheNum;
    private JLabel labelSecond;
    private JPanel panel;
    private JLabel labelConstIgnorePathTip;
    private JButton buttonSaveAndRemoveDesktop;
    private JButton buttonChooseFile;
    private JScrollPane scrollpaneIgnorePath;
    private JTextField textFieldHotkey;
    private JLabel labeltipPriorityFolder;
    private JTextField textFieldPriorityFolder;
    private JButton ButtonPriorityFolder;
    private JTabbedPane tabbedPane;
    private JCheckBox checkBoxAdmin;
    private JCheckBox checkBoxLoseFocus;
    private JLabel labelRunAsAdminHotKey;
    private JTextField textFieldRunAsAdminHotKey;
    private JLabel labelOpenFolderHotKey;
    private JTextField textFieldOpenLastFolder;
    private JButton buttonAddCMD;
    private JButton buttonDelCmd;
    private JScrollPane scrollPaneCmd;
    private JList<Object> listCmds;
    private JLabel labelAbout;
    private JLabel labelAboutGithub;
    private JLabel labelGitHubTip;
    private JLabel labelIcon;
    private JLabel labelGithubIssue;
    private JButton buttonCheckUpdate;
    private JLabel labelCopyPathHotKey;
    private JTextField textFieldCopyPath;
    private JLabel labelVersion;
    private JPanel tabGeneral;
    private JPanel tabSearchSettings;
    private JPanel tabSearchBarSettings;
    private JPanel tabHotKey;
    private JPanel tabCommands;
    private JTextField textFieldTransparency;
    private JLabel labelTransparency;
    private JPanel tabColorSettings;
    private JLabel labelColorTip;
    private JTextField textFieldBackgroundDefault;
    private JTextField textFieldFontColorWithCoverage;
    private JTextField textFieldLabelColor;
    private JLabel labelLabelColor;
    private JLabel labelFontColor;
    private JLabel labelDefaultColor;
    private JLabel labelSharpSign1;
    private JLabel labelSharpSign2;
    private JLabel labelSharp4;
    private JButton buttonResetColor;
    private JTextField textFieldFontColor;
    private JLabel labelSharpSign5;
    private JLabel labelNotChosenFontColor;
    private JLabel labelColorChooser;
    private JLabel FontColorWithCoverageChooser;
    private JLabel defaultBackgroundChooser;
    private JLabel FontColorChooser;
    private JLabel labelSearchBarColor;
    private JLabel labelSharp8;
    private JTextField textFieldSearchBarColor;
    private JLabel searchBarColorChooser;
    private JLabel labelCmdTip2;
    private JLabel labelDescription;
    private JLabel labelWebLookAndFeel;
    private JLabel labelFastJson;
    private JLabel labelJna;
    private JPanel tabLanguage;
    private JList<Object> listLanguage;
    private JLabel labelLanguageChooseTip;
    private JLabel labelPlaceHolder4;
    private JLabel labelTranslationTip;
    private JLabel labelPlaceHolder1;
    private JLabel labelPlaceHolder5;
    private JLabel labelPlaceHolderWhatever;
    private JPanel tabPlugin;
    private JLabel labelInstalledPluginNum;
    private JLabel labelPluginNum;
    private JPanel PluginListPanel;
    private JPanel PluginSettingsPanel;
    private JList<Object> listPlugins;
    private JLabel PluginIconLabel;
    private JLabel PluginNamelabel;
    private JTextArea textAreaDescription;
    private JLabel labelPluginVersion;
    private JButton buttonUpdatePlugin;
    private JPanel tabAbout;
    private JScrollPane scrollPane;
    private JLabel labelSQLite;
    private JButton buttonPluginMarket;
    private JLabel labelAuthor;
    private JLabel labelOfficialSite;
    private JLabel labelDownloadProgress;
    private JLabel labelProgress;
    private JButton buttonVacuum;
    private JLabel labelVacuumTip;
    private JPanel tabProxy;
    private JRadioButton radioButtonNoProxy;
    private JRadioButton radioButtonUseProxy;
    private JTextField textFieldAddress;
    private JTextField textFieldPort;
    private JTextField textFieldUserName;
    private JTextField textFieldPassword;
    private JLabel labelAddress;
    private JLabel labelPort;
    private JLabel labelUserName;
    private JLabel labelPassword;
    private JLabel labelPlaceHolder7;
    private JRadioButton radioButtonProxyTypeHttp;
    private JRadioButton radioButtonProxyTypeSocks5;
    private JLabel labelProxyTip;
    private JLabel labelPlaceHolder8;
    private JLabel labelVacuumStatus;
    private JLabel labelApiVersion;
    private JLabel placeHolder1;
    private JLabel placeHolder2;
    private JLabel placeHolder4;
    private JLabel placeHolder6;
    private JLabel placeHolder7;
    private JComboBox<String> chooseUpdateAddress;
    private JLabel chooseUpdateAddressLabel;
    private JLabel labelPlaceHolderWhatever2;
    private JPanel tabCache;
    private JLabel labelCacheSettings;
    private JLabel labelCacheTip;
    private JList<Object> listCache;
    private JLabel placeholderCache0;
    private JButton buttonDeleteCache;
    private JScrollPane cacheScrollPane;
    private JLabel labelVacuumTip2;
    private JLabel labelCacheTip2;
    private JButton buttonDeleteAllCache;
    private JLabel labelSearchBarFontColor;
    private JLabel labelSharp9;
    private JTextField textFieldSearchBarFontColor;
    private JLabel SearchBarFontColorChooser;
    private JLabel labelBorderColor;
    private JLabel labelSharp2;
    private JTextField textFieldBorderColor;
    private JLabel borderColorChooser;
    private JLabel labelCurrentCacheNum;
    private JLabel labelUninstallPluginTip;
    private JLabel labelUninstallPluginTip2;
    private JCheckBox checkBoxIsShowTipOnCreatingLnk;
    private JLabel labelPlaceHolder15;
    private JLabel labelPlaceHolder12;
    private JList<Object> listSwingThemes;
    private JScrollPane paneSwingThemes;
    private JButton buttonChangeTheme;
    private JLabel labelRemoveDesktop;
    private JLabel labelPlaceHolder2;
    private JLabel labelPlaceHolder3;
    private JScrollPane paneListPlugins;
    private JScrollPane paneListLanguage;
    private JButton buttonPreviewColor;
    private JButton buttonClosePreview;
    private JLabel placeholder;
    private JLabel placeholder2;
    private JTextField textFieldSearchCommands;
    private JLabel labelSearchCommand;
    private JLabel placeholderN;
    private JLabel placeholderl;
    private JLabel placeholderl2;
    private JLabel placeholderGeneral0;
    private JLabel placeholderPlugins0;
    private JLabel placeholderLanguage0;
    private JLabel placeholderColorSettings0;
    private JLabel placeholdersearch0;
    private JLabel labelTinyPinyin;
    private JLabel labelLombok;
    private JPanel tabModifyPriority;
    private JScrollPane suffixScrollpane;
    private JButton buttonAddSuffix;
    private JButton buttonDeleteSuffix;
    private JTable tableSuffix;
    private JButton buttonDeleteAllSuffix;
    private JLabel labelSuffixTip;
    private JLabel labelPlaceHolder;
    private JCheckBox checkBoxResponseCtrl;
    private JScrollPane tabGeneralScrollpane;
    private JScrollPane tabGeneralScrollPane;
    private JPanel tabGeneralPane;

    private static volatile SettingsFrame instance = null;


    public static SettingsFrame getInstance() {
        if (instance == null) {
            synchronized (SettingsFrame.class) {
                if (instance == null) {
                    instance = new SettingsFrame();
                }
            }
        }
        return instance;
    }

    private void addWindowCloseListener() {
        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                String errors = saveChanges();
                if (!errors.isEmpty()) {
                    int ret = JOptionPane.showConfirmDialog(null,
                            translateUtil.getTranslation("Errors") + ":\n" + errors + "\n" +
                            translateUtil.getTranslation("Failed to save settings, do you still close the window"));
                    if (ret == JOptionPane.YES_OPTION) {
                        hideFrame();
                    }
                } else {
                    hideFrame();
                }
            }
        });
    }

    private void addCheckBoxStartupListener() {
        checkBoxAddToStartup.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                isStartupChanged = true;
            }
        });
    }

    private void addButtonRemoveDesktopListener() {
        buttonSaveAndRemoveDesktop.addActionListener(e -> {
            String currentFolder = new File("").getAbsolutePath();
            if (currentFolder.equals(FileSystemView.getFileSystemView().getHomeDirectory().getAbsolutePath()) || "C:\\Users\\Public\\Desktop".equals(currentFolder)) {
                JOptionPane.showMessageDialog(frame, translateUtil.getTranslation("The program is detected on the desktop and cannot be moved"));
                return;
            }
            int isConfirmed = JOptionPane.showConfirmDialog(frame, translateUtil.getTranslation("Whether to remove and backup all files on the desktop," +
                    "they will be in the program's Files folder, which may take a few minutes"));
            if (isConfirmed == JOptionPane.YES_OPTION) {
                cachedThreadPoolUtil.executeTask(MoveDesktopFiles::start);
            }
        });
    }

    private void addFileChooserButtonListener() {
        buttonChooseFile.addActionListener(e -> {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
            int returnValue = fileChooser.showDialog(new JLabel(), translateUtil.getTranslation("Choose"));
            File file = fileChooser.getSelectedFile();
            if (file != null && returnValue == JFileChooser.APPROVE_OPTION) {
                textAreaIgnorePath.append(file.getAbsolutePath() + ",\n");
            }
        });
    }

    private void addTextFieldListener() {
        textFieldHotkey.addKeyListener(new KeyListener() {
            boolean reset = true;

            @Override
            public void keyTyped(KeyEvent e) {
            }

            @Override
            public void keyPressed(KeyEvent e) {
                int key = e.getKeyCode();
                if (reset) {
                    textFieldHotkey.setText(null);
                    reset = false;
                }
                textFieldHotkey.setCaretPosition(textFieldHotkey.getText().length());
                if (key == 17) {
                    if (!textFieldHotkey.getText().contains("Ctrl + ")) {
                        textFieldHotkey.setText(textFieldHotkey.getText() + "Ctrl + ");
                    }
                } else if (key == 18) {
                    if (!textFieldHotkey.getText().contains("Alt + ")) {
                        textFieldHotkey.setText(textFieldHotkey.getText() + "Alt + ");
                    }
                } else if (key == 524) {
                    if (!textFieldHotkey.getText().contains("Win + ")) {
                        textFieldHotkey.setText(textFieldHotkey.getText() + "Win + ");
                    }
                } else if (key == 16) {
                    if (!textFieldHotkey.getText().contains("Shift + ")) {
                        textFieldHotkey.setText(textFieldHotkey.getText() + "Shift + ");
                    }
                } else if (64 < key && key < 91) {
                    String txt = textFieldHotkey.getText();
                    if (!txt.isEmpty()) {
                        char c1 = Character.toUpperCase(txt.charAt(txt.length() - 1));
                        if (64 < c1 && c1 < 91) {
                            String text = txt.substring(0, txt.length() - 1);
                            textFieldHotkey.setText(text + (char) key);
                        } else {
                            textFieldHotkey.setText(txt + (char) key);
                        }
                    }
                    if (txt.length() == 1) {
                        textFieldHotkey.setText(null);
                    }
                }
            }

            @Override
            public void keyReleased(KeyEvent e) {
                reset = true;
            }
        });
    }

    private void addTextFieldRunAsAdminListener() {
        textFieldRunAsAdminHotKey.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                int code = e.getKeyCode();
                if (code == 17) {
                    textFieldRunAsAdminHotKey.setText("Ctrl + Enter");
                    tmp_runAsAdminKeyCode = 17;
                } else if (code == 16) {
                    textFieldRunAsAdminHotKey.setText("Shift + Enter");
                    tmp_runAsAdminKeyCode = 16;
                } else if (code == 18) {
                    textFieldRunAsAdminHotKey.setText("Alt + Enter");
                    tmp_runAsAdminKeyCode = 18;
                }
            }
        });
    }

    private void addPriorityFileChooserListener() {
        ButtonPriorityFolder.addActionListener(e -> {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
            int returnValue = fileChooser.showDialog(new JLabel(), translateUtil.getTranslation("Choose"));
            File file = fileChooser.getSelectedFile();
            if (returnValue == JFileChooser.APPROVE_OPTION) {
                textFieldPriorityFolder.setText(file.getAbsolutePath());
            }
        });
    }

    private void addPriorityTextFieldListener() {
        textFieldPriorityFolder.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    textFieldPriorityFolder.setText(null);
                }
            }
        });
    }

    private void addTextFieldOpenLastFolderListener() {
        textFieldOpenLastFolder.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                int code = e.getKeyCode();
                if (code == 17) {
                    textFieldOpenLastFolder.setText("Ctrl + Enter");
                    tmp_openLastFolderKeyCode = 17;
                } else if (code == 16) {
                    textFieldOpenLastFolder.setText("Shift + Enter");
                    tmp_openLastFolderKeyCode = 16;
                } else if (code == 18) {
                    textFieldOpenLastFolder.setText("Alt + Enter");
                    tmp_openLastFolderKeyCode = 18;
                }
            }
        });
    }

    private void addButtonCMDListener() {
        buttonAddCMD.addActionListener(e -> {
            String name = JOptionPane.showInputDialog(translateUtil.getTranslation("Please enter the ID of the command, then you can enter \": identifier\" in the search box to execute the command directly"));
            if (name == null || name.isEmpty()) {
                //未输入
                return;
            }
            if ("update".equalsIgnoreCase(name) || "clearbin".equalsIgnoreCase(name) ||
                    "help".equalsIgnoreCase(name) || "version".equalsIgnoreCase(name) || isRepeatCommand(name)) {
                JOptionPane.showMessageDialog(frame, translateUtil.getTranslation("Conflict with existing commands"));
                return;
            }
            if (name.length() == 1) {
                int ret = JOptionPane.showConfirmDialog(frame,
                        translateUtil.getTranslation("The identifier you entered is too short, continue") + "?");
                if (ret != JOptionPane.OK_OPTION) {
                    return;
                }
            }
            String cmd;
            JOptionPane.showMessageDialog(frame, translateUtil.getTranslation("Please select the location of the executable file (a folder is also acceptable)"));
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
            int returnValue = fileChooser.showDialog(new Label(), translateUtil.getTranslation("Choose"));
            if (returnValue == JFileChooser.APPROVE_OPTION) {
                cmd = fileChooser.getSelectedFile().getAbsolutePath();
                AddCmdEvent addCmdEvent = new AddCmdEvent(":" + name + ";" + cmd);
                eventUtil.putEvent(addCmdEvent);
                eventUtil.waitForEvent(addCmdEvent);
                listCmds.setListData(allConfigs.getCmdSet().toArray());
            }
        });
    }

    private void addButtonDelCMDListener() {
        buttonDelCmd.addActionListener(e -> {
            String del = (String) listCmds.getSelectedValue();
            if (del != null) {
                DeleteCmdEvent deleteCmdEvent = new DeleteCmdEvent(del);
                eventUtil.putEvent(deleteCmdEvent);
                eventUtil.waitForEvent(deleteCmdEvent);
                listCmds.setListData(allConfigs.getCmdSet().toArray());
            }

        });
    }

    private void addGitHubLabelListener() {
        labelAboutGithub.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                Desktop desktop;
                if (Desktop.isDesktopSupported()) {
                    desktop = Desktop.getDesktop();
                    try {
                        URI uri = new URI("https://github.com/XUANXUQAQ/File-Engine");
                        desktop.browse(uri);
                    } catch (Exception ignored) {

                    }
                }
            }
        });
    }

    private void addCheckForUpdateButtonListener() {
        AtomicBoolean isDownloadStarted = new AtomicBoolean(false);
        var downloadManager = new Object() {
            DownloadManager downloadManager;
        };

        buttonCheckUpdate.addActionListener(e -> {
            if (isDownloadStarted.get()) {
                eventUtil.putEvent(new StopDownloadEvent(downloadManager.downloadManager));
            } else {
                //开始下载
                JSONObject updateInfo;
                String latestVersion;
                try {
                    updateInfo = allConfigs.getUpdateInfo();
                    if (updateInfo != null) {
                        latestVersion = updateInfo.getString("version");
                    } else {
                        throw new IOException("failed");
                    }
                } catch (IOException e1) {
                    JOptionPane.showMessageDialog(frame, translateUtil.getTranslation("Check update failed"));
                    return;
                }
                if (Double.parseDouble(latestVersion) > Double.parseDouble(AllConfigs.version)) {
                    String description = updateInfo.getString("description");
                    int result = JOptionPane.showConfirmDialog(frame,
                            translateUtil.getTranslation(
                                    "New Version available") + latestVersion + "," +
                                    translateUtil.getTranslation("Whether to update") + "\n" +
                                    translateUtil.getTranslation("update content") + "\n" + description);
                    if (result == JOptionPane.YES_OPTION) {
                        //开始更新,下载更新文件到tmp
                        String urlChoose;
                        String fileName;
                        urlChoose = "url64";
                        fileName = AllConfigs.FILE_NAME;
                        downloadManager.downloadManager = new DownloadManager(
                                updateInfo.getString(urlChoose),
                                fileName,
                                new File("tmp").getAbsolutePath()
                        );
                        eventUtil.putEvent(new StartDownloadEvent(downloadManager.downloadManager));
                        cachedThreadPoolUtil.executeTask(
                                () -> SetDownloadProgress.setProgress(labelDownloadProgress,
                                        buttonCheckUpdate,
                                        downloadManager.downloadManager,
                                        isDownloadStarted,
                                        new File("user/update"),
                                        "",
                                        null,
                                        null));
                        //更新button为取消
                        buttonCheckUpdate.setText(translateUtil.getTranslation("Cancel"));
                    }
                } else {
                    JOptionPane.showMessageDialog(frame,
                            translateUtil.getTranslation("Latest version:") + latestVersion + "\n" +
                                    translateUtil.getTranslation("The current version is the latest"));
                }
            }
        });
    }

    private void addTextFieldCopyPathListener() {
        textFieldCopyPath.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                int code = e.getKeyCode();
                if (code == 17) {
                    textFieldCopyPath.setText("Ctrl + Enter");
                    tmp_copyPathKeyCode = 17;
                } else if (code == 16) {
                    textFieldCopyPath.setText("Shift + Enter");
                    tmp_copyPathKeyCode = 16;
                } else if (code == 18) {
                    textFieldCopyPath.setText("Alt + Enter");
                    tmp_copyPathKeyCode = 18;
                }
            }
        });
    }

    private void addResetColorButtonListener() {
        buttonResetColor.addActionListener(e -> {
            textFieldFontColorWithCoverage.setText(toRGBHexString(AllConfigs.defaultFontColorWithCoverage));
            textFieldSearchBarColor.setText(toRGBHexString(AllConfigs.defaultSearchbarColor));
            textFieldLabelColor.setText(toRGBHexString(AllConfigs.defaultLabelColor));
            textFieldBackgroundDefault.setText(toRGBHexString(AllConfigs.defaultWindowBackgroundColor));
            textFieldFontColor.setText(toRGBHexString(AllConfigs.defaultFontColor));
            textFieldSearchBarFontColor.setText(toRGBHexString(AllConfigs.defaultSearchbarFontColor));
            textFieldBorderColor.setText(toRGBHexString(AllConfigs.defaultBorderColor));
        });
    }

    private boolean canParseToRGB(String str) {
        if (str != null) {
            if (!str.isEmpty()) {
                return RegexUtil.rgbHexPattern.matcher(str).matches();
            }
        }
        return false;
    }

    private Color getColorFromTextFieldStr(JTextField textField) {
        String tmp;
        return canParseToRGB(tmp = textField.getText()) ? new Color(Integer.parseInt(tmp, 16)) : null;
    }

    private void setColorChooserLabel(Color color, JLabel label) {
        if (color != null) {
            label.setBackground(color);
            label.setForeground(color);
        }
    }

    private void addColorChooserLabelListener() {
        cachedThreadPoolUtil.executeTask(() -> {
            try {
                Color labelColor;
                Color fontColorWithCoverage;
                Color defaultBackgroundColor;
                Color defaultFontColor;
                Color searchBarColor;
                Color searchBarFontColor;
                Color borderColor;
                while (eventUtil.isNotMainExit()) {
                    labelColor = getColorFromTextFieldStr(textFieldLabelColor);
                    fontColorWithCoverage = getColorFromTextFieldStr(textFieldFontColorWithCoverage);
                    defaultBackgroundColor = getColorFromTextFieldStr(textFieldBackgroundDefault);
                    defaultFontColor = getColorFromTextFieldStr(textFieldFontColor);
                    searchBarColor = getColorFromTextFieldStr(textFieldSearchBarColor);
                    searchBarFontColor = getColorFromTextFieldStr(textFieldSearchBarFontColor);
                    borderColor = getColorFromTextFieldStr(textFieldBorderColor);
                    setColorChooserLabel(labelColor, labelColorChooser);
                    setColorChooserLabel(fontColorWithCoverage, FontColorWithCoverageChooser);
                    setColorChooserLabel(defaultBackgroundColor, defaultBackgroundChooser);
                    setColorChooserLabel(defaultFontColor, FontColorChooser);
                    setColorChooserLabel(searchBarColor, searchBarColorChooser);
                    setColorChooserLabel(searchBarFontColor, SearchBarFontColorChooser);
                    setColorChooserLabel(borderColor, borderColorChooser);
                    TimeUnit.MILLISECONDS.sleep(50);
                }
            } catch (InterruptedException ignored) {
            }
        });

        labelColorChooser.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                Color color = JColorChooser.showDialog(frame, translateUtil.getTranslation("Choose Color"), null);
                if (color == null) {
                    return;
                }
                textFieldLabelColor.setText(parseColorHex(color));
            }
        });
        FontColorWithCoverageChooser.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                Color color = JColorChooser.showDialog(frame, translateUtil.getTranslation("Choose Color"), null);
                if (color == null) {
                    return;
                }
                textFieldFontColorWithCoverage.setText(parseColorHex(color));
            }
        });
        defaultBackgroundChooser.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                Color color = JColorChooser.showDialog(frame, translateUtil.getTranslation("Choose Color"), null);
                if (color == null) {
                    return;
                }
                textFieldBackgroundDefault.setText(parseColorHex(color));
            }
        });
        FontColorChooser.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                Color color = JColorChooser.showDialog(frame, translateUtil.getTranslation("Choose Color"), null);
                if (color == null) {
                    return;
                }
                textFieldFontColor.setText(parseColorHex(color));
            }
        });

        SearchBarFontColorChooser.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                Color color = JColorChooser.showDialog(frame, translateUtil.getTranslation("Choose Color"), null);
                if (color == null) {
                    return;
                }
                textFieldSearchBarFontColor.setText(parseColorHex(color));
            }
        });

        borderColorChooser.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                Color color = JColorChooser.showDialog(frame, translateUtil.getTranslation("Choose Color"), null);
                if (color == null) {
                    return;
                }
                textFieldBorderColor.setText(parseColorHex(color));
            }
        });

        searchBarColorChooser.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                Color color = JColorChooser.showDialog(frame, translateUtil.getTranslation("Choose Color"), null);
                if (color == null) {
                    return;
                }
                textFieldSearchBarColor.setText(parseColorHex(color));
            }
        });
    }

    private void addListPluginMouseListener() {
        listPlugins.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                String pluginName = (String) listPlugins.getSelectedValue();
                if (pluginName != null) {
                    PluginUtil.PluginInfo pluginInfo = PluginUtil.getInstance().getPluginInfoByName(pluginName);
                    Plugin plugin = pluginInfo.plugin;
                    int apiVersion;
                    ImageIcon icon = plugin.getPluginIcon();
                    String description = plugin.getDescription();
                    String officialSite = plugin.getOfficialSite();
                    String version = plugin.getVersion();
                    String author = plugin.getAuthor();
                    apiVersion = plugin.getApiVersion();

                    labelPluginVersion.setText(translateUtil.getTranslation("Version") + ":" + version);
                    labelApiVersion.setText("API " + translateUtil.getTranslation("Version") + ":" + apiVersion);
                    PluginIconLabel.setIcon(icon);
                    PluginNamelabel.setText("<html><body><font size=\"+1\">" + pluginName + "</body></html>");
                    textAreaDescription.setText(description);
                    labelAuthor.setText(translateUtil.getTranslation("Author") + ":" + author);
                    labelOfficialSite.setText("<html><a href='" + officialSite + "'><font size=\"4\">" + pluginName + "</font></a></html>");
                    buttonUpdatePlugin.setVisible(true);
                    if (PluginUtil.getInstance().isPluginsNotLatest(pluginName)) {
                        Color color = new Color(51,122,183);
                        buttonUpdatePlugin.setText(translateUtil.getTranslation("Update"));
                        buttonUpdatePlugin.setBackground(color);
                    }
                }
            }
        });
    }

    private String parseColorHex(Color color) {
        int r = color.getRed();
        int g = color.getGreen();
        int b = color.getBlue();
        StringBuilder rgb = new StringBuilder();
        if (r == 0) {
            rgb.append("00");
        } else {
            rgb.append(Integer.toHexString(r));
        }
        if (g == 0) {
            rgb.append("00");
        } else {
            rgb.append(Integer.toHexString(g));
        }
        if (b == 0) {
            rgb.append("00");
        } else {
            rgb.append(Integer.toHexString(b));
        }
        return rgb.toString();
    }

    private void addPluginOfficialSiteListener() {
        labelOfficialSite.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                Desktop desktop;
                String url;
                int firstIndex;
                int lastIndex;
                if (Desktop.isDesktopSupported()) {
                    desktop = Desktop.getDesktop();
                    try {
                        String text = labelOfficialSite.getText();
                        firstIndex = text.indexOf("'");
                        lastIndex = text.lastIndexOf("'");
                        url = text.substring(firstIndex + 1, lastIndex);
                        URI uri = new URI(url);
                        desktop.browse(uri);
                    } catch (Exception ignored) {
                    }
                }
            }
        });
    }

    private void addButtonChangeThemeListener() {
        //移除显示theme框，改为弹出窗口
        tabGeneral.remove(paneSwingThemes);
        buttonChangeTheme.addActionListener(e -> JOptionPane.showMessageDialog(null, paneSwingThemes, "Theme", JOptionPane.PLAIN_MESSAGE));
    }

    private void addButtonDeleteCacheListener() {
        buttonDeleteCache.addActionListener(e -> {
            String cache = (String) listCache.getSelectedValue();
            if (cache != null) {
                eventUtil.putEvent(new DeleteFromCacheEvent(cache));
                cacheSet.remove(cache);
                listCache.setListData(cacheSet.toArray());
            }
        });
    }

    private void addButtonProxyListener() {
        radioButtonNoProxy.addActionListener(e -> {
            radioButtonProxyTypeHttp.setEnabled(false);
            radioButtonProxyTypeSocks5.setEnabled(false);
            textFieldAddress.setEnabled(false);
            textFieldPort.setEnabled(false);
            textFieldUserName.setEnabled(false);
            textFieldPassword.setEnabled(false);
        });

        radioButtonUseProxy.addActionListener(e -> {
            radioButtonProxyTypeHttp.setEnabled(true);
            radioButtonProxyTypeSocks5.setEnabled(true);
            textFieldAddress.setEnabled(true);
            textFieldPort.setEnabled(true);
            textFieldUserName.setEnabled(true);
            textFieldPassword.setEnabled(true);
            radioButtonProxyTypeHttp.setSelected(true);
        });
    }

    private void addButtonVacuumListener() {
        buttonVacuum.addActionListener(e -> {
            int ret = JOptionPane.showConfirmDialog(frame, translateUtil.getTranslation("Confirm whether to start optimizing the database?"));
            if (JOptionPane.YES_OPTION == ret) {
                Enums.DatabaseStatus status = DatabaseUtil.getInstance().getStatus();
                if (status == Enums.DatabaseStatus.NORMAL) {
                    if (IsDebug.isDebug()) {
                        System.out.println("开始优化");
                    }
                    eventUtil.putEvent(new OptimiseDatabaseEvent());
                    cachedThreadPoolUtil.executeTask(() -> {
                        //实时显示VACUUM状态
                        try {
                            DatabaseUtil instance = DatabaseUtil.getInstance();
                            while (instance.getStatus() == Enums.DatabaseStatus.VACUUM) {
                                labelVacuumStatus.setText(translateUtil.getTranslation("Optimizing..."));
                                TimeUnit.MILLISECONDS.sleep(50);
                            }
                            labelVacuumStatus.setText(translateUtil.getTranslation("Optimized"));
                            TimeUnit.SECONDS.sleep(3);
                            labelVacuumStatus.setText("");
                        } catch (InterruptedException ignored) {
                        }
                    });
                } else if (status == Enums.DatabaseStatus.MANUAL_UPDATE) {
                    JOptionPane.showMessageDialog(frame, translateUtil.getTranslation("Database is not usable yet, please wait..."));
                } else if (status == Enums.DatabaseStatus.VACUUM) {
                    JOptionPane.showMessageDialog(frame, translateUtil.getTranslation("Task is still running."));
                }
            }
        });
    }

    private void addTextFieldSearchCommandsListener() {
        class search {
            final String searchText;
            final CachedThreadPoolUtil cachedThreadPoolUtil = CachedThreadPoolUtil.getInstance();
            final HashSet<String> cmdSetTmp = new HashSet<>();

            search(String searchText) {
                this.searchText = searchText;
            }
            void doSearch() {
                cachedThreadPoolUtil.executeTask(() -> {
                    String tmp;
                    if ((tmp = searchText) == null || tmp.isEmpty()) {
                        listCmds.setListData(allConfigs.getCmdSet().toArray());
                    } else {
                        for (String each : allConfigs.getCmdSet()) {
                            if (each.toLowerCase().contains(tmp.toLowerCase())) {
                                cmdSetTmp.add(each);
                            }
                        }
                        listCmds.setListData(cmdSetTmp.toArray());
                    }
                });
            }
        }

        textFieldSearchCommands.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                new search(textFieldSearchCommands.getText()).doSearch();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                new search(textFieldSearchCommands.getText()).doSearch();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
            }
        });
    }

    private void addButtonClosePreviewListener() {
        buttonClosePreview.addActionListener(e -> eventUtil.putEvent(
                new HideSearchBarEvent()
        ));
    }

    private void addButtonPreviewListener() {
        buttonPreviewColor.addActionListener(e ->
                eventUtil.putEvent(
                        new PreviewSearchBarEvent(
                                textFieldBorderColor.getText(),
                                textFieldSearchBarColor.getText(),
                                textFieldSearchBarFontColor.getText(),
                                textFieldLabelColor.getText(),
                                textFieldFontColorWithCoverage.getText(),
                                textFieldFontColor.getText(),
                                textFieldBackgroundDefault.getText()
                                )
                )
        );
    }

    private void addButtonDeleteAllCacheListener() {
        buttonDeleteAllCache.addActionListener(e -> {
            int ret = JOptionPane.showConfirmDialog(frame,
                    translateUtil.getTranslation("The operation is irreversible. Are you sure you want to clear the cache?"));
            if (JOptionPane.YES_OPTION == ret) {
                for (String each : cacheSet) {
                    eventUtil.putEvent(new DeleteFromCacheEvent(each));
                }
                cacheSet.clear();
                listCache.setListData(cacheSet.toArray());
            }
        });
    }

    private void addButtonViewPluginMarketListener() {
        buttonPluginMarket.addActionListener(e -> eventUtil.putEvent(new ShowPluginMarket()));
    }

    private void addSwingThemePreviewListener() {
        listSwingThemes.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                String swingTheme = (String) listSwingThemes.getSelectedValue();
                eventUtil.putEvent(new SetSwingLaf(swingTheme));
            }
        });
    }

    private void addButtonDeleteAllSuffixListener() {
        buttonDeleteAllSuffix.addActionListener(e -> {
            int ret = JOptionPane.showConfirmDialog(
                    frame,
                    translateUtil.getTranslation("Are you sure to delete all the suffixes") + "?");
            if (ret == JOptionPane.YES_OPTION) {
                suffixMap.clear();
                suffixMap.put("defaultPriority", 0);
                eventUtil.putEvent(new ClearSuffixPriorityMapEvent());
                refreshPriorityTable();
            }
        });
    }

    private void refreshPriorityTable() {
        SwingUtilities.invokeLater(this::setTableGui);
    }

    private boolean isPriorityRepeat(int num) {
        for (int each : suffixMap.values()) {
            if (each == num) {
                return true;
            }
        }
        return false;
    }

    private boolean isSuffixRepeat(String suffix) {
        return suffixMap.containsKey(suffix);
    }

    private boolean checkSuffixAndPriority(String suffix, String priority, StringBuilder errMsg, boolean isSuffixChanged, boolean isPriorityChanged) {
        if (isSuffixChanged) {
            if (isSuffixRepeat(suffix)) {
                errMsg.append(translateUtil.getTranslation("Duplicate suffix, please check")).append("\n");
            }
        }
        if (isPriorityChanged){
            try {
                int _p = Integer.parseInt(priority);
                if (_p <= 0) {
                    errMsg.append(translateUtil.getTranslation("Priority num must be positive")).append("\n");
                }
                if (isPriorityRepeat(_p)) {
                    errMsg.append(translateUtil.getTranslation("Duplicate priority num, please check")).append("\n");
                }
            } catch (NumberFormatException e) {
                errMsg.append(translateUtil.getTranslation("What you entered is not a number, please try again")).append("\n");
            }
        }
        return errMsg.toString().isEmpty();
    }

    private void addTableModifySuffixListener() {
        final String[] lastSuffix = new String[1];
        AtomicInteger lastNum = new AtomicInteger();
        AtomicBoolean dontTrigger = new AtomicBoolean(false);

        DefaultTableModel model = new DefaultTableModel() {
            @Override
            public boolean isCellEditable(int row, int column) {
                return row != 0;
            }
        };
        tableSuffix.setModel(model);
        tableSuffix.getTableHeader().setReorderingAllowed(false);

        tableSuffix.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                //保存还未被修改之前的值
                int row = tableSuffix.getSelectedRow();
                int column = tableSuffix.getSelectedColumn();
                if (row != -1 && column != -1) {
                    lastSuffix[0] = String.valueOf(tableSuffix.getValueAt(row, 0));
                    lastNum.set(Integer.parseInt(String.valueOf(tableSuffix.getValueAt(row, 1))));
                }
            }
        });

        tableSuffix.getModel().addTableModelListener(e -> {
            if (dontTrigger.get()) {
                return;
            }
            int currentRow = tableSuffix.getSelectedRow();
            if (currentRow == -1) {
                return;
            }
            class restoreUtil {
                void doRestoreNum() {
                    // 恢复
                    SwingUtilities.invokeLater(() -> {
                        dontTrigger.set(true);
                        tableSuffix.setValueAt(lastNum.get(), currentRow, 1);
                        dontTrigger.set(false);
                    });
                }
                void doRestoreSuffix() {
                    SwingUtilities.invokeLater(() -> {
                        dontTrigger.set(true);
                        tableSuffix.setValueAt(lastSuffix[0], currentRow, 0);
                        dontTrigger.set(false);
                    });
                }
            }
            restoreUtil util = new restoreUtil();

            int column = tableSuffix.getSelectedColumn();
            if (column == 0) {
                //当前修改的是后缀
                String suffix = String.valueOf(tableSuffix.getValueAt(currentRow, 0));
                if (lastSuffix[0].equals(suffix)) {
                    return;
                }
                String priorityNum = String.valueOf(tableSuffix.getValueAt(currentRow, 1));
                StringBuilder errMsg = new StringBuilder();
                if (checkSuffixAndPriority(suffix, priorityNum, errMsg, true, false)) {
                    int num = Integer.parseInt(priorityNum);
                    eventUtil.putEvent(new UpdateSuffixPriorityEvent(
                            lastSuffix[0],
                            suffix,
                            num
                            )
                    );
                    suffixMap.remove(lastSuffix[0]);
                    suffixMap.put(suffix, num);
                    refreshPriorityTable();
                } else {
                    // 恢复
                    util.doRestoreSuffix();
                    JOptionPane.showMessageDialog(frame, errMsg.toString());
                }
            } else {
                //当前修改的是优先级
                String priorityNum = String.valueOf(tableSuffix.getValueAt(currentRow, 1));
                if (String.valueOf(lastNum.get()).equals(priorityNum)) {
                    return;
                }
                String suffix = String.valueOf(tableSuffix.getValueAt(currentRow, 0));
                StringBuilder errMsg = new StringBuilder();
                if (checkSuffixAndPriority(suffix, priorityNum, errMsg, false, true)) {
                    int num = Integer.parseInt(priorityNum);
                    eventUtil.putEvent(new UpdateSuffixPriorityEvent(
                            lastSuffix[0],
                            suffix,
                            num
                            )
                    );
                    suffixMap.remove(lastSuffix[0]);
                    suffixMap.put(suffix, num);
                    refreshPriorityTable();
                } else {
                    util.doRestoreNum();
                    JOptionPane.showMessageDialog(frame, errMsg.toString());
                }
            }
        });
    }

    private String getCurrentSelectedTableVal() {
        int row = tableSuffix.getSelectedRow();
        int column = tableSuffix.getSelectedColumn();
        if (row == -1 || column == -1) {
            return "";
        }
        return String.valueOf(tableSuffix.getValueAt(row, 0));
    }

    private void addButtonDeleteSuffixListener() {
        buttonDeleteSuffix.addActionListener(e -> {
            String current = getCurrentSelectedTableVal();
            if (current.isEmpty() || "defaultPriority".equals(current)) {
                return;
            }
            int ret = JOptionPane.showConfirmDialog(
                    frame,
                    translateUtil.getTranslation("Do you sure want to delete this suffix") + "  --  " + current + "?");
            if (ret == JOptionPane.YES_OPTION) {
                int rowNum = tableSuffix.getSelectedRow();
                if (rowNum != -1) {
                    String suffix = (String) tableSuffix.getValueAt(rowNum, 0);
                    suffixMap.remove(suffix);
                    eventUtil.putEvent(new DeleteFromSuffixPriorityMapEvent(suffix));
                    refreshPriorityTable();
                }
            }
        });
    }

    private void addButtonAddSuffixListener() {
        JPanel panel = new JPanel();
        JLabel labelSuffix = new JLabel(translateUtil.getTranslation("Suffix") + ":");
        JLabel labelNum = new JLabel(translateUtil.getTranslation("Priority num") + ":");
        JTextField suffixName = new JTextField();
        JTextField priorityNum = new JTextField();

        Box suffixBox = new Box(BoxLayout.X_AXIS);
        suffixBox.add(labelSuffix);
        suffixBox.add(suffixName);

        Box numBox = new Box(BoxLayout.X_AXIS);
        numBox.add(labelNum);
        numBox.add(priorityNum);

        panel.add(suffixBox);
        panel.add(numBox);
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        buttonAddSuffix.addActionListener(e -> {
            int ret = JOptionPane.showConfirmDialog(frame,
                    panel,
                    translateUtil.getTranslation("Add"),
                    JOptionPane.YES_NO_OPTION);
            if (ret == JOptionPane.YES_OPTION) {
                String suffix = suffixName.getText();
                String priorityNumTmp = priorityNum.getText();
                StringBuilder err = new StringBuilder();
                if (checkSuffixAndPriority(suffix, priorityNumTmp, err, true, true)) {
                    int num = Integer.parseInt(priorityNumTmp);
                    suffixMap.put(suffix, num);
                    eventUtil.putEvent(new AddToSuffixPriorityMapEvent(suffix, num));
                    refreshPriorityTable();
                } else {
                    JOptionPane.showMessageDialog(frame, err.toString());
                }
            }
        });
    }

    private void addButtonPluginUpdateCheckListener() {
        AtomicLong startCheckTime = new AtomicLong(0L);
        AtomicBoolean isVersionLatest = new AtomicBoolean(true);
        AtomicBoolean isSkipConfirm = new AtomicBoolean(false);
        AtomicBoolean isDownloadStarted = new AtomicBoolean(false);

        PluginUtil pluginUtil = PluginUtil.getInstance();
        var context = new Object() {
            DownloadManager downloadManager;
        };

        buttonUpdatePlugin.addActionListener(e -> {
            if (isDownloadStarted.get()) {
                eventUtil.putEvent(new StopDownloadEvent(context.downloadManager));
            } else {
                startCheckTime.set(0L);
                String pluginName = (String) listPlugins.getSelectedValue();
                Plugin plugin = PluginUtil.getInstance().getPluginInfoByName(pluginName).plugin;
                String pluginFullName = pluginName + ".jar";

                if (pluginUtil.isPluginsNotLatest(pluginName)) {
                    //已经检查过
                    isVersionLatest.set(false);
                    isSkipConfirm.set(true);
                } else {
                    Thread checkUpdateThread = new Thread(() -> {
                        startCheckTime.set(System.currentTimeMillis());
                        try {
                            isVersionLatest.set(plugin.isLatest());
                            if (!Thread.interrupted()) {
                                startCheckTime.set(0x100L); //表示检查成功
                            }
                        } catch (Exception exception) {
                            exception.printStackTrace();
                            startCheckTime.set(0xFFFL);  //表示检查失败
                        }
                    });
                    cachedThreadPoolUtil.executeTask(checkUpdateThread);
                    //等待获取插件更新信息
                    try {
                        while (startCheckTime.get() != 0x100L) {
                            TimeUnit.MILLISECONDS.sleep(200);
                            if ((System.currentTimeMillis() - startCheckTime.get() > 5000L &&
                                    startCheckTime.get() != 0x100L) ||
                                    startCheckTime.get() == 0xFFFL) {
                                checkUpdateThread.interrupt();
                                JOptionPane.showMessageDialog(frame, translateUtil.getTranslation("Check update failed"));
                                return;
                            }
                            if (!eventUtil.isNotMainExit()) {
                                return;
                            }
                        }
                    } catch (InterruptedException e1) {
                        e1.printStackTrace();
                    }
                }
                if (isVersionLatest.get()) {
                    JOptionPane.showMessageDialog(frame,
                            translateUtil.getTranslation("Latest version:") + plugin.getVersion() + "\n" +
                                    translateUtil.getTranslation("The current version is the latest"));
                    return;
                }
                if (!isSkipConfirm.get()) {
                    eventUtil.putEvent(new AddPluginsCanUpdateEvent(pluginName));
                    int ret = JOptionPane.showConfirmDialog(frame, translateUtil.getTranslation("New version available, do you want to update?"));
                    if (ret != JOptionPane.YES_OPTION) {
                        return;
                    }
                }
                //开始下载
                String url = plugin.getUpdateURL();
                context.downloadManager = new DownloadManager(
                        url,
                        pluginFullName,
                        new File("tmp", "pluginsUpdate").getAbsolutePath()
                );
                eventUtil.putEvent(new StartDownloadEvent(context.downloadManager));
                cachedThreadPoolUtil.executeTask(
                        () -> SetDownloadProgress.setProgress(labelProgress,
                        buttonUpdatePlugin,
                        context.downloadManager,
                        isDownloadStarted,
                        new File("user/updatePlugin"),
                        "",
                        null,
                        null));
            }
        });
    }

    private void setLabelGui() {
        labelAboutGithub.setText("<html><a href='https://github.com/XUANXUQAQ/File-Engine'><font size=\"4\">File-Engine</font></a></html>");
        labelWebLookAndFeel.setText("1.FlatLaf");
        labelFastJson.setText("2.FastJson");
        labelJna.setText("3.Java-Native-Access");
        labelSQLite.setText("4.SQLite-JDBC");
        labelTinyPinyin.setText("5.TinyPinyin");
        labelLombok.setText("6.Lombok");
        labelPluginNum.setText(String.valueOf(PluginUtil.getInstance().getInstalledPluginNum()));
        ImageIcon imageIcon = new ImageIcon(SettingsFrame.class.getResource("/icons/frame.png"));
        labelIcon.setIcon(imageIcon);
        labelVersion.setText(translateUtil.getTranslation("Current Version:") + AllConfigs.version);
        labelCurrentCacheNum.setText(translateUtil.getTranslation("Current Caches Num:") + DatabaseUtil.getInstance().getCacheNum());
    }

    private String toRGBHexString(int colorRGB) {
        return String.format("%06x", colorRGB);
    }

    private void setTextFieldAndTextAreaGui() {
        textFieldBackgroundDefault.setText(toRGBHexString(allConfigs.getDefaultBackgroundColor()));
        textFieldLabelColor.setText(toRGBHexString(allConfigs.getLabelColor()));
        textFieldFontColorWithCoverage.setText(toRGBHexString(allConfigs.getLabelFontColorWithCoverage()));
        textFieldTransparency.setText(String.valueOf(allConfigs.getOpacity()));
        textFieldBorderColor.setText(toRGBHexString(allConfigs.getBorderColor()));
        textFieldFontColor.setText(toRGBHexString(allConfigs.getLabelFontColor()));
        textFieldSearchBarFontColor.setText(toRGBHexString(allConfigs.getSearchBarFontColor()));
        textFieldCacheNum.setText(String.valueOf(allConfigs.getCacheNumLimit()));
        textFieldHotkey.setText(allConfigs.getHotkey());
        textFieldPriorityFolder.setText(allConfigs.getPriorityFolder());
        textFieldUpdateInterval.setText(String.valueOf(allConfigs.getUpdateTimeLimit()));
        textFieldSearchBarColor.setText(toRGBHexString(allConfigs.getSearchBarColor()));
        textFieldAddress.setText(allConfigs.getProxyAddress());
        textFieldPort.setText(String.valueOf(allConfigs.getProxyPort()));
        textFieldUserName.setText(allConfigs.getProxyUserName());
        textFieldPassword.setText(allConfigs.getProxyPassword());
        textAreaIgnorePath.setText(RegexUtil.comma.matcher(allConfigs.getIgnorePath()).replaceAll(",\n"));
        if (allConfigs.getRunAsAdminKeyCode() == 17) {
            textFieldRunAsAdminHotKey.setText("Ctrl + Enter");
        } else if (allConfigs.getRunAsAdminKeyCode() == 16) {
            textFieldRunAsAdminHotKey.setText("Shift + Enter");
        } else if (allConfigs.getRunAsAdminKeyCode() == 18) {
            textFieldRunAsAdminHotKey.setText("Alt + Enter");
        }
        if (allConfigs.getOpenLastFolderKeyCode() == 17) {
            textFieldOpenLastFolder.setText("Ctrl + Enter");
        } else if (allConfigs.getOpenLastFolderKeyCode() == 16) {
            textFieldOpenLastFolder.setText("Shift + Enter");
        } else if (allConfigs.getOpenLastFolderKeyCode() == 18) {
            textFieldOpenLastFolder.setText("Alt + Enter");
        }
        if (allConfigs.getCopyPathKeyCode() == 17) {
            textFieldCopyPath.setText("Ctrl + Enter");
        } else if (allConfigs.getCopyPathKeyCode() == 16) {
            textFieldCopyPath.setText("Shift + Enter");
        } else if (allConfigs.getCopyPathKeyCode() == 18) {
            textFieldCopyPath.setText("Alt + Enter");
        }
    }

    private void setColorChooserGui() {
        Color tmp_searchBarColor = new Color(allConfigs.getSearchBarColor());
        searchBarColorChooser.setBackground(tmp_searchBarColor);
        searchBarColorChooser.setForeground(tmp_searchBarColor);

        Color tmp_defaultBackgroundColor = new Color(allConfigs.getDefaultBackgroundColor());
        defaultBackgroundChooser.setBackground(tmp_defaultBackgroundColor);
        defaultBackgroundChooser.setForeground(tmp_defaultBackgroundColor);

        Color tmp_labelColor = new Color(allConfigs.getLabelColor());
        labelColorChooser.setBackground(tmp_labelColor);
        labelColorChooser.setForeground(tmp_labelColor);

        Color tmp_fontColorWithCoverage = new Color(allConfigs.getLabelFontColorWithCoverage());
        FontColorWithCoverageChooser.setBackground(tmp_fontColorWithCoverage);
        FontColorWithCoverageChooser.setForeground(tmp_fontColorWithCoverage);

        Color tmp_fontColor = new Color(allConfigs.getLabelFontColor());
        FontColorChooser.setBackground(tmp_fontColor);
        FontColorChooser.setForeground(tmp_fontColor);

        Color tmp_searchBarFontColor = new Color(allConfigs.getSearchBarFontColor());
        SearchBarFontColorChooser.setBackground(tmp_searchBarFontColor);
        SearchBarFontColorChooser.setForeground(tmp_searchBarFontColor);

        Color tmp_borderColor = new Color(allConfigs.getBorderColor());
        borderColorChooser.setBackground(tmp_borderColor);
        borderColorChooser.setForeground(tmp_borderColor);
    }

    private String getSuffixByValue(HashMap<String, Integer> suffixPriorityMap, int val) {
        for (String each : suffixPriorityMap.keySet()) {
            if (suffixPriorityMap.get(each) == val) {
                return each;
            }
        }
        return "";
    }

    private void setTableGui() {
        tableSuffix.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        DefaultTableModel tableModel=(DefaultTableModel) tableSuffix.getModel();    //获得表格模型
        tableModel.setRowCount(0);
        tableModel.setColumnIdentifiers(new String[] {translateUtil.getTranslation("suffix"), translateUtil.getTranslation("priority")});
        LinkedList<Integer> tmpKeySet = new LinkedList<>(suffixMap.values());
        tmpKeySet.sort(Integer::compare);
        for (int each : tmpKeySet) {
            String suffix = getSuffixByValue(suffixMap, each);
            if (suffix.isEmpty()) {
                continue;
            }
            String[] data = new String[2];
            data[0] = suffix;
            data[1] = String.valueOf(each);
            tableModel.addRow(data);
        }
    }

    private void setCheckBoxGui() {
        checkBoxLoseFocus.setSelected(allConfigs.isLoseFocusClose());
        checkBoxAddToStartup.setSelected(allConfigs.hasStartup());
        checkBoxAdmin.setSelected(allConfigs.isDefaultAdmin());
        checkBoxIsShowTipOnCreatingLnk.setSelected(allConfigs.isShowTipOnCreatingLnk());
        checkBoxResponseCtrl.setSelected(allConfigs.isResponseCtrl());
    }

    private void setListGui() {
        listCmds.setListData(allConfigs.getCmdSet().toArray());
        listLanguage.setListData(translateUtil.getLanguageSet().toArray());
        listLanguage.setSelectedValue(translateUtil.getLanguage(), true);
        Object[] plugins = PluginUtil.getInstance().getPluginNameArray();
        listPlugins.setListData(plugins);
        listCache.setListData(cacheSet.toArray());
        ArrayList<String> list = new ArrayList<>();
        for (Enums.SwingThemes each : Enums.SwingThemes.values()) {
            list.add(each.toString());
        }
        listSwingThemes.setListData(list.toArray());
        listSwingThemes.setSelectedValue(allConfigs.getSwingTheme(), true);
    }

    private void initSuffixMap() {
        try (PreparedStatement pStmt = SQLiteUtil.getPreparedStatement("SELECT * FROM priority;")) {
            ResultSet resultSet = pStmt.executeQuery();
            while (resultSet.next()) {
                suffixMap.put(resultSet.getString("SUFFIX"), resultSet.getInt("PRIORITY"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void initGUI() {
        //设置窗口显示
        setLabelGui();
        setListGui();
        setColorChooserGui();
        setTextFieldAndTextAreaGui();
        setCheckBoxGui();
        setTableGui();

        buttonUpdatePlugin.setVisible(false);

        if (allConfigs.getProxyType() == Enums.ProxyType.PROXY_DIRECT) {
            radioButtonNoProxy.setSelected(true);
            radioButtonUseProxy.setSelected(false);
            radioButtonProxyTypeHttp.setEnabled(false);
            radioButtonProxyTypeSocks5.setEnabled(false);
            textFieldAddress.setEnabled(false);
            textFieldPort.setEnabled(false);
            textFieldUserName.setEnabled(false);
            textFieldPassword.setEnabled(false);
        } else {
            radioButtonUseProxy.setSelected(true);
            radioButtonNoProxy.setSelected(false);
            radioButtonProxyTypeHttp.setEnabled(true);
            radioButtonProxyTypeSocks5.setEnabled(true);
            textFieldAddress.setEnabled(true);
            textFieldPort.setEnabled(true);
            textFieldUserName.setEnabled(true);
            textFieldPassword.setEnabled(true);
            selectProxyType();
        }
        chooseUpdateAddress.setSelectedItem(allConfigs.getUpdateAddress());
    }

    private void initCacheArray() {
        String eachLine;
        try (PreparedStatement statement = SQLiteUtil.getPreparedStatement("SELECT PATH FROM cache;");
             ResultSet resultSet = statement.executeQuery()) {
            while (resultSet.next()) {
                eachLine = resultSet.getString("PATH");
                cacheSet.add(eachLine);
            }
        } catch (Exception throwables) {
            throwables.printStackTrace();
        }
    }

    private void selectProxyType() {
        if (allConfigs.getProxyType() == Enums.ProxyType.PROXY_SOCKS) {
            radioButtonProxyTypeSocks5.setSelected(true);
        } else {
            radioButtonProxyTypeHttp.setSelected(true);
        }
    }

    private void initTabIndexMap() {
        //todo 添加新tab后在这里修改index
        int count = 0;
        tabComponentIndexMap.put("tabGeneral", count);
        count++;
        tabComponentIndexMap.put("tabSearchSettings", count);
        count++;
        tabComponentIndexMap.put("tabSearchBarSettings", count);
        count++;
        tabComponentIndexMap.put("tabModifyPriority", count);
        count++;
        tabComponentIndexMap.put("tabCache", count);
        count++;
        tabComponentIndexMap.put("tabProxy", count);
        count++;
        tabComponentIndexMap.put("tabPlugin", count);
        count++;
        tabComponentIndexMap.put("tabHotKey", count);
        count++;
        tabComponentIndexMap.put("tabLanguage", count);
        count++;
        tabComponentIndexMap.put("tabCommands", count);
        count++;
        tabComponentIndexMap.put("tabColorSettings", count);
        count++;
        tabComponentIndexMap.put("tabAbout", count);
    }

    private SettingsFrame() {
        frame.setUndecorated(true);
        frame.getRootPane().setWindowDecorationStyle(JRootPane.FRAME);
        frame.setIconImage(frameIcon.getImage());

        initTabIndexMap();

        tabbedPane.setBackground(new Color(0,0,0,0));

        ButtonGroup proxyButtonGroup = new ButtonGroup();
        proxyButtonGroup.add(radioButtonNoProxy);
        proxyButtonGroup.add(radioButtonUseProxy);

        ButtonGroup proxyTypeButtonGroup = new ButtonGroup();
        proxyTypeButtonGroup.add(radioButtonProxyTypeHttp);
        proxyTypeButtonGroup.add(radioButtonProxyTypeSocks5);

        tmp_openLastFolderKeyCode = allConfigs.getOpenLastFolderKeyCode();
        tmp_runAsAdminKeyCode = allConfigs.getRunAsAdminKeyCode();
        tmp_copyPathKeyCode = allConfigs.getCopyPathKeyCode();

        addUpdateAddressToComboBox();

        initCacheArray();

        initSuffixMap();

        translate();

        addListeners();

        initGUI();
    }

    private void addListeners() {
        addWindowCloseListener();
        addCheckBoxStartupListener();
        addButtonRemoveDesktopListener();
        addFileChooserButtonListener();
        addTextFieldListener();
        addPriorityFileChooserListener();
        addPriorityTextFieldListener();
        addTextFieldRunAsAdminListener();
        addTextFieldOpenLastFolderListener();
        addButtonCMDListener();
        addButtonDelCMDListener();
        addGitHubLabelListener();
        addCheckForUpdateButtonListener();
        addTextFieldCopyPathListener();
        addResetColorButtonListener();
        addColorChooserLabelListener();
        addListPluginMouseListener();
        addButtonPluginUpdateCheckListener();
        addButtonViewPluginMarketListener();
        addSwingThemePreviewListener();
        addButtonAddSuffixListener();
        addButtonDeleteSuffixListener();
        addTableModifySuffixListener();
        addButtonDeleteAllSuffixListener();
        addPluginOfficialSiteListener();
        addButtonVacuumListener();
        addButtonProxyListener();
        addButtonDeleteCacheListener();
        addButtonChangeThemeListener();
        addButtonDeleteAllCacheListener();
        addButtonPreviewListener();
        addButtonClosePreviewListener();
        addTextFieldSearchCommandsListener();
    }

    public boolean isCacheExist(String cache) {
        return cacheSet.contains(cache);
    }

    public void addCache(String cache) {
        cacheSet.add(cache);
    }

    private void addUpdateAddressToComboBox() {
        Set<String> updateAddresses = allConfigs.getAllUpdateAddress();
        for (String each : updateAddresses) {
            chooseUpdateAddress.addItem(each);
        }
    }

    private int getTabIndex(String componentName) {
        Integer index = tabComponentIndexMap.get(componentName);
        return  (index == null) ? 0 : index;
    }

    private void translateTabbedPane() {
        tabbedPane.setTitleAt(getTabIndex("tabGeneral"), translateUtil.getTranslation("General"));
        tabbedPane.setTitleAt(getTabIndex("tabSearchSettings"), translateUtil.getTranslation("Search settings"));
        tabbedPane.setTitleAt(getTabIndex("tabSearchBarSettings"), translateUtil.getTranslation("Search bar settings"));
        tabbedPane.setTitleAt(getTabIndex("tabCache"), translateUtil.getTranslation("Cache"));
        tabbedPane.setTitleAt(getTabIndex("tabProxy"), translateUtil.getTranslation("Proxy settings"));
        tabbedPane.setTitleAt(getTabIndex("tabPlugin"), translateUtil.getTranslation("Plugins"));
        tabbedPane.setTitleAt(getTabIndex("tabHotKey"), translateUtil.getTranslation("Hotkey settings"));
        tabbedPane.setTitleAt(getTabIndex("tabLanguage"), translateUtil.getTranslation("Language settings"));
        tabbedPane.setTitleAt(getTabIndex("tabCommands"), translateUtil.getTranslation("My commands"));
        tabbedPane.setTitleAt(getTabIndex("tabColorSettings"), translateUtil.getTranslation("Color settings"));
        tabbedPane.setTitleAt(getTabIndex("tabModifyPriority"), translateUtil.getTranslation("Modify suffix priority"));
        tabbedPane.setTitleAt(getTabIndex("tabAbout"), translateUtil.getTranslation("About"));
    }

    private void translateLabels() {
        labelMaxCacheNum.setText(translateUtil.getTranslation("Set the maximum number of caches:"));
        labelUpdateInterval.setText(translateUtil.getTranslation("File update detection interval:"));
        labelSecond.setText(translateUtil.getTranslation("Seconds"));
        labeltipPriorityFolder.setText(translateUtil.getTranslation("Priority search folder location (double-click to clear):"));
        labelConstIgnorePathTip.setText(translateUtil.getTranslation("Separate different paths with commas, and ignore C:\\Windows by default"));
        labelSetIgnorePathTip.setText(translateUtil.getTranslation("Set ignore folder:"));
        labelTransparency.setText(translateUtil.getTranslation("Search bar transparency:"));
        labelOpenSearchBarHotKey.setText(translateUtil.getTranslation("Open search bar:"));
        labelRunAsAdminHotKey.setText(translateUtil.getTranslation("Run as administrator:"));
        labelOpenFolderHotKey.setText(translateUtil.getTranslation("Open the parent folder:"));
        labelCopyPathHotKey.setText(translateUtil.getTranslation("Copy path:"));
        labelCmdTip2.setText(translateUtil.getTranslation("You can add custom commands here. After adding, " +
                "you can enter \": + your set identifier\" in the search box to quickly open"));
        labelColorTip.setText(translateUtil.getTranslation("Please enter the hexadecimal value of RGB color"));
        labelSearchBarColor.setText(translateUtil.getTranslation("Search bar Color:"));
        labelLabelColor.setText(translateUtil.getTranslation("Chosen label color:"));
        labelFontColor.setText(translateUtil.getTranslation("Chosen label font Color:"));
        labelDefaultColor.setText(translateUtil.getTranslation("Default background Color:"));
        labelNotChosenFontColor.setText(translateUtil.getTranslation("Unchosen label font Color:"));
        labelGitHubTip.setText(translateUtil.getTranslation("This is an open source software,GitHub:"));
        labelGithubIssue.setText(translateUtil.getTranslation("If you find a bug or have some suggestions, welcome to GitHub for feedback"));
        labelDescription.setText(translateUtil.getTranslation("Thanks for the following project"));
        labelTranslationTip.setText(translateUtil.getTranslation("The translation might not be 100% accurate"));
        labelLanguageChooseTip.setText(translateUtil.getTranslation("Choose a language"));
        labelVersion.setText(translateUtil.getTranslation("Current Version:") + AllConfigs.version);
        labelInstalledPluginNum.setText(translateUtil.getTranslation("Installed plugins num:"));
        labelVacuumTip.setText(translateUtil.getTranslation("Click to organize the database and reduce the size of the database"));
        labelVacuumTip2.setText(translateUtil.getTranslation("but it will consume a lot of time."));
        labelAddress.setText(translateUtil.getTranslation("Address"));
        labelPort.setText(translateUtil.getTranslation("Port"));
        labelUserName.setText(translateUtil.getTranslation("User name"));
        labelPassword.setText(translateUtil.getTranslation("Password"));
        labelProxyTip.setText(translateUtil.getTranslation("If you need a proxy to access the Internet, You can add a proxy here."));
        labelCacheSettings.setText(translateUtil.getTranslation("Cache Settings"));
        labelCacheTip.setText(translateUtil.getTranslation("You can edit the saved caches here"));
        labelCacheTip2.setText(translateUtil.getTranslation("The cache is automatically generated " +
                "by the software and will be displayed first when searching."));
        labelSearchBarFontColor.setText(translateUtil.getTranslation("SearchBar Font Color:"));
        labelBorderColor.setText(translateUtil.getTranslation("Border Color:"));
        labelCurrentCacheNum.setText(translateUtil.getTranslation("Current Caches Num:") + DatabaseUtil.getInstance().getCacheNum());
        labelUninstallPluginTip.setText(translateUtil.getTranslation("If you need to delete a plug-in, just delete it under the \"plugins\" folder in the software directory."));
        labelUninstallPluginTip2.setText(translateUtil.getTranslation("Tip:"));
        chooseUpdateAddressLabel.setText(translateUtil.getTranslation("Choose update address"));
        labelRemoveDesktop.setText(translateUtil.getTranslation("Backup and remove all desktop files") + ":");
        labelSearchCommand.setText(translateUtil.getTranslation("Search"));
        labelSuffixTip.setText(translateUtil.getTranslation("Modifying the suffix priority requires rebuilding the index (input \":update\") to take effect"));
    }

    private void translateCheckbox() {
        checkBoxAddToStartup.setText(translateUtil.getTranslation("Add to startup"));
        checkBoxLoseFocus.setText(translateUtil.getTranslation("Close search bar when focus lost"));
        checkBoxAdmin.setText(translateUtil.getTranslation("Open other programs as an administrator " +
                "(provided that the software has privileges)"));
        checkBoxIsShowTipOnCreatingLnk.setText(translateUtil.getTranslation("Show tip on creating shortcut"));
        checkBoxResponseCtrl.setText(translateUtil.getTranslation("Double-click \"Ctrl\" to open the search bar"));
    }

    private void translateButtons() {
        buttonSaveAndRemoveDesktop.setText(translateUtil.getTranslation("Backup and remove all desktop files"));
        ButtonPriorityFolder.setText(translateUtil.getTranslation("Choose"));
        buttonChooseFile.setText(translateUtil.getTranslation("Choose"));
        buttonAddCMD.setText(translateUtil.getTranslation("Add"));
        buttonDelCmd.setText(translateUtil.getTranslation("Delete"));
        buttonResetColor.setText(translateUtil.getTranslation("Reset to default"));
        buttonCheckUpdate.setText(translateUtil.getTranslation("Check for update"));
        buttonUpdatePlugin.setText(translateUtil.getTranslation("Check for update"));
        buttonPluginMarket.setText(translateUtil.getTranslation("Plugin Market"));
        buttonDeleteCache.setText(translateUtil.getTranslation("Delete cache"));
        buttonDeleteAllCache.setText(translateUtil.getTranslation("Delete all"));
        buttonChangeTheme.setText(translateUtil.getTranslation("change theme"));
        buttonVacuum.setText(translateUtil.getTranslation("Optimize database"));
        buttonPreviewColor.setText(translateUtil.getTranslation("Preview"));
        buttonClosePreview.setText(translateUtil.getTranslation("Close preview window"));
        buttonAddSuffix.setText(translateUtil.getTranslation("Add"));
        buttonDeleteSuffix.setText(translateUtil.getTranslation("Delete"));
        buttonDeleteAllSuffix.setText(translateUtil.getTranslation("Delete all"));
    }

    private void translateRadioButtons() {
        radioButtonNoProxy.setText(translateUtil.getTranslation("No proxy"));
        radioButtonUseProxy.setText(translateUtil.getTranslation("Configure proxy"));
    }

    private void translate() {
        translateTabbedPane();
        translateLabels();
        translateCheckbox();
        translateButtons();
        translateRadioButtons();
        frame.setTitle(translateUtil.getTranslation("Settings"));
    }

    private boolean isRepeatCommand(String name) {
        name = ":" + name;
        for (String each : allConfigs.getCmdSet()) {
            if (each.substring(0, each.indexOf(";")).equals(name)) {
                return true;
            }
        }
        return false;
    }

    private void hideFrame() {
        SwingUtilities.invokeLater(() -> frame.setVisible(false));
    }

    @EventRegister
    public static void registerEventHandler() {
        eventUtil.register(ShowSettingsFrameEvent.class, new EventHandler() {
            @Override
            public void todo(Event event) {
                ShowSettingsFrameEvent showSettingsFrameEvent = (ShowSettingsFrameEvent) event;
                SettingsFrame settingsFrame = getInstance();
                if (showSettingsFrameEvent.showTabName == null) {
                    settingsFrame.showWindow();
                } else {
                    settingsFrame.showWindow(settingsFrame.getTabIndex(showSettingsFrameEvent.showTabName));
                }
            }
        });
        eventUtil.register(HideSettingsFrameEvent.class, new EventHandler() {
            @Override
            public void todo(Event event) {
                getInstance().hideFrame();
            }
        });
    }

    protected boolean isSettingsFrameVisible() {
        return frame.isVisible();
    }

    private void showWindow() {
        showWindow(0);
    }

    private void showWindow(int index) {
        initGUI();
        frame.setResizable(true);
        int width = Integer.parseInt(translateUtil.getFrameWidth());
        int height = Integer.parseInt(translateUtil.getFrameHeight());

        frame.setContentPane(getInstance().panel);

        panel.setSize(width, height);
        frame.setSize(width, height);
        frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        frame.setResizable(false);
        tabbedPane.setSelectedIndex(index);
        frame.setLocationRelativeTo(null);
        //使swing风格生效
        SetSwingLaf event = new SetSwingLaf("current");
        eventUtil.putEvent(event);
        eventUtil.waitForEvent(event);
        textAreaDescription.setForeground(tabbedPane.getForeground());
        SwingUtilities.invokeLater(() -> frame.setVisible(true));
    }

    private void checkUpdateTimeLimit(StringBuilder strBuilder) {
        int updateTimeLimitTemp;
        try {
            updateTimeLimitTemp = Integer.parseInt(textFieldUpdateInterval.getText());
        } catch (Exception e1) {
            updateTimeLimitTemp = -1; // 输入不正确
        }
        if (updateTimeLimitTemp > 3600 || updateTimeLimitTemp <= 0) {
            strBuilder.append(translateUtil.getTranslation("The file index update setting is wrong, please change")).append("\n");
        }
    }

    private void checkCacheNumLimit(StringBuilder strBuilder) {
        int cacheNumLimitTemp;
        try {
            cacheNumLimitTemp = Integer.parseInt(textFieldCacheNum.getText());
        } catch (Exception e1) {
            cacheNumLimitTemp = -1;
        }
        if (cacheNumLimitTemp > 10000 || cacheNumLimitTemp <= 0) {
            strBuilder.append(translateUtil.getTranslation("The cache capacity is set incorrectly, please change")).append("\n");
        }
    }

    private void checkHotKey(StringBuilder strBuilder) {
        String tmp_hotkey = textFieldHotkey.getText();
        if (tmp_hotkey.length() < 5) {
            strBuilder.append(translateUtil.getTranslation("Hotkey setting is wrong, please change")).append("\n");
        } else {
            if (!CheckHotKeyUtil.getInstance().isHotkeyAvailable(tmp_hotkey)) {
                strBuilder.append(translateUtil.getTranslation("Hotkey setting is wrong, please change")).append("\n");
            }
        }
        if (tmp_openLastFolderKeyCode == tmp_runAsAdminKeyCode || tmp_openLastFolderKeyCode == tmp_copyPathKeyCode || tmp_runAsAdminKeyCode == tmp_copyPathKeyCode) {
            strBuilder.append(translateUtil.getTranslation("HotKey conflict")).append("\n");
        }
    }

    private void checkTransparency(StringBuilder strBuilder) {
        float transparencyTemp;
        try {
            transparencyTemp = Float.parseFloat(textFieldTransparency.getText());
        } catch (Exception e) {
            transparencyTemp = -1f;
        }
        if (transparencyTemp > 1 || transparencyTemp <= 0) {
            strBuilder.append(translateUtil.getTranslation("Transparency setting error")).append("\n");
        }
    }

    private void checkLabelColor(StringBuilder strBuilder) {
        if (!canParseToRGB(textFieldLabelColor.getText())) {
            strBuilder.append(translateUtil.getTranslation("Chosen label color is set incorrectly")).append("\n");
        }
    }

    private void checkLabelFontColorWithCoverage(StringBuilder strBuilder) {
        if (!canParseToRGB(textFieldFontColorWithCoverage.getText())) {
            strBuilder.append(translateUtil.getTranslation("Chosen label font color is set incorrectly")).append("\n");
        }
    }

    private void checkDefaultBackgroundColor(StringBuilder strBuilder) {
        if (!canParseToRGB(textFieldBackgroundDefault.getText())) {
            strBuilder.append(translateUtil.getTranslation("Incorrect default background color setting")).append("\n");
        }
    }

    private void checkBorderColor(StringBuilder stringBuilder) {
        if (!canParseToRGB(textFieldBorderColor.getText())) {
            stringBuilder.append(translateUtil.getTranslation("Border color is set incorrectly")).append("\n");
        }
    }

    private void checkLabelFontColor(StringBuilder strBuilder) {
        if (!canParseToRGB(textFieldFontColor.getText())) {
            strBuilder.append(translateUtil.getTranslation("Unchosen label font color is set incorrectly")).append("\n");
        }
    }

    private void checkSearchBarColor(StringBuilder strBuilder) {
        if (!canParseToRGB(textFieldSearchBarColor.getText())) {
            strBuilder.append(translateUtil.getTranslation("The color of the search bar is set incorrectly")).append("\n");
        }
    }

    private void checkSearchBarFontColor(StringBuilder strBuilder) {
        if (!canParseToRGB(textFieldSearchBarFontColor.getText())) {
            strBuilder.append(translateUtil.getTranslation("The font color of the search bar is set incorrectly")).append("\n");
        }
    }

    private void checkProxy(StringBuilder strBuilder) {
        try {
            Integer.parseInt(textFieldPort.getText());
        } catch (RuntimeException e) {
            strBuilder.append(translateUtil.getTranslation("Proxy port is set incorrectly.")).append("\n");
        }
    }

    private String saveChanges() {
        StringBuilder errorsStrb = new StringBuilder();

        checkProxy(errorsStrb);
        checkSearchBarColor(errorsStrb);
        checkSearchBarFontColor(errorsStrb);
        checkLabelColor(errorsStrb);
        checkLabelFontColor(errorsStrb);
        checkLabelFontColorWithCoverage(errorsStrb);
        checkBorderColor(errorsStrb);
        checkDefaultBackgroundColor(errorsStrb);
        checkTransparency(errorsStrb);
        checkHotKey(errorsStrb);
        checkCacheNumLimit(errorsStrb);
        checkUpdateTimeLimit(errorsStrb);

        String ignorePathTemp = RegexUtil.lineFeed.matcher(textAreaIgnorePath.getText()).replaceAll("");

        String swingTheme = (String) listSwingThemes.getSelectedValue();

        String errors = errorsStrb.toString();
        if (!errors.isEmpty()) {
            return errors;
        }

        //重新显示翻译GUI
        if (!listLanguage.getSelectedValue().equals(translateUtil.getLanguage())) {
            translateUtil.setLanguage((String) listLanguage.getSelectedValue());
            translate();
        }

        //所有配置均正确
        //使所有配置生效
        String tmp_proxyAddress = textFieldAddress.getText();
        String tmp_proxyUserName = textFieldUserName.getText();
        String tmp_proxyPassword = textFieldPassword.getText();

        ConfigEntity configEntity = new ConfigEntity();

        if (radioButtonProxyTypeSocks5.isSelected()) {
            configEntity.setProxyType(Enums.ProxyType.PROXY_SOCKS);
        } else if (radioButtonProxyTypeHttp.isSelected()) {
            configEntity.setProxyType(Enums.ProxyType.PROXY_HTTP);
        }
        if (radioButtonNoProxy.isSelected()) {
            configEntity.setProxyType(Enums.ProxyType.PROXY_DIRECT);
        }
        configEntity.setUpdateAddress((String) chooseUpdateAddress.getSelectedItem());
        configEntity.setPriorityFolder(textFieldPriorityFolder.getText());
        configEntity.setHotkey(textFieldHotkey.getText());
        configEntity.setCacheNumLimit(Integer.parseInt(textFieldCacheNum.getText()));
        configEntity.setUpdateTimeLimit(Integer.parseInt(textFieldUpdateInterval.getText()));
        configEntity.setIgnorePath(ignorePathTemp);
        configEntity.setDefaultAdmin(checkBoxAdmin.isSelected());
        configEntity.setLoseFocusClose(checkBoxLoseFocus.isSelected());
        configEntity.setShowTipCreatingLnk(checkBoxIsShowTipOnCreatingLnk.isSelected());
        configEntity.setTransparency(Float.parseFloat(textFieldTransparency.getText()));
        configEntity.setLabelColor(Integer.parseInt(textFieldLabelColor.getText(), 16));
        configEntity.setBorderColor(Integer.parseInt(textFieldBorderColor.getText(), 16));
        configEntity.setDefaultBackgroundColor(Integer.parseInt(textFieldBackgroundDefault.getText(), 16));
        configEntity.setSearchBarColor(Integer.parseInt(textFieldSearchBarColor.getText(), 16));
        configEntity.setFontColorWithCoverage(Integer.parseInt(textFieldFontColorWithCoverage.getText(), 16));
        configEntity.setFontColor(Integer.parseInt(textFieldFontColor.getText(), 16));
        configEntity.setSearchBarFontColor(Integer.parseInt(textFieldSearchBarFontColor.getText(), 16));
        configEntity.setProxyAddress(tmp_proxyAddress);
        configEntity.setProxyPort(Integer.parseInt(textFieldPort.getText()));
        configEntity.setProxyUserName(tmp_proxyUserName);
        configEntity.setProxyPassword(tmp_proxyPassword);
        configEntity.setOpenLastFolderKeyCode(tmp_openLastFolderKeyCode);
        configEntity.setRunAsAdminKeyCode(tmp_runAsAdminKeyCode);
        configEntity.setCopyPathKeyCode(tmp_copyPathKeyCode);
        configEntity.setSwingTheme(swingTheme);
        configEntity.setLanguage(translateUtil.getLanguage());
        configEntity.setDoubleClickCtrlOpen(checkBoxResponseCtrl.isSelected());

        setStartup(checkBoxAddToStartup.isSelected());
        eventUtil.putEvent(new ResponseCtrlEvent(checkBoxResponseCtrl.isSelected()));

        SaveConfigsEvent event = new SaveConfigsEvent(configEntity);
        eventUtil.putEvent(event);
        eventUtil.waitForEvent(event);
        eventUtil.putEvent(new SetConfigsEvent());

        Color tmp_color = new Color(allConfigs.getLabelColor());
        labelColorChooser.setBackground(tmp_color);
        labelColorChooser.setForeground(tmp_color);
        tmp_color = new Color(allConfigs.getDefaultBackgroundColor());
        defaultBackgroundChooser.setBackground(tmp_color);
        defaultBackgroundChooser.setForeground(tmp_color);
        tmp_color = new Color(allConfigs.getLabelFontColorWithCoverage());
        FontColorWithCoverageChooser.setBackground(tmp_color);
        FontColorWithCoverageChooser.setForeground(tmp_color);
        tmp_color = new Color(allConfigs.getLabelFontColor());
        FontColorChooser.setBackground(tmp_color);
        FontColorChooser.setForeground(tmp_color);
        tmp_color = new Color(allConfigs.getSearchBarFontColor());
        SearchBarFontColorChooser.setBackground(tmp_color);
        SearchBarFontColorChooser.setForeground(tmp_color);
        tmp_color = new Color(allConfigs.getBorderColor());
        borderColorChooser.setBackground(tmp_color);
        borderColorChooser.setForeground(tmp_color);

        //保存自定义命令
        StringBuilder strb = new StringBuilder();
        for (String each : allConfigs.getCmdSet()) {
            strb.append(each);
            strb.append("\n");
        }
        try (BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream("user/cmds.txt"), StandardCharsets.UTF_8))) {
            bw.write(strb.toString());
        } catch (IOException ignored) {
        }
        hideFrame();
        return "";
    }

    private void setStartup(boolean b) {
        if (isStartupChanged) {
            isStartupChanged = false;
            if (b) {
                String command = "cmd.exe /c schtasks /create /ru \"administrators\" /rl HIGHEST /sc ONLOGON /tn \"File-Engine\" /tr ";
                File FileEngine = new File(AllConfigs.FILE_NAME);
                String absolutePath = "\"\"" + FileEngine.getAbsolutePath() + "\"\" /f";
                command += absolutePath;
                Process p;
                try {
                    p = Runtime.getRuntime().exec(command);
                    p.waitFor();
                    BufferedReader outPut = new BufferedReader(new InputStreamReader(p.getErrorStream()));
                    String line;
                    StringBuilder result = new StringBuilder();
                    while ((line = outPut.readLine()) != null) {
                        result.append(line);
                    }
                    outPut.close();
                    if (!result.toString().isEmpty()) {
                        checkBoxAddToStartup.setSelected(false);
                        JOptionPane.showMessageDialog(frame, translateUtil.getTranslation("Add to startup failed, please try to run as administrator"));
                    }
                } catch (IOException | InterruptedException ignored) {
                }
            } else {
                String command = "cmd.exe /c schtasks /delete /tn \"File-Engine\" /f";
                Process p;
                try {
                    p = Runtime.getRuntime().exec(command);
                    p.waitFor();
                    StringBuilder result = new StringBuilder();
                    try (BufferedReader outPut = new BufferedReader(new InputStreamReader(p.getErrorStream()))) {
                        String line;
                        while ((line = outPut.readLine()) != null) {
                            result.append(line);
                        }
                    }
                    if (!result.toString().isEmpty()) {
                        checkBoxAddToStartup.setSelected(true);
                        JOptionPane.showMessageDialog(frame, translateUtil.getTranslation("Delete startup failed, please try to run as administrator"));
                    }
                } catch (IOException | InterruptedException ignored) {
                }
            }
        }
    }
}
