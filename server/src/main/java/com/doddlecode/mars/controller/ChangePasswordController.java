package com.doddlecode.mars.controller;

import com.doddlecode.mars.dto.ChangePasswordDto;
import com.doddlecode.mars.service.ChangePasswordService;
import com.google.common.base.Preconditions;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/change-passwords")
class ChangePasswordController {

    private final ChangePasswordService changePasswordService;

    public ChangePasswordController(ChangePasswordService changePasswordService) {
        this.changePasswordService = changePasswordService;
    }

    @RequestMapping(method = RequestMethod.PUT)
    @ResponseStatus(HttpStatus.OK)
    public void changePassword(@RequestBody ChangePasswordDto changePasswordDto, HttpServletRequest request) {
        Preconditions.checkNotNull(changePasswordDto);
        changePasswordService.changePassword(changePasswordDto, request);
    }

}
