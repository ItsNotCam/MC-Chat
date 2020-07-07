package skyechat.skyechat;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static org.bukkit.event.entity.EntityDamageEvent.DamageCause.BLOCK_EXPLOSION;


public class ChatListener implements Listener
{
    private SkyeChat plugin;
    public static final String SkyeColor = ChatColor.AQUA + "" +
            ChatColor.ITALIC;

    public ChatListener(SkyeChat _plugin) {
        plugin = _plugin;
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent _event) {
        String ogPlayerName, playerName, introMsg;

        ogPlayerName = _event.getPlayer().getName();
        playerName = this.getOrAddPlayerName(ogPlayerName);

        _event.getPlayer().setDisplayName(playerName);
        _event.getPlayer().setPlayerListName(playerName);
        _event.setJoinMessage(this.getIntroMessage(ogPlayerName));

/*
        if(playerName.equalsIgnoreCase("moosura")) {
            _event.setJoinMessage(ChatColor.GREEN + "" + ChatColor.ITALIC + "+ " + SkyeColor + "Moosura"
                            + ChatColor.GRAY + "" + ChatColor.ITALIC + "? More like Noobsura.");
        } else if(playerName.equalsIgnoreCase("axii0m")) {
            _event.setJoinMessage(defaultMessage + ChatColor.AQUA + "" + ChatColor.ITALIC + playerName + ChatColor.GRAY
                    + "" + ChatColor.ITALIC + " the realest is here. All is well.");
        } else if(playerName.equalsIgnoreCase("neonmaenad")) {
            String neon = ChatColor.AQUA + "" + ChatColor.ITALIC + "Ne" + ChatColor.LIGHT_PURPLE + "" + ChatColor.ITALIC
                    + "on" + ChatColor.WHITE + "" + ChatColor.ITALIC + "Ma" + ChatColor.LIGHT_PURPLE + ChatColor.ITALIC
                    + "en" + ChatColor.AQUA + "" + ChatColor.ITALIC + "ad";

            _event.setJoinMessage(defaultMessage + ChatColor.GRAY + "" + ChatColor.ITALIC
                    + "Queen " + neon + ChatColor.GRAY + "" + ChatColor.ITALIC
                    + " has arrived, long may she reign.");
        } else if(playerName.equalsIgnoreCase("micthemurph")) {
            _event.setJoinMessage(defaultMessage + ChatColor.GRAY + "" + ChatColor.ITALIC + "Lol, who's "
                    + ChatColor.AQUA + "" + ChatColor.ITALIC + playerName + ChatColor.GRAY + "" + ChatColor.ITALIC + "?");
        } else {
            _event.setJoinMessage(defaultMessage + ChatColor.GRAY + "" + ChatColor.ITALIC + "My man "
                    + ChatColor.AQUA + "" + ChatColor.ITALIC + playerName + ChatColor.GRAY + "" + ChatColor.ITALIC
            + " has joined up");
        }*/
    }

    @EventHandler
    public void onLeave(PlayerQuitEvent _event) {
        String ogPlayerName = _event.getPlayer().getName();
        _event.setQuitMessage(this.getOutroMessage(ogPlayerName));
    }

    @EventHandler
    public void onDeath(EntityDeathEvent _event)
    {
        Entity dead = _event.getEntity();
        if(!(dead instanceof Player)) return;

        Player player = (Player) dead;
        EntityDamageEvent de = player.getLastDamageCause();
        if(de == null) return;

        Entity killerEntity = this.getLastEntityDamager(player);
        String killer = killerEntity == null ? "murder thingy" : killerEntity.getName();

        EntityDamageEvent.DamageCause dc = de.getCause();
        String playerName = this.getOrAddPlayerName(player.getName());
        String deathMessage = ChatColor.RED + "" + ChatColor.ITALIC + playerName;

        switch(dc) {
            case BLOCK_EXPLOSION: deathMessage += " just exploded."; break;
            case CONTACT: deathMessage += " just blindly walked into a cactus. bet."; break;
            case CRAMMING: deathMessage += " got in the way of a wall."; break;
            case DRAGON_BREATH: deathMessage += " got too close to a dragon."; break;
            case DROWNING: deathMessage += " didn't breath enough and now regrets it."; break;
            case ENTITY_ATTACK:
            case PROJECTILE:
                deathMessage += " got clapped by a " + killer + "."; break;
            case ENTITY_EXPLOSION: deathMessage += " just got creeped."; break;
            case FALL: deathMessage += " up and jumped off a cliff."; break;
            case FALLING_BLOCK:
            case SUFFOCATION:
                deathMessage += " got clocked by something heavy."; break;
            case FIRE:
            case FIRE_TICK:
            case HOT_FLOOR:
            case LAVA:
                deathMessage += " made like Anikan and burned alive."; break;
            case FLY_INTO_WALL: deathMessage += " just slammed into a fucking wall."; break;
            case LIGHTNING: deathMessage += " was cancelled by Zeus."; break;
            case MAGIC: deathMessage += " was murdered by magic."; break;
            case POISON: deathMessage += " asphyxiated. Buh-Bye!"; break;
            case STARVATION: deathMessage += " apparently forgot how to eat."; break;
            case SUICIDE: deathMessage += " should have called 1-800-273-8255."; break;
            case THORNS: deathMessage += " just got pricked by some thorns."; break;
            case VOID: deathMessage += " fell into a black hole."; break;
            case WITHER: deathMessage += " decayed a lot."; break;
        }

        String finalDeathMessage = this.getDeathMessage(player.getName(), deathMessage);
        Bukkit.getOnlinePlayers().forEach(p -> p.sendMessage(finalDeathMessage));
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent _event) {
        _event.setDeathMessage("");
    }

