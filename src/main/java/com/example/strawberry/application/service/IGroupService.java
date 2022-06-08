package com.example.strawberry.application.service;

import com.example.strawberry.domain.dto.GroupDTO;
import com.example.strawberry.domain.entity.Group;
import com.example.strawberry.domain.entity.Post;
import com.example.strawberry.domain.entity.User;

import java.util.List;
import java.util.Set;

public interface IGroupService {
    List<Group> getAllGroup();
    Set<Group> getGroupByAccess(int access);

    Group createGroup(Long idUser, GroupDTO groupDTO);
    Group addUserToGroup(Long idGroup, Long idUser);

    Set<Post> getAllPostInGroup(Long idGroup, Long idUser);
    Set<User> getAllUserInGroup(Long idGroup);


}
