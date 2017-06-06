package com.cnaude.purpleirc;

import com.cnaude.purpleirc.Commands.IRCCommandInterface;
import com.cnaude.purpleirc.GameListeners.*;
import com.cnaude.purpleirc.Hooks.BungeeTabListPlusHook;
import com.cnaude.purpleirc.Utilities.*;
import com.cnaude.purpleirc.Events.IRCMessageEvent;
import com.google.common.base.Joiner;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;
import org.pircbotx.IdentServer;

import java.io.*;
import java.lang.management.ManagementFactory;
import java.net.Socket;
import java.net.UnknownHostException;
import java.text.Collator;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Chris Naudé
 */
public class PurpleIRC extends Plugin {

    public String LOG_HEADER;
    public String LOG_HEADER_F;
    private final String sampleFileName;
    private final String MAINCONFIG;
    private File pluginFolder;
    private File botsFolder;
    private File configFile;
    public static long startTime;
    public boolean identServerEnabled;
    private final CaseInsensitiveMap<HashMap<String, String>> messageTmpl;
    private final CaseInsensitiveMap<CaseInsensitiveMap<String>> ircHeroChannelMessages;
    private final CaseInsensitiveMap<CaseInsensitiveMap<String>> ircHeroActionChannelMessages;
    private final CaseInsensitiveMap<CaseInsensitiveMap<String>> heroChannelMessages;
    private final CaseInsensitiveMap<CaseInsensitiveMap<String>> heroActionChannelMessages;
    private final HashMap<ServerInfo, Integer> serverMaxCounts;
    public String defaultPlayerSuffix,
            defaultPlayerPrefix,
            defaultPlayerGroup,
            defaultGroupPrefix,
            defaultPlayerWorld,
            defaultGroupSuffix,
            customTabPrefix,
            customTabIcon,
            heroChatEmoteFormat,
            listFormat,
            listSeparator,
            listPlayer,
            ircNickPrefixIrcOp,
            ircNickPrefixOwner,
            ircNickPrefixSuperOp,
            ircNickPrefixOp,
            ircNickPrefixHalfOp,
            ircNickPrefixVoice;
    public boolean customTabList;
    public boolean listSingleLine;
    private final CaseInsensitiveMap<String> displayNameCache;

    public ArrayList<String> kickedPlayers = new ArrayList<>();

    public final String invalidBotName = ChatColor.RED + "Invalid bot name: "
            + ChatColor.WHITE + "%BOT%"
            + ChatColor.RED + ". Type '" + ChatColor.WHITE + "/irc listbots"
            + ChatColor.RED + "' to see valid bots.";

    public final String invalidChannelName = ChatColor.RED + "Invalid channel name: "
            + ChatColor.WHITE + "%CHANNEL%";

    public final String invalidChannel = ChatColor.RED + "Invalid channel: "
            + ChatColor.WHITE + "%CHANNEL%";
    public final String noPermission = ChatColor.RED + "You do not have permission to use this command.";

    private boolean debugEnabled;
    private boolean stripGameColors;
    private boolean stripIRCColors;
    private boolean stripIRCBackgroundColors;
    private boolean stripGameColorsFromIrc;
    private boolean listSortByName;
    public boolean exactNickMatch;
    public boolean ignoreChatCancel;
    public Long ircConnCheckInterval;
    public Long ircChannelCheckInterval;
    public ChannelWatcher channelWatcher;
    public ColorConverter colorConverter;
    public RegexGlobber regexGlobber;
    public CaseInsensitiveMap<PurpleBot> ircBots;
    private BotWatcher botWatcher;
    public IRCMessageHandler ircMessageHandler;
    public CommandQueueWatcher commandQueue;
    public CommandHandlers commandHandlers;
    public BungeeTabListPlusHook tabListHook;
    Configuration mainConfig;
    BungeeCordListener bungeeCordListener;

    public ChatTokenizer tokenizer;
    private final File cacheFile;
    public HashMap<String, IRCCommandInterface> commands;
    public ArrayList<String> sortedCommands;
    private final CaseInsensitiveMap<CaseInsensitiveMap<String>> mvChannelMessages;
    private final CaseInsensitiveMap<CaseInsensitiveMap<String>> mvActionMessages;
    private final CaseInsensitiveMap<CaseInsensitiveMap<String>> ircMvChannelMessages;
    private final CaseInsensitiveMap<CaseInsensitiveMap<String>> ircMvActionMessages;
    public String ircMinecraftServerName;

