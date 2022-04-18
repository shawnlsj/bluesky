package com.bluesky.mainservice.controller;

import com.bluesky.mainservice.controller.argument.MobilePage;
import org.springframework.mobile.device.Device;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping("/")
@Controller
public class HomeController {

    @GetMapping
    public String home(MobilePage mobilePage) {
        if (mobilePage.isRequested()) {
            return "index_m";
        }
        return "index";
    }
}
