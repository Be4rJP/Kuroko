package be4rjp.kuroko.script;

import be4rjp.kuroko.Config;

import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ScriptRunner {
    
    private static Map<String, Class<?>> classMap = new ConcurrentHashMap<>();

    private final Script script;
    private final ScriptEngineManager scriptEngineManager;
    private final ScriptEngine scriptEngine;

    public ScriptRunner(Script script){
        this.script = script;
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
            
            scriptEngine.eval(script.getScript());
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
