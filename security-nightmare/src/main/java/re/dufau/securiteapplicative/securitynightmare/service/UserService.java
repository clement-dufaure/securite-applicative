package re.dufau.securiteapplicative.securitynightmare.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class UserService {

    @Autowired
    UserDetailsService userDetailsService;

    @Autowired
    BCryptPasswordEncoder bCryptPasswordEncoder;

    private final Map<String, String> usersToEnable = new HashMap<>();


    public void saveNewUser(String username, String password, String message) {
        UserDetails user = User.builder().username(username)
                .password(bCryptPasswordEncoder.encode(password)).build();
        ((InMemoryUserDetailsManager) userDetailsService).createUser(user);
        usersToEnable.put(username, message);
    }

    public void enableNewUser(String username) {
        var currentUser = userDetailsService.loadUserByUsername(username);
        UserDetails user = User.builder().username(username)
                .password(currentUser.getPassword()).roles("USER").build();
        ((InMemoryUserDetailsManager) userDetailsService).updateUser(user);
        usersToEnable.remove(username);
    }

    public UserDetails findByUsername(String username) {
        return userDetailsService.loadUserByUsername(username);
    }

    public Map<String, String> getUsersToEnable() {
        return usersToEnable;
    }
}