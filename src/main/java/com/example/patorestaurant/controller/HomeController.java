package com.example.patorestaurant.controller;

import com.example.patorestaurant.model.Reservation;
import com.example.patorestaurant.repository.ReservationRepository;
import com.example.patorestaurant.security.SpringUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

@Controller
public class HomeController {

    @Autowired
    private ReservationRepository reservationRepository;



    @GetMapping("/")
    public String home() {
        return "redirect:/home";
    }

    @GetMapping("/home")
    public String openHome() {
        return "home";
    }

    @PostMapping("/saveReservation")
    public String reservationPage(@RequestParam("datee") String datee, @AuthenticationPrincipal SpringUser springUser, @ModelAttribute Reservation reservation, ModelMap modelMap) throws ParseException {
        if (reservationRepository.findAllByTimeLike(reservation.getTime()) == null) {
            SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
            Date parse = formatter.parse(datee);
            reservation.setDate(parse);
            reservationRepository.save(reservation);
            return "redirect:/home";
        } else {
/*
            modelMap.addAttribute("messageReservation", "Current time was already ordered");
*/
            return "home";
        }

    }

    @GetMapping("/about")
    public String openAbout() {
        return "about";
    }

}