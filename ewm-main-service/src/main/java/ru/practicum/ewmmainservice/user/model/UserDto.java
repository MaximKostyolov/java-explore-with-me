package ru.practicum.ewmmainservice.user.model;

import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Data
@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "users")
public class UserDto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Integer id;

    @NotBlank
    @Email
    @Size(min = 6, max = 254)
    @Column(unique = true, nullable = false)
    private String email;

    @NotBlank
    @Size(min = 2, max = 250)
    @Column(nullable = false)
    private String name;

}