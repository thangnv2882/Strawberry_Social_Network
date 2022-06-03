package com.example.strawberry.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class ResetPasswordDTO {

    @NotBlank(message = "Old Password cannot be left blank")
    private String oldPassword;

    @NotBlank(message = "New Password cannot be left blank")
    @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*[0-9])(?=.*[@#$%^&+=])[A-Za-z0-9@#$%^&+=]{8,}$",
            message = "Enter a stronger password")
    private String newPassword;
}
