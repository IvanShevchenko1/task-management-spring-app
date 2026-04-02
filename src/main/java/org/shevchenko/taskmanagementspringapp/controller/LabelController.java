package org.shevchenko.taskmanagementspringapp.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.shevchenko.taskmanagementspringapp.dto.label.LabelCreateRequestDto;
import org.shevchenko.taskmanagementspringapp.dto.label.LabelResponseDto;
import org.shevchenko.taskmanagementspringapp.service.LabelService;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/labels")
@Tag(name = "Labels", description = "Endpoints for managing labels")
public class LabelController {
    private final LabelService labelService;

    @Operation(summary = "Create label")
    @PreAuthorize("hasAnyAuthority('ADMIN','USER')")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public LabelResponseDto create(@RequestBody @Valid LabelCreateRequestDto requestDto) {
        return labelService.create(requestDto);
    }

    @Operation(summary = "Get all labels of authenticated user")
    @PreAuthorize("hasAnyAuthority('ADMIN','USER')")
    @GetMapping
    public List<LabelResponseDto> getAll() {
        return labelService.getAll();
    }

    @Operation(summary = "Update label by id")
    @PreAuthorize("hasAnyAuthority('ADMIN','USER')")
    @PutMapping("/{id}")
    public LabelResponseDto update(
            @PathVariable Long id,
            @RequestBody @Valid LabelCreateRequestDto requestDto
    ) {
        return labelService.update(id, requestDto);
    }

    @Operation(summary = "Delete label by id")
    @PreAuthorize("hasAnyAuthority('ADMIN','USER')")
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        labelService.delete(id);
    }
}
