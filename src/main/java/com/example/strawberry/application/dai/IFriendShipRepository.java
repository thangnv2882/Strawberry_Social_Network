package com.example.strawberry.application.dai;

import com.example.strawberry.domain.entity.FriendShip;
import com.example.strawberry.domain.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Set;

@Repository
public interface IFriendShipRepository extends JpaRepository<FriendShip, Long> {
    @Query("select f from FriendShip f where f.userReceiver.id = ?1")
    Set<FriendShip> findAllByUserReceiverId(Long idUserReceiver);

    @Query("select f from FriendShip f where f.userReceiver = ?1")
    Set<FriendShip> findAllByUserReceiver(Long idUserReceiver);
}
