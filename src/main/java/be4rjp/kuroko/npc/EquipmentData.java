package be4rjp.kuroko.npc;

import be4rjp.cinema4c.nms.WrappedItemSlot;
import org.bukkit.inventory.ItemStack;

import java.util.EnumMap;
import java.util.Map;

public class EquipmentData {
    
    private final NPCData npcData;
    
    private final EnumMap<WrappedItemSlot, ItemStack> itemStackEnumMap = new EnumMap<>(WrappedItemSlot.class);
    
    public EquipmentData(NPCData npcData){
        this.npcData = npcData;
        for(WrappedItemSlot wrappedItemSlot : WrappedItemSlot.values()){
            itemStackEnumMap.put(wrappedItemSlot, null);
        }
    }
    
    public void sendEquipmentPacket(NPC npc){
        for(Map.Entry<WrappedItemSlot, ItemStack> entry : itemStackEnumMap.entrySet()){
            WrappedItemSlot wrappedItemSlot = entry.getKey();
            ItemStack itemStack = entry.getValue();
            
            if(itemStack != null){
                npc.sendEquipmentPacket(wrappedItemSlot, itemStack);
            }
        }
    }
    
    public void setEquipment(WrappedItemSlot wrappedItemSlot, ItemStack itemStack){
        itemStackEnumMap.put(wrappedItemSlot, itemStack);
    }
    
    public NPCData getNpcData() {return npcData;}
}
