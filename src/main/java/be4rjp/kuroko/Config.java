package be4rjp.kuroko;

import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class Config {
    
    private static int viewDistance = 4;
    
    private static List<String> defaultLoadNPC = new ArrayList<>();

    private static String javascriptEngine;
    
    public static int getViewDistance() {return viewDistance;}
    
    public static List<String> getDefaultLoadNPCList() {return defaultLoadNPC;}

    public static String getJavascriptEngine() {return javascriptEngine;}

    public static void load(){
        File file = new File("plugins/Kuroko", "config.yml");
        file.getParentFile().mkdirs();
        
        if(!file.exists()){
            Kuroko.getPlugin().saveResource("config.yml", false);
        }
        
        //ロードと値の保持
        YamlConfiguration yml = YamlConfiguration.loadConfiguration(file);
        
        if(yml.contains("view-distance")) viewDistance = yml.getInt("view-distance");
        if(yml.contains("default-load-npc")) defaultLoadNPC = yml.getStringList("default-load-npc");
        if(yml.contains("javascript-engine")) javascriptEngine = yml.getString("javascript-engine");
    }
}
