package CCPTMT.DoAn.QuanLyCaLam.repository;

import CCPTMT.DoAn.QuanLyCaLam.entity.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<Users, Integer> {
    
    Optional<Users> findByEmail(String email);
    
    Optional<Users> findByUserId(Integer userId);
}
