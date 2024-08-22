package com.gusdev.library_app.services;


import com.gusdev.library_app.config.security.JwtUtils;
import com.gusdev.library_app.config.security.UserDetailServiceImpl;
import com.gusdev.library_app.repositories.UserRepository;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private JwtUtils jwtUtils;

    private UserDetailServiceImpl userDetailService;
}
