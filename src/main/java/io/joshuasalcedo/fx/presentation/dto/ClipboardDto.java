package io.joshuasalcedo.fx.presentation.dto;

import java.time.LocalDateTime;

public record ClipboardDto(Long id, LocalDateTime localDateTime, String content, boolean isPinned) {
}
