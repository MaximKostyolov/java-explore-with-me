package ru.practicum.ewmmainservice.user.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.ewmmainservice.user.model.User;

import java.util.List;

public interface UserJpaRepository extends JpaRepository<User, Integer> {

    @Query(value = "select * from users u " +
            "where (u.USER_ID in (?1))", nativeQuery = true)
    List<User> findUsersByIds(List<Integer> idsList, Pageable page);

}