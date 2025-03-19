package org.entermediadb.models;

import java.util.ArrayList;
import java.util.Collection;

import org.entermediadb.asset.BaseEnterMediaTest;
import org.entermediadb.llm.LLMManager;
import org.entermediadb.llm.LLMResponse;
import org.json.simple.JSONObject;
import org.openedit.Data;
import org.openedit.WebPageRequest;

public class LlmTest extends BaseEnterMediaTest
{
	public void testOpenAi() throws Exception
	{
		String model = "gpt-4o";
		String channeltype = "chatstreamer";
		
		WebPageRequest req = getFixture().createPageRequest();
		
		Collection modules = getMediaArchive().getList("module");
		req.getUserProfile().setModules(modules);
		
		Data chat = getMediaArchive().getSearcher("chatterbox").createNewData();
		chat.setValue("message","Search for a dog");
		
		req.putPageValue("recent", chat);
		req.putPageValue("chatprofile", req.getUserProfile());
				
		LLMManager manager = getMediaArchive().getLLM(model);
		String chattemplate = "/" + getMediaArchive().getMediaDbId() + "/gpt/inputs/" + manager.getType() + "/" + channeltype + ".html";
		LLMResponse response = manager.runPageAsInput(req, model, chattemplate);

		assertEquals(response.isToolCall(),true);

		String functionName = response.getFunctionName();
		JSONObject arguments = response.getArguments();
		String json = arguments.toJSONString();
		assertNotNull(json,"Missing json");
	}
	@Override
	protected void oneTimeSetup() throws Exception
	{
		getFixture().setCategoryId("site/catalog");	
		getFixture().setRootPath("serverside/webapp");	
		
	}
}
