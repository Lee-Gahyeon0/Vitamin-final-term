package com.springboot.dto;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter 
@Setter
public class SupplementFormDto {
	private Long id;
    private Long memberId;

    // 화면에서 체크박스로 받는 필드
    private List<String> tagCodes;

    // DB에 저장할 때 쓰는 문자열
    private String tags;

    private String name;
    private String brand;
    private String memo;
}
