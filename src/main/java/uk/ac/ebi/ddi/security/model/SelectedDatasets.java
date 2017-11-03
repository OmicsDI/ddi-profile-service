package uk.ac.ebi.ddi.security.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * Created by azorin on 02/11/2017.
 */
@Document(collection = "selecteddatasets")
public class SelectedDatasets {
    @Id
    public String UserId;

    public DataSetShort[] datasets;
}
