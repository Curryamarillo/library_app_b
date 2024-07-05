package com.gusdev.library_app.dtoResponse;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class LoginResponseDTO {

    private String email;
    private boolean isAuthenticated;


}
