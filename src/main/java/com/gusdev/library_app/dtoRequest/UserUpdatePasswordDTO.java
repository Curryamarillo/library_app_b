package com.gusdev.library_app.dtoRequest;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.NonNull;

public record UserUpdatePasswordDTO (
                                    @NonNull @JsonProperty("oldPassword") String oldPassword,
                                    @NonNull @JsonProperty("password")String password) {
}
