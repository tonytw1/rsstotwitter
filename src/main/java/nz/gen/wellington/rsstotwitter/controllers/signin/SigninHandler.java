package nz.gen.wellington.rsstotwitter.controllers.signin;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import nz.gen.wellington.rsstotwitter.model.Account;

import org.springframework.web.servlet.ModelAndView;

public interface SigninHandler {

    ModelAndView getLoginView(HttpServletRequest request, HttpServletResponse response) throws Exception;

    Object getExternalUserIdentifierFromCallbackRequest(HttpServletRequest request);

    Account getUserByExternalIdentifier(Object externalIdentifier);

    void decorateUserWithExternalSigninIdentifier(Account account, Object externalIdentifier);

}
