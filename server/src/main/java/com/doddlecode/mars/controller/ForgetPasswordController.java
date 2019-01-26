package com.doddlecode.mars.controller;

import com.doddlecode.mars.dto.ForgetPasswordHelperDto;
import com.doddlecode.mars.service.ForgetPasswordService;
import com.doddlecode.mars.util.RestPreconditions;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/forget-passwords")
class ForgetPasswordController {

    private final ForgetPasswordService forgetPasswordService;

    public ForgetPasswordController(ForgetPasswordService forgetPasswordService) {
        this.forgetPasswordService = forgetPasswordService;
    }

    @RequestMapping(method = RequestMethod.PUT)
    @ResponseStatus(HttpStatus.OK)
    public void forgetPasswordSendEmail(@RequestBody ForgetPasswordHelperDto forgetPasswordHelperDto) {
        RestPreconditions.checkNotNull(forgetPasswordHelperDto);
        forgetPasswordService.sendEmailWithChangingPasswordCredentials(forgetPasswordHelperDto.getEmail());
    }

    @RequestMapping(value = "/change-password", method = RequestMethod.PUT)
    @ResponseStatus(HttpStatus.OK)
    public void forgetPassword(@RequestBody ForgetPasswordHelperDto forgetPasswordHelperDto) {
        RestPreconditions.checkNotNull(forgetPasswordHelperDto);
        forgetPasswordService.forgetPassword(forgetPasswordHelperDto);
    }

}
