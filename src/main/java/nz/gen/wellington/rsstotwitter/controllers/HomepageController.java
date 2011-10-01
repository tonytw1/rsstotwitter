package nz.gen.wellington.rsstotwitter.controllers;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.multiaction.MultiActionController;

public class HomepageController extends MultiActionController {

    public ModelAndView homepage(HttpServletRequest request, HttpServletResponse response) throws IOException {
        ModelAndView mv = new ModelAndView();
        mv.setViewName("homepage");
        return mv;
    }
        
	
}
