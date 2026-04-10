package CCPTMT.DoAn.QuanLyCaLam.service;

import java.util.List;

import CCPTMT.DoAn.QuanLyCaLam.dto.UserUpsertDto;
import CCPTMT.DoAn.QuanLyCaLam.dto.UserViewDto;

public interface AdminUserService {

    List<UserViewDto> findAll(Boolean status);

    UserUpsertDto getEditData(Integer userId);

    void create(UserUpsertDto dto);

    void update(Integer userId, UserUpsertDto dto);

    void delete(Integer userId);
}
