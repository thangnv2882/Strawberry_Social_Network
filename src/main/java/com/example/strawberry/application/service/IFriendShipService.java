package com.example.strawberry.application.service;

import com.example.strawberry.domain.entity.FriendShip;

public interface IFriendShipService {
    FriendShip addFriend(Long idUserSender, Long idUserReceiver);
    String acceptFriend(Long idUserReceiver, Long idUserSender);


}