    public PurpleIRC() {
        this.sortedCommands = new ArrayList<>();
        this.commands = new HashMap<>();
        this.MAINCONFIG = "MAIN-CONFIG";
        this.sampleFileName = "SampleBot.yml";
        this.ircBots = new CaseInsensitiveMap<>();
        this.messageTmpl = new CaseInsensitiveMap<>();
        this.ircHeroChannelMessages = new CaseInsensitiveMap<>();
        this.ircHeroActionChannelMessages = new CaseInsensitiveMap<>();
        this.heroChannelMessages = new CaseInsensitiveMap<>();
        this.heroActionChannelMessages = new CaseInsensitiveMap<>();
        this.mvChannelMessages = new CaseInsensitiveMap<>();
        this.mvActionMessages = new CaseInsensitiveMap<>();
        this.ircMvActionMessages = new CaseInsensitiveMap<>();
        this.ircMvChannelMessages = new CaseInsensitiveMap<>();
        this.serverMaxCounts = new HashMap<>();
        this.displayNameCache = new CaseInsensitiveMap<>();
        this.cacheFile = new File("plugins/PurpleIRC/displayName.cache");
    }

    /**
     *
     */
    @Override
    public void onEnable() {
        LOG_HEADER = "[" + getDescription().getName() + "]";
        LOG_HEADER_F = ChatColor.DARK_PURPLE + "[" + getDescription().getName() + "]" + ChatColor.WHITE;
        pluginFolder = getDataFolder();
        botsFolder = new File(pluginFolder + "/bots");
        configFile = new File(pluginFolder, "config.yml");
        createConfig();
        this.getProxy().getConfig();
        loadConfig();
        loadDisplayNameCache();
        if (identServerEnabled) {
            logInfo("Starting Ident Server ...");
            try {
                IdentServer.startServer();
            } catch (Exception ex) {
                logError(ex.getMessage());
            }
        }
        this.getProxy().getPluginManager().registerListener(this, new GamePlayerChatListener(this));
        this.getProxy().getPluginManager().registerListener(this, new GamePlayerJoinListener(this));
        this.getProxy().getPluginManager().registerListener(this, new GamePlayerQuitListener(this));
        this.getProxy().getPluginManager().registerListener(this, new GameServerSwitchListener(this));
        bungeeCordListener = new BungeeCordListener(this);
        getProxy().registerChannel("BungeeCord");
        this.getProxy().getPluginManager().registerListener(this, bungeeCordListener);
        regexGlobber = new RegexGlobber();
        tokenizer = new ChatTokenizer(this);
        commandHandlers = new CommandHandlers(this);
        getProxy().getPluginManager().registerCommand(this, commandHandlers);
        loadBots();
        createSampleBot();
        channelWatcher = new ChannelWatcher(this);
        botWatcher = new BotWatcher(this);
        ircMessageHandler = new IRCMessageHandler(this);
        commandQueue = new CommandQueueWatcher(this);
        updateServerCache(this);
        if (customTabList && this.getProxy().getPluginManager().getPlugin("BungeeTabListPlus") != null) {
            this.tabListHook = new BungeeTabListPlusHook(this);
        }
    }

    /**
     *
     */
    @Override
    public void onDisable() {
        getProxy().unregisterChannel("BungeeCord");
        if (channelWatcher != null) {
            logDebug("Disabling channelWatcher ...");
            channelWatcher.cancel();
        }
        if (botWatcher != null) {
            logDebug("Disabling botWatcher ...");
            botWatcher.cancel();
        }
        if (ircBots.isEmpty()) {
            logInfo("No IRC bots to disconnect.");
        } else {
            logInfo("Disconnecting IRC bots.");
            for (PurpleBot ircBot : ircBots.values()) {
                commandQueue.cancel();
                ircBot.quit();
            }
            ircBots.clear();
        }
        if (identServerEnabled) {
            logInfo("Stopping Ident Server");
            try {
                IdentServer.stopServer();
            } catch (IOException ex) {
                logError(ex.getMessage());
            }
        }
        saveDisplayNameCache();
    }

    /**
     *
     * @return
     */
    public boolean debugMode() {
        return debugEnabled;
    }

