package com.gusdev.library_app.config.security;

import com.gusdev.library_app.dtoRequest.CreateUserRequestDTO;
import com.gusdev.library_app.dtoRequest.LoginRequestDTO;
import com.gusdev.library_app.dtoResponse.AuthResponseDTO;
import com.gusdev.library_app.entities.User;
import com.gusdev.library_app.repositories.UserRepository;
import jakarta.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class UserDetailServiceImpl implements UserDetailsService {

    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        User userEntity = userRepository.findByEmail(username);

        if (userEntity == null) {
            throw new UsernameNotFoundException("User not found");
        }
        return userEntity;


        }
        public AuthResponseDTO createUser(@NotNull CreateUserRequestDTO createUserRequest) {
        String email = createUserRequest.email();
        String name = createUserRequest.name();
        String surname = createUserRequest.surname();
        String password = createUserRequest.password();
        Boolean isAdmin = createUserRequest.isAdmin();

        System.out.println(email);
            System.out.println(name);
            System.out.println(surname);
            System.out.println(password);
            System.out.println(isAdmin);


        User userEntity = User.builder().email(email).name(name).surname(surname).password(passwordEncoder.encode(password)).isAdmin(isAdmin).build();
        User userSaved = userRepository.save(userEntity);

        ArrayList<SimpleGrantedAuthority> authorities = new ArrayList<>();
        if(userSaved.getIsAdmin()) {
            authorities.add(new SimpleGrantedAuthority("ROLE_ADMIN"));
        } else {
            authorities.add(new SimpleGrantedAuthority("ROLE_USER"));
        }
            SecurityContext securityContextHolder = SecurityContextHolder.getContext();
            Authentication authentication = new UsernamePasswordAuthenticationToken(userSaved, null, authorities);
            String accessToken = jwtUtils.createToken(authentication);
        return new AuthResponseDTO(email, "User created Succesfully", isAdmin, isAdmin, accessToken, true);
        }

        public AuthResponseDTO loginUser(LoginRequestDTO authLogin) {
        String username = authLogin.email();
        String password = authLogin.password();

        Authentication authentication = this.authenticate(username, password);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        String accessToken = jwtUtils.createToken(authentication);
        Boolean isAdmin = userRepository.findByEmail(username).getIsAdmin();
            return new AuthResponseDTO(username, "User logged succesfully", true, isAdmin,  accessToken, true);
        }

    public Authentication authenticate(String email, String password) {
        UserDetails userDetails = this.loadUserByUsername(email);

        if (userDetails == null) {
            throw new BadCredentialsException("Invalid username or password");
        }
        if (!passwordEncoder.matches(password, userDetails.getPassword())) {
            throw new BadCredentialsException("Invalid username or password");
        }
        return  new UsernamePasswordAuthenticationToken(email, password, userDetails.getAuthorities());
    }
}
