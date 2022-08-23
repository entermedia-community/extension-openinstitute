
import org.entermediadb.asset.MediaArchive
import org.openedit.users.User

public void init()
{
	MediaArchive archive = context.getPageValue("mediaarchive");

	String id = context.getRequestParameter("userid");

	User selecteduser = archive.getUser(id);			

	String email = selecteduser.getEmail();
	String first = selecteduser.getFirstName();
	String last = selecteduser.getLastName();
	selecteduser.setName(first + " " + last + " " + email);
	selecteduser.setEmail(null);
	selecteduser.setLastName("deleted");
	selecteduser.setFirstName("deleted");
	
	Collection uploads = archive.query("useruploads").exact("owner",selecteduser.getId()).query();
	for (upload in uploads)
	{
		archive.getSearcher("useruploads").delete(upload, selecteduser);
	}
		
	archive.saveData("user",selecteduser);
		
	context.putPageValue("data",selecteduser);
	context.putPageValue("searcher",searcher);
	
}

init();


