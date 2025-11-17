package repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import entity.UserEntity;

public interface UserRepository extends JpaRepository<UserEntity, Integer> {
    Optional<UserEntity> findByEmail(String email);
}
