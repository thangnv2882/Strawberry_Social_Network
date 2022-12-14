package com.example.strawberry.application.service.Impl;

import com.example.strawberry.adapter.web.base.AccessType;
import com.example.strawberry.application.constants.MessageConstant;
import com.example.strawberry.application.dai.*;
import com.example.strawberry.application.service.IPostService;
import com.example.strawberry.application.utils.UploadFile;
import com.example.strawberry.config.exception.ExceptionAll;
import com.example.strawberry.config.exception.NotFoundException;
import com.example.strawberry.domain.dto.PostDTO;
import com.example.strawberry.domain.entity.*;
import com.github.slugify.Slugify;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;

import static com.example.strawberry.adapter.web.base.AccessType.*;
import static com.example.strawberry.adapter.web.base.ReactionType.*;

@Service
public class PostServiceImpl implements IPostService {

    private final IPostRepository postRepository;
    private final IUserRepository userRepository;
    private final IGroupRepository groupRepository;
    private final IImageRepository imageRepository;
    private final IVideoRepository videoRepository;
    private final IReactionRepository reactionRepository;
    private final GroupServiceImpl groupService;
    private final ModelMapper modelMapper;
    private final UploadFile uploadFile;
    private final IUserGroupRepository userGroupRepository;
    private Slugify slg = new Slugify();

    public PostServiceImpl(IPostRepository postRepository, IUserRepository userRepository, IGroupRepository groupRepository, IImageRepository imageRepository, IVideoRepository videoRepository, IReactionRepository reactionRepository, GroupServiceImpl groupService, ModelMapper modelMapper, UploadFile uploadFile, IUserGroupRepository userGroupRepository) {
        this.postRepository = postRepository;
        this.userRepository = userRepository;
        this.groupRepository = groupRepository;
        this.imageRepository = imageRepository;
        this.videoRepository = videoRepository;
        this.reactionRepository = reactionRepository;
        this.groupService = groupService;
        this.modelMapper = modelMapper;
        this.uploadFile = uploadFile;
        this.userGroupRepository = userGroupRepository;
    }

    @Override
    public Post getPostById(Long idPost) {
        Optional<Post> post = postRepository.findById(idPost);
        checkPostExists(post);
        return post.get();
    }

