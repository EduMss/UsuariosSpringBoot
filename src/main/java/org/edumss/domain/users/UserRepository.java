package org.edumss.domain.users;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<UserModel, String> {
    //UserDetails findByName(String name);
    Optional<UserModel> findByName(String name);

    Optional<UserModel> findByNameAndActiveTrue(String name);

    //@Transactional
    void deleteByActiveFalse();

    List<UserModel> findByNameStartsWithIgnoreCase(String name);
}
