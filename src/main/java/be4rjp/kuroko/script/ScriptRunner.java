package be4rjp.kuroko.script;

import be4rjp.kuroko.Config;
import be4rjp.kuroko.npc.NPC;
import be4rjp.kuroko.npc.Speech;
import be4rjp.kuroko.player.KurokoPlayer;

import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ScriptRunner {
    
    private static Map<String, Class<?>> classMap = new ConcurrentHashMap<>();

    private final NPCScript npcScript;
    private final ScriptEngineManager scriptEngineManager;
    private final ScriptEngine scriptEngine;

    public ScriptRunner(NPCScript npcScript){
        this.npcScript = npcScript;
        this.scriptEngineManager = new ScriptEngineManager();
        this.scriptEngine = scriptEngineManager.getEngineByName(Config.getJavascriptEngine());
        try {
            for(String classPath : Config.getLoadClasses()){
                Class<?> clazz;
                if(classMap.containsKey(classPath)){
                    clazz = classMap.get(classPath);
                }else {
                    clazz = Class.forName(classPath);
                    classMap.put(classPath, clazz);
                }
                String simpleName = clazz.getSimpleName();
                
                scriptEngine.put("CLASS_" + simpleName, clazz);
            }
            
            scriptEngine.eval(npcScript.getScript());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void runFunction(String function, Object... objects){
        Invocable invocable = (Invocable) scriptEngine;
        try {
            invocable.invokeFunction(function, objects);
        } catch (Exception e) {e.printStackTrace();}
    }
}
