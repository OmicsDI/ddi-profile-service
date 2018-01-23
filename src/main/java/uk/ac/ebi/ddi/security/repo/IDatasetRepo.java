package uk.ac.ebi.ddi.security.repo;


import org.bson.types.ObjectId;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;
import uk.ac.ebi.ddi.service.db.model.dataset.Dataset;


import java.util.List;

/**
 * The Access Repository it give information about the access to any resource in the database and the system.
 *
 * @author Mingze
 */
@Repository
public interface IDatasetRepo extends MongoRepository<Dataset,ObjectId>{

    @Query("{'$and':[{accession : ?0}, {database : ?1}]}")
    Dataset findByAccessionDatabaseQuery(String acc, String database);

    @Query(value="{ database : ?0 }", fields ="{database : 1, accession : 1, hashCode: 1, currentStatus: 1}")
    List<Dataset> findByDatabase(String name);

    @Query("{_id: ?0}")
    Dataset findByIdDatabaseQuery(ObjectId _id);

    @Query("{accession: ?0}")
    List<Dataset> findByAccession(String accession);

    @Query("{crossReferences.pubmed:?0}")
    List<Dataset> findByCrossReferencesPubmed(String pubmedId);

    @Query("{'$and':[{database : ?0}, {crossReferences.biomodels__db:{'$exists':true}}]}")
    List<Dataset> findByDatabaseBioModels(String database);

}
