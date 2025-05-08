package com.example.auction.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.example.auction.model.DurationType;
import com.example.auction.model.Role;
import com.example.auction.repository.DurationTypeRepository;
import com.example.auction.repository.RoleRepository;

@Configuration
public class DataLoader  {

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private DurationTypeRepository durationTypeRepository;

    @Bean
    public CommandLineRunner loadData() {
        return args -> {
            createRoles();
            createDurationTypes();
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

    private void createDurationTypes() {
        if (durationTypeRepository.count() == 0) {
            DurationType shortDuration = new DurationType("Short", 30);  // 30 dakika
            DurationType longDuration = new DurationType("Long", 1440);  // 24 saat (1440 dakika)

            durationTypeRepository.save(shortDuration);
            durationTypeRepository.save(longDuration);
        }
    }
    
}
