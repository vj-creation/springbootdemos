package com.example.training;


import javax.sql.DataSource;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.provisioning.JdbcUserDetailsManager;
import org.springframework.security.provisioning.UserDetailsManager;

@Configuration //Creates object for current class
@EnableWebSecurity //Enables Spring Security
public class SecurityConfig extends WebSecurityConfigurerAdapter {
	
    //Creating users for Authentication purposes.
//    @Override
//    protected void configure(final AuthenticationManagerBuilder auth) throws Exception {
//        auth.inMemoryAuthentication()
//                .withUser("james").password(passwordEncoder().encode("welcome1")).roles("GROUP1")
//                .and()
//                .withUser("steven").password(passwordEncoder().encode("welcome1")).roles("GROUP1")
//                .and()
//                .withUser("vineet").password(passwordEncoder().encode("hello1")).roles("ADMIN","GROUP1");
//    }   
	
	@Bean
	public DataSource dataSource() {
		return new EmbeddedDatabaseBuilder()
			.setType(EmbeddedDatabaseType.H2)
			.addScript("classpath:org/springframework/security/core/userdetails/jdbc/users.ddl")
			.build();
	}

//    @Bean
//    public UserDetailsService userDetailsService() {
//    	UserDetails james = User.withUsername("james").password(passwordEncoder().encode("welcome1")).roles("GROUP1").build();
//    	UserDetails steven = User.withUsername("steven").password(passwordEncoder().encode("welcome1")).roles("GROUP1").build();
//    	UserDetails vineet = User.withUsername("vineet").password(passwordEncoder().encode("welcome1")).roles("ADMIN","GROUP1").build();
//        
//    	InMemoryUserDetailsManager userDetailsManager = new InMemoryUserDetailsManager();
//    	userDetailsManager.createUser(james);
//    	userDetailsManager.createUser(steven);
//       	userDetailsManager.createUser(vineet);
//    	return userDetailsManager;
//    }
    
    public UserDetailsManager users(DataSource dataSource) {
    	UserDetails user = User.builder()
    			.username("james")
    			.password(passwordEncoder().encode("welcome1"))
    			.roles("USER")
    			.build();
    	UserDetails admin = User.builder()
    			.username("steven")
    			.password(passwordEncoder().encode("welcome1"))
    			.roles("USER","ADMIN")
    			.build();
    	JdbcUserDetailsManager users = new JdbcUserDetailsManager(dataSource);
    	users.createUser(user);
    	users.createUser(admin);
    	return users;
    }
    //Configuring HTTP Authorization rules
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
        .authorizeRequests()
        .antMatchers("/mvc/employees/all").hasRole("USER")
        .antMatchers("/mvc/employees/create").hasRole("ADMIN")
        .and()
        .formLogin();
    }
    
    
   
    //Configuring how to encode the password.
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    } 
}
