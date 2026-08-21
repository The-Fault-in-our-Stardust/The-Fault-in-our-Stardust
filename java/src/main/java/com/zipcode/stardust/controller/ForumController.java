package com.zipcode.stardust.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.zipcode.stardust.model.Comment;
import com.zipcode.stardust.model.Post;
import com.zipcode.stardust.model.ReactionType;
import com.zipcode.stardust.model.Subforum;
import com.zipcode.stardust.model.User;
import com.zipcode.stardust.repository.CommentRepository;
import com.zipcode.stardust.repository.PostRepository;
import com.zipcode.stardust.repository.ReactionRepository;
import com.zipcode.stardust.repository.SubforumRepository;
import com.zipcode.stardust.repository.UserRepository;
import com.zipcode.stardust.service.ForumService;
import com.zipcode.stardust.service.MarkdownService;
import com.zipcode.stardust.service.UsernameGenerator;

@Controller
public class ForumController {
    @Autowired
    private MarkdownService markdownService;
    @Autowired private SubforumRepository subforumRepository;
    @Autowired private ReactionRepository reactionRepository;
    @Autowired private PostRepository postRepository;
    @Autowired private CommentRepository commentRepository;
    @Autowired private UserRepository userRepository;
    @Autowired private PasswordEncoder passwordEncoder;
    @Autowired private ForumService forumService;
    @Autowired private UsernameGenerator usernameGenerator;

    @Value("${site.name:Bye Bye Birdie}")
    private String siteName;

    @Value("${site.description:a schooner forum}")
    private String siteDescription;

    private User getCurrentUser(Authentication auth) {

        if (auth == null
                || !auth.isAuthenticated()
                || "anonymousUser".equals(auth.getPrincipal())) {

            return null;
        }

        return (User) auth.getPrincipal();
    }

    private void addCommonAttributes(
            Model model,
            Authentication auth) {

        model.addAttribute("siteName", siteName);
        model.addAttribute("siteDescription", siteDescription);

        if (auth != null
                && auth.isAuthenticated()
                && !"anonymousUser".equals(auth.getPrincipal())) {

            model.addAttribute("currentUser", auth.getName());
            model.addAttribute("isLoggedIn", true);

        } else {

            model.addAttribute("isLoggedIn", false);
        }
    }

    // =========================
    // HOME PAGE
    // =========================

    @GetMapping("/")
    public String index(
            Model model,
            Authentication auth) {

        addCommonAttributes(model, auth);

        List<Subforum> topLevel =
                subforumRepository.findByParentIsNull();

        model.addAttribute("subforums", topLevel);

        return "subforums";
    }

    // =========================
    // ALL DISCUSSIONS
    // =========================

    @GetMapping("/discussions")
    public String discussions(
            Model model,
            Authentication auth) {

        addCommonAttributes(model, auth);

        List<Post> posts =
                postRepository.findAllByOrderByPostdateDesc();

        model.addAttribute("posts", posts);

        return "discussions";
    }

    // =========================
    // SUBFORUM
    // =========================

    @GetMapping("/subforum")
    public String subforum(
            @RequestParam Long sub,
            Model model,
            Authentication auth) {

        addCommonAttributes(model, auth);

        Optional<Subforum> opt =
                subforumRepository.findById(sub);

        if (opt.isEmpty()) {
            return "redirect:/";
        }

        Subforum sf = opt.get();

        List<Post> posts =
                postRepository.findBySubforumOrderByPostdateDesc(sf);

        List<Subforum> children =
                subforumRepository.findByParent(sf);

        String breadcrumb =
                forumService.generateLinkPath(sub);

        model.addAttribute("subforum", sf);
        model.addAttribute("posts", posts);
        model.addAttribute("children", children);
        model.addAttribute("breadcrumb", breadcrumb);

        return "subforum";
    }

    // =========================
    // LOGIN
    // =========================

    @GetMapping("/loginform")
    public String loginForm(
            Model model,
            Authentication auth,
            @RequestParam(required = false) String error) {

        addCommonAttributes(model, auth);

        model.addAttribute("errors", new ArrayList<>());

        if (error != null) {

            List<String> errors = new ArrayList<>();

            errors.add("Invalid username or password.");

            model.addAttribute("errors", errors);
        }

        return "login";
    }

