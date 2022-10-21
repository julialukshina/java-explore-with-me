package ru.yandex.practicum.service.services.comments;

import ru.yandex.practicum.service.dto.comments.CommentDto;
import ru.yandex.practicum.service.dto.comments.InputCommentDto;

import java.util.List;

public interface CommentPrivateService {
    List<CommentDto> getComments(Long userId, Long eventId, int from);

    CommentDto addComment(Long userId, Long eventId, InputCommentDto dto);

    CommentDto updateComment(Long userId, Long eventId, Long commId, InputCommentDto dto);

    void deleteComment(Long userId, Long eventId, Long commId);
}
