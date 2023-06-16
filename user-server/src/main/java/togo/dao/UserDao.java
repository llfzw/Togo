package togo.dao;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;
import togo.pojo.RegisterDto;
import toogoo.entity.User;


@Repository
@Mapper
public interface UserDao {
    User selectUserByEmail(String email);

    User selectUserById(Long id);

    int insertUser(@Param("user") RegisterDto user);

}
