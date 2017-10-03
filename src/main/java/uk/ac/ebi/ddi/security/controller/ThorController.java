package uk.ac.ebi.ddi.security.controller;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.springframework.social.connect.Connection;
import org.springframework.social.connect.ConnectionRepository;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;


import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.client.HttpClient;
import static org.springframework.http.HttpHeaders.USER_AGENT;

/**
 * Created by azorin on 29/09/2017.
 */
@RestController
public class ThorController {

    @RequestMapping(value = "/api/thor/{Orcid}", method = RequestMethod.POST)
    @CrossOrigin
    public String getUserConnections(@PathVariable String Orcid
            , @RequestParam("srcDatabase") String srcDatabase
            , @RequestHeader("accessToken") String accessToken
            , @RequestBody String req) throws Exception {

        String url = String.format("https://www.ebi.ac.uk/europepmc/hubthor/api/dataclaiming/claimBatchBehalf/%s?srcDatabase=%s",Orcid,srcDatabase);

        HttpClient client = new DefaultHttpClient();
        HttpPost post = new HttpPost(url);

        // add header
        post.setHeader("accessToken", accessToken);
        post.setHeader("Accept","application/json");
        post.setHeader("Content-Type","application/json");

        StringEntity reqEntity = new StringEntity(req);

        post.setEntity(reqEntity);

        HttpResponse response = client.execute(post);

        System.out.println("\nSending 'POST' request to URL : " + url);
        System.out.println("Post parameters : " + post.getEntity());
        System.out.println("Response Code : " +
                response.getStatusLine().getStatusCode());

        BufferedReader rd = new BufferedReader(
                new InputStreamReader(response.getEntity().getContent()));

        StringBuffer result = new StringBuffer();
        String line = "";
        while ((line = rd.readLine()) != null) {
            result.append(line);
        }

        System.out.println(result.toString());

        return "OK";
    }
}
