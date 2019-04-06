package com.example.patorestaurant.controller;

import com.example.patorestaurant.model.Contact;
import com.example.patorestaurant.repository.ContactRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class ContactController {

    @Value("${image.upload.dir}")
    private String imageUploadDir;

    @Autowired
    private ContactRepository contactRepository;

    @GetMapping("/contact")
    public String openContact() {
        return "contact";
    }

    @PostMapping("/contactAdmin")
    public String contact(@ModelAttribute Contact contact) {
        contactRepository.save(contact);
        return "redirect:/contact";
    }
}