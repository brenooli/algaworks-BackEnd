package com.dev.demo.config;

import com.dev.demo.security.filter.CustomAuthenticationFilter;
import com.dev.demo.security.filter.CustomAuthorizationFilter;
import com.dev.demo.service.UsuarioService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    private UsuarioService usuarioService;

    @Override
    protected void configure(HttpSecurity http) throws Exception {

        CustomAuthenticationFilter authenticationFilter = new CustomAuthenticationFilter(authenticationManagerBean());
        authenticationFilter.setFilterProcessesUrl("/login");

        http.csrf().disable();
        http.authorizeRequests().antMatchers("/login/**", "/usuarios/token/refresh/**").permitAll(); // Para quando quiser deixar a entity liberada para todos
        http.authorizeRequests().anyRequest().authenticated();
        http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);      
        http.addFilter(authenticationFilter);
        http.addFilterBefore(new CustomAuthorizationFilter(), UsernamePasswordAuthenticationFilter.class);
        // .httpBasic()
        // .antMatchers("/demo/admin/**").hasRole("ADMIN") Para proteger as url's exiga que seja ADMIN
        // .antMatchers("/demo/**").hasRole("USER")Para proteger a url exiga que seja USER
        // .oauth2ResourceServer().jwt().jwtAuthenticationConverter(getJwtAuthenticationConverter());


}

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {

        // Autenticação no Banco de Dados
        auth.userDetailsService(usuarioService).passwordEncoder(passwordEncoder());

        // Autenticação em Memória
        // auth.inMemoryAuthentication()
        // .withUser("breno")
        // .password(encoder().encode("breno"))
        // .roles("USER","ADMIN")
        // .and()
        // .withUser("estagiario")
        // .password(encoder().encode("estage"))
        // .roles("USER","ADMIN");

    }

    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // @Bean
    // public JwtAuthenticationConverter getJwtAuthenticationConverter() {
    // JwtAuthenticationConverter jwtAuthenticationConverter = new
    // JwtAuthenticationConverter();
    // jwtAuthenticationConverter.setJwtGrantedAuthoritiesConverter(jwt -> {
    // List<String> authorities = jwt.getClaimAsStringList("authorities");

    // if (authorities == null) {
    // authorities = Collections.emptyList();
    // }

    // JwtGrantedAuthoritiesConverter scopesAuthoritiesConverter = new
    // JwtGrantedAuthoritiesConverter();
    // Collection<GrantedAuthority> grantedAuthorities =
    // scopesAuthoritiesConverter.convert(jwt);

    // grantedAuthorities.addAll(authorities.stream()
    // .map(SimpleGrantedAuthority::new)
    // .collect(Collectors.toList()));

    // return grantedAuthorities;
    // });

    // return jwtAuthenticationConverter;
    // }
}
