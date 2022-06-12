package com.example.strawberry.application.service.Impl;

import com.example.strawberry.application.constants.MessageConstant;
import com.example.strawberry.application.dai.*;
import com.example.strawberry.application.service.IPostService;
import com.example.strawberry.application.utils.UploadFile;
import com.example.strawberry.config.exception.ExceptionAll;
import com.example.strawberry.config.exception.NotFoundException;
import com.example.strawberry.domain.dto.PostDTO;
import com.example.strawberry.domain.dto.ReactionDTO;
import com.example.strawberry.domain.entity.*;
import com.github.slugify.Slugify;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;

import static com.example.strawberry.adapter.web.base.ReactionType.*;
import static com.example.strawberry.adapter.web.base.ReactionType.ANGRY;
import static com.example.strawberry.application.service.Impl.UserServiceImpl.checkUserExists;

@Service
public class PostServiceImpl implements IPostService {

    private final IPostRepository postRepository;
    private final IUserRepository userRepository;
    private final IGroupRepository groupRepository;
    private final IImageRepository imageRepository;
    private final IVideoRepository videoRepository;
    private final IReactionRepository reactionRepository;
    private final UserServiceImpl userService;
    private final GroupServiceImpl groupService;
    private final ModelMapper modelMapper;
    private final UploadFile uploadFile;
    private Slugify slg = new Slugify();

    public PostServiceImpl(IPostRepository postRepository, IUserRepository userRepository, IGroupRepository groupRepository, IImageRepository imageRepository, IVideoRepository videoRepository, IReactionRepository reactionRepository, UserServiceImpl userService, GroupServiceImpl groupService, ModelMapper modelMapper, UploadFile uploadFile) {
        this.postRepository = postRepository;
        this.userRepository = userRepository;
        this.groupRepository = groupRepository;
        this.imageRepository = imageRepository;
        this.videoRepository = videoRepository;
        this.reactionRepository = reactionRepository;
        this.userService = userService;
        this.groupService = groupService;
        this.modelMapper = modelMapper;
        this.uploadFile = uploadFile;
    }

//    @Override
//    public Set<Post> getAllPostPublic(int access) {
//        return postRepository.findAllByAccess(access);
//    }

    @Override
    public Post createPost(Long idUser, PostDTO postDTO, MultipartFile[] fileImages, MultipartFile[] fileVideos) {
        Optional<User> user = userRepository.findById(idUser);
        checkUserExists(user);
        Post post = modelMapper.map(postDTO, Post.class);
        post.setUser(user.get());
        setMediaToPost(post, fileImages, fileVideos);
        postRepository.save(post);
        return post;
    }

    @Override
    public Post updatePost(Long idUserFix, Long idPost, PostDTO postDTO, MultipartFile[] fileImages, MultipartFile[] fileVideos) {
        Optional<Post> post = postRepository.findById(idPost);
        checkPostExists(post);

        Optional<User> userFix = userRepository.findById(idUserFix);
        checkUserExists(userFix);
        User userOwns = post.get().getUser();
        if(userOwns.getId() != userFix.get().getId()) {
            throw new ExceptionAll("This post is not yours.");
        }

        modelMapper.map(postDTO, post.get());
        setMediaToPost(post.get(), fileImages, fileVideos);
        postRepository.save(post.get());
        return post.get();
    }

    @Override
    public Post deletePostById(Long idUserFix, Long idPost) {
        Optional<Post> post = postRepository.findById(idPost);
        checkPostExists(post);
        Optional<User> userFix = userRepository.findById(idUserFix);
        checkUserExists(userFix);
        User userOwns = post.get().getUser();
        if(userOwns.getId() != userFix.get().getId()) {
            throw new ExceptionAll("This post is not yours.");
        }
        postRepository.delete(post.get());
        return post.get();
    }


    @Override
    public List<?> getAllPostPublic(int access) {
        List<Post> posts = postRepository.findAll();
        return getAllPostNotInGroup(posts);
    }

