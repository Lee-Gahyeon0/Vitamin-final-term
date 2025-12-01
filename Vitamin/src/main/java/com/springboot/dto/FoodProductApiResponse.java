package com.springboot.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FoodProductApiResponse {

    @JsonProperty("body")
    private FoodProductApiBody body;
}