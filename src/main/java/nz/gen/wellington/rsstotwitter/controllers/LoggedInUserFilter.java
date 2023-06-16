package nz.gen.wellington.rsstotwitter.controllers;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import jakarta.servlet.http.HttpServletRequest;
import nz.gen.wellington.rsstotwitter.model.Account;
import nz.gen.wellington.rsstotwitter.model.ConnectedAccount;
import nz.gen.wellington.rsstotwitter.model.Destination;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;

@Component
public class LoggedInUserFilter {

    private static final String LOGGED_IN_USER = "loggedInUser";

    public void setLoggedInUser(HttpServletRequest request, Account account) {
        request.getSession().setAttribute(LOGGED_IN_USER, account);
    }

    public Account getLoggedInUser(HttpServletRequest request) {
        return (Account) request.getSession().getAttribute(LOGGED_IN_USER);
    }

    public void removeLoggedInUser(HttpServletRequest request) {
        request.getSession().removeAttribute(LOGGED_IN_USER);
    }

    public List<ConnectedAccount> connectedAccountsFor(Account account) {
        List<ConnectedAccount> accounts = Lists.newArrayList();
        for (Destination destination : destinationsConnectedToAccount(account)) {
            accounts.add(new ConnectedAccount(destination.getAccountUsername(account), destination, destination.getAccountUrl(account)));
        }
        return accounts;
    }

    private Set<Destination> destinationsConnectedToAccount(Account account) {
        Set<Destination> connected = Sets.newHashSet();
        if (isAccountConnectedToMastodon(account)) {
            connected.add(Destination.MASTODON);
        }
        if (isAccountContentedToTwitter(account)) {
            connected.add(Destination.TWITTER);
        }
        return connected;
    }

    private boolean isAccountConnectedToMastodon(Account account) {
        return account.getMastodonAccessToken() != null;
    }

    private boolean isAccountContentedToTwitter(Account account) {
        return account.getTwitterAccessToken() != null && account.getTwitterRefreshToken() != null;
    }

}
