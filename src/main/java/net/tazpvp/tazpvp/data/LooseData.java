package net.tazpvp.tazpvp.data;

import net.tazpvp.tazpvp.Tazpvp;
import net.tazpvp.tazpvp.data.entity.PlayerStatEntity;
import net.tazpvp.tazpvp.data.services.PlayerStatService;

import java.util.UUID;
import java.util.WeakHashMap;

public final class LooseData {

    private static final PlayerStatService playerStatService = Tazpvp.getInstance().getPlayerStatService();

    private static final WeakHashMap<UUID, Integer> ks = new WeakHashMap<>();
    private static final WeakHashMap<UUID, Integer> chatCount = new WeakHashMap<>();
    private static final WeakHashMap<UUID, Integer> mineCount = new WeakHashMap<>();

    public static int getExpLeft(UUID id) {
        PlayerStatEntity pStatEntity = playerStatService.getOrDefault(id);
        return Math.round((float) (pStatEntity.getLevel() * 1.94) + 40);
    }

    public static int getKs(UUID uuid) {
        if (ks.containsKey(uuid)) return ks.get(uuid);
        return 0;
    }

    public static void addKs(UUID uuid) {
        if (ks.containsKey(uuid)) {
            ks.put(uuid, getKs(uuid) + 1);
        } else {
            ks.put(uuid, 1);
        }
    }

    public static void setChatCount(UUID p, int val) {
        chatCount.put(p, val);
    }

    public static Integer getChatCount(UUID p) {
        if (chatCount.containsKey(p)) {
            return chatCount.get(p);
        }
        return 0;
    }

    public static void setMineCount(UUID p, int val) {
        mineCount.put(p, val);
    }

    public static Integer getMineCount(UUID p) {
        if (mineCount.containsKey(p)) {
            return mineCount.get(p);
        }
        return 0;
    }

    public static void resetKs(UUID id) {
//        PersistentData.topKs(id); IDK WHAT THIS IS
        ks.put(id, 0);
    }

}

