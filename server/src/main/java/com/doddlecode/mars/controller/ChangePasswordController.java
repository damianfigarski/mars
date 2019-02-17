package com.doddlecode.mars.controller;

import com.doddlecode.mars.dto.ChangePasswordDto;
import com.doddlecode.mars.service.ChangePasswordService;
import com.doddlecode.mars.util.RestPreconditions;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/change-passwords")
@RequiredArgsConstructor
class ChangePasswordController {

    private final ChangePasswordService changePasswordService;

    @PutMapping
    public void changePassword(@RequestBody ChangePasswordDto changePasswordDto) {
        RestPreconditions.checkNotNull(changePasswordDto);
        changePasswordService.changePassword(changePasswordDto);
    }

}
