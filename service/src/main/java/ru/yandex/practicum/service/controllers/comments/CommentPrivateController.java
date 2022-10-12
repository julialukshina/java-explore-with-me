package ru.yandex.practicum.service.controllers.comments;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.service.dto.comments.CommentDto;
import ru.yandex.practicum.service.dto.comments.NewCommentDto;
import ru.yandex.practicum.service.services.comments.CommentPrivateService;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@RestController
@RequestMapping("/users/{userId}/events/{eventId}/comments")
@Slf4j
@Validated
public class CommentPrivateController {
    private final CommentPrivateService service;

    @Autowired
    public CommentPrivateController(CommentPrivateService service) {
        this.service = service;
    }

    @GetMapping
    public List<CommentDto> getComments(@PathVariable Long userId,
                                        @PathVariable Long eventId,
                                        @RequestParam(name = "from", defaultValue = "0") @PositiveOrZero int from) {
        return service.getComments(userId, eventId, from);
    }

    @PostMapping
    public CommentDto addComment(@PathVariable Long userId,
                                 @PathVariable Long eventId,
                                 @RequestBody NewCommentDto dto) {
        return service.addComment(userId, eventId, dto);
    }

    @PatchMapping("/{commId}")
    public CommentDto updateComment(@PathVariable Long userId,
                                    @PathVariable Long eventId,
                                    @PathVariable Long commId,
                                    @RequestBody @NotBlank String text) {
        return service.updateComment(userId, eventId, commId, text);
    }

    @DeleteMapping("/{commId}")
    public void deleteComment(@PathVariable Long userId,
                              @PathVariable Long eventId,
                              @PathVariable Long commId) {
        service.deleteComment(userId, eventId, commId);
    }
}