    public String getMsgTemplate(String botName, String tmpl) {
        if (messageTmpl.containsKey(botName)) {
            if (messageTmpl.get(botName).containsKey(tmpl)) {
                return messageTmpl.get(botName).get(tmpl);
            }
        }
        if (messageTmpl.get(MAINCONFIG).containsKey(tmpl)) {
            return messageTmpl.get(MAINCONFIG).get(tmpl);
        }
        return "INVALID TEMPLATE";
    }

    public String getMsgTemplate(String tmpl) {
        return getMsgTemplate(MAINCONFIG, tmpl);
    }

    public String getHeroTemplate(CaseInsensitiveMap<CaseInsensitiveMap<String>> hc,
            String botName, String hChannel) {
        if (hc.containsKey(botName)) {
            logDebug("HC1 => " + hChannel);
            for (String s : hc.get(botName).keySet()) {
                logDebug("HT => " + s);
            }
            if (hc.get(botName).containsKey(hChannel)) {
                logDebug("HC2 => " + hChannel);
                return hc.get(botName).get(hChannel);
            }
        }
        if (hc.containsKey(MAINCONFIG)) {
            logDebug("HC3 => " + hChannel);
            for (String s : hc.get(MAINCONFIG).keySet()) {
                logDebug("HT => " + s);
            }
            if (hc.get(MAINCONFIG).containsKey(hChannel)) {
                logDebug("HC4 => " + hChannel);
                return hc.get(MAINCONFIG).get(hChannel);
            }
        }
        return "";
    }

    public String getHeroChatChannelTemplate(String botName, String hChannel) {
        String tmpl = getHeroTemplate(heroChannelMessages, botName, hChannel);
        if (tmpl.isEmpty()) {
            return getMsgTemplate(MAINCONFIG, TemplateName.HERO_CHAT);
        }
        return getHeroTemplate(heroChannelMessages, botName, hChannel);
    }

    public String getHeroActionChannelTemplate(String botName, String hChannel) {
        String tmpl = getHeroTemplate(heroActionChannelMessages, botName, hChannel);
        if (tmpl.isEmpty()) {
            return getMsgTemplate(MAINCONFIG, TemplateName.HERO_ACTION);
        }
        return getHeroTemplate(heroActionChannelMessages, botName, hChannel);
    }

    public String getIRCHeroChatChannelTemplate(String botName, String hChannel) {
        String tmpl = getHeroTemplate(ircHeroChannelMessages, botName, hChannel);
        if (tmpl.isEmpty()) {
            return getMsgTemplate(MAINCONFIG, TemplateName.IRC_HERO_CHAT);
        }
        return getHeroTemplate(ircHeroChannelMessages, botName, hChannel);
    }

    public String getIRCHeroActionChannelTemplate(String botName, String hChannel) {
        String tmpl = getHeroTemplate(ircHeroActionChannelMessages, botName, hChannel);
        if (tmpl.isEmpty()) {
            return getMsgTemplate(MAINCONFIG, TemplateName.IRC_HERO_ACTION);
        }
        return getHeroTemplate(ircHeroActionChannelMessages, botName, hChannel);
    }

    public void loadCustomColors(Configuration config) {

        for (String t : config.getSection("irc-color-map").getKeys()) {
            colorConverter.addIrcColorMap(t, config.getString("irc-color-map." + t));
        }
        for (String t : config.getSection("game-color-map").getKeys()) {
            colorConverter.addGameColorMap(t, config.getString("game-color-map." + t));
        }
    }

    public void loadTemplates(Configuration config, String configName) {
        messageTmpl.put(configName, new HashMap<String, String>());

        if (config.getString("message-format") != null) {
            for (String t : config.getSection("message-format").getKeys()) {
                if (!t.startsWith("MemorySection")) {
                    messageTmpl.get(configName).put(t, ChatColor.translateAlternateColorCodes('&',
                            config.getString("message-format." + t, "")));
                    logDebug("message-format: " + t + " => " + messageTmpl.get(configName).get(t));
                }
            }
        } else {
            logDebug("No message-format section found for " + configName);
        }
    }

