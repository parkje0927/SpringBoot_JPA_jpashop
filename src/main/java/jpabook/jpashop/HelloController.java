package jpabook.jpashop;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HelloController {

    @GetMapping("hello")
    public String hello(Model model) {
        model.addAttribute("data", "Hello!");
        // return => 화면 이름
        // devtools 를 사용하면 build -> 현재 html 파일을 rebuild 하면 바로 화면이 변한다.
        return "hello";
    }
}
