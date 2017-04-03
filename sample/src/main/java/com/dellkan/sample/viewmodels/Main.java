package com.dellkan.sample.viewmodels;

import com.dellkan.net.Request;
import com.dellkan.net.RequestCallback;
import com.dellkan.net.parsers.json.JSONInboundParser;
import com.dellkan.robobinding.helpers.model.IHasPresentationModel;
import com.dellkan.robobinding.helpers.model.ListContainer;
import com.dellkan.robobinding.helpers.model.PresentationModelWrapper;
import com.dellkan.robobinding.helpers.modelgen.ListItems;
import com.dellkan.robobinding.helpers.modelgen.PresentationMethod;
import com.dellkan.robobinding.helpers.modelgen.PresentationModel;
import com.dellkan.robobinding.helpers.modelgen.SkipMethod;
import com.dellkan.robobinding.helpers.modelgen.TwoStateGetSet;
import com.dellkan.sample.URLS;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

@PresentationModel
public class Main extends PresentationModelWrapper implements Serializable {
    @ListItems
    ListContainer<YoutubeVideo> videoList = new ListContainer<>();

    @TwoStateGetSet
    boolean loading;

	private static Main sModel;

	@SkipMethod
	public static Main get() {
		if (sModel == null) {
			sModel = new Main();
		}
		return sModel;
	}

    private Main() {
        load();
    }

	@PresentationMethod
	public IHasPresentationModel getCopy() {
		return getPresentationModel();
	}

	@PresentationMethod
    public void load() {
	    if (videoList.size() > 0) {
		    videoList.getItems().clear();
		    refresh("videoList");
		    return;
	    }

        Map<String, Object> params = new HashMap<>();
        params.put("part", "snippet");
        params.put("maxResults", 15);
        params.put("order", "viewCount");
        params.put("type", "video");
        params.put("key", "AIzaSyDPt5BDDbNARTY48E8epDtqKjcSMvDknhg");
        Request.newInstance(URLS.YOUTUBE_SEARCH.getURL(), Request.Method.GET, params, new RequestCallback<JSONInboundParser>(new JSONInboundParser()) {
            @Override
            public void onStart() {
                super.onStart();

                loading = true;
                refresh("loading");
	            refresh("loadingActive");
	            refresh("loadingInactive");
            }

            @Override
            public void onFailure() {
                super.onFailure();
            }

            @Override
            public void onSuccess() {
                super.onSuccess();

	            videoList.getItems().clear();

                // Parse response
                JSONArray items = getParser().getObjectResponse().optJSONArray("items");
                for (int i = 0; i < items.length(); i++) {
                    JSONObject item = items.optJSONObject(i);
                    if (item != null) {
	                    JSONObject id = item.optJSONObject("id");
                        JSONObject snippet = item.optJSONObject("snippet");
                        videoList.addItem(new YoutubeVideo(
		                        id.optString("videoId"),
                                snippet.optString("title"),
                                snippet.optJSONObject("thumbnails").optJSONObject("medium").optString("url")
                        ));
                    }
                }

                // List is refreshed. Let the presentationmodel know
                refresh("videoList");
            }

            @Override
            public void onFinish() {
                super.onFinish();

                loading = false;
	            refresh("loading");
	            refresh("loadingActive");
	            refresh("loadingInactive");
            }
        });
    }
}
