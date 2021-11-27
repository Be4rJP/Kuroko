package be4rjp.kuroko.util;

import org.bukkit.Bukkit;
import org.bukkit.Location;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class KurokoLocation {
    
    private final static Set<KurokoLocation> locations = ConcurrentHashMap.newKeySet();
    
    private String worldName;
    private double x;
    private double y;
    private double z;
    private float yaw;
    private float pitch;
    
    public KurokoLocation(String worldName, double x, double y, double z, float yaw, float pitch){
        this.worldName = worldName;
        this.x = x;
        this.y = y;
        this.z = z;
        this.yaw = yaw;
        this.pitch = pitch;
        
        locations.add(this);
    }
    
    public double getX() {return x;}
    
    public double getY() {return y;}
    
    public double getZ() {return z;}
    
    public float getPitch() {return pitch;}
    
    public float getYaw() {return yaw;}
    
    public Location asLocationAtMainThread(){
        if(!Bukkit.isPrimaryThread()) throw new IllegalStateException("asLocationAtMainThread() only main thread!");
        return new Location(Bukkit.getWorld(worldName), x, y, z, pitch, yaw);
    }
    
    public static Set<KurokoLocation> getLocations() {return locations;}
}
