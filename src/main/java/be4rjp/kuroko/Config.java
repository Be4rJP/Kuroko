package be4rjp.kuroko;

import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;

public class Config {
    
    private static int viewDistance = 4;
    
    public static int getViewDistance() {return viewDistance;}
    
    public static void load(){
        File file = new File("plugins/Kuroko", "config.yml");
        file.getParentFile().mkdirs();
        
        if(!file.exists()){
            Kuroko.getPlugin().saveResource("config.yml", false);
        }
        
        //ロードと値の保持
        YamlConfiguration yml = YamlConfiguration.loadConfiguration(file);
        
        if(yml.contains("view-distance")) viewDistance = yml.getInt("view-distance");
    }
}
