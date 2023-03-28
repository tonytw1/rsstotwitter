package nz.gen.wellington.rsstotwitter.controllers.signin;

import nz.gen.wellington.rsstotwitter.model.Account;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public interface SigninHandler<T> {

    ModelAndView getLoginView(HttpServletRequest request, HttpServletResponse response) throws Exception;

    T getExternalUserIdentifierFromCallbackRequest(HttpServletRequest request);

    Account getUserByExternalIdentifier(T externalIdentifier);

    void decorateUserWithExternalSigninIdentifier(Account account, T externalIdentifier);

}
