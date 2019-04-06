package com.example.patorestaurant.controller;

import com.example.patorestaurant.model.Reservation;
import com.example.patorestaurant.repository.ReservationRepository;
import com.example.patorestaurant.security.SpringUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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
public class ReservationController {
    @Value("${image.upload.dir}")
    private String imageUploadDir;
    @Autowired
    private ReservationRepository reservationRepository;

    @GetMapping("/reservation")
    public String openReservation() {
        return "reservation";
    }

    @PostMapping("/reservationAdmin")
    public String reservationPage(@RequestParam("datee") String datee, @AuthenticationPrincipal SpringUser springUser,
                                  @ModelAttribute Reservation reservation, ModelMap modelMap) throws ParseException {
        if (reservationRepository.findAllByTimeLike(reservation.getTime()) == null) {
            SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
            Date parse = formatter.parse(datee);
            reservation.setDate(parse);
            reservationRepository.save(reservation);
            return "redirect:/reservation";
        } else {
            modelMap.addAttribute("messageReservation", "Current time was already ordered");
            return "reservation";
        }

    }
}
