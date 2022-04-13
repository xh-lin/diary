package com.xuhuang.diary.services;

import static com.xuhuang.diary.utils.Utils.asTimestamp;

import java.sql.Timestamp;
import java.util.List;
import java.util.Map;

import javax.security.auth.message.AuthException;

import com.xuhuang.diary.models.Tag;
import com.xuhuang.diary.repositories.TagRepository;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TagService extends BaseService {

    public static final String NAME_MUST_NOT_BE_BLANK = "Name must not be blank.";

    private final TagRepository tagRepository;
    private final UserService userService;

    /*
     * Throws:
     * IllegalArgumentException - if name is blank
     */
    public Tag createTag(String name) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException(NAME_MUST_NOT_BE_BLANK);
        }
        return tagRepository.save(new Tag(name.trim(), userService.getCurrentUser()));
    }

    public List<Tag> getTags() {
        return tagRepository.findByUser(userService.getCurrentUser());
    }

    /*
     * Throws:
     * AuthException - if tag does not belong to the current user
     * NoSuchElementException - if tag is not found
     */
    public Tag getTag(Long tagId) throws AuthException {
        Tag tag = tagRepository.findById(tagId).orElseThrow();
        throwIfIsNotCurrentUser(tag.getUser());
        return tag;
    }

    /*
     * Throws:
     * AuthException - if tag does not belong to the current user
     * NoSuchElementException - if tag is not found
     * IllegalArgumentException - if name is blank
     */
    public Tag updateTag(Long tagId, String name) throws AuthException {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException(NAME_MUST_NOT_BE_BLANK);
        }
        Tag tag = tagRepository.findById(tagId).orElseThrow();
        throwIfIsNotCurrentUser(tag.getUser());
        tag.setName(name.trim());
        return tagRepository.save(tag);
    }

    /*
     * Throws:
     * AuthException - if tag does not belong to the current user
     * NoSuchElementException - if tag is not found
     */
    public Tag deleteTag(Long tagId) throws AuthException {
        Tag tag = tagRepository.findById(tagId).orElseThrow();
        throwIfIsNotCurrentUser(tag.getUser());
        tagRepository.deleteById(tagId);
        return tag;
    }

    public Tag parseTagJson(Map<String, Object> tagJson, boolean getRelated) {
        Long id = Long.valueOf(((Integer) tagJson.get("id")).longValue());

        if (getRelated) {
            try {
                return getTag(id);
            } catch (Exception e) {
                throw new IllegalArgumentException(e);
            }
        }

        Timestamp createdAt = asTimestamp((String) tagJson.get("createdAt"));
        Timestamp updateddAt = asTimestamp((String) tagJson.get("updatedAt"));
        String name = (String) tagJson.get("name");
        Tag tag = new Tag();

        tag.setId(id);
        tag.setCreatedAt(createdAt);
        tag.setUpdatedAt(updateddAt);
        tag.setName(name);
        return tag;
    }

    public Tag parseTagJson(Map<String, Object> tagJson) {
        return parseTagJson(tagJson, false);
    }

}
