package uk.ac.ebi.ddi.security.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Facet {



    @JsonProperty("label")
    String label = null;
    
    @JsonProperty("id")
    String id = null;

    @JsonProperty("value")
    String value = null;
    
    @JsonProperty("name")
    String name = null;

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Facet(String label, String id, String value, String name) {
		super();
		this.label = label;
		this.id = id;
		this.value = value;
		this.name = name;
	}
    
	
    
}
