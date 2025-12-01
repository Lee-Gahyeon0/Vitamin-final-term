package com.springboot.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FoodProductItemDto {

    @JsonProperty("PRDLST_NM")
    private String productName;

    @JsonProperty("BSSH_NM")
    private String companyName;

    @JsonProperty("PRDLST_REPORT_NO")
    private String productCode;

    @JsonProperty("PRMS_DT")
    private String reportDate;

    @JsonProperty("POG_DAYCNT")
    private String expireDate;

    @JsonProperty("PRDLST_DCNM")
    private String formType;

    @JsonProperty("NTK_MTHD")
    private String intakeMethod;

    @JsonProperty("PRIMARY_FNCLTY")
    private String mainFunction;

    @JsonProperty("RAWMTRL_NM")
    private String rawMaterialsText;

    @JsonProperty("IFTKN_ATNT_MATR_CN")
    private String cautionText;
}
