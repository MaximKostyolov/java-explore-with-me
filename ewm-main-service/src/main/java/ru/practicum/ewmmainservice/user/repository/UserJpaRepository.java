package ru.practicum.ewmmainservice.user.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.ewmmainservice.user.model.UserDto;

import java.util.List;

public interface UserJpaRepository extends JpaRepository<UserDto, Integer> {

    @Query(value = "select * from users u " +
            "where (u.USER_ID in (?1))", nativeQuery = true)
    List<UserDto> findUsersByIds(List<Integer> idsList, Pageable page);

    @Query(value = "select * from users u", nativeQuery = true)
    List<UserDto> findAllUsers(Pageable page);

}