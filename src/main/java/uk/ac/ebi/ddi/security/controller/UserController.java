package uk.ac.ebi.ddi.security.controller;

import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import uk.ac.ebi.ddi.security.exceptions.UnauthenticatedException;
import uk.ac.ebi.ddi.security.model.*;
import uk.ac.ebi.ddi.security.repo.MongoUserDetailsRepository;
import uk.ac.ebi.ddi.security.repo.SavedSearchRepository;
import uk.ac.ebi.ddi.security.repo.SelectedDatasetsRepository;
import uk.ac.ebi.ddi.security.repo.WatchedDatasetsRepository;
import uk.ac.ebi.ddi.security.security.UserSecureUtils;
import uk.ac.ebi.ddi.security.service.MongoUserDetailsService;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

@RestController
public class UserController {

	@Autowired
	MongoUserDetailsRepository mongoUserDetailsRepository;

	@Autowired
	SavedSearchRepository savedSearchRepository;

	@Autowired
	WatchedDatasetsRepository watchedDatasetsRepository;

	@Autowired
	SelectedDatasetsRepository selectedDatasetsRepository;
	
	@Autowired
	MongoUserDetailsService mongoUserDetailsService;

	@RequestMapping(value = "/api/user/current", method = RequestMethod.GET)
	@CrossOrigin
	public MongoUser getCurrent() {
		final Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		if (authentication instanceof UserAuthentication) {
			MongoUser user = ((UserAuthentication) authentication).getDetails();
			MongoUser userDetails = mongoUserDetailsRepository.findByUserId(user.getUserId());
			userDetails.setImage(null);
			return userDetails;
		}
		return new MongoUser(); //anonymous user support
	}

	@RequestMapping(value = "/api/user", method = RequestMethod.GET)
	@CrossOrigin
	public MongoUser getUser(@RequestParam(value="username", required=true) String username) {
		return mongoUserDetailsRepository.findPublicById(username);
	}

	//@RequestMapping(value = "/api/mongo", method = Re
	// questMethod.GET)
	//@CrossOrigin
	//public MongoUser getMongoUser() {
	//	return mongoUserDetailsRepository.findByUserId("0");
	//}

	@RequestMapping(value = "/api/user/current", method = RequestMethod.POST)
	@CrossOrigin
	public void setMongoUser(@RequestBody MongoUser mongoUser) {
		UserSecureUtils.verifyUser(mongoUser.getUserId());
		MongoUser user = mongoUserDetailsRepository.findByUserId(mongoUser.getUserId());

		mongoUser.setImage(user.getImage());
		//TODO:
		//mongoUser.coAuthors =

		mongoUserDetailsRepository.save(mongoUser);
	}

	@RequestMapping(value = "/api/users/{userId}/picture2", method = RequestMethod.POST)
	@CrossOrigin
	public void setUserPicture(@PathVariable String userId, @RequestBody String picture) {
		UserSecureUtils.verifyUser(userId);
		MongoUser user = mongoUserDetailsRepository.findByUserId(userId);

		try {
			byte[] b = picture.getBytes();
			user.setImage(b);
			mongoUserDetailsRepository.save(user);
		} catch(Exception ex){
			System.out.print("Exception:"+ex.getMessage());
		}
		//System.out.print("PIC:"+picture);
	}

	@PostMapping("/api/users/{userId}/picture")
	public void handleFileUpload(@PathVariable String userId, @RequestParam(value = "file") MultipartFile file,
								   RedirectAttributes redirectAttributes) {
		UserSecureUtils.verifyUser(userId);
		MongoUser user = mongoUserDetailsRepository.findByUserId(userId);
		try {
			byte[] b = file.getBytes();
			user.setImage(b);
		}catch(Exception ex){
			System.out.print("Exception:"+ex.getMessage());
		}
		mongoUserDetailsRepository.save(user);
	}

	@Autowired
	ServletContext servletContext;

	@RequestMapping(value = "/api/users/{userId}/picture", method = RequestMethod.GET, produces = MediaType.IMAGE_JPEG_VALUE)
	public byte[] getUserPicture(@PathVariable String userId, final HttpServletResponse response) throws IOException {
		response.setHeader("Cache-Control", "no-cache");

		MongoUser user = mongoUserDetailsRepository.findByUserId(userId);
		byte[] b = user.getImage();
		if(null==b){
			InputStream in = servletContext.getResourceAsStream("no_image.jpg");
			b = IOUtils.toByteArray(in);
		}
		return b;
	}

	@RequestMapping(value = "/api/users/{userId}/coauthors", method = RequestMethod.GET)
	@CrossOrigin
	public UserShort[] getCoAuthors(@PathVariable String userId) {

		ArrayList<UserShort> result = new ArrayList<UserShort>();
		/************ NullPointerException here ********************
		MongoUser me = mongoUserDetailsRepository.findByUserId(userId);

		for(MongoUser mongoUser : mongoUserDetailsRepository.findAll()) {
			Boolean found = false;
			for (DataSet ds : mongoUser.getDataSets()) {
				for (DataSet ds2 : me.getDataSets()) {
					if ((ds.getId() == ds2.getId())
							&& (ds.getSource() == ds.getSource())) {
						found = true;
						break;
					}
					if (found)
						break;
				}
				if (found) {
					UserShort user = new UserShort();
					user.setUserId(mongoUser.getUserId());
					user.setUserName(mongoUser.getUserName());
					result.add(user);
				}
			}
		}
		******************************************************/
		UserShort user = new UserShort();
		user.setUserId("0");
		user.setUserName("Mark Twain");
		result.add(user);

		return result.toArray(new UserShort[0]);
	}

