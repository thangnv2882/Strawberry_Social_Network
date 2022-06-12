package com.example.strawberry.application.service.Impl;

import com.example.strawberry.adapter.web.base.ReactionType;
import com.example.strawberry.application.dai.IPostRepository;
import com.example.strawberry.application.dai.IReactionRepository;
import com.example.strawberry.application.dai.IUserRepository;
import com.example.strawberry.application.service.IReactionService;
import com.example.strawberry.domain.dto.ReactionDTO;
import com.example.strawberry.domain.entity.Post;
import com.example.strawberry.domain.entity.Reaction;
import com.example.strawberry.domain.entity.User;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static com.example.strawberry.adapter.web.base.ReactionType.*;

@Service
public class ReactionServiceImpl implements IReactionService {

    private final IReactionRepository reactionRepository;
    private final IPostRepository postRepository;
    private final IUserRepository userRepository;
    private final PostServiceImpl postService;
    private final UserServiceImpl userService;


    public ReactionServiceImpl(IReactionRepository reactionRepository, IPostRepository postRepository, IUserRepository userRepository, PostServiceImpl postService, UserServiceImpl userService) {
        this.reactionRepository = reactionRepository;
        this.postRepository = postRepository;
        this.userRepository = userRepository;
        this.postService = postService;
        this.userService = userService;
    }

    @Override
    public Reaction setReactionForPost(ReactionDTO reactionDTO) {
//        Optional<Post> post = postRepository.findById(reactionDTO.getIdPost());
//        postService.checkPostExists(post);
//
//        Optional<User> userReact = userRepository.findById(reactionDTO.getIdUserReact());
//        userService.checkUserExists(userReact);
//
//        Reaction reaction = reactionRepository.findByPostIdAndUserId(reactionDTO.getIdPost(), reactionDTO.getIdUserReact());
//        if (reaction != null) {
//            if(reaction.getReactionType().equals(reactionDTO.getReactionType())) {
//                reactionRepository.delete(reaction);
//                return reaction;
//            }
//            reactionRepository.delete(reaction);
//            Reaction reaction1 = SetReaction(reactionDTO, post, userReact);
//            return reaction1;
//        }
//
//        reaction = SetReaction(reactionDTO, post, userReact);
//        return reaction;
        Optional<Post> post = postRepository.findById(reactionDTO.getIdPost());
        postService.checkPostExists(post);

        Optional<User> userReact = userRepository.findById(reactionDTO.getIdUserReact());
        userService.checkUserExists(userReact);

        Reaction reaction = reactionRepository.findByPostIdAndUserId(reactionDTO.getIdPost(), reactionDTO.getIdUserReact());
        if (reaction != null) {
            if (reaction.getReactionType().equals(reactionDTO.getReactionType())) {
                reactionRepository.delete(reaction);
                return reaction;
            }
            SetReaction(reactionDTO, post, userReact, reaction);
        } else {
            reaction = new Reaction();
            reaction.setPost(post.get());
            reaction.setUser(userReact.get());
            SetReaction(reactionDTO, post, userReact, reaction);
        }
        return reaction;
    }

    private void SetReaction(ReactionDTO reactionDTO, Optional<Post> post, Optional<User> userReact, Reaction reaction) {
        if (LIKE.equals(reactionDTO.getReactionType())) {
            reaction.setReactionType(LIKE);
        } else if (LOVE.equals(reactionDTO.getReactionType())) {
            reaction.setReactionType(LOVE);
        } else if (CARE.equals(reactionDTO.getReactionType())) {
            reaction.setReactionType(CARE);
        } else if (HAHA.equals(reactionDTO.getReactionType())) {
            reaction.setReactionType(HAHA);
        } else if (WOW.equals(reactionDTO.getReactionType())) {
            reaction.setReactionType(WOW);
        } else if (SAD.equals(reactionDTO.getReactionType())) {
            reaction.setReactionType(SAD);
        } else {
            reaction.setReactionType(ANGRY);
        }
        reactionRepository.save(reaction);
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
}
