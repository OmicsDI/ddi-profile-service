package uk.ac.ebi.ddi.security.security;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import uk.ac.ebi.ddi.security.exceptions.UnauthenticatedException;
import uk.ac.ebi.ddi.security.model.MongoUser;
import uk.ac.ebi.ddi.security.model.UserAuthentication;

public class UserSecureUtils {

    public static void verifyUser(String userId) {
        final Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication instanceof UserAuthentication) {
            MongoUser user = ((UserAuthentication) authentication).getDetails();
            if (user.getUserId().equals(userId)) {
                return;
            }
        }
        throw new UnauthenticatedException();
    }
}
