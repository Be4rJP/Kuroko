package be4rjp.kuroko;

import be4rjp.kuroko.command.kurokoCommand;
import be4rjp.kuroko.listener.PlayerJoinQuitListener;
import be4rjp.kuroko.npc.NPCData;
import org.bukkit.Bukkit;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Objects;

public final class Kuroko extends JavaPlugin {
    
    private static Kuroko plugin;
    
    @Override
    public void onEnable() {
        // Plugin startup logic
        plugin = this;
        
        Config.load();
        NPCData.loadAllNPCData();
    
        PluginManager pluginManager = Bukkit.getPluginManager();
        pluginManager.registerEvents(new PlayerJoinQuitListener(), this);

        //Register command executors
        getLogger().info("Registering command executors...");
        Objects.requireNonNull(getCommand("c4c")).setExecutor(new kurokoCommand());
        Objects.requireNonNull(getCommand("c4c")).setTabCompleter(new kurokoCommand());
    }
    
    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
    
    public static Kuroko getPlugin() {return plugin;}
}
