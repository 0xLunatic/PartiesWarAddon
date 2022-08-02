package war.addon;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import war.addon.commands.PartiesWarCommand;
import war.addon.data.DataManager;

import java.io.File;
import java.util.Objects;

public final class Main extends JavaPlugin {

    FileConfiguration config = getConfig();

    public DataManager data;

    @Override
    public void onEnable() {
        // Plugin startup logic

        data = new DataManager(this);

        File configFile = new File(getDataFolder(), "config.yml");
        if (!configFile.exists()) {
            saveResource("config.yml", true);
        }else{
            saveDefaultConfig();
        }

        if (getServer().getPluginManager().isPluginEnabled("Parties")) {
            Objects.requireNonNull(getCommand("clanwars")).setExecutor(new PartiesWarCommand(this));

            getServer().getPluginManager().registerEvents(new PartiesWarCommand(this), this);
            System.out.println("§aClan Wars Plugin Enabled!");
        }else{
            System.out.println("§cParties Plugin not installed! Disabling the plugin!");
            this.getPluginLoader().disablePlugin(this);
        }

    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
