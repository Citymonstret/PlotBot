package xyz.kvantum.plotbot.commands;

import com.intellectualsites.commands.Command;
import com.intellectualsites.commands.CommandDeclaration;
import com.intellectualsites.commands.CommandInstance;
import com.intellectualsites.commands.parser.impl.StringParser;
import xyz.kvantum.plotbot.BukkitLegacyMappings;
import xyz.kvantum.plotbot.BukkitLegacyMappings.LegacyBlock;
import xyz.kvantum.plotbot.StringComparison;

@CommandDeclaration(command = "material", usage = "material <material>", description = "Finds the closets matching material", permission = "all")
public class Material extends Command  {

  private final BukkitLegacyMappings bukkitLegacyMappings = new BukkitLegacyMappings();

  public Material() {
     withArgument("material", new StringParser(), "material name to match");
  }

  @Override
  public boolean onCommand(CommandInstance instance) {
    final String material = instance.getString("material");
    final org.bukkit.Material match;
    final LegacyBlock legacyBlock;
    if (material.contains(":") && !material.startsWith("minecraft")) {
      // Probably numerical
      final String [] parts = material.split(":");
      legacyBlock = bukkitLegacyMappings.fromLegacyToString(Integer.parseInt(parts[0]), Integer.parseInt(parts[0]));
      if (legacyBlock == null) {
        instance.getCaller().message("Couldn't find a material matching that ID and data pair");
        return true;
      }
      match = org.bukkit.Material.valueOf(legacyBlock.getNewName().toUpperCase());
    } else {
      final StringComparison<org.bukkit.Material> comparison = new StringComparison<>(material,
          org.bukkit.Material.values());
      match = comparison.getMatchObject();
      legacyBlock = bukkitLegacyMappings.fromStringToLegacy(match.name());
    }
    instance.getCaller().message(String.format("Closest matching material name is %s having data type %s. Minecraft name is minecraft:%s and numerical ID is %d:%d", match.name(), match.data.getSimpleName(), match.name().toLowerCase(), legacyBlock.getNumericalId(), legacyBlock.getDataValue()));

    return true;
  }
}
