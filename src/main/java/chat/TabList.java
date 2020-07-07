package chat;

import net.minecraft.server.v1_16_R1.ChatComponentText;
import net.minecraft.server.v1_16_R1.PacketPlayOutPlayerListHeaderFooter;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.craftbukkit.v1_16_R1.entity.CraftPlayer;
import org.bukkit.scheduler.BukkitRunnable;

import java.lang.reflect.Field;

public class TabList extends BukkitRunnable
{
    private int colorIteration;
    private final PacketPlayOutPlayerListHeaderFooter tabList;

    public TabList(){
        tabList = new PacketPlayOutPlayerListHeaderFooter();
        colorIteration = 0;
    }

    @Override
    public void run()
    {
        if(colorIteration == 9)
            colorIteration = 0;

        try {
            Field head = this.tabList.getClass().getDeclaredField("header");
            head.setAccessible(true);

            Field foot = this.tabList.getClass().getDeclaredField("footer");
            foot.setAccessible(true);

            head.set(this.tabList,new ChatComponentText(this.createHeader()));
            foot.set(this.tabList,new ChatComponentText(this.createFooter()));

            if(Bukkit.getOnlinePlayers().size() == 0) return;

            Bukkit.getOnlinePlayers().forEach(player ->
                ((CraftPlayer)player).getHandle().playerConnection.sendPacket(tabList));

            colorIteration++;
        } catch (NoSuchFieldException | IllegalAccessException e) {
                e.printStackTrace();
        }
    }

    private String createHeader() {
        String aqua = ChatColor.AQUA + "" + ChatColor.ITALIC;

        int playersOnline = Bukkit.getOnlinePlayers().size();
        String singOrPlur = playersOnline > 1 ? "players" : "player";

        String out =  ChatColor.LIGHT_PURPLE + "VRChat " + ChatColor.DARK_PURPLE + "Ganggg\n" + ChatColor.GRAY + "" +
                aqua + playersOnline + ChatColor.GRAY + " " + ChatColor.ITALIC + singOrPlur + " online\n";
//                ChatColor.ITALIC + ": " + aqua +
//                Bukkit.getOnlinePlayers().size() + "\n";

        out = out.concat(this.createLines(false));
        return out.trim();
    }

    private String createFooter() {
        return "\n";
//        return createLines(true);
    }

    private String createLines(boolean backwards) {
        String out = "";
        if(backwards) {
            for(int i = 9; i > 0; i--) {
                out = out.concat((i == colorIteration)
                        ? ChatColor.AQUA + "" + ChatColor.ITALIC + " -"
                        : ChatColor.DARK_AQUA + "" + ChatColor.ITALIC + " -");
            }
        } else {
            for (int i = 0; i < 9; i++) {
                out = out.concat((i == colorIteration)
                        ? ChatColor.AQUA + "" + ChatColor.ITALIC + "- "
                        : ChatColor.DARK_AQUA + "" + ChatColor.ITALIC + "- ");
            }
        }

        return out.trim();
    }
}
