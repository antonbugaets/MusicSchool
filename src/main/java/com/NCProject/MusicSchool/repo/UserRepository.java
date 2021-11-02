package com.NCProject.MusicSchool.repo;

import com.NCProject.MusicSchool.models.Specialization;
import com.NCProject.MusicSchool.models.User;
import org.springframework.data.jpa.repository.JpaRepository;


public interface UserRepository extends JpaRepository<User, Long> {
    User findByUsername(String username);
    Iterable<User> findBySpecialization(Specialization specialization);
}