    @EventHandler
    public void onMessage(AsyncPlayerChatEvent e) {
        e.setCancelled(true);

        String rawMessage = e.getMessage();
        String playerName = getOrAddPlayerName(e.getPlayer().getName());

        String message = ChatColor.AQUA + "" + ChatColor.ITALIC + playerName + ChatColor.GRAY + " > "
                + ChatColor.WHITE + rawMessage;

        for(Player player : this.plugin.getServer().getOnlinePlayers()) {
            player.sendMessage(message);
        }
    }

    private Entity getLastEntityDamager(Entity entity) {
        EntityDamageEvent event = entity.getLastDamageCause();
        if(event != null && !event.isCancelled() && (event instanceof EntityDamageByEntityEvent)) {
            Entity damager = ((EntityDamageByEntityEvent) event).getDamager();
            if(damager instanceof Projectile) {
                Object shooter = ((Projectile) damager).getShooter();
                if((shooter instanceof Entity)) return (Entity) shooter;
            }
            return damager;
        }

        return null;
    }

    private String getOrAddPlayerName(String playerName) {
        List<String> playerNames = this.plugin.getConfig().getStringList("playerNames");
        for(String name : playerNames) {
            String[] customName = name.split(":");
            if(customName[0].equalsIgnoreCase(playerName)) {
                return customName[1];
            }
        }

        playerNames.add(String.format("%s:%s", playerName, playerName));
        this.plugin.getConfig().set("playerNames", playerNames);
        this.plugin.saveConfig();

        return playerName;
    }

    private String getDeathMessage(String ogPlayerName, String cause) {
        String playerName = this.getOrAddPlayerName(ogPlayerName);
        List<String> deathMsgs = this.plugin.getConfig().getStringList("playerDeathMessages");
        for(String message : deathMsgs) {
            String[] msg = message.split(":");
            if(msg[0].equalsIgnoreCase(ogPlayerName)) {
                return ChatColor.RED + msg[1].replaceAll("%PLAYERNAME%", playerName);
            }
        }

        return cause;
    }

    private String getIntroMessage(String ogPlayerName) {
        String introMsg = "My man %PLAYERNAME% has joined the game.";
        String playerName = ChatColor.DARK_AQUA + "" + ChatColor.ITALIC + this.getOrAddPlayerName(ogPlayerName)
                + ChatColor.GRAY + "" + ChatColor.ITALIC;

        List<String> introMsgs = this.plugin.getConfig().getStringList("playerIntroMessages");
        for(String message : introMsgs) {
            String[] msg = message.split(":");
            if(msg[0].equalsIgnoreCase(ogPlayerName)) {
                return ChatColor.GREEN + "" + ChatColor.ITALIC + "+ " + ChatColor.GRAY + "" + ChatColor.ITALIC +
                        msg[1].replaceAll("%PLAYERNAME%", playerName);
            }
        }

        introMsg = ChatColor.GREEN + "" + ChatColor.ITALIC + "+ " + ChatColor.GRAY + "" + ChatColor.ITALIC +
                introMsg.replaceAll("%PLAYERNAME%", playerName);
        introMsgs.add(String.format("%s:%s", ogPlayerName, introMsg));
        this.plugin.getConfig().set("playerIntroMessages", introMsgs);
        this.plugin.saveConfig();

        return introMsg;
    }

    private String getOutroMessage(String ogPlayerName) {
        String outroMsg = "%PLAYERNAME%";
        String playerName = ChatColor.DARK_AQUA + "" + ChatColor.ITALIC + this.getOrAddPlayerName(ogPlayerName)
                + ChatColor.GRAY + "" + ChatColor.ITALIC;

        List<String> outroMsgs = this.plugin.getConfig().getStringList("playerOutroMessages");
        for(String message : outroMsgs) {
            String[] msg = message.split(":");
            if(msg[0].equalsIgnoreCase(ogPlayerName)) {
                return ChatColor.RED + "" + ChatColor.ITALIC + "- " +
                        msg[1].replaceAll("%PLAYERNAME%", playerName);
            }
        }

        outroMsg = outroMsg.replaceAll("%PLAYERNAME%", playerName);
        outroMsgs.add(String.format("%s:%s", ogPlayerName, outroMsg.substring(2)));
        this.plugin.getConfig().set("playerOutroMessages", outroMsgs);
        this.plugin.saveConfig();

        return ChatColor.RED + "" + ChatColor.ITALIC + "- " + ChatColor.DARK_AQUA + "" + ChatColor.ITALIC + outroMsg;
    }
}

