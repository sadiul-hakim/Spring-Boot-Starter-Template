package xyz.sadiulhakim.security.user;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import xyz.sadiulhakim.user.User;
import xyz.sadiulhakim.user.UserService;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserService service;

    public CustomUserDetailsService(UserService service) {
        this.service = service;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        User user = service.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Could not find user with username " + username));
        return new CustomUserDetails(user.getUsername(), user.getPassword(), user.getRole(), user.isEnabled());
    }
}
