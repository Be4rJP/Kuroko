var npcInstance;
var Speech = CLASS_Speech.static;
var Material = CLASS_Material.static;
var ItemStack = CLASS_ItemStack.static;
var NPCAnimation = CLASS_Animation.static;

var questProgress = 0;

/**
 * NPCがスポーンしたときに呼び出される関数
 * @param npc スポーンするNPC(JavaのObject)
 */
function initialize(npc){
    npcInstance = npc;

    //スパイク関数を登録
    //任意のtickで関数を実行させるように設定する
    npc.setSpikeFunction(100, "pause");
}

/**
 * プレイヤーがNPCをクリックしたときに呼び出される関数
 * @param kurokoPlayer クリックしたプレイヤー(JavaのObject)
 * @param npc クリックされたNPC(JavaのObject)
 */
function onClick(kurokoPlayer, npc){

    var player = kurokoPlayer.getPlayer();

    //スポーンしてからのtickが100に満たない場合は何もしない
    if(npc.getTick() < 100) return;

    //設定されたスピーチをすべてしゃべり終えた後、又はスピーチがまだ設定されていない場合
    if(!npc.hasSpeech()){
        var speech;

        //questProgressが0の場合(初回クリック)
        if(questProgress == 0){

            //最初のスピーチを設定
            speech = getQuestFirstSpeech();
            //クエストの状態を進める
            questProgress++;

        }
        
        else if(questProgress == 1){//questProgressが1の場合

            //プレイヤーがオークの原木を持っているかどうかチェック
            if(player.getInventory().getItemInMainHand().getType() == Material.OAK_LOG){
                //プレイヤーからオークを取り上げる
                player.getInventory().setItemInMainHand(new ItemStack(Material.AIR));
                //オークの原木を持っていればクエストクリア用のスピーチを設定する
                speech = getQuestFinishSpeech();
                //NPCが腕を振るアニメーションを再生する
                npc.playAnimation(NPCAnimation.SWING_MAIN_ARM);
                //スパイク関数登録を削除する
                npc.removeSpikeFunction(100);
                //クエストの状態を進める
                questProgress++;
            }else{
                //オークの原木を持っていなければ最初のスピーチを設定する
                speech = getQuestFirstSpeech();
            }

        }

        else if(questProgress == 2){//questProgressが2の場合
            //クエストクリア後のスピーチを設定する
            speech = getQuestEndSpeech();
        }

        //NPCにスピーチを設定
        npc.setSpeech(speech);
    }
}

/**
 * NPCとの会話(スピーチ)が終了したときの動作
 * @param kurokoPlayer クリックしたプレイヤー(JavaのObject)
 * @param npc クリックされたNPC(JavaのObject)
 */
function onSpeechEnd(kurokoPlayer, npc){
    //None
}

/**
 * questProgressが0のときのスピーチを取得
 * @returns Speech
 */
 function getQuestFirstSpeech(){
    var speech = new Speech();
    speech.addLine("be4r_jp > &6すまんがオークの原木を一つ採ってきてはくれんかね...?");
    return speech;
}

/**
 * questProgressが1のときのスピーチを取得
 * @returns Speech
 */
function getQuestFinishSpeech(){
    var speech = new Speech();
    speech.addLine("be4r_jp > &6おお！/n/be4r_jp > &6採ってきてくれたのか！");
    speech.addLine("be4r_jp > &6ありがとう！");
    speech.addLine("be4r_jp > &6これはお礼のTNTだ。ぜひ受け取ってくれ！！/n/command{console/give %player tnt}");
    return speech;
}

/**
 * questProgressが2のときのスピーチを取得
 * @returns Speech
 */
function getQuestEndSpeech(){
    var speech = new Speech();
    speech.addLine("be4r_jp > &6この前はありがとうな！");
    speech.addLine("be4r_jp > &6何かあればまた頼むぜ。");
    return speech;
}

/**
 * NPCの移動を停止
 */
function pause(){
    npcInstance.setPause(true);
}
