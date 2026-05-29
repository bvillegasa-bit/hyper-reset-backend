package com.hyperreset.api.security;

import com.hyperreset.api.entity.Usuario;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;

/**
 * Custom UserDetails implementation that wraps a Usuario entity.
 * Stores the user ID, email, password hash, and role for Spring Security authentication.
 */
public class UserPrincipal implements UserDetails {

    private final Long userId;
    private final String email;
    private final String password;
    private final String role;
    private final Collection<? extends GrantedAuthority> authorities;

    public UserPrincipal(Long userId, String email, String password, String role,
                         Collection<? extends GrantedAuthority> authorities) {
        this.userId = userId;
        this.email = email;
        this.password = password;
        this.role = role;
        this.authorities = authorities;
    }

    /**
     * Factory method to create a UserPrincipal from a Usuario entity.
     * The granted authority is ROLE_COACH, ROLE_DEPORTISTA, or ROLE_ADMIN.
     *
     * @param usuario the JPA entity
     * @return a UserPrincipal instance
     */
    public static UserPrincipal create(Usuario usuario) {
        return new UserPrincipal(
                usuario.getIdUsuario(),
                usuario.getCorreo(),
                usuario.getContrasenaHash(),
                usuario.getRol().name(),
                Collections.singletonList(
                        new SimpleGrantedAuthority("ROLE_" + usuario.getRol().name())
                )
        );
    }

    public Long getUserId() {
        return userId;
    }

    public String getRole() {
        return role;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return email;
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

    @Override
    public String toString() {
        return "UserPrincipal{" +
                "userId=" + userId +
                ", email='" + email + '\'' +
                ", role='" + role + '\'' +
                '}';
    }
}
