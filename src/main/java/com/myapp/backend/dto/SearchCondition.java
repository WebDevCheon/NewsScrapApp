package com.myapp.backend.dto;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class SearchCondition {
    private Long userId;
    private String query;
}