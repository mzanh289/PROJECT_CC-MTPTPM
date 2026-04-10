package CCPTMT.DoAn.QuanLyCaLam.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import CCPTMT.DoAn.QuanLyCaLam.entity.Request;
import CCPTMT.DoAn.QuanLyCaLam.entity.enums.RequestStatus;

public interface RequestRepository extends JpaRepository<Request, Integer> {

    List<Request> findByUserUserId(Integer userId);

    List<Request> findByStatus(RequestStatus status);
}
