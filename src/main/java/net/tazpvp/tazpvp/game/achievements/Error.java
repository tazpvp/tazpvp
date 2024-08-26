package net.tazpvp.tazpvp.game.achievements;

import net.tazpvp.tazpvp.Tazpvp;
import net.tazpvp.tazpvp.data.entity.AchievementEntity;
import net.tazpvp.tazpvp.data.entity.UserAchievementEntity;
import net.tazpvp.tazpvp.data.services.AchievementService;
import net.tazpvp.tazpvp.data.services.UserAchievementService;
import net.tazpvp.tazpvp.enums.StatEnum;
import net.tazpvp.tazpvp.helpers.ChatHelper;
import net.tazpvp.tazpvp.utils.observer.Observable;
import org.bukkit.entity.Player;

public class Error extends Observable {
    private final UserAchievementService userAchievementService = Tazpvp.getInstance().getUserAchievementService();
    private final AchievementService achievementService = Tazpvp.getInstance().getAchievementService();
    @Override
    public void death(Player victim, Player killer) {
        final UserAchievementEntity userAchievementEntity =  userAchievementService.getOrDefault(killer.getUniqueId());
        final AchievementEntity achievementEntity = userAchievementEntity.getError();

        if (!achievementEntity.isCompleted()) {
            if (StatEnum.DEATHS.getInt(victim.getUniqueId()) >= 100) {
                achievementEntity.setCompleted(true);
                achievementService.saveAchievementEntity(achievementEntity);
                ChatHelper.achievement(killer, "Error");
            }
        }
    }
}
