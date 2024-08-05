package com.beancontainer.domain.map.Controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class MapController {

    @GetMapping("/create/map")
    public String createMyMap() {
        return "map/CreateMap";
    }

}
