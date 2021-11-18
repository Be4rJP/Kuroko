package be4rjp.kuroko.script;

import be4rjp.kuroko.Kuroko;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class Script {

    private static Map<String, Script> npcScriptMap = new HashMap<>();

    public static Script getNPCScript(String id){return npcScriptMap.get(id);}


    public static void loadAllNPCScript() {
        npcScriptMap.clear();
        
        Kuroko.getPlugin().getLogger().info("Loading scripts...");
        File dir = new File("plugins/Kuroko/script");

        dir.getParentFile().mkdir();
        dir.mkdir();
        File[] files = dir.listFiles();
        if (files.length == 0) {
            Kuroko.getPlugin().saveResource("script/example.js", false);
            files = dir.listFiles();
        }

        if (files != null) {
            for (File file : files) {
                Kuroko.getPlugin().getLogger().info(file.getName());
                String id = file.getName().replace(".js", "");
                Script script = new Script(id, file);
                script.load();
                npcScriptMap.put(id, script);
            }
        }
    }




    private final String id;

    private final File file;

    private String script;

    public Script(String id, File file){
        this.id = id;
        this.file = file;
    }

    public void load(){
        StringBuilder script = new StringBuilder();
        try{
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(new FileInputStream(file) , StandardCharsets.UTF_8));
            String data;
            while ((data = bufferedReader.readLine()) != null) {
                script.append(data);
                script.append('\n');
            }
            bufferedReader.close();
        }catch(Exception e){e.printStackTrace();}

        this.script = script.toString();
    }

    public String getScript() {return script;}

    public ScriptRunner createScriptRunner(){
        return new ScriptRunner(this);
    }
}
