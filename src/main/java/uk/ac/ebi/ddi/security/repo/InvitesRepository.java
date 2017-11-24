package uk.ac.ebi.ddi.security.repo;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.repository.CrudRepository;

import org.springframework.stereotype.Repository;
import uk.ac.ebi.ddi.security.model.Invite;
import uk.ac.ebi.ddi.security.model.MongoUser;
import uk.ac.ebi.ddi.security.model.SavedSearch;

/**
 * Created by azorin on 22/11/2017.
 */

@Repository
public interface InvitesRepository extends MongoRepository<Invite, String> {

    @Query("{id: ?0}")
    Invite findById(String UserId);
}
