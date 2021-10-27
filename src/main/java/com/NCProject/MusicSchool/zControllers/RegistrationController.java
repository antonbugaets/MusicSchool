package com.NCProject.MusicSchool.zControllers;

import com.NCProject.MusicSchool.Models.Role;
import com.NCProject.MusicSchool.Models.User;
import com.NCProject.MusicSchool.repo.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Collections;
import java.util.Set;

@Controller
public class RegistrationController {
    @Autowired
    private UserRepository userRepository;

    @GetMapping("/registration")
    public String registration() {
        return "registration";
    }

    @PostMapping("/registration")
    public String addUser(User user, Model model,
                          @RequestParam String username, @RequestParam String password,
                          @RequestParam String name, @RequestParam String surname) {
        User userFromDB = userRepository.findByUsername(user.getUsername());
        if (userFromDB != null) {
            model.addAttribute("message", "User already exist!");

            return "registration";
        } else {
            user.setUsername(username);
            user.setPassword(password);
            user.setName(name);
            user.setSurname(surname);
            user.setRoles(Set.of(Role.USER, Role.STUDENT));
            userRepository.save(user);
            return "redirect:/login";
        }

    }
}