    //user stuff?
    @GetMapping("/user")
public String userPage(Model model, Authentication auth) {
    addCommonAttributes(model, auth);
    User user = getCurrentUser(auth);

    if (user == null) {
        return "redirect:/loginform";
    }

    model.addAttribute("accountUser", user);
    return "user";
}

@PostMapping("/generate-username")
public String generateUsername(Authentication auth) {

    User user = getCurrentUser(auth);

    if (user == null) {
        return "redirect:/loginform";
    }

    String newUsername = usernameGenerator.generateUsername();

    user.setUsername(newUsername);
    userRepository.save(user);

    return "redirect:/user";
}

@GetMapping("/avatar")
public String avatarPage(Model model, Authentication auth) {
    addCommonAttributes(model, auth);

    User user = getCurrentUser(auth);

    if (user == null) {
        return "redirect:/loginform";
    }

    model.addAttribute("accountUser", user);
    return "avatar";
}

@PostMapping("/avatar")
public String chooseAvatar(@RequestParam String avatar,
                           Authentication auth) {

    User user = getCurrentUser(auth);

    if (user == null) {
        return "redirect:/loginform";
    }

    user.setAvatar(avatar);
    userRepository.save(user);

    return "redirect:/user";
}

    @PostMapping("/action_createaccount")
    public String createAccount(
            @RequestParam String username,
            @RequestParam String password,
            @RequestParam String email,
            Model model,
            Authentication auth) {

        addCommonAttributes(model, auth);

        List<String> errors = new ArrayList<>();

        if (!forumService.validUsername(username)) {

            errors.add(
                    "Username must be 4-40 alphanumeric characters "
                            + "(also allowed: !@#%&)."
            );
        }

        if (!forumService.validPassword(password)) {

            errors.add(
                    "Password must be 6-40 alphanumeric characters "
                            + "(also allowed: !@#%&)."
            );
        }

        if (forumService.usernameTaken(username)) {
            errors.add("Username is already taken.");
        }

        if (forumService.emailTaken(email)) {
            errors.add("Email is already registered.");
        }

        if (!errors.isEmpty()) {

            model.addAttribute("errors", errors);

            return "login";
        }

        User user =
                new User(
                        email,
                        username,
                        password,
                        passwordEncoder
                );

        userRepository.save(user);

        return "redirect:/loginform";
    }

    // =========================
    // CREATE POST FORM
    // =========================

    @GetMapping("/addpost")
    public String addPostForm(
            @RequestParam Long sub,
            Model model,
            Authentication auth) {

        addCommonAttributes(model, auth);

        Optional<Subforum> opt =
                subforumRepository.findById(sub);

        if (opt.isEmpty()) {
            return "redirect:/";
        }

        model.addAttribute("subforum", opt.get());
        model.addAttribute("errors", new ArrayList<>());

        return "createpost";
    }

    // =========================
    // CREATE POST
    // =========================

    @PostMapping("/action_post")
    public String createPost(
            @RequestParam Long sub,
            @RequestParam String title,
            @RequestParam String content,
            Model model,
            Authentication auth) {

        addCommonAttributes(model, auth);

        if (auth == null || !auth.isAuthenticated()) {
            return "redirect:/loginform";
        }

        List<String> errors = new ArrayList<>();

        if (!forumService.validTitle(title)) {

            errors.add(
                    "Title must be between 5 and 139 characters."
            );
        }

        if (!forumService.validContent(content)) {

            errors.add(
                    "Content must be between 11 and 4999 characters."
            );
        }

        Optional<Subforum> opt =
                subforumRepository.findById(sub);

        if (opt.isEmpty()) {
            return "redirect:/";
        }

        if (!errors.isEmpty()) {

            model.addAttribute("subforum", opt.get());
            model.addAttribute("errors", errors);

            return "createpost";
        }

        User user = getCurrentUser(auth);

        Post post =
                new Post(
                        title,
                        content,
                        user,
                        opt.get()
                );

        postRepository.save(post);

        return "redirect:/subforum?sub=" + sub;
    }

    // =========================
    // VIEW POST
    // =========================

