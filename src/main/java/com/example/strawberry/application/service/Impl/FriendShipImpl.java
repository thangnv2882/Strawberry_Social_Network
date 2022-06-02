package com.example.strawberry.application.service.Impl;

import com.example.strawberry.application.dai.IFriendShipRepository;
import com.example.strawberry.application.dai.IUserRepository;
import com.example.strawberry.application.service.IFriendShipService;
import com.example.strawberry.domain.entity.FriendShip;
import com.example.strawberry.domain.entity.User;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.Set;

@Service
public class FriendShipImpl implements IFriendShipService {

    private final IFriendShipRepository friendShipRepository;
    private final IUserRepository userRepository;
    private final UserServiceImpl userService;


    public FriendShipImpl(IFriendShipRepository friendShipRepository, IUserRepository userRepository, UserServiceImpl userService) {
        this.friendShipRepository = friendShipRepository;
        this.userRepository = userRepository;
        this.userService = userService;
    }


    @Override
    public FriendShip addFriend(Long idUserSender, Long idUserReceiver) {
        Optional<User> userSender = userRepository.findById(idUserSender);
        userService.checkUserExists(userSender);
        Optional<User> userReceiver = userRepository.findById(idUserReceiver);
        userService.checkUserExists(userReceiver);

        FriendShip friendShip = new FriendShip();
        friendShip.setUserSender(userSender.get());
        friendShip.setUserReceiver(userReceiver.get());
        friendShip.setIsAccept(Boolean.FALSE);

        friendShipRepository.save(friendShip);
        return friendShip;
    }

    @Override
    public String acceptFriend(Long idUserReceiver, Long idUserSender) {
        Set<FriendShip> listUserReceiver = friendShipRepository.findAllByUserReceiverId(idUserReceiver);
        final int[] d = {0};

        listUserReceiver.forEach(userReceiver -> {
            if(userReceiver.getUserSender().getId() == idUserSender) {
                userReceiver.setIsAccept(Boolean.TRUE);
                d[0]++;
            }
        });
        if(d[0] != 0) {
            return "Added";
        }
        return "Add fail";
    }




}
