package com.example.strawberry.domain.dto;

import com.example.strawberry.adapter.web.base.AccessType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PostDTO {
    private String contentPost;
    private AccessType access;
}
