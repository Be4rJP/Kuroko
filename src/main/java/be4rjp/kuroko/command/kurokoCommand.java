package be4rjp.kuroko.command;

import be4rjp.kuroko.npc.NPCData;
import be4rjp.kuroko.player.KurokoPlayer;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class kurokoCommand implements CommandExecutor, TabExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
        if (args == null) return false;
        if (args.length == 0) return false;

        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "このコマンドはコンソールから実行できません。");
            return true;
        }

        Player player = (Player) sender;
        KurokoPlayer kurokoPlayer = KurokoPlayer.getKurokoPlayer(player);

        switch (args[0]) {
            case "spawn": {
                if(args.length != 2) return false;

                NPCData npcData = NPCData.getNPCData(args[1]);
                if(npcData == null){
                    player.sendMessage(ChatColor.RED + "NPCデータが見つかりませんでした。");
                    return true;
                }

                kurokoPlayer.addNPC(npcData);
                player.sendMessage(ChatColor.GREEN + "追加しました。");
                return true;
            }

            case "create": {
                if(args.length != 2) return false;
                String name = args[1];
                if(NPCData.getNPCData(name) != null) {
                    player.sendMessage(ChatColor.RED + "指定された名前のNPCは既に存在しています。");
                    return true;
                }

                kurokoPlayer.setCurrentSetupNPCName(name);
                player.sendMessage(ChatColor.GREEN + "NPC作成の準備が整いました。");
                player.sendMessage(ChatColor.GREEN + "アイテム持ち替えキーで記録を開始し、もう一度キーを押すと記録を終了します。");
                return true;
            }
        }

        return true;
    }


    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {

        List<String> list = new ArrayList<>();

        if (args.length == 1) {
            list.add("record");
            list.add("recorder");
            list.add("movie");
            list.add("reload");
            list.add("help");

            return list;
        }

        if (args.length == 2){
            if(args[0].equals("spawn")){
                list.addAll(NPCData.getAllDataName());
                return list;
            }

            if(args[0].equals("create")){
                list.add("[name]");
                return list;
            }
        }


        return null;
    }
}