    private void loadConfig() {
        try {
            mainConfig = ConfigurationProvider.getProvider(YamlConfiguration.class).load(configFile);
        } catch (IOException ex) {
            Logger.getLogger(PurpleIRC.class.getName()).log(Level.SEVERE, null, ex);
            return;
        }
        debugEnabled = mainConfig.getBoolean("Debug");
        identServerEnabled = mainConfig.getBoolean("enable-ident-server");
        logDebug("Debug enabled");
        stripGameColors = mainConfig.getBoolean("strip-game-colors", false);
        stripIRCColors = mainConfig.getBoolean("strip-irc-colors", false);
        stripIRCBackgroundColors = mainConfig.getBoolean("strip-irc-bg-colors", true);
        stripGameColorsFromIrc = mainConfig.getBoolean("strip-game-colors-from-irc", true);
        exactNickMatch = mainConfig.getBoolean("nick-exact-match", true);
        ignoreChatCancel = mainConfig.getBoolean("ignore-chat-cancel", false);
        colorConverter = new ColorConverter(this, stripGameColors, stripIRCColors, stripIRCBackgroundColors, stripGameColorsFromIrc);
        logDebug("strip-game-colors: " + stripGameColors);
        logDebug("strip-irc-colors: " + stripIRCColors);

        loadTemplates(mainConfig, MAINCONFIG);
        loadCustomColors(mainConfig);

        defaultPlayerSuffix = ChatColor.translateAlternateColorCodes('&', mainConfig.getString("message-format.default-player-suffix", ""));
        defaultPlayerPrefix = ChatColor.translateAlternateColorCodes('&', mainConfig.getString("message-format.default-player-prefix", ""));
        defaultPlayerGroup = ChatColor.translateAlternateColorCodes('&', mainConfig.getString("message-format.default-player-group", ""));
        defaultGroupSuffix = ChatColor.translateAlternateColorCodes('&', mainConfig.getString("message-format.default-group-suffix", ""));
        defaultGroupPrefix = ChatColor.translateAlternateColorCodes('&', mainConfig.getString("message-format.default-group-prefix", ""));
        defaultPlayerWorld = ChatColor.translateAlternateColorCodes('&', mainConfig.getString("message-format.default-player-world", ""));

        ircNickPrefixIrcOp = ChatColor.translateAlternateColorCodes('&', mainConfig.getString("nick-prefixes.ircop", "~"));
        ircNickPrefixOwner = ChatColor.translateAlternateColorCodes('&', mainConfig.getString("nick-prefixes.owner", "@"));
        ircNickPrefixSuperOp = ChatColor.translateAlternateColorCodes('&', mainConfig.getString("nick-prefixes.ircsuperop", "&&"));
        ircNickPrefixOp = ChatColor.translateAlternateColorCodes('&', mainConfig.getString("nick-prefixes.op", "@"));
        ircNickPrefixHalfOp = ChatColor.translateAlternateColorCodes('&', mainConfig.getString("nick-prefixes.halfop", "%"));
        ircNickPrefixVoice = ChatColor.translateAlternateColorCodes('&', mainConfig.getString("nick-prefixes.voice", "+"));

        listSingleLine = mainConfig.getBoolean("list-single-line", false);
        listFormat = ChatColor.translateAlternateColorCodes('&', mainConfig.getString("list-format", ""));
        listSeparator = ChatColor.translateAlternateColorCodes('&', mainConfig.getString("list-separator", ""));
        listPlayer = ChatColor.translateAlternateColorCodes('&', mainConfig.getString("list-player", ""));
        listSortByName = mainConfig.getBoolean("list-sort-by-name", true);

        ircConnCheckInterval = mainConfig.getLong("conn-check-interval");
        ircChannelCheckInterval = mainConfig.getLong("channel-check-interval");

        customTabPrefix = ChatColor.translateAlternateColorCodes('&', mainConfig.getString("custom-tab-prefix", ""));
        customTabList = mainConfig.getBoolean("custom-tab-list", false);
        customTabIcon = mainConfig.getString("custom-tab-icon", "MHF_ArrowRight");
        logDebug("custom-tab-prefix: " + customTabPrefix);
        ircMinecraftServerName = mainConfig.getString("irc-minecraft-server-name", "Webchat");
    }

