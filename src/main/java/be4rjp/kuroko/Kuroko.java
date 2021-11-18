package be4rjp.kuroko;

import be4rjp.kuroko.command.kurokoCommand;
import be4rjp.kuroko.listener.PlayerItemSwitchListener;
import be4rjp.kuroko.listener.PlayerJoinQuitListener;
import be4rjp.kuroko.npc.NPCData;
import be4rjp.kuroko.script.Script;
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
        Script.loadAllNPCScript();
        NPCData.loadAllNPCData();
    
        PluginManager pluginManager = Bukkit.getPluginManager();
        pluginManager.registerEvents(new PlayerJoinQuitListener(), this);
        pluginManager.registerEvents(new PlayerItemSwitchListener(), this);

        //Register command executors
        getLogger().info("Registering command executors...");
        Objects.requireNonNull(getCommand("kuroko")).setExecutor(new kurokoCommand());
        Objects.requireNonNull(getCommand("kuroko")).setTabCompleter(new kurokoCommand());
    }
    
    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
    
    public static Kuroko getPlugin() {return plugin;}
}
