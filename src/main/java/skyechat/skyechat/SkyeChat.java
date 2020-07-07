package skyechat.skyechat;

import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.IOException;
import java.util.ArrayList;

public final class SkyeChat extends JavaPlugin {

    ChatListener chatListener;

    @Override
    public void onEnable() {
        // init config

        try {
            this.getConfig().load("plugins/SkyeChat/config.yml");
        } catch (IOException | InvalidConfigurationException e) {
            e.printStackTrace();

            this.getConfig().addDefault("playerNames", new ArrayList<String>());
            this.getConfig().addDefault("playerIntroMessages", new ArrayList<String>());
            this.getConfig().addDefault("playerDeathMessages", new ArrayList<String>());
            this.getConfig().addDefault("playerOutroMessages", new ArrayList<String>());
            this.getConfig().options().copyDefaults(true);
            this.saveConfig();
        }

        // init listeners
        chatListener = new ChatListener(this);
        this.getServer().getPluginManager().registerEvents(chatListener, this);

        new TabList().runTaskTimer(this,0L,10L);
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
