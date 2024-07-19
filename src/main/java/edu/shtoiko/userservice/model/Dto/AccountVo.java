package edu.shtoiko.userservice.model.Dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AccountVo {
    private long accountId;
    private long ownerId;
    private String accountName;
    private long accountNumber;
    private String currencyCode;
    private long amount;
    private String accountType;
    private String accountStatus;
}
