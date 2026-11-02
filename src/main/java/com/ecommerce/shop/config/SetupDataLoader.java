package com.ecommerce.shop.config;

import com.ecommerce.shop.domain.user.entity.PrivilegeEntity;
import com.ecommerce.shop.domain.user.entity.RoleEntity;
import com.ecommerce.shop.domain.user.entity.Roles;
import com.ecommerce.shop.domain.user.entity.UserEntity;
import com.ecommerce.shop.domain.user.repository.PrivilegeRepository;
import com.ecommerce.shop.domain.user.repository.RoleRepository;
import com.ecommerce.shop.domain.user.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class SetupDataLoader implements ApplicationListener<ContextRefreshedEvent> {
    private boolean isAlreadySetup = false;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PrivilegeRepository privilegeRepository;
    private final PasswordEncoder passwordEncoder;

    public SetupDataLoader(UserRepository userRepository, RoleRepository roleRepository, PrivilegeRepository privilegeRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.privilegeRepository = privilegeRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        if (isAlreadySetup)
            return;

        PrivilegeEntity readPrivilege = createPrivilegeIfNotFound("READ_PRIVILEGE");
        PrivilegeEntity writePrivilege = createPrivilegeIfNotFound("WRITE_PRIVILEGE");
        List<PrivilegeEntity> adminPrivileges = List.of(readPrivilege,writePrivilege);
        List<PrivilegeEntity> userPrivileges = List.of(readPrivilege);
        RoleEntity adminRole =  createRoleIfNotFound(Roles.ROLE_ADMIN, adminPrivileges);
        createRoleIfNotFound(Roles.ROLE_USER, userPrivileges);
        userRepository.findByEmail("test@test.com")
                .ifPresentOrElse(userEntity -> {},()->{
                    UserEntity newAdminUser = new UserEntity();
                    newAdminUser.setName("AdminTest");
                    newAdminUser.setPassword(passwordEncoder.encode("test"));
                    newAdminUser.setEmail("test@test.com");
                    newAdminUser.setRoles(List.of(adminRole));
                    userRepository.save(newAdminUser);

                    isAlreadySetup = true;
                });
    }

    @Transactional
    PrivilegeEntity createPrivilegeIfNotFound(String name) {

        PrivilegeEntity privilege = privilegeRepository.findByName(name);
        if(privilege==null){
            privilege=new PrivilegeEntity(name);
            privilegeRepository.save(privilege);
        }
        return privilege;
    }
    @Transactional
    RoleEntity createRoleIfNotFound(
            Roles name, List<PrivilegeEntity> privileges) {


        RoleEntity role = roleRepository.findByName(String.valueOf(name));
        if (role == null) {
            role = new RoleEntity(String.valueOf(name));
            role.setPrivileges(privileges);
            roleRepository.save(role);
        }
        return role;
    }
}