    @GetMapping("/viewpost")
    public String viewPost(
            @RequestParam Long post,
            Model model,
            Authentication auth) {

        addCommonAttributes(model, auth);

        Optional<Post> opt =
                postRepository.findById(post);

        if (opt.isEmpty()) {
            return "redirect:/";
        }

        Post p = opt.get();

        // Convert Markdown into HTML
        String renderedContent =
                markdownService.toHtml(p.getContent());

        // -------------------------
        // POST REACTION COUNTS
        // -------------------------

        long likeCount =
                reactionRepository.countByPostAndType(
                        p,
                        ReactionType.LIKE
                );

        long dislikeCount =
                reactionRepository.countByPostAndType(
                        p,
                        ReactionType.DISLIKE
                );

        long loveCount =
                reactionRepository.countByPostAndType(
                        p,
                        ReactionType.LOVE
                );

        long laughCount =
                reactionRepository.countByPostAndType(
                        p,
                        ReactionType.LAUGH
                );

        // -------------------------
        // COMMENTS
        // -------------------------

        List<Comment> comments =
                commentRepository.findByPostOrderByPostdateAsc(p);

        // -------------------------
        // COMMENT REACTION COUNTS
        // -------------------------

        Map<Long, Long> commentLikeCounts =
                new HashMap<>();

        Map<Long, Long> commentDislikeCounts =
                new HashMap<>();

        Map<Long, Long> commentLoveCounts =
                new HashMap<>();

        Map<Long, Long> commentLaughCounts =
                new HashMap<>();

        for (Comment comment : comments) {

            Long commentId = comment.getId();

            commentLikeCounts.put(
                    commentId,
                    reactionRepository.countByCommentAndType(
                            comment,
                            ReactionType.LIKE
                    )
            );

            commentDislikeCounts.put(
                    commentId,
                    reactionRepository.countByCommentAndType(
                            comment,
                            ReactionType.DISLIKE
                    )
            );

            commentLoveCounts.put(
                    commentId,
                    reactionRepository.countByCommentAndType(
                            comment,
                            ReactionType.LOVE
                    )
            );

            commentLaughCounts.put(
                    commentId,
                    reactionRepository.countByCommentAndType(
                            comment,
                            ReactionType.LAUGH
                    )
            );
        }

        // -------------------------
        // BREADCRUMB
        // -------------------------

        String breadcrumb =
                forumService.generateLinkPath(
                        p.getSubforum().getId()
                );

        // -------------------------
        // SEND DATA TO THYMELEAF
        // -------------------------

        model.addAttribute("post", p);
        model.addAttribute("comments", comments);
        model.addAttribute("breadcrumb", breadcrumb);
        model.addAttribute("errors", new ArrayList<>());
        model.addAttribute("renderedContent", renderedContent);

        // Post reactions
        model.addAttribute("likeCount", likeCount);
        model.addAttribute("dislikeCount", dislikeCount);
        model.addAttribute("loveCount", loveCount);
        model.addAttribute("laughCount", laughCount);

        // Comment reactions
        model.addAttribute(
                "commentLikeCounts",
                commentLikeCounts
        );

        model.addAttribute(
                "commentDislikeCounts",
                commentDislikeCounts
        );

        model.addAttribute(
                "commentLoveCounts",
                commentLoveCounts
        );

        model.addAttribute(
                "commentLaughCounts",
                commentLaughCounts
        );

        return "viewpost";
    }

    // =========================
    // ADD COMMENT
    // =========================

    @PostMapping("/action_comment")
    public String addComment(
            @RequestParam Long post,
            @RequestParam String content,
            Authentication auth) {

        if (auth == null || !auth.isAuthenticated()) {
            return "redirect:/loginform";
        }

        Optional<Post> opt =
                postRepository.findById(post);

        if (opt.isEmpty()) {
            return "redirect:/";
        }

        User user = getCurrentUser(auth);

        Comment comment =
                new Comment(
                        content,
                        user,
                        opt.get()
                );

        commentRepository.save(comment);

        return "redirect:/viewpost?post=" + post;
    }

    // =========================
    // REDIRECT GET COMMENT
    // =========================

    @GetMapping("/action_comment")
    public String addCommentGet(
            @RequestParam Long post) {

        return "redirect:/viewpost?post=" + post;
    }
}