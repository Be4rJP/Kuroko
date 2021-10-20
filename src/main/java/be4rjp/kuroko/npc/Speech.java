package be4rjp.kuroko.npc;

import java.util.ArrayList;
import java.util.List;

public class Speech {
    
    private final List<String> lines;
    
    public Speech(List<String> lines){
        this.lines = lines;
    }

    public Speech(){
        this.lines = new ArrayList<>();
    }

    public void addLine(String line){
        this.lines.add(line);
    }
    
    public String getLine(int index){
        if(index >= lines.size()) return null;
        return lines.get(index);
    }
}
