package nz.gen.wellington.rsstotwitter.controllers.signin;

import jakarta.servlet.http.HttpServletRequest;
import nz.gen.wellington.rsstotwitter.controllers.LoggedInUserFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;

@Controller
public class SignoutController {

    private final LoggedInUserFilter loggedInUserFilter;
    private final String homePageUrl;

    @Autowired
    public SignoutController(LoggedInUserFilter loggedInUserFilter,
                             @Value("${homepage.url}") String homePageUrl) {
        this.loggedInUserFilter = loggedInUserFilter;
        this.homePageUrl = homePageUrl;
    }

    @RequestMapping(value = "/signout", method = RequestMethod.GET)
    public ModelAndView signout(HttpServletRequest request) {
        loggedInUserFilter.removeLoggedInUser(request);
        return new ModelAndView(new RedirectView(homePageUrl));
    }

}
