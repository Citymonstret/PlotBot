package xyz.kvantum.plotbot.commands;

import com.github.intellectualsites.plotsquared.bukkit.util.BukkitLegacyMappings;
import com.github.intellectualsites.plotsquared.bukkit.util.BukkitUtil;
import com.github.intellectualsites.plotsquared.plot.IPlotMain;
import com.github.intellectualsites.plotsquared.plot.PlotSquared;
import com.github.intellectualsites.plotsquared.plot.flag.Flag;
import com.github.intellectualsites.plotsquared.plot.flag.Flags;
import com.github.intellectualsites.plotsquared.plot.generator.GeneratorWrapper;
import com.github.intellectualsites.plotsquared.plot.generator.HybridUtils;
import com.github.intellectualsites.plotsquared.plot.generator.IndependentPlotGenerator;
import com.github.intellectualsites.plotsquared.plot.object.BlockRegistry;
import com.github.intellectualsites.plotsquared.plot.object.PlotPlayer;
import com.github.intellectualsites.plotsquared.plot.util.AbstractTitle;
import com.github.intellectualsites.plotsquared.plot.util.ChatManager;
import com.github.intellectualsites.plotsquared.plot.util.ChunkManager;
import com.github.intellectualsites.plotsquared.plot.util.EconHandler;
import com.github.intellectualsites.plotsquared.plot.util.EventUtil;
import com.github.intellectualsites.plotsquared.plot.util.InventoryUtil;
import com.github.intellectualsites.plotsquared.plot.util.LegacyMappings;
import com.github.intellectualsites.plotsquared.plot.util.SchematicHandler;
import com.github.intellectualsites.plotsquared.plot.util.SetupUtils;
import com.github.intellectualsites.plotsquared.plot.util.StringComparison;
import com.github.intellectualsites.plotsquared.plot.util.TaskManager;
import com.github.intellectualsites.plotsquared.plot.util.UUIDHandlerImplementation;
import com.github.intellectualsites.plotsquared.plot.util.WorldUtil;
import com.github.intellectualsites.plotsquared.plot.util.block.QueueProvider;
import com.intellectualsites.commands.Command;
import com.intellectualsites.commands.CommandDeclaration;
import com.intellectualsites.commands.CommandInstance;
import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.time.Instant;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import net.dv8tion.jda.core.EmbedBuilder;
import org.apache.commons.lang.StringUtils;
import org.bukkit.BanList;
import org.bukkit.BanList.Type;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Keyed;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.OfflinePlayer;
import org.bukkit.Server;
import org.bukkit.StructureType;
import org.bukkit.Tag;
import org.bukkit.UnsafeValues;
import org.bukkit.Warning.WarningState;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.advancement.Advancement;
import org.bukkit.block.data.BlockData;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarFlag;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.boss.KeyedBossBar;
import org.bukkit.command.CommandException;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.generator.ChunkGenerator.ChunkData;
import org.bukkit.help.HelpMap;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemFactory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Merchant;
import org.bukkit.inventory.Recipe;
import org.bukkit.loot.LootTable;
import org.bukkit.map.MapView;
import org.bukkit.material.MaterialData;
import org.bukkit.plugin.InvalidPluginException;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.ServicesManager;
import org.bukkit.plugin.messaging.Messenger;
import org.bukkit.scheduler.BukkitScheduler;
import org.bukkit.scoreboard.ScoreboardManager;
import org.bukkit.util.CachedServerIcon;
import xyz.kvantum.plotbot.DiscordCommandCaller;

@CommandDeclaration(command = "plotflag", permission = "all", usage = "!flag <flag>", aliases = {"pf", "flag", "pflag"} )
public class PlotSquaredFlag extends Command {

  private static final Collection<Flag<?>> flags = Flags.getFlags();

  private static final Collection<String> getFlagNames() {
    return flags.stream().map(Flag::getName).collect(Collectors.toList());
  }

