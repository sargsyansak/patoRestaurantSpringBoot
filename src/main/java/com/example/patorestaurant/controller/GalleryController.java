package com.example.patorestaurant.controller;

import com.example.patorestaurant.model.Gallery;
import com.example.patorestaurant.model.GalleryCategories;
import com.example.patorestaurant.repository.GalleryCategoriesRepository;
import com.example.patorestaurant.repository.GalleryRepository;
import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

@Controller
public class GalleryController {
    @Value("${image.upload.dir}")
    private String imageUploadDir;
    @Autowired
    private GalleryCategoriesRepository galleryCategoriesRepository;
    @Autowired
    private GalleryRepository galleryRepository;

    @GetMapping("/gallery")
    public String openGallery(ModelMap modelMap) {
        List<GalleryCategories> allCat = galleryCategoriesRepository.findAll();
        List<Gallery> allImg = galleryRepository.findAll();
        modelMap.addAttribute("galleryCat", allCat);
        modelMap.addAttribute("galleryImg", allImg);
        return "gallery";
    }

    @PostMapping("/galleryCategories")
    public String categories(@ModelAttribute GalleryCategories galleryCategories, ModelMap modelMap) throws IOException {
        if (galleryCategoriesRepository.findByName(galleryCategories.getName()) == null) {
            galleryCategoriesRepository.save(galleryCategories);
            return "redirect:/adminPage";
        } else {
            modelMap.addAttribute("messageGalleryCategories", "Categories with this name is already exist");
            return "adminPage";
        }
    }

    @GetMapping("/gallery/getImage")
    public void getImageAsByteArray(HttpServletResponse response, @RequestParam("imgGallery") String picUrl) throws IOException {
        InputStream in = new FileInputStream(imageUploadDir + File.separator + picUrl);
        response.setContentType(MediaType.IMAGE_JPEG_VALUE);
        IOUtils.copy(in, response.getOutputStream());
    }

    @PostMapping("/galleryAdmin")
    public String menuItem(@ModelAttribute Gallery gallery, @RequestParam("picture") MultipartFile file) throws IOException {
        String fileName = System.currentTimeMillis() + "_" + file.getOriginalFilename();
        File picture = new File(imageUploadDir + File.separator + fileName);
        file.transferTo(picture);
        gallery.setGalleryPicUrl(fileName);
        galleryRepository.save(gallery);
        return "redirect:/adminPage";
    }
}
