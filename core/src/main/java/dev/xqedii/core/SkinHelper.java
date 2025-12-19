package dev.xqedii.core;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class SkinHelper {
  private static final Map<String, Object> cachedProfiles = new HashMap<>();
  private static final Map<String, Boolean> loadingFlags = new HashMap<>();

  public static boolean isProfileLoading(String name) {
    return loadingFlags.getOrDefault(name, false);
  }

  public static void setProfileLoading(String name, boolean loading) {
    loadingFlags.put(name, loading);
  }

  public static Object getCachedProfile(String name) {
    return cachedProfiles.get(name);
  }

  public static void cacheProfile(String name, Object profile) {
    cachedProfiles.put(name, profile);
  }
}