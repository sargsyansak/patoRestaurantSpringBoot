package com.example.patorestaurant.controller;

import com.example.patorestaurant.model.Menu;
import com.example.patorestaurant.model.MenuItem;
import com.example.patorestaurant.repository.GalleryCategoriesRepository;
import com.example.patorestaurant.repository.MenuItemRepository;
import com.example.patorestaurant.repository.MenuRepository;
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
public class MenuController {

    @Value("${image.upload.dir}")
    private String imageUploadDir;

    @Autowired
    private MenuRepository menuRepository;

    @Autowired
    private MenuItemRepository menuItemRepository;

    @Autowired
    private GalleryCategoriesRepository galleryCategoriesRepository;

    @GetMapping("/menu")
    public String openMenu(ModelMap modelMap) {
        List<Menu> all = menuRepository.findAll();
        modelMap.addAttribute("menus", all);
        return "menu";
    }

    @PostMapping("/menuAdmin")
    public String menu(@ModelAttribute Menu menu, @RequestParam("picture") MultipartFile file, ModelMap modelMap) throws IOException {
        if (menuRepository.findByName(menu.getName()) == null) {
            String fileName = System.currentTimeMillis() + "_" + file.getOriginalFilename();
            File picture = new File(imageUploadDir + File.separator + fileName);
            file.transferTo(picture);
            menu.setMenuPicUrl(fileName);
            menuRepository.save(menu);
            return "redirect:adminPage";
        } else {
            modelMap.addAttribute("menues", menuRepository.findAll());
            modelMap.addAttribute("galleryCategories", galleryCategoriesRepository.findAll());
            modelMap.addAttribute("messageMenu", "Menu with this name is already exist");
            return "adminPage";
        }
    }

    @GetMapping("/menu/getImage")
    public void getImageAsByteArray(HttpServletResponse response, @RequestParam("menuImg") String picUrl) throws IOException {
        InputStream in = new FileInputStream(imageUploadDir + File.separator + picUrl);
        response.setContentType(MediaType.IMAGE_JPEG_VALUE);
        IOUtils.copy(in, response.getOutputStream());
    }

    @PostMapping("/menuItem")
    public String menuItem(@ModelAttribute MenuItem menuItem, @RequestParam("picture") MultipartFile file, ModelMap modelMap) throws IOException {
        if (menuRepository.findByName(menuItem.getName()) == null) {
            String fileName = System.currentTimeMillis() + "_" + file.getOriginalFilename();
            File picture = new File(imageUploadDir + File.separator + fileName);
            file.transferTo(picture);
            menuItem.setMenuItemPicUrl(fileName);
            menuItemRepository.save(menuItem);
            return "redirect:adminPage";
        } else {
            modelMap.addAttribute("messageItem", "MenuItem with this name is already exist");
            return "adminPage";
        }
    }
}
