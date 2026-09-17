package com.wenyan.api.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.wenyan.api.util.UserContext;
import com.wenyan.common.result.Result;
import com.wenyan.common.util.SnowflakeIdUtil;
import com.wenyan.entity.dto.SaveGradeDTO;
import com.wenyan.entity.dto.SaveRemindDTO;
import com.wenyan.entity.dto.SaveUiDTO;
import com.wenyan.entity.entity.SysUser;
import com.wenyan.entity.entity.UserSetting;
import com.wenyan.entity.vo.CalendarVO;
import com.wenyan.service.CalendarService;
import com.wenyan.service.UserService;
import com.wenyan.service.UserSettingService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/** 用户信息、设置、打卡日历 */
@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final UserSettingService userSettingService;
    private final CalendarService calendarService;
    private final ObjectMapper objectMapper;

    @PostMapping("/save-grade")
    public Result<Void> saveGrade(@Valid @RequestBody SaveGradeDTO dto) {
        Long userId = UserContext.getUserId();
        UserSetting setting = getSetting(userId);
        setting.setStudyGrade(dto.getStudyGrade());
        userSettingService.updateById(setting);
        return Result.success();
    }

    @PostMapping("/setting/save-remind")
    public Result<Void> saveRemind(@Valid @RequestBody SaveRemindDTO dto) {
        Long userId = UserContext.getUserId();
        UserSetting setting = getSetting(userId);
        if (dto.getRemindSwitch() != null) setting.setRemindSwitch(dto.getRemindSwitch());
        if (dto.getRemindTime() != null) setting.setRemindTime(dto.getRemindTime());
        userSettingService.updateById(setting);
        return Result.success();
    }

    @PostMapping("/setting/save-ui")
    public Result<Void> saveUi(@Valid @RequestBody SaveUiDTO dto) {
        Long userId = UserContext.getUserId();
        UserSetting setting = getSetting(userId);
        if (dto.getFontType() != null) setting.setFontType(dto.getFontType());
        if (dto.getBgType() != null) setting.setBgType(dto.getBgType());
        if (dto.getFontSize() != null) setting.setFontSize(dto.getFontSize());
        if (dto.getLineHeight() != null) setting.setLineHeight(dto.getLineHeight());
        userSettingService.updateById(setting);
        return Result.success();
    }

    @GetMapping("/setting")
    public Result<UserSetting> getSetting() {
        return Result.success(getSetting(UserContext.getUserId()));
    }

    @GetMapping("/info")
    public Result<SysUser> info() {
        return Result.success(userService.getById(UserContext.getUserId()));
    }

    @GetMapping("/calendar/get")
    public Result<CalendarVO> calendar(@RequestParam(required = false) String month) {
        return Result.success(calendarService.getCalendar(UserContext.getUserId(), month));
    }

    private UserSetting getSetting(Long userId) {
        UserSetting setting = userSettingService.getOne(new LambdaQueryWrapper<UserSetting>()
                .eq(UserSetting::getUserId, userId));
        if (setting == null) {
            setting = new UserSetting();
            setting.setId(SnowflakeIdUtil.nextId());
            setting.setUserId(userId);
            setting.setStudyGrade(1);
            setting.setRemindSwitch(0);
            setting.setNewWordDaily(10);
            setting.setFontType("system");
            setting.setBgType("paper");
            setting.setFontSize(18);
            setting.setLineHeight(new java.math.BigDecimal("1.8"));
            userSettingService.save(setting);
        }
        return setting;
    }
}
