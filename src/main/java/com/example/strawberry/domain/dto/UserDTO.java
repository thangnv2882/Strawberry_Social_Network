package com.example.strawberry.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Nationalized;
import org.hibernate.validator.constraints.Length;
import org.springframework.data.domain.AfterDomainEventPublication;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserDTO {

    @NotBlank(message = "First name cannot be left blank")
    private String firstName;

    @NotBlank(message = "Last name cannot be left blank")
    private String lastName;

    @Pattern(regexp = "[\\w-.]+@([\\w-]+\\.)+[\\w-]{2,4}", message = "Email invalidate")
    private String email;

    @Pattern(regexp = "^0[0-9]{9}$", message = "Phone number invalidate")
    private String phoneNumber;

    @NotBlank(message = "Password cannot be left blank")
    @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*[0-9])(?=.*[@#$%^&+=])[A-Za-z0-9@#$%^&+=]{8,}$",
            message = "Enter a stronger password")
    private String password;

    private String gender;

    @Length(max = 10000, message = "Address too long")
    private String address;

    private String birthday;

    @Length(max = 10000, message = "Biography too long")
    private String biography;

    private String linkAvt;
}