    private void loadBots() {
        if (botsFolder.exists()) {
            logInfo("Checking for bot files in " + botsFolder);
            for (final File file : botsFolder.listFiles()) {
                if (file.getName().endsWith(".yml")) {
                    logInfo("Loading bot file: " + file.getName());
                    PurpleBot pircBot = new PurpleBot(file, this);
                    ircBots.put(file.getName(), pircBot);
                    logInfo("Loaded bot: " + pircBot.botNick);
                }
            }
        }
    }

    private void createSampleBot() {
        File file = new File(pluginFolder + "/" + sampleFileName);
        try {
            try (InputStream in = PurpleIRC.class.getResourceAsStream("/" + sampleFileName)) {
                byte[] buf = new byte[1024];
                int len;
                try (OutputStream out = new FileOutputStream(file)) {
                    while ((len = in.read(buf)) > 0) {
                        out.write(buf, 0, len);
                    }
                }
            }
        } catch (IOException ex) {
            logError("Problem creating sample bot: " + ex.getMessage());
        }
    }

    private void createSampleConfig() {
        if (!configFile.exists()) {
            try {
                try (InputStream in = PurpleIRC.class.getResourceAsStream("/config.yml")) {
                    byte[] buf = new byte[1024];
                    int len;
                    try (OutputStream out = new FileOutputStream(configFile)) {
                        while ((len = in.read(buf)) > 0) {
                            out.write(buf, 0, len);
                        }
                    }
                }
            } catch (IOException ex) {
                logError("Problem creating sample config: " + ex.getMessage());
            }
        }
    }

    /**
     *
     * @param sender
     */
    public void reloadMainConfig(CommandSender sender) {
        sender.sendMessage(new TextComponent(LOG_HEADER_F + " Reloading config.yml ..."));
        loadConfig();
        sender.sendMessage(new TextComponent(LOG_HEADER_F + ChatColor.WHITE + " Done."));
    }

    private void createConfig() {
        if (!pluginFolder.exists()) {
            try {
                pluginFolder.mkdir();
            } catch (Exception e) {
                logError(e.getMessage());
            }
        }

        if (!configFile.exists()) {
            createSampleConfig();
        }

        if (!botsFolder.exists()) {
            try {
                botsFolder.mkdir();
            } catch (Exception e) {
                logError(e.getMessage());
            }
        }
    }

    /**
     *
     * @param message
     */
    public void logInfo(String message) {
        getLogger().log(Level.INFO, message);
    }

    /**
     *
     * @param message
     */
    public void logError(String message) {
        getLogger().log(Level.SEVERE, message);
    }

    /**
     *
     * @param message
     */
    public void logDebug(String message) {
        if (debugEnabled) {
            getLogger().log(Level.INFO, String.format("[DEBUG] %s", message));
        }
    }

    /**
     *
     * @return
     */
    public String getMCUptime() {
        long jvmUptime = ManagementFactory.getRuntimeMXBean().getUptime();
        String msg = "Server uptime: " + (int) (jvmUptime / 86400000L) + " days"
                + " " + (int) (jvmUptime / 3600000L % 24L) + " hours"
                + " " + (int) (jvmUptime / 60000L % 60L) + " minutes"
                + " " + (int) (jvmUptime / 1000L % 60L) + " seconds.";
        return msg;
    }

    /**
     *
     * @param si
     * @param ircBot
     * @param channelName
     * @return
     */
    public String getMCPlayers(ServerInfo si, PurpleBot ircBot, String channelName) {
        int maxPlayers = -1;
        if (serverMaxCounts.containsKey(si)) {
            maxPlayers = serverMaxCounts.get(si);
        }
        Map<String, String> playerList = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
        for (ProxiedPlayer player : si.getPlayers()) {
            String pName = tokenizer.playerTokenizer(player, listPlayer);
            playerList.put(player.getName(), pName);
        }

        String pList;
        if (!listSortByName) {
            // sort as before
            ArrayList<String> tmp = new ArrayList<>(playerList.values());
            Collections.sort(tmp, Collator.getInstance());
            pList = Joiner.on(listSeparator).join(tmp);
        } else {
            // sort without nick prefixes 
            pList = Joiner.on(listSeparator).join(playerList.values());
        }

        String msg = listFormat
                .replace("%SERVER%", si.getName())
                .replace("%COUNT%", Integer.toString(playerList.size()))
                .replace("%MAX%", Integer.toString(maxPlayers))
                .replace("%PLAYERS%", pList);
        logDebug("L: " + msg);
        return colorConverter.gameColorsToIrc(msg);
    }

