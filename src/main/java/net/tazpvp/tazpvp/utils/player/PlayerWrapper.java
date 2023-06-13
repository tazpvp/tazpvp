package net.tazpvp.tazpvp.utils.player;

import lombok.Getter;
import lombok.Setter;
import net.tazpvp.tazpvp.Tazpvp;
import net.tazpvp.tazpvp.utils.data.PersistentData;
import net.tazpvp.tazpvp.utils.data.Rank;
import net.tazpvp.tazpvp.utils.report.ReportDebounce;
import net.tazpvp.tazpvp.utils.report.ReportLogger;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionAttachment;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

/**
 * Wrapper for the player object which contains valuable methods exclusive to tazpvp
 */
public class PlayerWrapper {
    @Getter
    private final UUID uuid;
    @Getter @Setter
    private boolean launching;
    @Getter @Setter
    private boolean respawning;
    @Getter @Setter
    private boolean canRestore;
    @Getter @Setter
    private boolean dueling;
    @Getter
    private Rank rank;
    @Getter
    private final List<ReportDebounce> reportDebouncesList;
    @Getter
    private final List<ReportLogger> reportLoggerList;
    private PermissionAttachment permissionAttachment;
    @Getter
    private boolean receivedDialogue;

    /**
     * Should only take UUID, all other values should not have to persist.
     * @param uuid UUID.
     */
    public PlayerWrapper(UUID uuid) {
        this.uuid = uuid;
        this.launching = false;
        this.respawning = false;
        this.canRestore = false;
        this.dueling = false;
        this.rank = PersistentData.getRank(uuid);
        this.reportDebouncesList = new ArrayList<>();
        this.reportLoggerList = new ArrayList<>();
        this.receivedDialogue = false;

        refreshPermissions();
    }

    public Player getPlayer() {
        return Bukkit.getPlayer(uuid);
    }

    /**
     * Hide the player from ALL other players.
     */
    public void hidePlayer() {
        Bukkit.getOnlinePlayers().forEach(this::hidePlayer);
    }

    /**
     * Hide the target from the owner.
     * @param target The player to hide from the owner
     */
    public void hidePlayer(Player target) {
        Player owner = getPlayer();
        owner.hidePlayer(Tazpvp.getInstance(), target);
    }

    public void hideFromPlayer(Player target) {
        PlayerWrapper.getPlayer(target).hidePlayer(getPlayer());
    }

    public void hideFromPlayer() {
        Bukkit.getOnlinePlayers().forEach(player -> PlayerWrapper.getPlayer(player).hidePlayer(getPlayer()));
    }

    /**
     * Hide the player from ALL other players.
     */
    public void showPlayer() {
        Bukkit.getOnlinePlayers().forEach(target -> {
            if (!target.canSee(getPlayer())) {
                showPlayer(target);
            }
        });
    }

    /**
     * Show the target from the owner.
     * @param target The player to hide from the owner
     */
    public void showPlayer(Player target) {
        Player owner = getPlayer();
        owner.showPlayer(Tazpvp.getInstance(), target);
    }

    public void showFromPlayer(Player target) {
        PlayerWrapper.getPlayer(target).showPlayer(getPlayer());
    }

    public void showFromPlayer() {
        Bukkit.getOnlinePlayers().forEach(uuid -> {
            PlayerWrapper wrapper = PlayerWrapper.getPlayer(uuid);
            wrapper.showPlayer(getPlayer());
        });
    }

    public void reportPlayer(Player target, String reason) {
        this.reportDebouncesList.add(new ReportDebounce(target.getUniqueId(), System.currentTimeMillis()));
        PlayerWrapper.getPlayer(target).addReport(getUuid(), reason);
    }

    private void addReport(UUID reportee, String reason) {
        this.reportLoggerList.add(new ReportLogger(reportee, reason, System.currentTimeMillis()));
    }

    private void refreshPermissions() {
        if (this.permissionAttachment != null) {
            this.permissionAttachment.remove();
        }
        this.permissionAttachment = getPlayer().addAttachment(Tazpvp.getInstance());
        getRank().getPermissions().forEach(perm -> this.permissionAttachment.setPermission(perm, true));
        this.permissionAttachment.getPermissible().recalculatePermissions();
    }

    public void setRank(Rank rank) {
        this.rank = rank;
        PersistentData.setRank(getUuid(), rank);
        refreshPermissions();
    }

    public void setReceivedDialogue(boolean value) {
        if (!value) {
            new BukkitRunnable() {
                @Override
                public void run() {
                    PlayerWrapper.getPlayer(getUuid()).receivedDialogue = false;
                }
            }.runTaskLater(Tazpvp.getInstance(), 20 * 3);
            return;
        }
        this.receivedDialogue = true;
    }

    private static final WeakHashMap<UUID, PlayerWrapper> playerMap = new WeakHashMap<>();
    public static void addPlayer(Player p) {
        playerMap.put(p.getUniqueId(), new PlayerWrapper(p.getUniqueId()));
    }
    public static void addPlayer(UUID uuid) {
        playerMap.put(uuid, new PlayerWrapper(uuid));
    }
    public static void removePlayer(Player p) {
        playerMap.remove(p.getUniqueId(), new PlayerWrapper(p.getUniqueId()));
    }
    public static void removePlayer(UUID uuid) {
        playerMap.remove(uuid, new PlayerWrapper(uuid));
    }
    public static PlayerWrapper getPlayer(Player p) {
        return playerMap.get(p.getUniqueId());
    }
    public static PlayerWrapper getPlayer(UUID uuid) {
        return playerMap.get(uuid);
    }
}