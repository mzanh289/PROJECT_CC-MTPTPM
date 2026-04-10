package CCPTMT.DoAn.QuanLyCaLam.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import CCPTMT.DoAn.QuanLyCaLam.entity.User;
import CCPTMT.DoAn.QuanLyCaLam.entity.enums.Role;

public interface UserRepository extends JpaRepository<User, Integer> {

    Optional<User> findByEmail(String email);

    Optional<User> findByEmailAndStatusTrue(String email);

    boolean existsByEmail(String email);

    boolean existsByEmailAndUserIdNot(String email, Integer userId);

    List<User> findAllByOrderByUserIdDesc();

    List<User> findAllByRoleOrderByUserIdDesc(Role role);

    List<User> findAllByRoleAndStatusOrderByUserIdDesc(Role role, Boolean status);

    Optional<User> findByUserIdAndRole(Integer userId, Role role);
}
