package nz.gen.wellington.rsstotwitter.controllers.signin;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import nz.gen.wellington.rsstotwitter.model.Account;
import org.springframework.web.servlet.ModelAndView;

public interface SigninHandler<T> {

    ModelAndView getLoginView(HttpServletRequest request, HttpServletResponse response) throws Exception;

    T getExternalUserIdentifierFromCallbackRequest(HttpServletRequest request);

    Account getUserByExternalIdentifier(T externalIdentifier);

    void decorateUserWithExternalSigninIdentifier(Account account, T externalIdentifier);

}