    public List<?> getAllPostNotInGroup(List<Post> posts) {
        Set<Post> postEnd = new HashSet<>();
        posts.forEach(i -> {
            if (i.getGroup() == null) {
                postEnd.add(i);
            }
        });
        List<Map<String, Object>> list = new ArrayList<>();
        for (Post post : postEnd) {
            Map<String, Object> map = new HashMap<>();
            map.put("id", post.getId());
            map.put("createdAt", post.getCreatedAt());
            map.put("updatedAt", post.getUpdatedAt());
            map.put("contentPost", post.getContentPost());
            map.put("access", post.getAccess());
            map.put("user", post.getUser());
            map.put("reactions", getCountReactionOfPost(post.getId()));
            map.put("images", getAllImageByIdPost(post.getId()));
            map.put("videos", getAllVideoByIdPost(post.getId()));
            map.put("comments", getAllCommentByIdPost(post.getId()));
            list.add(map);
        }
        return list;
    }

    @Override
    public Map<String, Long> getCountReactionOfPost(Long idPost) {
        Map<String, Long> countReaction = new HashMap<>();
        countReaction.put("LIKE", reactionRepository.countByPostIdAndAndReactionType(idPost, LIKE));
        countReaction.put("LOVE", reactionRepository.countByPostIdAndAndReactionType(idPost, LIKE));
        countReaction.put("CARE", reactionRepository.countByPostIdAndAndReactionType(idPost, CARE));
        countReaction.put("HAHA", reactionRepository.countByPostIdAndAndReactionType(idPost, HAHA));
        countReaction.put("WOW", reactionRepository.countByPostIdAndAndReactionType(idPost, WOW));
        countReaction.put("SAD", reactionRepository.countByPostIdAndAndReactionType(idPost, SAD));
        countReaction.put("ANGRY", reactionRepository.countByPostIdAndAndReactionType(idPost, ANGRY));
        countReaction.put("ALL", reactionRepository.countByPostId(idPost));
        return countReaction;
    }
    @Override
    public Set<Image> getAllImageByIdPost(Long idPost) {
        Optional<Post> post = postRepository.findById(idPost);
        checkPostExists(post);
        Set<Image> images = postRepository.findById(idPost).get().getImages();
        return images;
    }

    @Override
    public Set<Video> getAllVideoByIdPost(Long idPost) {
        Optional<Post> post = postRepository.findById(idPost);
        checkPostExists(post);
        Set<Video> videos = postRepository.findById(idPost).get().getVideos();
        return videos;
    }

    @Override
    public Set<Comment> getAllCommentByIdPost(Long idPost) {
        Optional<Post> post = postRepository.findById(idPost);
        checkPostExists(post);
        Set<Comment> comments = post.get().getComments();
        return comments;
    }

    @Override
    public Post createPostInGroup(Long idGroup, Long idUser, PostDTO postDTO, MultipartFile[] fileImages, MultipartFile[] fileVideos) {
        Optional<Group> group = groupRepository.findById(idGroup);
        groupService.checkGroupExists(group);
        Optional<User> user = userRepository.findById(idUser);
        checkUserExists(user);

        Set<User> users = group.get().getUsers();
        for (User i : users) {
            if(i.getId() == user.get().getId()) {
                Post post = modelMapper.map(postDTO, Post.class);
                post.setUser(user.get());
                post.setGroup(group.get());

                setMediaToPost(post, fileImages, fileVideos);
                postRepository.save(post);
                return post;
            }
        }
        throw new NotFoundException(MessageConstant.USER_NOT_IN_GROUP);
    }


    public static void checkPostExists(Optional<Post> post) {
        if (post.isEmpty()) {
            throw new NotFoundException(MessageConstant.POST_NOT_EXISTS);
        }
    }

    public void setMediaToPost(Post post, MultipartFile[] fileImages, MultipartFile[] fileVideos) {
        if (fileImages != null) {
            Set<Image> images = new HashSet<>();

            for (MultipartFile file : fileImages) {
                Image image = new Image();
                image.setLinkImage(uploadFile.getUrlFromFile(file));
                image.setPost(post);
                imageRepository.save(image);
                images.add(image);
            }
            post.setImages(images);
        } else {
            post.setImages(new HashSet<>());
        }
        if (fileVideos != null) {
            Set<Video> videos = new HashSet<>();
            for (MultipartFile file : fileVideos) {
                Video video = new Video();
                try {
                    video.setLinkVideo(uploadFile.getUrlFromLargeFile(file));
                    video.setPost(post);
                    videoRepository.save(video);
                    videos.add(video);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            post.setVideos(videos);
        } else {
            post.setVideos(new HashSet<>());
        }
    }
}
