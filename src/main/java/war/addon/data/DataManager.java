package war.addon.data;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import war.addon.Main;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.logging.Level;

public class DataManager {

    private final Main plugin;
    private FileConfiguration dataConfig = null;
    private File configFile = null;

    public DataManager(Main plugin) {
        this.plugin = plugin;
        saveDefaultConfig("language.yml");
        saveResource("location.yml");
    }

    public void reloadConfig(String config) {
        if (this.configFile == null)
            this.configFile = new File(this.plugin.getDataFolder(), config);

        this.dataConfig = YamlConfiguration.loadConfiguration(this.configFile);

        InputStream defaultStream = this.plugin.getResource(config);
        if (defaultStream != null) {
            YamlConfiguration defaultConfig = YamlConfiguration.loadConfiguration(new InputStreamReader(defaultStream));
            this.dataConfig.setDefaults(defaultConfig);
        }
    }

    public FileConfiguration getConfig(String config) {
        if (this.dataConfig == null)
            reloadConfig(config);
        return this.dataConfig;
    }

    public void saveConfig(String config) {
        if (this.dataConfig == null || this.configFile == null)
            return;
        try {
            this.getConfig(config).save(this.configFile);
        } catch (IOException e) {
            plugin.getLogger().log(Level.SEVERE, "Could not save config to" + this.configFile, e);
        }
    }

    public void saveDefaultConfig(String config) {
        if(this.configFile == null)
            this.configFile = new File(this.plugin.getDataFolder(), config);
        if (!this.configFile.exists()) {
            this.plugin.saveResource(config, false);
        }
    }
    public void saveResource(String data){
        this.configFile = new File(this.plugin.getDataFolder(), data);
        if (!this.configFile.exists()) {
            this.plugin.saveResource(data, false);
        }
    }
}


