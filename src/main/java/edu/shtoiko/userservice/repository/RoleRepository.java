package edu.shtoiko.userservice.repository;

import edu.shtoiko.userservice.model.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoleRepository extends JpaRepository<Role, Long> {

//    @Query("SELECT u.roles FROM User u WHERE u.id = :userId")
//    List<Role> findAllByUserId(@Param("userId") long userId);
}
