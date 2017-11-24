package uk.ac.ebi.ddi.security.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.social.facebook.api.User;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import uk.ac.ebi.ddi.security.model.Invite;
import uk.ac.ebi.ddi.security.repo.InvitesRepository;

import java.util.List;

/**
 * Created by azorin on 22/11/2017.
 */

@RestController
public class InviteController {
    private InvitesRepository invitesRepository;

    @Autowired
    public void InviteController(InvitesRepository invitesRepository)
    {
        this.invitesRepository = invitesRepository;
    }

    @RequestMapping(value = "/api/invites/{inviteId}", method = RequestMethod.GET)
    public Invite getInvite(@PathVariable String inviteId) {
        return this.invitesRepository.findById(inviteId);
    }

    @RequestMapping(value = "/api/invites", method = RequestMethod.GET)
    public List<Invite> getAll() {
        return this.invitesRepository.findAll();
    }


}