    @Override
    public Post createPost(Long idUser, PostDTO postDTO, MultipartFile[] fileImages, MultipartFile[] fileVideos) {
        Optional<User> user = userRepository.findById(idUser);
        UserServiceImpl.checkUserExists(user);
        Post post = modelMapper.map(postDTO, Post.class);
        if (PRIVATE.equals(postDTO.getAccess())) {
            post.setAccess(PRIVATE);
        }
        else {
            post.setAccess(PUBLIC);
        }
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
        UserServiceImpl.checkUserExists(userFix);
        User userOwns = post.get().getUser();
        if (userOwns.getIdUser() != userFix.get().getIdUser()) {
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
        UserServiceImpl.checkUserExists(userFix);
        User userOwns = post.get().getUser();
        if (userOwns.getIdUser() != userFix.get().getIdUser()) {
            throw new ExceptionAll("This post is not yours.");
        }
        postRepository.delete(post.get());
        return post.get();
    }

    @Override
    public List<?> getAllPostByAccess(AccessType access) {
        Set<Post> posts = postRepository.findAllByAccess(access);
        return getAllPostNotInGroup(posts);
    }

    public List<?> getAllPostNotInGroup(Set<Post> posts) {
        Set<Post> postEnd = new HashSet<>();
        posts.forEach(i -> {
            if (i.getGroup() == null) {
                postEnd.add(i);
            }
        });
        List<Map<String, Object>> list = new ArrayList<>();
        for (Post post : postEnd) {
            Map<String, Object> map = getDetailPost(post);
            list.add(map);
        }

        list.sort((l1, l2) -> ((Long) l2.get("idPost")).compareTo((Long) l1.get("idPost")));

        return list;
    }

    public Map<String, Object> getDetailPost(Post post) {
        Map<String, Object> map = new HashMap<>();
        map.put("idPost", post.getIdPost());
        map.put("createdAt", post.getCreatedAt());
        map.put("updatedAt", post.getUpdatedAt());
        map.put("contentPost", post.getContentPost());
        map.put("access", post.getAccess());
        map.put("user", post.getUser());
        map.put("reactions", getCountReactionOfPost(post.getIdPost()));
        map.put("images", getAllImageByIdPostSimple(post.getIdPost()));
        map.put("videos", getAllVideoByIdPostSimple(post.getIdPost()));
        map.put("countComments", countCommentByIdPost(post.getIdPost()));
        return map;
    }

//    L???y ra th??ng tin chi ti???t c???a t???t c??? b??nh lu???n trong 1 b??i vi???t
    public List<Comment> getAllCommentByIdPost(Long idPost) {
        Optional<Post> post = postRepository.findById(idPost);
        checkPostExists(post);
        Set<Comment> comments = post.get().getComments();
        List<Comment> commentList = new ArrayList<>(comments);

        commentList.sort((l1, l2) -> (l2.getIdComment()).compareTo(l1.getIdComment()));

        return commentList;
    }

    //    Hi???n th??? c??c l?????t b??y t??? c???m x??c c???a b??i post n??y
    public Map<String, Long> getCountReactionOfPost(Long idPost) {
        Map<String, Long> countReaction = new HashMap<>();
        countReaction.put("LIKE", reactionRepository.countByPostIdPostAndAndReactionType(idPost, LIKE));
        countReaction.put("LOVE", reactionRepository.countByPostIdPostAndAndReactionType(idPost, LIKE));
        countReaction.put("CARE", reactionRepository.countByPostIdPostAndAndReactionType(idPost, CARE));
        countReaction.put("HAHA", reactionRepository.countByPostIdPostAndAndReactionType(idPost, HAHA));
        countReaction.put("WOW", reactionRepository.countByPostIdPostAndAndReactionType(idPost, WOW));
        countReaction.put("SAD", reactionRepository.countByPostIdPostAndAndReactionType(idPost, SAD));
        countReaction.put("ANGRY", reactionRepository.countByPostIdPostAndAndReactionType(idPost, ANGRY));
        countReaction.put("ALL", reactionRepository.countByPostIdPost(idPost));
        return countReaction;
    }


    //    L???y ra th??ng tin c?? b???n c???a Video trong Post (idImage, linkImage)
    public List<Map<String, Object>> getAllImageByIdPostSimple(Long idPost) {
        Optional<Post> post = postRepository.findById(idPost);
        checkPostExists(post);
        Set<Image> images = postRepository.findById(idPost).get().getImages();
        List<Map<String, Object>> list = new ArrayList<>();
        images.forEach(image -> {
            Map<String, Object> map = new HashMap<>();
            map.put("idImage", image.getIdImage());
            map.put("linkImage", image.getLinkImage());
            list.add(map);
        });

//        S???p x???p theo id t??ng d???n
        list.sort((l1, l2) -> ((Long) l1.get("idImage")).compareTo((Long) l2.get("idImage")));

        return list;
    }

    //    L???y ra th??ng tin c?? b???n c???a Video trong Post (idVideo, linkVideo)
    public List<Map<String, Object>> getAllVideoByIdPostSimple(Long idPost) {
        Optional<Post> post = postRepository.findById(idPost);
        checkPostExists(post);
        Set<Video> videos = postRepository.findById(idPost).get().getVideos();
        List<Map<String, Object>> list = new ArrayList<>();
        videos.forEach(video -> {
            Map<String, Object> map = new HashMap<>();
            map.put("idVideo", video.getIdVideo());
            map.put("linkVideo", video.getLinkVideo());
            list.add(map);
        });

//        S???p x???p theo id t??ng d???n
        list.sort((l1, l2) -> ((Long) l1.get("idVideo")).compareTo((Long) l2.get("idVideo")));
        return list;
    }

    //    L???y ra th??ng tin chi ti???t c???a t???t c??? ???nh trong 1 b??i vi???t
    public Set<Image> getAllImageByIdPost(Long idPost) {
        Optional<Post> post = postRepository.findById(idPost);
        checkPostExists(post);
        Set<Image> images = postRepository.findById(idPost).get().getImages();
        return images;
    }


    //    L???y ra th??ng tin chi ti???t c???a t???t c??? video trong 1 b??i vi???t
    public Set<Video> getAllVideoByIdPost(Long idPost) {
        Optional<Post> post = postRepository.findById(idPost);
        checkPostExists(post);
        Set<Video> videos = postRepository.findById(idPost).get().getVideos();
        return videos;
    }

    public Long countCommentByIdPost(Long idPost) {
        Optional<Post> post = postRepository.findById(idPost);
        checkPostExists(post);
        Set<Comment> comments = post.get().getComments();
        Long countComment = Long.valueOf(comments.size());

        for (Comment comment : comments) {
            countComment += comment.getCommentChilds().size();
        }

        return countComment;
    }



    @Override
    public Post createPostInGroup(Long idGroup, Long idUser, PostDTO postDTO, MultipartFile[] fileImages, MultipartFile[] fileVideos) {
        Optional<Group> group = groupRepository.findById(idGroup);
        groupService.checkGroupExists(group);
        Optional<User> user = userRepository.findById(idUser);
        UserServiceImpl.checkUserExists(user);

        UserGroup userGroup = userGroupRepository.findByUserIdUserAndGroupIdGroup(idUser, idGroup);

        // N???u nh??m ri??ng t?? th?? ch??? th??nh vi??n c?? th??? ????ng b??i trong nh??m
        if (group.get().getAccess().equals(AccessType.PRIVATE) && userGroup == null) {
            throw new ExceptionAll(MessageConstant.USER_NOT_IN_GROUP);
        }

        // N???u nh??m c??ng khai th?? t???t c??? m???i ng?????i c?? th??? ??i???u c?? th??? ????ng b??i trong nh??m
        Post post = modelMapper.map(postDTO, Post.class);
        post.setUser(user.get());
        post.setGroup(group.get());

        setMediaToPost(post, fileImages, fileVideos);
        post.setAccess(PUBLIC);
        postRepository.save(post);
        return post;
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
