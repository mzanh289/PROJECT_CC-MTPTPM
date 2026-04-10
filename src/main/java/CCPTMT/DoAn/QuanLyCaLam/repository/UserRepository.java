package CCPTMT.DoAn.QuanLyCaLam.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import CCPTMT.DoAn.QuanLyCaLam.entity.User;

public interface UserRepository extends JpaRepository<User, Integer> {

    Optional<User> findByEmail(String email);
}
