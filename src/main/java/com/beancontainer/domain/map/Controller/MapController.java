package com.beancontainer.domain.map.Controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class MapController {

    @GetMapping("/create/mymap")
    public String createMyMap() {
        return "CreateMap";
    }

}
