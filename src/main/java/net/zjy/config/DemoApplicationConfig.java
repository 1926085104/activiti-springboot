package net.zjy.config;


import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @description:  临时解决没有用户的问题，解决spring security集成相关
 * 在文件中定义出来的用户信息，当然也可以是数据库中查询的用户权限信息
 * 后面处理流程时用到的任务负责人，需要添加在这里
 * @author:@zhujinyu
 * @date:2021-04-04 21:12
 */
@Configuration
public class DemoApplicationConfig {

    private Logger logger = LoggerFactory.getLogger(DemoApplicationConfig.class);

    @Bean
    public UserDetailsService myUserDetailsService() {
        //放入内存用户
        InMemoryUserDetailsManager inMemoryUserDetailsManager = new InMemoryUserDetailsManager();
        //这里添加用户，后面处理流程时用到的任务负责人，需要添加在这里
        // username,password,role,group
        //                security中角色必须以Role_开头，用户组必须以 GROUP_ 开头
        String[][] usersGroupsAndRoles = {{"jack", "password", "ROLE_ACTIVITI_USER", "GROUP_activitiTeam"}
                , {"rose", "password", "ROLE_ACTIVITI_USER", "GROUP_activitiTeam"}
                , {"tom", "password", "ROLE_ACTIVITI_USER", "GROUP_activitiTeam"}
                , {"other", "password", "ROLE_ACTIVITI_USER", "GROUP_otherTeam"}
                , {"system", "password", "ROLE_ACTIVITI_USER"}
                , {"admin", "password", "ROLE_ACTIVITI_ADMIN"},
        };

        for (String[] user : usersGroupsAndRoles) {
            List<String> authoritiesStrings = Arrays.asList(Arrays.copyOfRange(user, 2, user.length));
            logger.info("> Registering new user: 【{}】 with the following Authorities【{}】", user[0], authoritiesStrings);
            inMemoryUserDetailsManager.createUser(new User(user[0]
                    , passwordEncoder().encode(user[1])
                    , authoritiesStrings.stream().map(s -> new SimpleGrantedAuthority(s)).collect(Collectors.toList())));
        }
        return inMemoryUserDetailsManager;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }


}
