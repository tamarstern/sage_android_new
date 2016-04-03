package com.sage.utils;

public class RecipeOwnerContext {

	private static CachedMap<String, String> ownerMap = new CachedMap<String, String>(1000);

	public static void setOwnerForUrl(String url, String owner) {
		ownerMap.put(url, owner);
	}
	
	public static String getOwner(String url) {
		if(ownerMap.containsKey(url)) {
			return ownerMap.get(url);
		}
		return null;
	}
}
