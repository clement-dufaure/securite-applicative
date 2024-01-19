package re.dufau.securiteapplicative.securitynightmare.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class UserService {

    @Autowired
    UserDetailsService userDetailsService;

    @Autowired
    BCryptPasswordEncoder bCryptPasswordEncoder;

    private final List<String> usersToEnable = new ArrayList<>();


    public void saveNewUser(String username, String password) {
        UserDetails user = User.builder().username(username)
                .password(bCryptPasswordEncoder.encode(password)).build();
        ((InMemoryUserDetailsManager) userDetailsService).createUser(user);
        usersToEnable.add(username);
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

    public List<String> getUsersToEnable() {
        return usersToEnable;
    }
}