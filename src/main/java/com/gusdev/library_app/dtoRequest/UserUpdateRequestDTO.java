package com.gusdev.library_app.dtoRequest;

import lombok.NonNull;

public record UserUpdateRequestDTO(@NonNull String name,
                                   @NonNull String surname,
                                   @NonNull String email,
                                   @NonNull Boolean isAdmin) {
}

