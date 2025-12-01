package com.springboot.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class FoodProductApiBody {

    @JsonProperty("pageNo")
    private int pageNo;

    @JsonProperty("totalCount")
    private int totalCount;

    @JsonProperty("items")
    private List<FoodProductItemDto> items;
}
