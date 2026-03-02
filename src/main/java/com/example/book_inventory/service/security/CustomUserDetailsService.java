package com.example.book_inventory.service.security;

import com.example.book_inventory.config.UserPrincipal;
import com.example.book_inventory.model.User.UserDocument;
import com.example.book_inventory.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {

        UserDocument userDocument = userRepository.findByEmail(email);

        if (userDocument == null) {
            throw new UsernameNotFoundException("User not found with email: " + email);
        }

        // Return UserPrincipal with userId for authorization checks
        return UserPrincipal.create(
                userDocument.getUserId(),
                userDocument.getEmail(),
                userDocument.getPassword(),
                userDocument.getRole().name());
    }
}