    public String getRemotePlayers(String commandArgs) {
        if (commandArgs != null) {
            String host;
            int port = 25565;
            if (commandArgs.contains(":")) {
                host = commandArgs.split(":")[0];
                port = Integer.parseInt(commandArgs.split(":")[1]);
            } else {
                host = commandArgs;
            }
            Query query = new Query(host, port);
            try {
                query.sendQuery();
            } catch (IOException ex) {
                return ex.getMessage();
            }
            String players[] = query.getOnlineUsernames();
            for (String s : query.getValues().keySet()) {
                logDebug(s + " => " + query.getValues().get(s));
            }
            String m;
            if (players.length == 0) {
                m = "There are no players on " + host
                        + ":" + port;
            } else {
                m = "Players on " + host + "("
                        + players.length
                        + "): " + Joiner.on(", ")
                                .join(players);
            }
            return m;
        } else {
            return "Invalid host.";
        }
    }

    private void updateServerCache(final PurpleIRC plugin) {
        getProxy().getScheduler().runAsync(this, new Runnable() {
            @Override
            public void run() {
                for (ServerInfo server : plugin.getProxy().getServers().values()) {
                    updateServerCache(server);
                }
            }
        });
    }

    public void updateServerCache(ServerInfo si) {
        int maxPlayers;
        try {
            Socket sock = new Socket(si.getAddress().getAddress(), si.getAddress().getPort());

            DataOutputStream out = new DataOutputStream(sock.getOutputStream());
            DataInputStream in = new DataInputStream(sock.getInputStream());

            out.write(0xFE);

            int b;
            StringBuilder str = new StringBuilder();
            while ((b = in.read()) != -1) {
                if (b != 0 && b > 16 && b != 255 && b != 23 && b != 24) {
                    str.append((char) b);
                    //logDebug(b + ":" + ((char) b));                    
                }
            }

            String[] data = str.toString().split("§");
            maxPlayers = Integer.parseInt(data[data.length - 1]);
        } catch (UnknownHostException e) {
            logInfo(e.getMessage());
            return;
        } catch (IOException e) {
            logInfo(e.getMessage());
            return;
        }
        serverMaxCounts.put(si, maxPlayers);

    }

    /**
     *
     * @param pName
     * @return
     */
    public String getDisplayName(String pName) {
        String displayName = null;
        ProxiedPlayer player = getPlayer(pName);
        logDebug("player: " + player);
        if (player != null) {
            displayName = player.getDisplayName();
        }
        if (displayName != null) {
            logDebug("Caching displayName for " + pName + " = " + displayName);
            displayNameCache.put(pName, displayName);
        } else if (displayNameCache.containsKey(pName)) {
            displayName = displayNameCache.get(pName);
        } else {
            displayName = pName;
        }
        return displayName;
    }

    /**
     *
     * @param player
     */
    public void updateDisplayNameCache(ProxiedPlayer player) {
        logDebug("Caching displayName for " + player.getName() + " = " + player.getDisplayName());
        displayNameCache.put(player.getName(), player.getDisplayName());
    }

    /**
     *
     * @param player
     * @param displayName
     */
    public void updateDisplayNameCache(String player, String displayName) {
        logDebug("Caching displayName for " + player + " = " + displayName);
        displayNameCache.put(player, displayName);
    }

    public void saveDisplayNameCache() {
        BufferedWriter writer;
        try {
            writer = new BufferedWriter(new FileWriter(cacheFile));
        } catch (IOException ex) {
            logError(ex.getMessage());
            return;
        }

        try {
            for (String s : displayNameCache.keySet()) {
                logDebug("Saving to displayName.cache: " + s + "\t" + displayNameCache.get(s));
                writer.write(s + "\t" + displayNameCache.get(s) + "\n");
            }
            writer.close();
        } catch (IOException ex) {
            logError(ex.getMessage());
        }
    }

    public void loadDisplayNameCache() {
        try {
            try (BufferedReader in = new BufferedReader(new FileReader(cacheFile))) {
                String line;
                while ((line = in.readLine()) != null) {
                    if (line.equals("\n")) {
                        continue;
                    }
                    String[] parts = line.split("\t", 2);
                    updateDisplayNameCache(parts[0], parts[1]);
                }
            }
        } catch (IOException | NumberFormatException e) {
            logError(e.getMessage());
        }
    }