  public PlotSquaredFlag() {
    new PlotSquared(new IPlotMain() {
      @Override
      public void log(String s) {

      }

      @Override
      public File getDirectory() {
        return null;
      }

      @Override
      public File getWorldContainer() {
        return null;
      }

      @Override
      public PlotPlayer wrapPlayer(Object o) {
        return null;
      }

      @Override
      public void disable() {

      }

      @Override
      public void shutdown() {

      }

      @Override
      public int[] getPluginVersion() {
        return new int[0];
      }

      @Override
      public String getPluginVersionString() {
        return null;
      }

      @Override
      public String getPluginName() {
        return null;
      }

      @Override
      public int[] getServerVersion() {
        return new int[0];
      }

      @Override
      public String getNMSPackage() {
        return null;
      }

      @Override
      public SchematicHandler initSchematicHandler() {
        return null;
      }

      @Override
      public ChatManager initChatManager() {
        return null;
      }

      @Override
      public TaskManager getTaskManager() {
        return null;
      }

      @Override
      public void runEntityTask() {

      }

      @Override
      public void registerCommands() {

      }

      @Override
      public void registerPlayerEvents() {

      }

      @Override
      public void registerInventoryEvents() {

      }

      @Override
      public void registerPlotPlusEvents() {

      }

      @Override
      public void registerForceFieldEvents() {

      }

      @Override
      public boolean initWorldEdit() {
        return false;
      }

      @Override
      public EconHandler getEconomyHandler() {
        return null;
      }

      @Override
      public QueueProvider initBlockQueue() {
        return null;
      }

      @Override
      public WorldUtil initWorldUtil() {
        return null;
      }

      @Override
      public EventUtil initEventUtil() {
        return null;
      }

      @Override
      public ChunkManager initChunkManager() {
        return null;
      }

      @Override
      public SetupUtils initSetupUtils() {
        return null;
      }

      @Override
      public HybridUtils initHybridUtils() {
        return null;
      }

      @Override
      public void startMetrics() {

      }

      @Override
      public void setGenerator(String s) {

      }

      @Override
      public UUIDHandlerImplementation initUUIDHandler() {
        return null;
      }

      @Override
      public InventoryUtil initInventoryUtil() {
        return null;
      }

      @Override
      public boolean initPlotMeConverter() {
        return false;
      }

      @Override
      public void unregister(PlotPlayer plotPlayer) {

      }

      @Override
      public GeneratorWrapper<?> getGenerator(String s, String s1) {
        return null;
      }

      @Override
      public GeneratorWrapper<?> wrapPlotGenerator(String s,
          IndependentPlotGenerator independentPlotGenerator) {
        return null;
      }

      @Override
      public void registerChunkProcessor() {

      }

      @Override
      public void registerWorldEvents() {

      }

      @Override
      public IndependentPlotGenerator getDefaultGenerator() {
        return null;
      }

      @Override
      public AbstractTitle initTitleManager() {
        return null;
      }

      @Override
      public List<String> getPluginIds() {
        return null;
      }

      @Override
      public BlockRegistry<?> getBlockRegistry() {
        return null;
      }

      @Override
      public LegacyMappings getLegacyMappings() {
        return new BukkitLegacyMappings();
      }
    }, "plotbot");
    Bukkit.setServer(new Server() {
      @Override
      public String getName() {
        return "plotbot";
      }

      @Override
      public String getVersion() {
        return "fake";
      }

      @Override
      public String getBukkitVersion() {
        return "fake";
      }

      @Override
      public Collection<? extends Player> getOnlinePlayers() {
        return null;
      }

      @Override
      public int getMaxPlayers() {
        return 0;
      }

      @Override
      public int getPort() {
        return 0;
      }

      @Override
      public int getViewDistance() {
        return 0;
      }

      @Override
      public String getIp() {
        return null;
      }

      @Override
      public String getServerName() {
        return null;
      }

      @Override
      public String getServerId() {
        return null;
      }

      @Override
      public String getWorldType() {
        return null;
      }

      @Override
      public boolean getGenerateStructures() {
        return false;
      }

      @Override
      public boolean getAllowEnd() {
        return false;
      }

      @Override
      public boolean getAllowNether() {
        return false;
      }

      @Override
      public boolean hasWhitelist() {
        return false;
      }

      @Override
      public void setWhitelist(boolean value) {

      }

      @Override
      public Set<OfflinePlayer> getWhitelistedPlayers() {
        return null;
      }

      @Override
      public void reloadWhitelist() {

      }

      @Override
      public int broadcastMessage(String message) {
        return 0;
      }

      @Override
      public String getUpdateFolder() {
        return null;
      }

      @Override
      public File getUpdateFolderFile() {
        return null;
      }

      @Override
      public long getConnectionThrottle() {
        return 0;
      }

      @Override
      public int getTicksPerAnimalSpawns() {
        return 0;
      }

      @Override
      public int getTicksPerMonsterSpawns() {
        return 0;
      }

      @Override
      public Player getPlayer(String name) {
        return null;
      }

      @Override
      public Player getPlayerExact(String name) {
        return null;
      }

      @Override
      public List<Player> matchPlayer(String name) {
        return null;
      }

      @Override
      public Player getPlayer(UUID id) {
        return null;
      }

      @Override
      public PluginManager getPluginManager() {
        return null;
      }

      @Override
      public BukkitScheduler getScheduler() {
        return null;
      }

      @Override
      public ServicesManager getServicesManager() {
        return null;
      }

      @Override
      public List<World> getWorlds() {
        return null;
      }

      @Override
      public World createWorld(WorldCreator creator) {
        return null;
      }

      @Override
      public boolean unloadWorld(String name, boolean save) {
        return false;
      }

      @Override
      public boolean unloadWorld(World world, boolean save) {
        return false;
      }

      @Override
      public World getWorld(String name) {
        return null;
      }

      @Override
      public World getWorld(UUID uid) {
        return null;
      }

      @Override
      public MapView getMap(short id) {
        return null;
      }

      @Override
      public MapView createMap(World world) {
        return null;
      }

      @Override
      public ItemStack createExplorerMap(World world, Location location,
          StructureType structureType) {
        return null;
      }

      @Override
      public ItemStack createExplorerMap(World world, Location location,
          StructureType structureType, int radius, boolean findUnexplored) {
        return null;
      }

      @Override
      public void reload() {

      }

      @Override
      public void reloadData() {

      }

      @Override
      public Logger getLogger() {
        return Logger.getLogger("null");
      }

      @Override
      public PluginCommand getPluginCommand(String name) {
        return null;
      }

      @Override
      public void savePlayers() {

      }

      @Override
      public boolean dispatchCommand(CommandSender sender, String commandLine)
          throws CommandException {
        return false;
      }

      @Override
      public boolean addRecipe(Recipe recipe) {
        return false;
      }

      @Override
      public List<Recipe> getRecipesFor(ItemStack result) {
        return null;
      }

      @Override
      public Iterator<Recipe> recipeIterator() {
        return null;
      }

      @Override
      public void clearRecipes() {

      }

      @Override
      public void resetRecipes() {

      }

      @Override
      public Map<String, String[]> getCommandAliases() {
        return null;
      }

      @Override
      public int getSpawnRadius() {
        return 0;
      }

      @Override
      public void setSpawnRadius(int value) {

      }

      @Override
      public boolean getOnlineMode() {
        return false;
      }

      @Override
      public boolean getAllowFlight() {
        return false;
      }

      @Override
      public boolean isHardcore() {
        return false;
      }

      @Override
      public void shutdown() {

      }

      @Override
      public int broadcast(String message, String permission) {
        return 0;
      }

      @Override
      public OfflinePlayer getOfflinePlayer(String name) {
        return null;
      }

      @Override
      public OfflinePlayer getOfflinePlayer(UUID id) {
        return null;
      }

      @Override
      public Set<String> getIPBans() {
        return null;
      }

      @Override
      public void banIP(String address) {

      }

      @Override
      public void unbanIP(String address) {

      }

      @Override
      public Set<OfflinePlayer> getBannedPlayers() {
        return null;
      }

      @Override
      public BanList getBanList(Type type) {
        return null;
      }

      @Override
      public Set<OfflinePlayer> getOperators() {
        return null;
      }

      @Override
      public GameMode getDefaultGameMode() {
        return null;
      }

      @Override
      public void setDefaultGameMode(GameMode mode) {

      }

      @Override
      public ConsoleCommandSender getConsoleSender() {
        return null;
      }

      @Override
      public File getWorldContainer() {
        return null;
      }

      @Override
      public OfflinePlayer[] getOfflinePlayers() {
        return new OfflinePlayer[0];
      }

      @Override
      public Messenger getMessenger() {
        return null;
      }

      @Override
      public HelpMap getHelpMap() {
        return null;
      }

      @Override
      public Inventory createInventory(InventoryHolder owner, InventoryType type) {
        return null;
      }

      @Override
      public Inventory createInventory(InventoryHolder owner, InventoryType type, String title) {
        return null;
      }

      @Override
      public Inventory createInventory(InventoryHolder owner, int size)
          throws IllegalArgumentException {
        return null;
      }

      @Override
      public Inventory createInventory(InventoryHolder owner, int size, String title)
          throws IllegalArgumentException {
        return null;
      }

      @Override
      public Merchant createMerchant(String title) {
        return null;
      }

      @Override
      public int getMonsterSpawnLimit() {
        return 0;
      }

      @Override
      public int getAnimalSpawnLimit() {
        return 0;
      }

      @Override
      public int getWaterAnimalSpawnLimit() {
        return 0;
      }

      @Override
      public int getAmbientSpawnLimit() {
        return 0;
      }

      @Override
      public boolean isPrimaryThread() {
        return false;
      }

      @Override
      public String getMotd() {
        return null;
      }

      @Override
      public String getShutdownMessage() {
        return null;
      }

      @Override
      public WarningState getWarningState() {
        return null;
      }

      @Override
      public ItemFactory getItemFactory() {
        return null;
      }

      @Override
      public ScoreboardManager getScoreboardManager() {
        return null;
      }

      @Override
      public CachedServerIcon getServerIcon() {
        return null;
      }

      @Override
      public CachedServerIcon loadServerIcon(File file) throws IllegalArgumentException, Exception {
        return null;
      }

      @Override
      public CachedServerIcon loadServerIcon(BufferedImage image)
          throws IllegalArgumentException, Exception {
        return null;
      }

      @Override
      public void setIdleTimeout(int threshold) {

      }

      @Override
      public int getIdleTimeout() {
        return 0;
      }

      @Override
      public ChunkData createChunkData(World world) {
        return null;
      }

      @Override
      public BossBar createBossBar(String title, BarColor color, BarStyle style, BarFlag... flags) {
        return null;
      }

      @Override
      public KeyedBossBar createBossBar(NamespacedKey key, String title, BarColor color,
          BarStyle style, BarFlag... flags) {
        return null;
      }

      @Override
      public Iterator<KeyedBossBar> getBossBars() {
        return null;
      }

      @Override
      public KeyedBossBar getBossBar(NamespacedKey key) {
        return null;
      }

      @Override
      public boolean removeBossBar(NamespacedKey key) {
        return false;
      }

      @Override
      public Entity getEntity(UUID uuid) {
        return null;
      }

      @Override
      public Advancement getAdvancement(NamespacedKey key) {
        return null;
      }

      @Override
      public Iterator<Advancement> advancementIterator() {
        return null;
      }

      @Override
      public BlockData createBlockData(Material material) {
        return null;
      }

      @Override
      public BlockData createBlockData(Material material, Consumer<BlockData> consumer) {
        return null;
      }

      @Override
      public BlockData createBlockData(String data) throws IllegalArgumentException {
        return null;
      }

      @Override
      public BlockData createBlockData(Material material, String data)
          throws IllegalArgumentException {
        return null;
      }

      @Override
      public <T extends Keyed> Tag<T> getTag(String registry, NamespacedKey tag, Class<T> clazz) {
        return null;
      }

      @Override
      public LootTable getLootTable(NamespacedKey key) {
        return null;
      }

      @Override
      public UnsafeValues getUnsafe() {
        return new UnsafeValues() {
          @Override
          public Material toLegacy(Material material) {
            return null;
          }

          @Override
          public Material fromLegacy(Material material) {
            if (material == null) {
              return Material.AIR;
            }
            return Material.valueOf(material.name().replace("LEGACY_", ""));
          }

          @Override
          public Material fromLegacy(MaterialData material) {
            return null;
          }

          @Override
          public Material fromLegacy(MaterialData material, boolean itemPriority) {
            return null;
          }

          @Override
          public BlockData fromLegacy(Material material, byte data) {
            return null;
          }

          @Override
          public int getDataVersion() {
            return 0;
          }

          @Override
          public ItemStack modifyItemStack(ItemStack stack, String arguments) {
            return null;
          }

          @Override
          public void checkSupported(PluginDescriptionFile pdf) throws InvalidPluginException {

          }

          @Override
          public byte[] processClass(PluginDescriptionFile pdf, String path, byte[] clazz) {
            return new byte[0];
          }

          @Override
          public Advancement loadAdvancement(NamespacedKey key, String advancement) {
            return null;
          }

          @Override
          public boolean removeAdvancement(NamespacedKey key) {
            return false;
          }
        };
      }

      @Override
      public Spigot spigot() {
        return null;
      }

      @Override
      public void sendPluginMessage(Plugin source, String channel, byte[] message) {

      }

      @Override
      public Set<String> getListeningPluginChannels() {
        return null;
      }
    });
    WorldUtil.IMP = new BukkitUtil();
  }

