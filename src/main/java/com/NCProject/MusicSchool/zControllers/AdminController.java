package com.NCProject.MusicSchool.zControllers;

import com.NCProject.MusicSchool.models.Lesson;
import com.NCProject.MusicSchool.models.Role;
import com.NCProject.MusicSchool.models.User;
import com.NCProject.MusicSchool.repo.LessonRepository;
import com.NCProject.MusicSchool.repo.UserRepository;
import com.NCProject.MusicSchool.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;


@Controller
public class AdminController {

    @Autowired
    private LessonRepository lessonRepository;

    @Autowired
    private UserService userService;

    @Autowired
    UserRepository userRepository;

    @GetMapping("/admin")
    public String admin(Model model) {

        model.addAttribute("users", userService.findAllUsers());
        return "admin";
    }

    @PostMapping("/admin/delete/{userID}")
    public String deleteUser(@AuthenticationPrincipal User admin, @PathVariable("userID") User userID, //спринг сразу обращается к репозирию и ищет юзера
                             @RequestParam(required = true, defaultValue = "") String action,
                             Model model) {
        if (action.equals("delete")) {

            //нашли все уроки, в которых может быть пользователь по специализации.
            List<Lesson> lessonFromDBBySpecialization = lessonRepository.findBySpecialization(userID.getSpecialization());

            for (Lesson value :
                    lessonFromDBBySpecialization) {
                //удалили из студентов
                value.getUsers().remove(userID);
                //удалили из учителей
                if (value.getTeacher().getUsername().equals(userID.getUsername())) {
                    value.setTeacher(null);
                }
            }

            lessonRepository.saveAll(lessonFromDBBySpecialization);
            userRepository.delete(userID);
        }

        return "redirect:/admin";
    }

    @PostMapping("/admin/update/{userID}")
    public String updateUser(@AuthenticationPrincipal User admin, @PathVariable("userID") User userID, @RequestParam String roleString,
                             @RequestParam(required = true, defaultValue = "") String action,
                             Model model) {
        if (action.equals("update")) {

            User user = userID;

            user.getRoles().clear();

            user.getRoles().add(Role.USER);

            user.getRoles().add(Role.valueOf(roleString));

            userService.saveUser(user);

            checkLessons();


        }


        return "redirect:/admin";
    }

    /**
     * удаление студентов из уроков, если они перестали таковыми быть. И удаление уроков, если учитель перестал быть учителем
     */

    public void checkLessons() {

        List<Lesson> lessonsFromDB = lessonRepository.findAll();

        for (Lesson value :
                lessonsFromDB) {
            //удалили урок, если учитель перестал быть таковым
            if (!value.getTeacher().getRoles().contains(Role.TEACHER)) {
                lessonsFromDB.remove(value);
                lessonRepository.delete(value);
            }
            //удалили НЕстудента из из сета студентов и засетали в урок
            value.getUsers().removeIf(user -> !user.getRoles().contains(Role.STUDENT));
        }
        lessonRepository.saveAll(lessonsFromDB);
    }
}