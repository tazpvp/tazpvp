package net.tazpvp.tazpvp.data.implementations;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import net.tazpvp.tazpvp.Tazpvp;
import net.tazpvp.tazpvp.data.entity.GameRankEntity;
import net.tazpvp.tazpvp.data.entity.KitEntity;
import net.tazpvp.tazpvp.data.entity.PermissionEntity;
import net.tazpvp.tazpvp.data.services.PermissionService;

import java.sql.SQLException;
import java.util.UUID;

public class PermissionServiceImpl implements PermissionService {
    @Override
    public Dao<PermissionEntity, Integer> getUserDao() {
        try {
            return DaoManager.createDao(Tazpvp.getPostgresqlDatabase().getConnectionSource(), PermissionEntity.class);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public PermissionEntity createPermission(GameRankEntity gameRankEntity, String permission) {
        final PermissionEntity permissionEntity = new PermissionEntity();
        permissionEntity.setGameRankEntity(gameRankEntity);
        permissionEntity.setPermission(permission);
        return permissionEntity;
    }
}