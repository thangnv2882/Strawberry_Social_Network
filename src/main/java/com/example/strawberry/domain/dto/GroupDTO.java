package com.example.strawberry.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotBlank;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class GroupDTO {

    @NotBlank(message = "Group name cannot be left blank")
    private String name;

    private int access;

}
