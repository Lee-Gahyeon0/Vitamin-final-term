package com.springboot.dto;

import lombok.Getter;
import lombok.Setter;

@Getter 
@Setter
public class SupplementFormDto {
    private Long id;
    private Long memberId;
    private String name;
    private String brand;
    private String tags;
    private String memo;
}
