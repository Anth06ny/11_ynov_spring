package org.example.messagerieservice.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.core.userdetails.User
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.provisioning.InMemoryUserDetailsManager
import org.springframework.security.web.SecurityFilterChain

@Configuration
class SpringSecurityConfig {

    @Bean
    fun passwordEncoder() = BCryptPasswordEncoder()

    @Bean
    fun userDetailsService(encoder: PasswordEncoder): UserDetailsService {
        //Créer des utilisateurs fixes
        val user: UserDetails = User.withUsername("aaa")
            .password(encoder.encode("bbb"))
            .roles("USER")
            .build()

        val admin: UserDetails = User.withUsername("Admin")
            .password(encoder.encode("Admin"))
            .roles("ADMIN")
            .build()

        return InMemoryUserDetailsManager(user, admin)
    }

    @Bean
    fun securityFilterChain(http: HttpSecurity): SecurityFilterChain {
        http.authorizeHttpRequests { authorize ->
            authorize.requestMatchers("/testPrivate").authenticated()
            authorize.requestMatchers("/tchat/allMessages").authenticated()
            authorize.requestMatchers("/tchat/saveMessage").hasRole("ADMIN")
            authorize.requestMatchers("/testPrivateAdmin").hasRole("ADMIN")
                .anyRequest().permitAll()
        }
            .httpBasic { }
            .formLogin { }
            .csrf { it.disable() }
        return http.build()
    }

}