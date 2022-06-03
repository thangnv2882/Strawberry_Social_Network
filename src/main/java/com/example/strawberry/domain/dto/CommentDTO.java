package com.example.strawberry.domain.dto;

import com.example.strawberry.application.constants.CommonConstant;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Nationalized;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CommentDTO {

    @NotBlank(message = "Comment cannot be left blank")
    @Nationalized
    @Length(max = 20000, message = "Comment too long")
    private String contentComment;
}
