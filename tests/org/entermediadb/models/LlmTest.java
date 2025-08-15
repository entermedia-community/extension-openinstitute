package org.entermediadb.models;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.collections.map.HashedMap;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.entermedia.elasticsearch.locks.ClusterLockTest;
import org.entermediadb.ai.llm.LlmConnection;
import org.entermediadb.ai.llm.LlmResponse;
import org.entermediadb.asset.BaseEnterMediaTest;
import org.json.simple.JSONObject;
import org.openedit.Data;
import org.openedit.hittracker.HitTracker;
import org.openedit.hittracker.ListHitTracker;

public class LlmTest extends BaseEnterMediaTest
{
	protected static final Log log = LogFactory.getLog(ClusterLockTest.class);
	
//	public void testOpenAi() throws Exception
//	{
//		String functionName = aisearch("Search for dogs");
//		assertEquals("searchall", functionName);
//		
//		functionName = aisearch("Search for Larry");
//		assertEquals("searchentityperson", functionName);
//		
//		functionName = aisearch("Search for an image of nature");
//		assertEquals("searchasset", functionName);
//	
//	}
	
//	public String aisearch(String message)
//	{
//		String model = "gpt-4o";
//		String channeltype = "chatstreamer";
//		String channelid = "aichattest";
//		
//		Map req = new HashMap();
//		
//		Collection modules = getMediaArchive().getList("module");
//		req.getUserProfile().setModules(modules);
//		
//		Data chat = getMediaArchive().getSearcher("chatterbox").createNewData();
//		chat.setValue("message",message);
//		chat.setValue("channel", channelid);
//		chat.setValue("userid", "admin");
//		HitTracker recent = new ListHitTracker();
//		recent.add(chat);
//		
//		req.putPageValue("recent", recent);
//		req.putPageValue("chatprofile", req.getUserProfile());
//				
//		LlmConnection manager = getMediaArchive().getLlmConnection(model);
//		String chattemplate = "/" + getMediaArchive().getMediaDbId() + "/gpt/inputs/" + manager.getServerName() + "/" + channeltype + ".html";
//		LlmResponse response = manager.runPageAsInput(req, model, chattemplate);
//
//		assertEquals(response.isToolCall(),true);
//
//		String functionName = response.getFunctionName();
//		
//		JSONObject arguments = response.getArguments();
//		String json = arguments.toJSONString();
//		log.info("Execute: " + functionName + " with Args: " + arguments);
//		
//		assertNotNull(json,"Missing json");
//		
//		return functionName;
//		
//	}
	@Override
	protected void oneTimeSetup() throws Exception
	{
		getFixture().setCategoryId("site/catalog");	
		getFixture().setRootPath("serverside/webapp");	
		
	}
}
