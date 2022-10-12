package ru.yandex.practicum.service.dto.comments;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Getter
@Setter
@ToString
@EqualsAndHashCode
@AllArgsConstructor
@NoArgsConstructor
public class CommentDto {
    private long id;
    @NotNull
    @NotBlank
    private String text;
    @NotNull
    @NotBlank
    private String authorName;
    private String created;
    private String commentStatus;
}