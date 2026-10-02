package com.dvarela.expenses.category;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record CategoryRequest(
        @NotBlank @Size(max = 50) String name,
        @NotNull CategoryKind kind,
        @NotNull @Pattern(regexp = CategoryIcons.PATTERN, message = "is not an allowed icon") String icon) {
}