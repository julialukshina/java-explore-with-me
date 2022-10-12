package ru.yandex.practicum.service.mappers.comments;

import ru.yandex.practicum.service.dto.comments.CommentDto;
import ru.yandex.practicum.service.models.Comment;

import java.time.format.DateTimeFormatter;

public class CommentMapper {
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    public static CommentDto toCommentDto(Comment comment){
        return new CommentDto(comment.getId(),
                comment.getText(),
                comment.getAuthor().getName(),
                comment.getCreated().format(formatter),
                comment.getCommentStatus().toString());
    }
}
