package com.example.collab.controller;

import com.example.collab.dto.CommentCreateDTO;
import com.example.collab.dto.CommentResponseDTO;
import com.example.collab.service.CommentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Comment Controller
 */
@RestController
@RequestMapping("/api/v1/comments")
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;

    /**
     * Create comment
     */
    @PostMapping
    public ResponseEntity<CommentResponseDTO> create(@Valid @RequestBody CommentCreateDTO dto) {
        CommentResponseDTO result = commentService.create(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(result);
    }

    /**
     * Get comment by ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<CommentResponseDTO> getById(@PathVariable Long id) {
        CommentResponseDTO result = commentService.getById(id);
        if (result == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(result);
    }

    /**
     * Get comments by document ID
     */
    @GetMapping
    public ResponseEntity<List<CommentResponseDTO>> getByDocumentId(@RequestParam Long docId) {
        List<CommentResponseDTO> results = commentService.getByDocumentId(docId);
        return ResponseEntity.ok(results);
    }

    /**
     * Update comment
     */
    @PutMapping("/{id}")
    public ResponseEntity<CommentResponseDTO> update(@PathVariable Long id, 
                                                      @Valid @RequestBody CommentCreateDTO dto) {
        CommentResponseDTO result = commentService.update(id, dto);
        return ResponseEntity.ok(result);
    }

    /**
     * Delete comment
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        commentService.delete(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Reply to comment
     */
    @PostMapping("/{id}/reply")
    public ResponseEntity<CommentResponseDTO> reply(@PathVariable Long id,
                                                      @Valid @RequestBody CommentCreateDTO dto) {
        CommentResponseDTO result = commentService.reply(id, dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(result);
    }
}