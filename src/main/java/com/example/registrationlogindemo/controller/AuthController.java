package com.example.registrationlogindemo.controller;

import com.example.registrationlogindemo.dto.UserDto;
import com.example.registrationlogindemo.entity.User;
import com.example.registrationlogindemo.service.UserService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;

import org.apache.commons.lang3.StringUtils;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.io.File;
import java.net.http.HttpClient.Redirect;
import java.security.Principal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Controller
@Slf4j
public class AuthController {

    private UserService userService;

    public AuthController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("index")
    public String home(){
        return "index";
    }

    @GetMapping("/login")
    public String loginForm() {
        return "login";
    }

    // handler method to handle user registration request
    @GetMapping("register")
    public String showRegistrationForm(Model model){
        UserDto user = new UserDto();
        model.addAttribute("user", user);
        return "register";
    }

    // handler method to handle register user form submit request
    @PostMapping("/register/save")
    public String registration(@Valid @ModelAttribute("user") UserDto user,
                               BindingResult result,
                               Model model){
        User existing = userService.findByEmail(user.getEmail());
        if (existing != null) {
            result.rejectValue("email", null, "There is already an account registered with that email");
        }
        if (result.hasErrors()) {
            model.addAttribute("user", user);
            return "register";
        }
        userService.saveUser(user);
        String name = user.getFirstName() + " " + user.getLastName();
        LocalDateTime localDateTime = LocalDateTime.now();
        log.info("Register Page (" + localDateTime + "):" + name + " has registered");
        return "redirect:/register?success";
    }


    @GetMapping("/users")
    public String STaaS(Model model){
       
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        List<String> list = new ArrayList<String>();
        //Collection<SimpleGrantedAuthority> authorities = (Collection<SimpleGrantedAuthority>) SecurityContextHolder.getContext().getAuthentication().getAuthorities();
        //System.out.println(authorities);    
        //System.out.println(StringUtils.join(authorities, "|"));
        //String author_permission = StringUtils.join(authorities, "|");
        //if(author_permission == "ROLE_ADMIN"){
           // return "redirect:/admin";
        //}
        if (principal instanceof UserDetails) {
            String username = ((UserDetails)principal).getUsername();
            System.out.println(username);
            
            model.addAttribute("username", username);
            File file = new File("C:\\Users\\junxian428\\Downloads\\SUCloud\\application\\src\\main\\resources\\storage\\"+ username);
        

            if (!file.exists()) {
                if (file.mkdir()) {
                     System.out.println("Directory is created!");
                } else {
                System.out.println("Failed to create directory or already exist!");
            }
            // After Create File show files
            String[] fileList = ((File) file).list();
            for (String name : fileList) {
                list.add(name);
             }
        }
            
        } else {
            String username = principal.toString();
            System.out.println(username);
        }
        model.addAttribute("list", list);
      return "upload";
    }





    @GetMapping("/admin")
    public String listRegisteredUsers(Model model){
        List<UserDto> users = userService.findAllUsers();
        model.addAttribute("users", users);
        LocalDateTime localDateTime = LocalDateTime.now();
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal instanceof UserDetails) {
            String username = ((UserDetails)principal).getUsername();
            log.info("Register Page (" + localDateTime + "): " + username + " has already accessed admin portal" ); 
        }
           

        return "users";
    }
}
