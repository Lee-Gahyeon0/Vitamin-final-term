package com.springboot.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class IntakeFormDto {

    private Long supplementId;  // 어떤 영양제인지
    private String timeSlot;    // 아침/점심/저녁
    private boolean taken;      // 먹었니 안먹었니
    private String memo;        // 메모 
}
