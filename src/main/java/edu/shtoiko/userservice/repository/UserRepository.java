package edu.shtoiko.userservice.repository;

import edu.shtoiko.userservice.model.Dto.UserDto;
import edu.shtoiko.userservice.model.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
//    @Query("SELECT DISTINCT NEW edu.shtoiko.userservice.model.Dto.UserDto(u) FROM User u LEFT JOIN FETCH u.role WHERE u.id = :id")
//    UserDto findUserDtoByUserId(@Param("id") long id);
}


