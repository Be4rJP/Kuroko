package be4rjp.kuroko;

import be4rjp.kuroko.listener.PlayerJoinQuitListener;
import be4rjp.kuroko.npc.NPCData;
import org.bukkit.Bukkit;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

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
    }
    
    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
    
    public static Kuroko getPlugin() {return plugin;}
}
