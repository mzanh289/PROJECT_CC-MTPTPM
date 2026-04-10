package CCPTMT.DoAn.QuanLyCaLam.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import CCPTMT.DoAn.QuanLyCaLam.entity.WorkSchedule;

public interface WorkScheduleRepository extends JpaRepository<WorkSchedule, Integer> {

    List<WorkSchedule> findByUserUserId(Integer userId);

    List<WorkSchedule> findByUserUserIdOrderByWorkDateAsc(Integer userId);

    List<WorkSchedule> findByWorkDateBetween(LocalDate fromDate, LocalDate toDate);

    Optional<WorkSchedule> findByUserUserIdAndWorkDate(Integer userId, LocalDate workDate);
}
