package be4rjp.kuroko.npc;

import java.util.List;

public class Speech {
    
    private final List<String> lines;
    
    public Speech(List<String> lines){
        this.lines = lines;
    }
    
    public String getLine(int index){
        if(index >= lines.size()) return null;
        return lines.get(index);
    }
}
