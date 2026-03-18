package com.example.collab.controller;

import com.example.collab.dto.AnnotationCreateDTO;
import com.example.collab.dto.AnnotationResponseDTO;
import com.example.collab.service.AnnotationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Annotation Controller
 */
@RestController
@RequestMapping("/api/v1/annotations")
@RequiredArgsConstructor
public class AnnotationController {

    private final AnnotationService annotationService;

    /**
     * Create annotation
     */
    @PostMapping
    public ResponseEntity<AnnotationResponseDTO> create(@Valid @RequestBody AnnotationCreateDTO dto) {
        AnnotationResponseDTO result = annotationService.create(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(result);
    }

    /**
     * Get annotation by ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<AnnotationResponseDTO> getById(@PathVariable Long id) {
        AnnotationResponseDTO result = annotationService.getById(id);
        if (result == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(result);
    }

    /**
     * Get annotations by document ID
     */
    @GetMapping
    public ResponseEntity<List<AnnotationResponseDTO>> getByDocumentId(@RequestParam Long docId) {
        List<AnnotationResponseDTO> results = annotationService.getByDocumentId(docId);
        return ResponseEntity.ok(results);
    }

    /**
     * Update annotation
     */
    @PutMapping("/{id}")
    public ResponseEntity<AnnotationResponseDTO> update(@PathVariable Long id,
                                                         @Valid @RequestBody AnnotationCreateDTO dto) {
        AnnotationResponseDTO result = annotationService.update(id, dto);
        return ResponseEntity.ok(result);
    }

    /**
     * Delete annotation
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        annotationService.delete(id);
        return ResponseEntity.noContent().build();
    }
}