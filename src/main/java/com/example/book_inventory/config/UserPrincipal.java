package com.example.book_inventory.config;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;

/**
 * Custom UserDetails implementation that includes userId for authorization
 * checks
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserPrincipal implements UserDetails {

    private String userId;
    private String email;
    private String password;
    private Collection<? extends GrantedAuthority> authorities;


     // Factory method to create UserPrincipal from UserDocument

    public static UserPrincipal create(String userId, String email, String password, String role) {
        Collection<GrantedAuthority> authorities = Collections.singletonList(
                new SimpleGrantedAuthority("ROLE_" + role));

        return new UserPrincipal(userId, email, password, authorities);
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    // Expose userId for @PreAuthorize expressions
    public String getUserId() {
        return userId;
    }
}
