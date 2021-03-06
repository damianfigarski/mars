package com.doddlecode.mars.controller;

import com.doddlecode.mars.dto.UserAccountCreateDto;
import com.doddlecode.mars.dto.UserAccountDto;
import com.doddlecode.mars.entity.UserAccount;
import com.doddlecode.mars.service.UserAccountService;
import com.doddlecode.mars.util.RestPreconditions;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import static com.doddlecode.mars.security.SecurityConstants.HEADER_STRING;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
class UserAccountController {

    private final UserAccountService userAccountService;
    private final ModelMapper modelMapper;

    @GetMapping("/logged-user")
    public UserAccountDto getLoggedUser(@RequestHeader(HEADER_STRING) String token) {
        UserAccount userAccount = userAccountService.getUserByToken(token);
        return modelMapper.map(userAccount, UserAccountDto.class);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UserAccountDto create(@RequestBody UserAccountCreateDto userAccountCreateDto) {
        RestPreconditions.checkNotNull(userAccountCreateDto);
        UserAccount userAccount = modelMapper.map(userAccountCreateDto, UserAccount.class);
        UserAccount savedUserAccount = userAccountService.create(userAccount);
        return modelMapper.map(savedUserAccount, UserAccountDto.class);
    }

    @PutMapping
    public UserAccountDto update(@RequestBody UserAccountDto userAccountDto) {
        RestPreconditions.checkNotNull(userAccountDto);
        UserAccount oldUser = userAccountService.getById(userAccountDto.getUserAccountId());
        UserAccount userToEdit = modelMapper.map(userAccountDto, UserAccount.class);
        userToEdit.setPassword(oldUser.getPassword());
        userToEdit = userAccountService.update(userToEdit);
        return modelMapper.map(userToEdit, UserAccountDto.class);
    }

}
