package be4rjp.kuroko.npc;

import be4rjp.cinema4c.data.record.RecordData;
import be4rjp.cinema4c.recorder.RecordManager;
import be4rjp.kuroko.Kuroko;
import be4rjp.kuroko.script.Script;
import be4rjp.kuroko.util.ConfigUtil;
import org.bukkit.Location;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.util.*;

public class NPCData {

    private static Map<String, NPCData> npcDataMap = new HashMap<>();
    
    public static NPCData getNPCData(String name){return npcDataMap.get(name);}

    public static Set<String> getAllDataName(){return npcDataMap.keySet();}
    
    public static void loadAllNPCData(){
        npcDataMap.clear();
        
        Kuroko.getPlugin().getLogger().info("Loading npc data...");
        File dir = new File("plugins/Kuroko/data");
    
        dir.getParentFile().mkdir();
        dir.mkdir();
        File[] files = dir.listFiles();
        if (files.length == 0) {
            Kuroko.getPlugin().saveResource("data/example-data.yml", false);
            files = dir.listFiles();
        }
    
        if (files != null) {
            for (File file : files) {
                Kuroko.getPlugin().getLogger().info(file.getName());
                String id = file.getName().replace(".yml", "");
                try {
                    YamlConfiguration yml = YamlConfiguration.loadConfiguration(file);
                    NPCData npcData = new NPCData(id);
                    npcData.load(yml);
                }catch (Exception e){e.printStackTrace();}
            }
        }
    }
    
    
    
    //NPCDataのid
    private final String id;
    //設定ファイル
    private YamlConfiguration yml;
    //基礎とする録画データ
    private RecordData recordData;
    //標準座標
    private Location baseLocation;
    //ループさせるかどうか
    private boolean loop = false;
    //開始tick
    private int startTick = 0;
    //停止tick
    private int endTick = 0;
    //セリフ
    private List<Speech> speeches;
    //描画範囲外でアンロードさせるかどうか
    private boolean distanceUnload = true;
    //セリフをリセットする距離
    private double speechResetDistance = -1;
    //スクリプト
    private Script script = null;
    //会話機能の無効化
    private boolean disableSpeech = false;
    
    
    public NPCData(String id){
        this.id = id;
        npcDataMap.put(id, this);
    }
    
    public NPCData(String id, RecordData recordData, Location location, int endTick){
        this.id = id;
        this.recordData = recordData;
        this.baseLocation = location;
        this.endTick = endTick;
        npcDataMap.put(id, this);
    }
    
    public boolean isLoop() {return loop;}
    
    public int getEndTick() {return endTick;}
    
    public int getStartTick() {return startTick;}
    
    public RecordData getRecordData() {return recordData;}
    
    public String getId() {return id;}
    
    public List<Speech> getSpeeches() {return speeches;}
    
    public Speech getRandomSpeech(){return speeches.get(new Random().nextInt(speeches.size()));}
    
    public Location getBaseLocation() {return baseLocation;}
    
    public boolean isDistanceUnload() {return distanceUnload;}
    
    public double getSpeechResetDistance() {return speechResetDistance;}

    public Script getNpcScript() {return script;}

    public boolean isDisableSpeech() {return disableSpeech;}

    public void load(YamlConfiguration yml){
        this.yml = yml;
        
        if(yml.contains("record-data")) this.recordData = RecordManager.getRecordData(yml.getString("record-data"));
        if(yml.contains("loop")) this.loop = yml.getBoolean("loop");
        if(yml.contains("start-tick")) this.startTick = yml.getInt("start-tick");
        if(yml.contains("end-tick")) this.endTick = yml.getInt("end-tick");
        if(yml.contains("base-location")) this.baseLocation = ConfigUtil.getLocationByString(yml.getString("base-location"));
        if(yml.contains("distance-unload")) this.distanceUnload = yml.getBoolean("distance-unload");
        if(yml.contains("speech-reset-distance")) this.speechResetDistance = yml.getDouble("speech-reset-distance");
        if(yml.contains("script")){
            this.script = Script.getNPCScript(yml.getString("script"));
            if(this.script == null){
                Kuroko.getPlugin().getLogger().info("Javascript file '" + yml.getString("script") + "' is not found.");
            }
        }
        if(yml.contains("disable-speech")) this.disableSpeech = yml.getBoolean("disable-speech");
        
        if(yml.contains("speech")){
            this.speeches = new ArrayList<>();
            for(String key : yml.getConfigurationSection("speech").getKeys(false)){
                this.speeches.add(new Speech(yml.getStringList("speech." + key)));
            }
        }
    }
    
    public void save(YamlConfiguration yml){
        yml.set("record-data", id);
        yml.set("loop", loop);
        yml.set("start-tick", startTick);
        yml.set("end-tick", endTick);
        yml.set("base-location", baseLocation.getWorld().getName() + ", " + baseLocation.getBlockX() + ", " + baseLocation.getBlockY() + ", " + baseLocation.getBlockZ());
        yml.set("distance-unload", distanceUnload);
        yml.set("speech-reset-distance", speechResetDistance);
        yml.set("script", "");
        yml.set("disable-speech", disableSpeech);
    }
}
