package com.beancontainer.domain.map.Controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class MapController {

    @GetMapping("/create/map")
    public String createMyMap() {
        return "map/CreateMap";
    }

    @GetMapping("/mymap")
    public String checkMyMap() {
        return "map/MapList";
    }

    @GetMapping("/mymap/update/{mapId}")
    public String updateMyMap() {
        return "map/UpdateMap";
    }

    @GetMapping("/mymap/{mapId}")
    public String viewDetailMap() {
        return "map/DetailMap";
    }

}
