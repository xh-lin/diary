package com.xuhuang.diary.controllers.api.v1;

import java.util.LinkedHashMap;
import java.util.Map;

import javax.security.auth.message.AuthException;
import javax.validation.constraints.NotBlank;

import com.xuhuang.diary.models.Tag;
import com.xuhuang.diary.services.TagService;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Validated
@RestController
@RequestMapping("/api/v1/tag")
@RequiredArgsConstructor
public class TagRestController {

    public static final String CREATED_SUCCESSFULLY = "Created successfully.";
    public static final String FETCHED_SUCCESSFULLY = "Fetched successfully.";
    public static final String UPDATED_SUCCESSFULLY = "Updated successfully.";
    public static final String DELETED_SUCCESSFULLY = "Deleted successfully.";

    public static final String NAME_NOTBLANK = "Name must not be blank.";

    private static final String DATA = "data";
    private static final String MESSAGE = "message";

    private final TagService tagService;

    @PostMapping()
    public ResponseEntity<Object> createTag(@RequestParam @NotBlank(message = NAME_NOTBLANK) String name) {
        log.info("createTag(name: {})", name);

        Map<String, Object> body = new LinkedHashMap<>();
        Tag tag = tagService.createTag(name);
        body.put(DATA, tag);
        body.put(MESSAGE, CREATED_SUCCESSFULLY);

        ResponseEntity<Object> response = new ResponseEntity<>(body, HttpStatus.CREATED);
        log.info("{}", response);
        return response;
    }

    @GetMapping()
    public ResponseEntity<Object> getTags() {
        log.info("getTags()");

        Map<String, Object> body = new LinkedHashMap<>();
        body.put(DATA, tagService.getTags());
        body.put(MESSAGE, FETCHED_SUCCESSFULLY);

        ResponseEntity<Object> response = new ResponseEntity<>(body, HttpStatus.OK);
        log.info("{}", response);
        return response;
    }

    @GetMapping("/{tagId}")
    public ResponseEntity<Object> getTag(@PathVariable Long tagId) throws AuthException {
        log.info("getTag(tagId: {})", tagId);

        Map<String, Object> body = new LinkedHashMap<>();
        Tag tag = tagService.getTag(tagId);
        body.put(DATA, tag);
        body.put(MESSAGE, FETCHED_SUCCESSFULLY);

        ResponseEntity<Object> response = new ResponseEntity<>(body, HttpStatus.OK);
        log.info("{}", response);
        return response;
    }

    @PutMapping("/{tagId}")
    public ResponseEntity<Object> updateTag(@PathVariable Long tagId,
            @RequestParam @NotBlank(message = NAME_NOTBLANK) String name) throws AuthException {
        log.info("updateTag(tagId: {}, name: {})", tagId, name);

        Map<String, Object> body = new LinkedHashMap<>();
        Tag tag = tagService.updateTag(tagId, name);
        body.put(DATA, tag);
        body.put(MESSAGE, UPDATED_SUCCESSFULLY);

        ResponseEntity<Object> response = new ResponseEntity<>(body, HttpStatus.OK);
        log.info("{}", response);
        return response;
    }

    @DeleteMapping("/{tagId}")
    public ResponseEntity<Object> deleteTag(@PathVariable Long tagId) throws AuthException {
        log.info("deleteTag(tagId: {})", tagId);

        Map<String, Object> body = new LinkedHashMap<>();
        Tag tag = tagService.deleteTag(tagId);
        body.put(DATA, tag);
        body.put(MESSAGE, DELETED_SUCCESSFULLY);

        ResponseEntity<Object> response = new ResponseEntity<>(body, HttpStatus.OK);
        log.info("{}", response);
        return response;
    }

}
