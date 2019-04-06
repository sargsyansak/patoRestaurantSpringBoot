package com.example.patorestaurant.controller;

import com.example.patorestaurant.model.User;
import com.example.patorestaurant.model.UserType;
import com.example.patorestaurant.repository.*;
import com.example.patorestaurant.security.SpringUser;
import com.example.patorestaurant.service.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

@Controller
public class UserController {
    @Value("${image.upload.dir}")
    private String imageUploadDir;

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private MenuRepository menuRepository;
    @Autowired
    private GalleryCategoriesRepository galleryCategoriesRepository;
    @Autowired
    private EmployeeRepository employeeRepository;
    @Autowired
    private BlogRepository blogRepository;
    @Autowired
    private EmailService emailService;


    @GetMapping("/login")
    public String loginPage() {
        return "login";
    }

    @GetMapping("/loginSuccess")
    public String loginSuccess(@AuthenticationPrincipal SpringUser springUser, ModelMap modelMap) {
        if (springUser.getUser().getUserType() == UserType.ADMIN) {
            return "redirect:/adminPage";
        } else if (springUser.getUser().getUserType() == UserType.USER) {
            return "redirect:/adminPage";
        } else {
            modelMap.addAttribute("message", "User with this email and password is exist");
            return "login";
        }
    }

    @GetMapping("/adminPage")
    public String adminPage(@AuthenticationPrincipal SpringUser springUser, ModelMap modelMap) {
        if (springUser.getUser().getUserType() == UserType.ADMIN) {
            modelMap.addAttribute("blogs", blogRepository.findAll());
            modelMap.addAttribute("menues", menuRepository.findAll());
            modelMap.addAttribute("galleryCategories", galleryCategoriesRepository.findAll());
            return "adminPage";
        } else {
            return "redirect:/login";
        }
    }

    @GetMapping("/admin")
    public String adminPage() {
        return "redirect:/login";

    }

    @GetMapping("/register")
    public String registerView() {

        return "register";
    }

    @PostMapping("/register")
    public String userRegister(@ModelAttribute User user, @RequestParam("picture") MultipartFile file, ModelMap modelMap) throws IOException {
        if (userRepository.findByEmail(user.getEmail()) == null) {
            String fileName = System.currentTimeMillis() + "_" + file.getOriginalFilename();
            File picture = new File(imageUploadDir + File.separator + fileName);
            file.transferTo(picture);
            String token = UUID.randomUUID().toString();
            user.setPicUrl(fileName);
            user.setToken(token);
            user.setPassword(passwordEncoder.encode(user.getPassword()));
            userRepository.save(user);
            emailService.sendSimpleMessage(user.getEmail(),"Welcome dear"+ user.getName(),"Please check the link for activating your profile" + "\n http://localhost:8080/activateProfile?token=" + token);
            return "redirect:/register";
        } else {
            modelMap.addAttribute("messageUser", "User with this email is exist");
            return "register";
        }
    }

    @GetMapping("/activateProfile")
    public String activate(@RequestParam("token") String token) {
        User byToken = userRepository.findByToken(token);
        byToken.setActive(true);
        userRepository.save(byToken);
        return "redirect:/login";
    }

}
