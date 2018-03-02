package uk.ac.ebi.ddi.security.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.social.security.SocialUserDetails;
import org.springframework.social.security.SocialUserDetailsService;
import org.springframework.stereotype.Service;

import uk.ac.ebi.ddi.security.model.DataSet;
import uk.ac.ebi.ddi.security.model.DomainStats;
import uk.ac.ebi.ddi.security.model.Facet;
import uk.ac.ebi.ddi.security.model.MongoUser;
import uk.ac.ebi.ddi.security.model.StatRecord;
import uk.ac.ebi.ddi.security.repo.MongoUserDetailsRepository;
import uk.ac.ebi.ddi.service.db.model.database.DatabaseDetail;
import uk.ac.ebi.ddi.service.db.model.dataset.Dataset;
import uk.ac.ebi.ddi.service.db.repo.dataset.IDatasetRepo;
import uk.ac.ebi.ddi.service.db.service.database.DatabaseDetailService;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

/**
 * Created by user on 3/12/2017.
 */

@Service
public class MongoUserDetailsService implements UserDetailsService, SocialUserDetailsService {

    private MongoUserDetailsRepository mongoUserDetailsRepository;

	private IDatasetRepo datasetRepo;

	private DatabaseDetailService databaseDetailService;

    @Autowired
    MongoUserDetailsService(MongoUserDetailsRepository mongoUserDetailsRepository){
        this.mongoUserDetailsRepository = mongoUserDetailsRepository;
        this.datasetRepo = datasetRepo;
        this.databaseDetailService = databaseDetailService;
    }

    public MongoUser findById(String Id){

        MongoUser mongoUser = mongoUserDetailsRepository.findByUserId(Id);

        if(null==mongoUser)
            return null;

        MongoUser user = new MongoUser();

        String UserName = mongoUser.getUserName();

        user.setUserId(mongoUser.getUserId());

        user.setUserName(UserName);

        return user;
    }
    public MongoUser findByUsername(String name){

        MongoUser mongoUser = mongoUserDetailsRepository.findByName(name);

        if(null==mongoUser)
            return null;

        MongoUser user = new MongoUser();

        String UserName = mongoUser.getUserName();

        user.setUserId(mongoUser.getUserId());

        user.setUserName(UserName);

        return user;
    }
    public MongoUser findByProviderIdAndProviderUserId(String Id1, String Id2){
        return null;
    }

    public void save(MongoUser u){
        //TODO:calculate unique UID
        String UserId = u.getUserId();

        if(null==UserId){
            /*** Long newUserId = 0L;
            PageRequest request = new PageRequest(0, 1, new Sort(Sort.Direction.DESC, "userId"));
            List<MongoUser> foundUsers = mongoUserDetailsRepository.findByUserIdNotNull(request).getContent();
            if(foundUsers.size() > 0) {
                MongoUser mongoUser = foundUsers.get(0);
                if (null != mongoUser) {
                    newUserId = Long.parseLong(mongoUser.getUserId()) + 1;
                }
            }
            UserId = newUserId.toString(); ***/
            UserId = getToken(8); //UUID.randomUUID().toString();
            u.setUserId(UserId);
        }
        //TODO: roles
        u.setRoles("USER,ADMIN");
        mongoUserDetailsRepository.save(u);
    }

    static public String getToken(int chars) {
        String CharSet = "abcdefghijkmnopqrstuvwxyzABCDEFGHJKLMNOPQRSTUVWXYZ1234567890";
        String Token = "";
        Random random = new Random();
        for (int a = 1; a <= chars; a++) {
            Token += CharSet.charAt(random.nextInt(CharSet.length()));
        }
        return Token;
    }

    public void saveAndFlush(MongoUser u){
        save(u);

        return;
    }

    public List<MongoUser> findAll(){
        return new ArrayList<MongoUser>();
    }

    public int count(){
        return 0;
    }

    @Override
    public MongoUser loadUserByUsername(String s) throws UsernameNotFoundException {
        return findByUsername(s);
    }

    @Override
    public SocialUserDetails loadUserByUserId(String s) throws UsernameNotFoundException {
        return findById(s);
    }
    
