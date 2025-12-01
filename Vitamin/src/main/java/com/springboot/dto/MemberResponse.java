package com.springboot.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MemberResponse {
    private Long id;
    private String email;
    private String nickname;
}
