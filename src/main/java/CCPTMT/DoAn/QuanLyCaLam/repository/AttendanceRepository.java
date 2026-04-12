package CCPTMT.DoAn.QuanLyCaLam.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import CCPTMT.DoAn.QuanLyCaLam.entity.Attendance;

public interface AttendanceRepository extends JpaRepository<Attendance, Integer> {

    List<Attendance> findByUserUserId(Integer userId);

    List<Attendance> findByUserUserIdOrderByWorkDateDesc(Integer userId);

    Optional<Attendance> findByUserUserIdAndWorkDate(Integer userId, LocalDate workDate);

    List<Attendance> findByWorkDateBetween(LocalDate fromDate, LocalDate toDate);
}
