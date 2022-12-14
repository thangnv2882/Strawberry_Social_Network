package com.example.strawberry.application.dai;

import com.example.strawberry.domain.entity.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {

    @Query("select n from Notification n where n.user.idUser = ?1")
    List<Notification> findByUserIdUser(Long idUser);

}
