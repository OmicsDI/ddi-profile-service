package uk.ac.ebi.ddi.security.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.social.github.api.GitHub;
import org.springframework.social.github.api.GitHubUserProfile;
import org.springframework.social.facebook.api.User;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.Enumeration;

@RestController
public class GithubController {

    @Autowired
    GitHub github;

    @RequestMapping(value = "/api/github/details", method = RequestMethod.GET)
    public GitHubUserProfile getSocialDetails() {
        return github.userOperations().getUserProfile();
    }

    @RequestMapping(value = "/api/github/headers", method = RequestMethod.GET)
    public String getHeaders(HttpServletRequest request) {

        String result = "";

        Enumeration<String> headerNames = request.getHeaderNames();
        while (headerNames.hasMoreElements()) {
            String headerName = headerNames.nextElement();
            result += ("Header Name: <em>" + headerName);
            String headerValue = request.getHeader(headerName);
            result += ("</em>, Header Value: <em>" + headerValue);
            result += ("</em><br/>");
        }

        return result;
    }
}
