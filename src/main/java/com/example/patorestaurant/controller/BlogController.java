package com.example.patorestaurant.controller;

import com.example.patorestaurant.model.*;
import com.example.patorestaurant.repository.*;
import com.example.patorestaurant.security.SpringUser;
import com.example.patorestaurant.service.EmailService;
import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Controller
public class BlogController {
    @Value("${image.upload.dir}")
    private String imageUploadDir;
    @Autowired
    private BlogRepository blogRepository;
    @Autowired
    private MenuRepository menuRepository;

    @Autowired
    private GalleryCategoriesRepository galleryCategoriesRepository;
    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private EmailService emailService;
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EmailRepository emailRepository;

    @GetMapping("/blog")
    public String openBlog(ModelMap modelMap,
                           @RequestParam("page") Optional<Integer> page,
                           @RequestParam("size") Optional<Integer> size) {
        List<Blog> all = blogRepository.findAll();
//        modelMap.addAttribute("blogss", all);
        int currentPage = page.orElse(1);
        int pageSize = size.orElse(5);
        Page<Blog> blogss = blogRepository.findAll(PageRequest.of(currentPage - 1, pageSize));
        modelMap.addAttribute("blogsPage", blogss);
        int totalPages = blogss.getTotalPages();
        if (totalPages > 0) {
            List<Integer> pageNumbers = IntStream.rangeClosed(1, totalPages)
                    .boxed()
                    .collect(Collectors.toList());
            modelMap.addAttribute("blogPageNumbers", pageNumbers);
        }
        return "blog";
    }

    @PostMapping("/blogAdmin")
    public String blog(@ModelAttribute Blog
                               blog, @RequestParam("picBlog") MultipartFile file, @AuthenticationPrincipal SpringUser springUser, ModelMap modelMap) throws IOException {
        List<Email> emailList = emailRepository.findAll();
        List<User> all = userRepository.findAll();
        String fileName = System.currentTimeMillis() + "_" + file.getOriginalFilename();
        File picture = new File(imageUploadDir + File.separator + fileName);
        file.transferTo(picture);
        blog.setBlogPicUrl(fileName);
        blog.setDate(new Date());
        blog.setUser(springUser.getUser());

        blogRepository.save(blog);
        for (User user : all) {
            emailService.sendSimpleMessage(user.getEmail(), "Hello: " + user.getName(), " We have a new post " + " \n http://localhost:8080/blog/detail?id=" + blog.getId());
        }
        for (Email email : emailList) {
            emailService.sendSimpleMessage(email.getEmail(), "Hello my friend: ", " We have a new post " + " \n http://localhost:8080/blog/detail?id=" + blog.getId());
        }
        System.out.println(blog.getId());
        modelMap.addAttribute("menues", menuRepository.findAll());
        modelMap.addAttribute("galleryCategories", galleryCategoriesRepository.findAll());
        return "adminPage";
    }


    @GetMapping("/blog/getImage")
    public void getImageAsByteArray(HttpServletResponse response, @RequestParam("imgBlog") String picUrl) throws IOException {
        InputStream in = new FileInputStream(imageUploadDir + File.separator + picUrl);
        response.setContentType(MediaType.IMAGE_JPEG_VALUE);
        IOUtils.copy(in, response.getOutputStream());
    }

    @GetMapping("/blog/detail")
    public String currentPage(ModelMap modelMap, @RequestParam("id") int id) {
        Optional<Blog> byId = blogRepository.findById(id);
        if (byId.isPresent()) {
            List<Comment> allByBlog_id = commentRepository.findAllByBlog_Id(id);
            modelMap.addAttribute("blogItem", byId.get());
            modelMap.addAttribute("comments", allByBlog_id);
            return "blog-detail";

        }
        return "redirect:/blog";
    }

    @PostMapping("/addComment")
    public String addComment(@ModelAttribute Comment comment, @RequestParam("blogId") int id, RedirectAttributes attributes) {
        Optional<Blog> byId = blogRepository.findById(id);
        if (byId.isPresent()) {
            comment.setBlog(byId.get());
            comment.setDate(new Date());
            commentRepository.save(comment);
        }
        attributes.addAttribute("id", id);
        return "redirect:/blog/detail";
    }

    @PostMapping("/searchBlog")
    public String searchBlog(@RequestParam("searchText") String name, ModelMap modelMap) {
        List<Blog> blogs = blogRepository.findAllByNameContains(name);
        modelMap.addAttribute("blogs", blogs);
        return "blog";
    }

    @PostMapping("/subscribe")
    public String subscribe(@RequestParam("subEmail") String emailStr, @ModelAttribute Email email) {
        email.setEmail(emailStr);
        emailRepository.save(email);
        return "redirect:/blog";

    }
}
