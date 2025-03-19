package org.entermediadb.models;

import static org.entermediadb.asset.BaseEnterMediaTest.log;

import java.util.Collection;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.entermedia.elasticsearch.locks.ClusterLockTest;
import org.entermediadb.asset.BaseEnterMediaTest;
import org.entermediadb.llm.LLMManager;
import org.entermediadb.llm.LLMResponse;
import org.json.simple.JSONObject;
import org.openedit.Data;
import org.openedit.WebPageRequest;
import org.openedit.hittracker.HitTracker;
import org.openedit.hittracker.ListHitTracker;

public class LlmTest extends BaseEnterMediaTest
{
	protected static final Log log = LogFactory.getLog(ClusterLockTest.class);
	
	public void testOpenAi() throws Exception
	{
		String functionName = aisearch("Search for dogs");
		assertEquals("searchall", functionName);
		
		functionName = aisearch("Search for Larry");
		assertEquals("searchentityperson", functionName);
		
		functionName = aisearch("Search for an image of nature");
		assertEquals("searchasset", functionName);
	
	}
	
	public String aisearch(String message)
	{
		String model = "gpt-4o";
		String channeltype = "chatstreamer";
		String channelid = "aichattest";
		
		WebPageRequest req = getFixture().createPageRequest();
		
		Collection modules = getMediaArchive().getList("module");
		req.getUserProfile().setModules(modules);
		
		Data chat = getMediaArchive().getSearcher("chatterbox").createNewData();
		chat.setValue("message",message);
		chat.setValue("channel", channelid);
		chat.setValue("userid", "admin");
		HitTracker recent = new ListHitTracker();
		recent.add(chat);
		
		req.putPageValue("recent", recent);
		req.putPageValue("chatprofile", req.getUserProfile());
				
		LLMManager manager = getMediaArchive().getLLM(model);
		String chattemplate = "/" + getMediaArchive().getMediaDbId() + "/gpt/inputs/" + manager.getType() + "/" + channeltype + ".html";
		LLMResponse response = manager.runPageAsInput(req, model, chattemplate);

		assertEquals(response.isToolCall(),true);

		String functionName = response.getFunctionName();
		
		JSONObject arguments = response.getArguments();
		String json = arguments.toJSONString();
		log.info("Execute: " + functionName + " with Args: " + arguments);
		
		assertNotNull(json,"Missing json");
		
		return functionName;
		
	}
	@Override
	protected void oneTimeSetup() throws Exception
	{
		getFixture().setCategoryId("site/catalog");	
		getFixture().setRootPath("serverside/webapp");	
		
	}
}
