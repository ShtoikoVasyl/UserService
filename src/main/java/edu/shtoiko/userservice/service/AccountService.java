package edu.shtoiko.userservice.service;

import edu.shtoiko.userservice.model.Dto.AccountVo;

import java.util.List;


public interface AccountService {
    List<AccountVo> getUsersAccounts(Long userId);
}
