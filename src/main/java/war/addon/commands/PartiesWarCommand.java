package war.addon.commands;

import com.alessiodp.parties.api.Parties;
import com.alessiodp.parties.api.interfaces.PartiesAPI;
import com.alessiodp.parties.api.interfaces.Party;
import com.alessiodp.parties.api.interfaces.PartyPlayer;
import org.bukkit.*;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import war.addon.Main;

import java.util.HashMap;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

public class PartiesWarCommand implements CommandExecutor, Listener {
    private final Main plugin;

    public HashMap<Player, UUID> leaderConfirm = new HashMap<>();
    public static HashMap<Player, UUID> team1 = new HashMap<>();
    public static HashMap<Player, UUID> team2 = new HashMap<>();
    public static HashMap<Player, String> teamName = new HashMap<>();

    public PartiesWarCommand(Main plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent e) {
        Player p = e.getEntity().getPlayer();
        assert p != null;
        PartiesAPI api = Parties.getApi();
        PartyPlayer player = api.getPartyPlayer(p.getUniqueId());
        assert player != null;
        Party playerClan = api.getParty(Objects.requireNonNull(player.getPartyId()));
        if (playerClan != null) {
            if (teamName.get(p).equalsIgnoreCase(playerClan.getName())) {
                if (team1.get(p) != null) {
                    Party loseClan = api.getParty(team1.get(p));
                    assert loseClan != null;

                    String deathMsg = Objects.requireNonNull(plugin.data.getConfig("language.yml").getString("death-message"))
                            .replace("%clan_name%", Objects.requireNonNull(loseClan.getName()))
                            .replace("%player%", p.getName())
                            .replace("%remaining members%", String.valueOf(team1.size()-1));

                    Bukkit.broadcastMessage("");
                    Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&', deathMsg));
                    Bukkit.broadcastMessage("");

                    for (Player online : Bukkit.getOnlinePlayers()){
                        Sound sound = Sound.valueOf(plugin.getConfig().getString("sound-manager.member-death.sound"));
                        int volume = Integer.parseInt(Objects.requireNonNull(plugin.getConfig().getString("sound-manager.member-death.volume")));
                        int pitch = Integer.parseInt(Objects.requireNonNull(plugin.getConfig().getString("sound-manager.member-death.pitch")));

                        online.playSound(online.getLocation(), sound, volume, pitch);
                    }

                    if (team1.size() <= 1){

                        String message = Objects.requireNonNull(plugin.data.getConfig("language.yml").getString("clan-wars-lose"))
                                .replace("%clan_name%", Objects.requireNonNull(loseClan.getName()));

                        Bukkit.broadcastMessage("");
                        Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&', message));
                        Bukkit.broadcastMessage("");

                        if (team2.size() >= 1 ){
                            for (Player winner : Bukkit.getOnlinePlayers()) {
                                PartyPlayer list = api.getPartyPlayer(winner.getUniqueId());

                                assert list != null;
                                UUID clanId = list.getPartyId();
                                if (team2.containsValue(clanId)){
                                    assert clanId != null;
                                    Party clanWinner = api.getParty(clanId);
                                    assert clanWinner != null;
                                    UUID leader = clanWinner.getLeader();

                                    String winMessage = Objects.requireNonNull(plugin.data.getConfig("language.yml").getString("clan-wars-win"))
                                            .replace("%clan_name%", Objects.requireNonNull(clanWinner.getName()));

                                    Bukkit.broadcastMessage("");
                                    Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&', winMessage));
                                    Bukkit.broadcastMessage("");

                                    for (Player online : Bukkit.getOnlinePlayers()){
                                        Sound sound = Sound.valueOf(plugin.getConfig().getString("sound-manager.clan-win.sound"));
                                        int volume = Integer.parseInt(Objects.requireNonNull(plugin.getConfig().getString("sound-manager.clan-win.volume")));
                                        int pitch = Integer.parseInt(Objects.requireNonNull(plugin.getConfig().getString("sound-manager.clan-win.pitch")));

                                        online.playSound(online.getLocation(), sound, volume, pitch);
                                    }

                                    ConfigurationSection section = plugin.getConfig().getConfigurationSection("reward-manager.items");
                                    assert section != null;
                                    for(String type : section.getKeys(false)) {
                                        Material mat = Material.valueOf(type);
                                        int amount = Integer.parseInt(Objects.requireNonNull(plugin.getConfig().getString("reward-manager.items."+ type + ".amount")));

                                        ItemStack reward = new ItemStack(mat, amount);
                                        assert leader != null;
                                        Objects.requireNonNull(Bukkit.getPlayer(leader)).getInventory().addItem(reward);
                                    }
                                    break;
                                }
                            }
                        }
                    }
                    team1.remove(p);
                    teamName.remove(p);
                }
                if (team2.get(p) != null) {
                    Party loseClan = api.getParty(team2.get(p));
                    assert loseClan != null;

                    String deathMsg = Objects.requireNonNull(plugin.data.getConfig("language.yml").getString("death-message"))
                            .replace("%clan_name%", Objects.requireNonNull(loseClan.getName()))
                            .replace("%player%", p.getName())
                            .replace("%remaining members%", String.valueOf(team2.size()-1));

                    Bukkit.broadcastMessage("");
                    Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&', deathMsg));
                    Bukkit.broadcastMessage("");

                    for (Player online : Bukkit.getOnlinePlayers()){
                        Sound sound = Sound.valueOf(plugin.getConfig().getString("sound-manager.member-death.sound"));
                        int volume = Integer.parseInt(Objects.requireNonNull(plugin.getConfig().getString("sound-manager.member-death.volume")));
                        int pitch = Integer.parseInt(Objects.requireNonNull(plugin.getConfig().getString("sound-manager.member-death.pitch")));

                        online.playSound(online.getLocation(), sound, volume, pitch);
                    }

                    if (team2.size() <= 1){

                        String message = Objects.requireNonNull(plugin.data.getConfig("language.yml").getString("clan-wars-lose"))
                                .replace("%clan_name%", Objects.requireNonNull(loseClan.getName()));

                        Bukkit.broadcastMessage("");
                        Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&', message));
                        Bukkit.broadcastMessage("");

                        if (team1.size() >= 1 ){
                            for (Player winner : Bukkit.getOnlinePlayers()) {
                                PartyPlayer list = api.getPartyPlayer(winner.getUniqueId());

                                assert list != null;
                                UUID clanId = list.getPartyId();
                                if (team1.containsValue(clanId)){
                                    assert clanId != null;
                                    Party clanWinner = api.getParty(clanId);
                                    assert clanWinner != null;
                                    UUID leader = clanWinner.getLeader();

                                    String winMessage = Objects.requireNonNull(plugin.data.getConfig("language.yml").getString("clan-wars-win"))
                                            .replace("%clan_name%", Objects.requireNonNull(clanWinner.getName()));

                                    Bukkit.broadcastMessage("");
                                    Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&', winMessage));
                                    Bukkit.broadcastMessage("");

                                    for (Player online : Bukkit.getOnlinePlayers()){
                                        Sound sound = Sound.valueOf(plugin.getConfig().getString("sound-manager.clan-win.sound"));
                                        int volume = Integer.parseInt(Objects.requireNonNull(plugin.getConfig().getString("sound-manager.clan-win.volume")));
                                        int pitch = Integer.parseInt(Objects.requireNonNull(plugin.getConfig().getString("sound-manager.clan-win.pitch")));

                                        online.playSound(online.getLocation(), sound, volume, pitch);
                                    }

                                    ConfigurationSection section = plugin.getConfig().getConfigurationSection("reward-manager.items");
                                    assert section != null;
                                    for(String type : section.getKeys(false)) {
                                        Material mat = Material.valueOf(type);
                                        int amount = Integer.parseInt(Objects.requireNonNull(plugin.getConfig().getString("reward-manager.items."+ type + ".amount")));

                                        ItemStack reward = new ItemStack(mat, amount);
                                        assert leader != null;
                                        Objects.requireNonNull(Bukkit.getPlayer(leader)).getInventory().addItem(reward);
                                    }
                                    break;
                                }
                            }
                        }
                    }
                    team2.remove(p);
                    teamName.remove(p);

                }
            }
        }
    }
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0 ){
            for (String msg : plugin.getConfig().getStringList("messages.help-message")) {
                sender.sendMessage(ChatColor.translateAlternateColorCodes('&',
                        msg));
            }
            return true;
        } else {
            if (args[0].equalsIgnoreCase("reload")) {
                if (sender.hasPermission(Objects.requireNonNull(plugin.getConfig().getString("reload.reload-permission")))) {
                    for (String msg : plugin.getConfig().getStringList("reload.reload-message")) {
                        sender.sendMessage(ChatColor.translateAlternateColorCodes('&',
                                msg));
                    }
                }
            } else if (args[0].equalsIgnoreCase("set")) {
                if (sender instanceof Player) {
                    Player p = (Player) sender;
                    if (args.length >= 2) {
                        if (args[1].equalsIgnoreCase("1")){
                            Location loc = p.getLocation();
                            plugin.data.getConfig("location.yml").set("position.pos1", serialize(loc));
                            p.sendMessage("§aSuccessfully set location pos1 to " +loc);
                            plugin.data.saveConfig("location.yml");
                        }
                        if (args[1].equalsIgnoreCase("2")){
                            Location loc = p.getLocation();
                            plugin.data.getConfig("location.yml").set("position.pos2", serialize(loc));
                            p.sendMessage("§aSuccessfully set location pos2 to " +loc);
                            plugin.data.saveConfig("location.yml");
                        }
                    }
                }
            } else if (args[0].equalsIgnoreCase("accept")){
                if (sender instanceof Player) {
                    Player p = (Player) sender;
                    if (args.length >= 2) {
                        if (args[1] != null) {
                            PartiesAPI api = Parties.getApi();
                            Party enemyClan = api.getParty(args[1]);
                            PartyPlayer player = api.getPartyPlayer(p.getUniqueId()); // Get the player
                            assert enemyClan != null;
                            if (leaderConfirm.get(p) == enemyClan.getId()) {
                                leaderConfirm.remove(p);
                                assert player != null;
                                Party playerClan = api.getParty(Objects.requireNonNull(player.getPartyId()));
                                Bukkit.broadcastMessage("");
                                assert playerClan != null;
                                int time = Integer.parseInt(Objects.requireNonNull(plugin.getConfig().getString("teleport-time")));
                                Bukkit.broadcastMessage(sendMessageAccept("clan-wars-accept", enemyClan.getName(), playerClan.getName(), time));
                                Bukkit.broadcastMessage("");
                                Set<PartyPlayer> onlineMembersEnemy = enemyClan.getOnlineMembers();
                                Set<PartyPlayer> onlineMembersPlayer = Objects.requireNonNull(playerClan).getOnlineMembers();

                                int totalEnemy = onlineMembersEnemy.size();
                                int totalPlayer = onlineMembersPlayer.size();

                                Bukkit.broadcastMessage("");
                                Bukkit.broadcastMessage("§e " + enemyClan.getName() + " : §f" + totalEnemy + " Players");
                                Bukkit.broadcastMessage("§e " + playerClan.getName() + " : §f" + totalPlayer + " Players");
                                Bukkit.broadcastMessage("");

                                int listEnemy = onlineMembersEnemy.size();
                                int listPlayer = onlineMembersPlayer.size();

                                for (Player online : Bukkit.getOnlinePlayers()){
                                    Sound sound = Sound.valueOf(plugin.getConfig().getString("sound-manager.wars-accepted.sound"));
                                    int volume = Integer.parseInt(Objects.requireNonNull(plugin.getConfig().getString("sound-manager.wars-accepted.volume")));
                                    int pitch = Integer.parseInt(Objects.requireNonNull(plugin.getConfig().getString("sound-manager.wars-accepted.pitch")));

                                    online.playSound(online.getLocation(), sound, volume, pitch);
                                }

                                new BukkitRunnable() {
                                    int time = Integer.parseInt(Objects.requireNonNull(plugin.getConfig().getString("teleport-time")));

                                    public void run() {
                                        if (time > 0) {
                                            time--;
                                            if (time < 5) {
                                                String message = Objects.requireNonNull(plugin.data.getConfig("language.yml").getString("teleport-countdown"))
                                                        .replace("%seconds%", String.valueOf(time));
                                                Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&', message));

                                                for (Player online : Bukkit.getOnlinePlayers()){
                                                    Sound sound = Sound.valueOf(plugin.getConfig().getString("sound-manager.teleport-message.sound"));
                                                    int volume = Integer.parseInt(Objects.requireNonNull(plugin.getConfig().getString("sound-manager.teleport-message.volume")));
                                                    int pitch = Integer.parseInt(Objects.requireNonNull(plugin.getConfig().getString("sound-manager.teleport-message.pitch")));

                                                    online.playSound(online.getLocation(), sound, volume, pitch);
                                                }
                                            }
                                        }
                                        if (time <= 1) {
                                            for (Player online : Bukkit.getOnlinePlayers()){
                                                Sound sound = Sound.valueOf(plugin.getConfig().getString("sound-manager.wars-started.sound"));
                                                int volume = Integer.parseInt(Objects.requireNonNull(plugin.getConfig().getString("sound-manager.wars-started.volume")));
                                                int pitch = Integer.parseInt(Objects.requireNonNull(plugin.getConfig().getString("sound-manager.wars-started.pitch")));

                                                online.playSound(online.getLocation(), sound, volume, pitch);
                                            }
                                            cancel();
                                            for (Player enemies : Bukkit.getOnlinePlayers()) {
                                                PartyPlayer list = api.getPartyPlayer(enemies.getUniqueId());
                                                assert list != null;
                                                if (Objects.equals(list.getPartyId(), enemyClan.getId())) {
                                                    if (plugin.data.getConfig("location.yml").getString("position.pos1") != null) {
                                                        Location loc = deserialize(Objects.requireNonNull(plugin.data.getConfig("location.yml").getString("position.pos1")));
                                                        enemies.teleport(loc);
                                                        team1.put(enemies, enemyClan.getId());
                                                        teamName.put(enemies, enemyClan.getName());
                                                    }else{
                                                        System.out.println("Error! Position 1 is not set on location.yml");
                                                    }
                                                }
                                                if (Objects.equals(list.getPartyId(), playerClan.getId())) {
                                                    if (plugin.data.getConfig("location.yml").getString("position.pos2") != null){
                                                        Location loc = deserialize(Objects.requireNonNull(plugin.data.getConfig("location.yml").getString("position.pos2")));
                                                        enemies.teleport(loc);
                                                        team2.put(enemies, playerClan.getId());
                                                        teamName.put(enemies, playerClan.getName());
                                                    }else{
                                                        System.out.println("Error! Position 2 is not set on location.yml");
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }.runTaskTimer(plugin, 20, 20);

                            }
                        }
                    }
                }
            } else if (args[0].equalsIgnoreCase("declare")) {
                if (sender instanceof Player) {
                    Player p = (Player) sender;
                    if (args.length >= 2) {
                        if (args[1] != null) {
                            PartiesAPI api = Parties.getApi();
                            Party enemyClan = api.getParty(args[1]);
                            PartyPlayer player = api.getPartyPlayer(p.getUniqueId()); // Get the player
                            assert player != null;
                            if (player.isInParty()) {
                                Party playerClan = api.getParty(Objects.requireNonNull(player.getPartyId()));
                                if (enemyClan != null) {
                                    UUID leader = enemyClan.getLeader();
                                    assert leader != null;
                                    if (Bukkit.getServer().getPlayer(leader) != null) {
                                        Player enemyLeader = Bukkit.getPlayer(Objects.requireNonNull(leader));
                                        assert enemyLeader != null;
                                        if (playerClan != null) {
                                            if (enemyClan != playerClan) {
                                                Set<PartyPlayer> onlineMembersEnemy = enemyClan.getOnlineMembers();
                                                Set<PartyPlayer> onlineMembersPlayer = playerClan.getOnlineMembers();
                                                int listEnemy = onlineMembersEnemy.size();
                                                int listPlayer = onlineMembersPlayer.size();

                                                int getMinimumOnline = Integer.parseInt(Objects.requireNonNull(plugin.getConfig().getString("minimum-online-members")));

                                                if (listEnemy >= getMinimumOnline) {
                                                    if (listPlayer >= getMinimumOnline) {
                                                        enemyLeader.sendMessage(sendMessageChallenge(
                                                                "clan-wars-send-enemy",
                                                                player.getName(),
                                                                playerClan.getName(),
                                                                enemyClan.getName(),
                                                                listEnemy));
                                                        enemyLeader.sendMessage("");
                                                        enemyLeader.sendMessage(sendMessageConfirmation("clan-wars-confirm-message", playerClan.getName()));
                                                        enemyLeader.sendMessage("");

                                                        Sound sound = Sound.valueOf(plugin.getConfig().getString("sound-manager.wars-invite.sound"));
                                                        int volume = Integer.parseInt(Objects.requireNonNull(plugin.getConfig().getString("sound-manager.wars-invite.volume")));
                                                        int pitch = Integer.parseInt(Objects.requireNonNull(plugin.getConfig().getString("sound-manager.wars-invite.pitch")));

                                                        enemyLeader.playSound(enemyLeader.getLocation(), sound, volume, pitch);

                                                        p.sendMessage(sendMessagePlayer("clan-wars-send-player", enemyClan.getName(), listEnemy));
                                                        leaderConfirm.put(enemyLeader, playerClan.getId());
                                                    }else{
                                                        p.sendMessage(notEnoughMembers("not-enough-members", playerClan.getName(), getMinimumOnline));
                                                    }
                                                }else {
                                                    p.sendMessage(notEnoughMembers("not-enough-members", enemyClan.getName(), getMinimumOnline));
                                                }
                                            } else {
                                                p.sendMessage(sendMessageLang("clan-wars-self"));
                                            }
                                        }
                                    } else {
                                        sender.sendMessage(sendMessageLang("leader-offline"));
                                    }
                                } else {
                                    sender.sendMessage(sendMessageLangValue("clan-not-found", args[1]));
                                }
                            } else {
                                sender.sendMessage(sendMessageLangValue("clan-not-found", args[1]));
                            }
                        }
                    } else {
                        sender.sendMessage(sendMessageLang("input-clan-name"));
                    }
                }
            }
        }

        return false;
    }
    public String notEnoughMembers(String key, String clanName, int req){
        String message = Objects.requireNonNull(Objects.requireNonNull(Objects.requireNonNull(plugin.data.getConfig("language.yml")
                        .getString(key))
                .replace("%clan_name%", clanName)
                .replace("%require_online_members%", String.valueOf(req))));
        return ChatColor.translateAlternateColorCodes('&', message);
    }

    public String sendMessageLang(String key){
        String message = Objects.requireNonNull(Objects.requireNonNull(plugin.data.getConfig("language.yml")
                .getString(key)));
        return ChatColor.translateAlternateColorCodes('&', message);
    }
    public String sendMessageLangValue(String key, String value){
        String message = Objects.requireNonNull(Objects.requireNonNull(Objects.requireNonNull(plugin.data.getConfig("language.yml")
                .getString(key)).replace("%clan_name%", value)));
        return ChatColor.translateAlternateColorCodes('&', message);
    }
    public String sendMessageChallenge(String key, String playerName, String playerClan, String enemyClan, int totalOnline){
        String message = Objects.requireNonNull(Objects.requireNonNull(Objects.requireNonNull(plugin.data.getConfig("language.yml")
                .getString(key))
                .replace("%player%", playerName)
                .replace("%player_clan_name%", playerClan)
                .replace("%enemy_clan_name%", enemyClan)
                .replace("%online_members%", String.valueOf(totalOnline))));
        return ChatColor.translateAlternateColorCodes('&', message);
    }
    public String sendMessageConfirmation(String key, String clanName){
        String message = Objects.requireNonNull(plugin.data.getConfig("language.yml").getString(key))
                .replace("%enemy_clan_name%", clanName);
        return ChatColor.translateAlternateColorCodes('&', message);
    }
    public String sendMessagePlayer(String key, String clanName, int totalOnline){
        String message = Objects.requireNonNull(plugin.data.getConfig("language.yml").getString(key))
                .replace("%enemy_clan_name%", clanName)
                .replace("%online_members%", String.valueOf(totalOnline));
        return ChatColor.translateAlternateColorCodes('&', message);
    }
    public String sendMessageAccept(String key, String enemyClan, String playerClan, int time){
        String message = Objects.requireNonNull(plugin.data.getConfig("language.yml").getString(key))
                .replace("%enemy_clan_name%", enemyClan)
                .replace("%player_clan_name%", playerClan)
                .replace("%seconds%", String.valueOf(time));
        return ChatColor.translateAlternateColorCodes('&', message);
    }
    public String serialize(Location loc) {
        World world = loc.getWorld();
        double x = loc.getX();
        double y = loc.getY();
        double z = loc.getZ();
        float yaw = loc.getYaw();
        float pitch = loc.getPitch();
        assert world != null;
        return world.getName() + ";" + x + ";" + y + ";" + z + ";" + yaw + ";" + pitch;
    }
    public Location deserialize(String string) {
        String[] vals = string.split(";");
        World world = Bukkit.getWorld(vals[0]);
        double x = Double.parseDouble(vals[1]);
        double y = Double.parseDouble(vals[2]);
        double z = Double.parseDouble(vals[3]);
        float yaw = Float.parseFloat(vals[4]);
        float pitch = Float.parseFloat(vals[5]);
        return new Location(world, x, y, z, yaw, pitch);
    }

}
