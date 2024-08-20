package com.gusdev.library_app.dtoRequest;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NonNull;


public record LoginRequestDTO(@NonNull String email,
                              @NonNull String password) {



}
