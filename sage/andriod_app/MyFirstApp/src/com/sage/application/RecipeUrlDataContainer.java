package com.sage.application;

import android.support.annotation.NonNull;

import com.sage.entities.RecipeDetails;

import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by tamar.twena on 5/8/2016.
 */
public class RecipeUrlDataContainer {

    private ConcurrentHashMap<RecipeDetails, UrlData> urlDataMap = new ConcurrentHashMap<RecipeDetails, UrlData>();

    private static volatile RecipeUrlDataContainer instance;

    private static final Object LOCK = new Object();

    private RecipeUrlDataContainer() {

    }

    public static RecipeUrlDataContainer getInstance() {
        if (instance == null) {
            synchronized (LOCK) {
                if (instance == null) {
                    instance = new RecipeUrlDataContainer();
                }
            }
        }
        return instance;
    }

    @NonNull
    private UrlData getUrlData(RecipeDetails details) {
        UrlData urlData = urlDataMap.get(details);
        if(urlData == null) {
            urlData = new UrlData();
        }
        return urlData;
    }


    public void setTtileForLinkDetails(RecipeDetails details, String title) {
        UrlData urlData = getUrlData(details);
        urlData.setLinkTitle(title);
        urlDataMap.put(details, urlData);
    }


    public String getTitle(RecipeDetails details) {
        UrlData urlData = urlDataMap.get(details);
        return urlData == null ?  null : urlData.getLinkTitle();
    }


    public void setSiteNameForLinkDetails(RecipeDetails details, String siteName) {
        UrlData urlData = getUrlData(details);
        urlData.setLinkSiteName(siteName);
        urlDataMap.put(details, urlData);
    }


    public String getSiteName(RecipeDetails details) {
        UrlData urlData = urlDataMap.get(details);
        return urlData == null ?  null : urlData.getLinkSiteName();
    }


    public void setLinkImageUrl(RecipeDetails details, String linkImageUrl) {
        UrlData urlData = getUrlData(details);
        urlData.setLinkImageUrl(linkImageUrl);
        urlDataMap.put(details, urlData);
    }

    public String getLinkImageUrl(RecipeDetails details) {
        UrlData urlData = urlDataMap.get(details);
        return urlData == null ?  null : urlData.getLinkImageUrl();
    }

    public boolean hasDataForRecipe(RecipeDetails details) {
        return urlDataMap.containsKey(details);
    }



    class UrlData {

        private String linkTitle;

        private String linkImageUrl;

        private String linkSiteName;


        public String getLinkTitle() {
            return linkTitle;
        }

        public void setLinkTitle(String linkTitle) {
            this.linkTitle = linkTitle;
        }

        public String getLinkImageUrl() {
            return linkImageUrl;
        }

        public void setLinkImageUrl(String linkImageUrl) {
            this.linkImageUrl = linkImageUrl;
        }

        public String getLinkSiteName() {
            return linkSiteName;
        }

        public void setLinkSiteName(String linkSiteName) {
            this.linkSiteName = linkSiteName;
        }
    }
}
