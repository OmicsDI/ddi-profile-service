package uk.ac.ebi.ddi.security.repo;

import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import uk.ac.ebi.ddi.security.model.DataSetShort;
import uk.ac.ebi.ddi.security.model.SelectedDatasets;
import uk.ac.ebi.ddi.security.model.WatchedDataset;

/**
 * Created by azorin on 02/11/2017.
 */
@Repository
public interface SelectedDatasetsRepository extends CrudRepository<SelectedDatasets, String> {
    @Query("{_id: ?0}")
    Iterable<SelectedDatasets> findByUserId(String UserId);
}
