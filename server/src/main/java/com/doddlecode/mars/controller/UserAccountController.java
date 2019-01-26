package com.doddlecode.mars.controller;

import com.doddlecode.mars.dto.UserAccountCreateDto;
import com.doddlecode.mars.dto.UserAccountDto;
import com.doddlecode.mars.entity.UserAccount;
import com.doddlecode.mars.service.UserAccountService;
import com.doddlecode.mars.util.RestPreconditions;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

import static com.doddlecode.mars.security.SecurityConstants.HEADER_STRING;

@RestController
@RequestMapping("/users")
class UserAccountController {

    private final UserAccountService userAccountService;
    private final ModelMapper modelMapper;

    public UserAccountController(UserAccountService userAccountService,
                                 ModelMapper modelMapper) {
        this.userAccountService = userAccountService;
        this.modelMapper = modelMapper;
    }

    @RequestMapping(value = "/logged-user", method = RequestMethod.GET)
    public UserAccountDto getLoggedUser(HttpServletRequest request) {
        String token = request.getHeader(HEADER_STRING);
        UserAccount userAccount = RestPreconditions
                .checkFound(
                        userAccountService.getUserByToken(token)
                );
        return modelMapper.map(userAccount, UserAccountDto.class);
    }

    @RequestMapping(method = RequestMethod.POST)
    @ResponseStatus(HttpStatus.CREATED)
    public UserAccountDto create(@RequestBody UserAccountCreateDto userAccountCreateDto) {
        RestPreconditions.checkNotNull(userAccountCreateDto);
        UserAccount userAccount = modelMapper.map(userAccountCreateDto, UserAccount.class);
        UserAccount savedUserAccount = userAccountService.create(userAccount);

        return modelMapper.map(savedUserAccount, UserAccountDto.class);
    }

    @RequestMapping(method = RequestMethod.PUT)
    @ResponseStatus(HttpStatus.OK)
    public UserAccountDto update(@RequestBody UserAccountDto userAccountDto) {
        RestPreconditions.checkNotNull(userAccountDto);
        UserAccount oldUser = RestPreconditions
                .checkFound(
                        userAccountService.getById(userAccountDto.getUserAccountId())
                );
        UserAccount userToEdit = modelMapper.map(userAccountDto, UserAccount.class);
        userToEdit.setPassword(oldUser.getPassword());
        userToEdit = userAccountService.update(userToEdit);

        return modelMapper.map(userToEdit, UserAccountDto.class);
    }

}
