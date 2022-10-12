package ru.yandex.practicum.service.controllers.comments;

import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.service.services.comments.CommentAdminService;

@RestController
@Slf4j
@Validated
public class CommentAdminController {
    private final CommentAdminService service;

    public CommentAdminController(CommentAdminService service) {
        this.service = service;
    }

    @DeleteMapping("/admin/comments/{commId}")
    public void deleteComment(@PathVariable Long commId) {
        service.deleteComment(commId);
    }
}
