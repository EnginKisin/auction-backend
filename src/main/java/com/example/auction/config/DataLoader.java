package com.example.auction.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.example.auction.model.Role;
import com.example.auction.repository.RoleRepository;

@Configuration
public class DataLoader  {

    @Autowired
    private RoleRepository roleRepository;

    // @Autowired
    // private UserRepository userRepository;

    @Bean
    public CommandLineRunner loadData() {
        return args -> {
            createRoles();

            //createSuperAdmin();
        };
    }

    private void createRoles() {
        if (roleRepository.count() == 0) {
            Role userRole = new Role("user");
            Role adminRole = new Role("admin");
            Role superAdminRole = new Role("superadmin");
    
            roleRepository.save(userRole);
            roleRepository.save(adminRole);
            roleRepository.save(superAdminRole);
        }
    }


    // private void createSuperAdmin() {
    //     if (userRepository.findByUsername("superadmin").isEmpty()) {
    //         Set<Role> roles = new HashSet<>();
    //         Role superAdminRole = roleRepository.findByName("superadmin");
    //         roles.add(superAdminRole);
    
    //         User superAdminUser = new User();
    //         superAdminUser.setUsername("superadmin");
    
    //         String hashedPassword = BCrypt.hashpw("superadmin", BCrypt.gensalt());
    //         superAdminUser.setPassword(hashedPassword);
    
    //         superAdminUser.setRoles(roles);
    
    //         userRepository.save(superAdminUser);
    //     }
    // }
    
}