  @Override
  public boolean onCommand(CommandInstance instance) {
    final DiscordCommandCaller discordCommandCaller = (DiscordCommandCaller) instance.getCaller();

    if (instance.getArguments().length < 1) {
      return false;
    }

    if (instance.getArguments()[0].equalsIgnoreCase("list")) {
      discordCommandCaller.message(String.format("Available flags: %s", StringUtils.join(getFlagNames(), ", ")));
      return true;
    }

    final String flag = instance.getArguments()[0].toLowerCase();
    final Flag flg = Flags.getFlag(flag);
    if (flg == null) {
      discordCommandCaller.message("That is not a valid flag");
      try {
        final String rflag = new StringComparison<>(flag, getFlagNames())
            .getMatchObject();
        if (rflag != null) {
          discordCommandCaller.message(String.format("Did you mean: %s", rflag));
        }
      } catch (final Exception e) {
        e.printStackTrace();
      }
    } else {
      Object value = null;
      if (instance.getArguments().length > 1) {
        try {
          final StringBuilder arg = new StringBuilder();
          for (int i = 1; i < instance.getArguments().length; i++) {
            arg.append(instance.getArguments()[i]);
            if ((i + 1) < instance.getArguments().length) {
              arg.append(" ");
            }
          }
          value = flg.parseValue(arg.toString());
        } catch (final Exception e) {
          value = String.format("Failed to parse: %s", e.getMessage());
          e.printStackTrace();
        } finally {
          if (value == null) {
            value = "Invalid value";
          }
        }
      }

      final EmbedBuilder embed = new EmbedBuilder().setTitle("Flag Information", "https://github.com/IntellectualSites/PlotSquared/blob/breaking/Core/src/main/java/com/github/intellectualsites/plotsquared/plot/flag/Flags.java")
          .addField("Flag", "/plot flag set " + flg.getName() + " <value>", true)
          .addField("Usage", flg.getValueDescription(), true)
          .addField("Type", flg.getClass().getSimpleName(), true);

      if (value != null) {
          embed.addField("Flag response when parsing value \"" + instance.getArguments()[1] + "\"", value.toString(), false);
      }

      embed.setColor(Color.red)
          .setFooter("Requested by: " + discordCommandCaller.getMessage().getMember().getEffectiveName(), discordCommandCaller.getMessage().getMember().getUser().getAvatarUrl())
          .setTimestamp(Instant.now())
          .build();
      discordCommandCaller.getChannel().sendMessage(embed.build()).queue();
    }
    return true;
  }
}
