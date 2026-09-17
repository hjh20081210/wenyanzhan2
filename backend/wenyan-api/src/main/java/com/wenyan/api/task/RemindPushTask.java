package com.wenyan.api.task;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.wenyan.common.util.SnowflakeIdUtil;
import com.wenyan.entity.entity.SysUser;
import com.wenyan.entity.entity.UserPushRecord;
import com.wenyan.entity.entity.UserSetting;
import com.wenyan.service.UserPushRecordService;
import com.wenyan.service.UserService;
import com.wenyan.service.UserSettingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * 打卡定时提醒：每分钟扫描，remind_switch=1 且时间匹配且当日未推送则推送。
 * 依据 user_push_record 保证同一用户一天只推一次，消息携带昵称。
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class RemindPushTask {

    private final UserSettingService userSettingService;
    private final UserService userService;
    private final UserPushRecordService pushRecordService;

    @Scheduled(cron = "0 * * * * ?")
    public void pushRemind() {
        try {
            LocalTime now = LocalTime.now().withSecond(0).withNano(0);
            String nowHm = now.format(DateTimeFormatter.ofPattern("HH:mm"));
            String today = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));

            List<UserSetting> settings = userSettingService.list(new LambdaQueryWrapper<UserSetting>()
                    .eq(UserSetting::getRemindSwitch, 1)
                    .eq(UserSetting::getRemindTime, nowHm));

            for (UserSetting setting : settings) {
                // 当日是否已推送
                Long count = pushRecordService.count(new LambdaQueryWrapper<UserPushRecord>()
                        .eq(UserPushRecord::getUserId, setting.getUserId())
                        .eq(UserPushRecord::getPushDate, today));
                if (count > 0) continue;

                SysUser user = userService.getById(setting.getUserId());
                String nickname = user == null ? "同学" : (user.getNickname() == null ? "同学" : user.getNickname());
                String content = "@" + nickname + "，该来打卡训练文言文啦！";

                // TODO 接入APP本地通知 + 微信小程序服务通知
                log.info("打卡提醒推送 => userId={}, content={}", setting.getUserId(), content);

                UserPushRecord record = new UserPushRecord();
                record.setId(SnowflakeIdUtil.nextId());
                record.setUserId(setting.getUserId());
                record.setPushDate(today);
                record.setPushTime(now.format(DateTimeFormatter.ofPattern("HH:mm:ss")));
                record.setPushType("remind");
                record.setContent(content);
                pushRecordService.save(record);
            }
        } catch (Exception e) {
            log.error("打卡提醒任务执行失败", e);
        }
    }
}
