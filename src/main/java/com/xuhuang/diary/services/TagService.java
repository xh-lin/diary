package com.xuhuang.diary.services;

import java.util.List;

import javax.security.auth.message.AuthException;

import com.xuhuang.diary.models.Tag;
import com.xuhuang.diary.models.User;
import com.xuhuang.diary.repositories.TagRepository;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TagService {

    public static final String NAME_MUST_NOT_BE_BLANK = "Name must not be blank.";
    public static final String YOU_DO_NOT_HAVE_PERMISSION_TO_ACCESS = "You do not have permission to access.";

    private final TagRepository tagRepository;
    private final UserService userService;

    public Tag createTag(String name) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException(NAME_MUST_NOT_BE_BLANK);
        }
        return tagRepository.save(new Tag(name.trim(), userService.getCurrentUser()));
    }

    public List<Tag> getTags() {
        return tagRepository.findByUser(userService.getCurrentUser());
    }

    public Tag getTag(Long tagId) throws AuthException {
        Tag tag = tagRepository.findById(tagId).orElseThrow();
        throwIfIsNotCurrentUser(tag.getUser());
        return tag;
    }

    public Tag updateTag(Long tagId, String name) throws AuthException {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException(NAME_MUST_NOT_BE_BLANK);
        }
        Tag tag = tagRepository.findById(tagId).orElseThrow();
        throwIfIsNotCurrentUser(tag.getUser());
        tag.setName(name.trim());
        return tagRepository.save(tag);
    }

    public Tag deleteTag(Long tagId) throws AuthException {
        Tag tag = tagRepository.findById(tagId).orElseThrow();
        throwIfIsNotCurrentUser(tag.getUser());
        tagRepository.deleteById(tagId);
        return tag;
    }

    private void throwIfIsNotCurrentUser(User user) throws AuthException {
        if (!userService.isCurrentUser(user)) {
            throw new AuthException(YOU_DO_NOT_HAVE_PERMISSION_TO_ACCESS);
        }
    }

}
