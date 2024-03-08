package com.isyb.obd.models.entities;

import lombok.Data;

@Data
public class FunctionResultOfLoginLogoutNotify {
    private String savedresult;
    private String savedresultmessage;
    private Long progressid;
}
