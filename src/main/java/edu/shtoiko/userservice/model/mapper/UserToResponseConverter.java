package edu.shtoiko.userservice.model.mapper;

import edu.shtoiko.userservice.client.AccountClient;
import edu.shtoiko.userservice.model.Dto.AccountVo;
import edu.shtoiko.userservice.model.Dto.UserVo;
import edu.shtoiko.userservice.model.entity.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.Converter;
import org.modelmapper.spi.MappingContext;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class UserToResponseConverter implements Converter<User, UserVo> {

    private final AccountClient accountClient;

    @Override
    public UserVo convert(MappingContext<User, UserVo> context) {
        User user = context.getSource();
        UserVo userVo = new UserVo();
        userVo.setId(user.getId());
        userVo.setEmail(user.getEmail());
        userVo.setFirstName(user.getFirstName());
        userVo.setLastName(user.getLastName());
        userVo.setAccounts(getAccountsByUserId(user.getId()));
        return userVo;
    }

    private List<AccountVo> getAccountsByUserId(Long userId) {
        return accountClient.getAccountsByUserId(userId);
    }
}