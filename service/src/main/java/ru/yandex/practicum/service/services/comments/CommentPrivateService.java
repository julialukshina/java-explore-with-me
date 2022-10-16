package ru.yandex.practicum.service.services.comments;

import ru.yandex.practicum.service.dto.comments.CommentDto;
import ru.yandex.practicum.service.dto.comments.NewCommentDto;

import java.util.List;

public interface CommentPrivateService {
    List<CommentDto> getComments(Long userId, Long eventId, int from);

    CommentDto addComment(Long userId, Long eventId, NewCommentDto dto);

    CommentDto updateComment(Long userId, Long eventId, Long commId, String text);

    void deleteComment(Long userId, Long eventId, Long commId);
}
