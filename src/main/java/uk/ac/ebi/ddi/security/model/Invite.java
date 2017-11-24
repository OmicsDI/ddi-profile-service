package uk.ac.ebi.ddi.security.model;

import org.hibernate.validator.constraints.NotEmpty;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import sun.util.resources.cldr.id.CurrencyNames_id;

/**
 * Created by azorin on 22/11/2017.
 */


@Document(collection = "invites")
public class Invite {
    @Id
    @NotEmpty
    public String id;

    public String userName;

    public String email;

    public DataSetShort[] dataSets;
}