    /**
     *
     * @param message
     * @param channel
     * @param permission
     */
    public void broadcast(String message, String channel, String permission) {
        ProxyServer.getInstance().getPluginManager().callEvent(new IRCMessageEvent(message, channel, permission));
        for (ProxiedPlayer player : ProxyServer.getInstance().getPlayers()) {
            if (player.hasPermission(permission)) {
                player.sendMessage(new TextComponent(message));
            }
        }
    }

    public ProxiedPlayer getPlayer(String name) {
        for (ProxiedPlayer player : ProxyServer.getInstance().getPlayers()) {
            if (player.getName().equalsIgnoreCase(name)) {
                return player;
            }
        }
        return null;
    }

    /**
     *
     * @param debug
     */
    public void debugMode(boolean debug) {
        debugEnabled = debug;
        mainConfig.set("Debug", debug);
        try {
            ConfigurationProvider.getProvider(YamlConfiguration.class).save(mainConfig, configFile);
        } catch (IOException ex) {
            logError("Problem saving to " + configFile.getName() + ": " + ex.getMessage());
        }
    }

    /**
     *
     * @param botName
     * @param channelName
     * @param template
     * @return
     */
    public String getMessageTemplate(String botName, String channelName, String template) {
        if (messageTmpl.containsKey(botName + "." + channelName)) {
            if (messageTmpl.get(botName + "." + channelName).containsKey(template)) {
                return messageTmpl.get(botName + "." + channelName).get(template);
            }
        }
        if (messageTmpl.containsKey(botName)) {
            if (messageTmpl.get(botName).containsKey(template)) {
                return messageTmpl.get(botName).get(template);
            }
        }
        if (messageTmpl.get(MAINCONFIG).containsKey(template)) {
            return messageTmpl.get(MAINCONFIG).get(template);
        }
        logDebug("No such template: " + template);
        return "";
    }

    public String getMineverseChannelTemplate(String botNick, String channel) {
        String tmpl = getMvTemplate(mvChannelMessages, botNick, channel);
        if (tmpl.isEmpty()) {
            return getMsgTemplate(MAINCONFIG, TemplateName.HERO_CHAT);
        }
        return getMvTemplate(mvChannelMessages, botNick, channel);
    }

    public String getMvTemplate(CaseInsensitiveMap<CaseInsensitiveMap<String>> mv,
            String botName, String hChannel) {
        if (mv.containsKey(botName)) {
            if (mv.get(botName).containsKey(hChannel)) {
                return mv.get(botName).get(hChannel);
            }
        }
        if (mv.containsKey(MAINCONFIG)) {
            if (mv.get(MAINCONFIG).containsKey(hChannel)) {
                return mv.get(MAINCONFIG).get(hChannel);
            }
        }
        return "";
    }

    public String getIRCMVChatChannelTemplate(String botName, String hChannel) {
        String tmpl = getHeroTemplate(ircHeroChannelMessages, botName, hChannel);
        if (tmpl.isEmpty()) {
            return getMsgTemplate(MAINCONFIG, TemplateName.IRC_HERO_CHAT);
        }
        return getHeroTemplate(ircHeroChannelMessages, botName, hChannel);
    }

    public String getIRCMVActionChannelTemplate(String botName, String hChannel) {
        String tmpl = getHeroTemplate(ircHeroActionChannelMessages, botName, hChannel);
        if (tmpl.isEmpty()) {
            return getMsgTemplate(MAINCONFIG, TemplateName.IRC_HERO_ACTION);
        }
        return getHeroTemplate(ircHeroActionChannelMessages, botName, hChannel);
    }

    protected void transmitMessage(byte[] data, String subChannel) {
        ByteArrayDataOutput out = ByteStreams.newDataOutput();
        out.writeUTF("Forward");
        out.writeUTF("ALL");
        out.writeUTF(subChannel);
        out.writeShort(data.length);
        out.write(data);
        for (ServerInfo server : getProxy().getServers().values()) {
            logDebug("Server: " + server.getName());
            if (!server.getPlayers().isEmpty()) {
                logDebug("Sending data");
                server.sendData("BungeeCord", out.toByteArray());
            } else {
                logDebug("Not sending data");
            }
        }
    }
}
