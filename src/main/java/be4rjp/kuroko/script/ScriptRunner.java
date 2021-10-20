package be4rjp.kuroko.script;

import be4rjp.kuroko.Config;

import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;

public class ScriptRunner {

    private final NPCScript npcScript;
    private final ScriptEngineManager scriptEngineManager;
    private final ScriptEngine scriptEngine;

    public ScriptRunner(NPCScript npcScript){
        this.npcScript = npcScript;
        this.scriptEngineManager = new ScriptEngineManager();
        this.scriptEngine = scriptEngineManager.getEngineByName(Config.getJavascriptEngine());
    }

    public void runFunction(String function, Object... objects){
        Invocable invocable = (Invocable) scriptEngine;
        try {
            invocable.invokeFunction(function, objects);
        } catch (Exception e) {e.printStackTrace();}
    }
}
