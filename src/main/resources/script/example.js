
var npcInstance;

function initialize(npc){
    npcInstance = npc;

    npc.setSpikeFunction(100, "testFunction");

}

function onClick(kurokoPlayer, npc){
    kurokoPlayer.getPlayer().sendMessage("you clicked!");
}


function testFunction(){
    npcInstance.getKurokoPlayer().getPlayer().sendMessage("Test message.");
}