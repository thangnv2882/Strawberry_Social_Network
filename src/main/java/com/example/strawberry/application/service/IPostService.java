package com.example.strawberry.application.service;

import com.example.strawberry.adapter.web.base.AccessType;
import com.example.strawberry.domain.dto.PostDTO;
import com.example.strawberry.domain.entity.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Set;

public interface IPostService {
    Post getPostById(Long idPost);

    List<?> getAllPostByAccess(AccessType access);

    Post createPost(Long idUser, PostDTO postDTO, MultipartFile[] fileImages, MultipartFile[] fileVideos);

    Post updatePost(Long idUserFix, Long idPost, PostDTO postDTO, MultipartFile[] fileImages, MultipartFile[] fileVideos);

    Post deletePostById(Long idUserFix, Long idPost);

//    Set<Image> getAllImageByIdPost(Long id);
//    Set<Video> getAllVideoByIdPost(Long id);
//    Map<String, Long> getCountReactionOfPost(Long idPost);

    List<Comment> getAllCommentByIdPost(Long idPost);

    Post createPostInGroup(Long idGroup, Long idUser, PostDTO postDTO, MultipartFile[] fileImages, MultipartFile[] fileVideos);

    Set<Post> findByContentPost(String contentPost);

}