	@RequestMapping(value = "/api/users", method = RequestMethod.GET)
	@CrossOrigin
	public Iterable<MongoUser> getUsers() {
		return mongoUserDetailsRepository.findAll();
	}

	@RequestMapping(value = "/api/users/count", method = RequestMethod.GET)
	@CrossOrigin
	public long getUsersCount() {
		return mongoUserDetailsRepository.count();
	}

	@RequestMapping(value = "/api/users/{userId}/savedsearches", method = RequestMethod.GET)
	@CrossOrigin
	public Iterable<SavedSearch> getSavedSearches(@PathVariable String userId) {
		UserSecureUtils.verifyUser(userId);
		return savedSearchRepository.findByUserId(userId);
	}

	@RequestMapping(value = "/api/users/{userId}/savedsearches", method = RequestMethod.POST)
	@CrossOrigin
	public void saveSavedSearch(@RequestBody SavedSearch savedSearch) {
		UserSecureUtils.verifyUser(savedSearch.userId);
		savedSearchRepository.save(savedSearch);
	}

	@RequestMapping(value = "/api/users/{userId}/savedsearches/{id}", method = RequestMethod.DELETE)
	@CrossOrigin
	public void deleteSavedSearch(@PathVariable String userId,@PathVariable String id) {
		UserSecureUtils.verifyUser(userId);
		SavedSearch savedSearch = savedSearchRepository.findOne(id);
		if (savedSearch.userId.equals(userId)) {
			savedSearchRepository.delete(id);
		} else {
			throw new UnauthenticatedException();
		}
	}

	@RequestMapping(value = "/api/users/{userId}/watches", method = RequestMethod.GET)
	@CrossOrigin
	public Iterable<WatchedDataset> getWatchedDatasets(@PathVariable String userId) {
		UserSecureUtils.verifyUser(userId);
		return watchedDatasetsRepository.findByUserId(userId);
	}

	@RequestMapping(value = "/api/users/{userId}/watches", method = RequestMethod.POST)
	@CrossOrigin
	public WatchedDataset saveWatchedDataset(@RequestBody WatchedDataset watchedDataset) {
		UserSecureUtils.verifyUser(watchedDataset.userId);
		return watchedDatasetsRepository.save(watchedDataset);
	}

	@RequestMapping(value = "/api/users/{userId}/watches/{id}", method = RequestMethod.DELETE)
	@CrossOrigin
	public void deleteWatchedDataset(@PathVariable String userId,@PathVariable String id) {
		UserSecureUtils.verifyUser(userId);
		WatchedDataset watchedDataset = watchedDatasetsRepository.findOne(id);
		if (watchedDataset.userId.equals(userId)) {
			watchedDatasetsRepository.delete(id);
		} else {
			throw new UnauthenticatedException();
		}
	}

	@RequestMapping(value = "/api/users/{userId}/selected", method = RequestMethod.POST)
	@CrossOrigin
	public void setSelectedDatasets(@PathVariable String userId, @RequestBody DataSetShort[] datasets) {
		UserSecureUtils.verifyUser(userId);
		SelectedDatasets d = new SelectedDatasets();
		d.UserId = userId;
		d.datasets = datasets;
		selectedDatasetsRepository.save(d);
	}

	@RequestMapping(value = "/api/users/{userId}/selected", method = RequestMethod.GET)
	@CrossOrigin
	public DataSetShort[] getSelectedDatasets(@PathVariable String userId) {
		UserSecureUtils.verifyUser(userId);
		Iterable<SelectedDatasets> datasets = selectedDatasetsRepository.findByUserId(userId);
		if (datasets.iterator().hasNext()) {
			SelectedDatasets d = datasets.iterator().next();
			return (null!=d ? d.datasets : new DataSetShort[]{});
		}
		return new DataSetShort[]{};
	}
	
	@RequestMapping(value = "/api/users/{userId}/domain", method = RequestMethod.GET)
	@CrossOrigin
	public List getDatasets(@PathVariable String userId) {
		return mongoUserDetailsService.getDomain(userId);
	}
	
	@RequestMapping(value = "/api/users/{userId}/omics", method = RequestMethod.GET)
	@CrossOrigin
	public List getOmicstype(@PathVariable String userId) {
		return mongoUserDetailsService.getOmicsType(userId);
	}
	
	@RequestMapping(value = "/api/users/{userId}/omicsbyyears", method = RequestMethod.GET)
	@CrossOrigin
	public List getOmicsByYears(@PathVariable String userId) {
		return mongoUserDetailsService.getOmicsTypeByYear(userId);
	}

}