    public List getDomain(String userId) {
		List<DatabaseDetail> databaseList =  databaseDetailService.getDatabaseList();
		Map<String, Integer> databaseMap = new HashMap<>();
		//get all database name
		for(DatabaseDetail databaseDetail : databaseList){
			databaseMap.put(databaseDetail.getSource(),0);
		}

		MongoUser mongoUser = mongoUserDetailsRepository.findByUserId(userId);
		DataSet[] dataSets = mongoUser.getDataSets();

		if (dataSets != null) {
			ArrayList<DataSet> list = new ArrayList<DataSet>(Arrays.asList(dataSets));
			for (DataSet dataSet : list) {
				databaseMap.put(dataSet.getSource(), databaseMap.get(dataSet.getSource()) + 1);
			}

			ArrayList<DomainStats> domainList = new ArrayList<>();

			for (String str : databaseMap.keySet()) {
				DomainStats domainStats = new DomainStats(new StatRecord(str, databaseMap.get(str).toString(), str), null);
				domainList.add(domainStats);
			}

			return domainList;
		} else {
			return null;
		}
	}

	public List getOmicsType(String userId) {
		Map<String, Integer> map;
		{
			map = new HashMap<String, Integer>();
			map.put("Total", 0);
			map.put("Transcriptomics", 0);
			map.put("Genomics", 0);
			map.put("Multiomics", 0);
			map.put("Proteomics", 0);
			map.put("Metabolomics", 0);
			map.put("Models", 0);
			map.put("Unknown", 0);
			map.put("Not available", 0);
		}
		MongoUser mongoUser = mongoUserDetailsRepository.findByUserId(userId);
		DataSet[] dataSets = mongoUser.getDataSets();
		if (dataSets != null) {
			ArrayList<DataSet> list = new ArrayList<DataSet>(Arrays.asList(dataSets));
			for (DataSet dataSet : list) {
				String id = dataSet.getId();
				String database = databaseDetailService.retriveAnchorName(dataSet.getSource());
				Dataset existingDataset = datasetRepo.findByAccessionDatabaseQuery(id, database);
				// issues need to fix
				String omicsType = (String) existingDataset.getAdditional().get("omics_type").toArray()[0];
				map.put(omicsType, (int) (map.get(omicsType) + 1));
			}

			ArrayList<Facet> omicsList = new ArrayList<>();

			for (String str : map.keySet()) {
				Facet facet = new Facet(str, str, map.get(str).toString(), str);
				omicsList.add(facet);
			}

			return omicsList;
		} else {
			return null;
		}
	}

	public List getOmicsTypeByYear(String userId) {
		MongoUser mongoUser = mongoUserDetailsRepository.findByUserId(userId);
		DataSet[] dataSets = mongoUser.getDataSets();
		if (dataSets != null) {
			List<Dataset> list = new ArrayList<>();
			Set<String> set = new HashSet<>();
			for (DataSet dataSet : dataSets) {
				Dataset data = datasetRepo.findByAccessionDatabaseQuery(dataSet.getId(),
						databaseDetailService.retriveAnchorName(dataSet.getSource()));
				list.add(data);
				// issue need to fix
				String time = (String) data.getDates().get("publication").toArray()[0];
				String year = time.substring(0,time.indexOf("-"));

				set.add(year);
			}
			List<Map<String, String>> listForMap = new ArrayList<Map<String, String>>();

			for (String str : set) {

				Map<String, Integer> map = new HashMap<>();
				map.put("year", Integer.parseInt(str));
				map.put("genomics", 0);
				map.put("metabolomics", 0);
				map.put("proteomics", 0);
				map.put("transcriptomics", 0);

				Map<String, String> mapForList = new HashMap<String, String>();

				for (Dataset dataset : list) {
					String time = (String) dataset.getDates().get("publication").toArray()[0];
					String year = time.substring(0,time.indexOf("-"));
					if (!year.equals(str)) {
						continue;
					} else {
						Set<String> omics_types = dataset.getAdditional().get("omics_type");
						for (String strForSet : omics_types) {
							map.put(strForSet.toLowerCase(), (int) (map.get(strForSet.toLowerCase()) + 1));
						}
						mapForList.put("year", str);
						mapForList.put("genomics", map.get("genomics").toString());
						mapForList.put("metabolomics", map.get("metabolomics").toString());
						mapForList.put("proteomics", map.get("proteomics").toString());
						mapForList.put("transcriptomics", map.get("transcriptomics").toString());

					}
				}
				listForMap.add(mapForList);
			}
			return listForMap;
		} else {
			return null;
		}
	}
}
