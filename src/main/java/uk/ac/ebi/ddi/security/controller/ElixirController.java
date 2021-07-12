package uk.ac.ebi.ddi.security.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.HttpMessageConverterExtractor;
import uk.ac.ebi.ddi.social.elixir.api.Elixir;
import uk.ac.ebi.ddi.social.elixir.api.ElixirProfile;

@RestController
public class ElixirController {

    @Autowired
    Elixir elixir;

    @RequestMapping(value = "/api/elixir/details", method = RequestMethod.GET)
    public ElixirProfile getSocialDetails() {

        return elixir.userOperations().getUserProfile();
    }
}
