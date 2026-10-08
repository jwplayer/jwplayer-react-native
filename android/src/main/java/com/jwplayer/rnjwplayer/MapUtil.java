package com.jwplayer.rnjwplayer;

import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.bridge.ReadableMapKeySetIterator;
import com.facebook.react.bridge.ReadableType;
import com.facebook.react.bridge.WritableMap;

import java.util.Map;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONException;

public class MapUtil {

  public static JSONObject toJSONObject(ReadableMap readableMap) throws JSONException {
    JSONObject jsonObject = new JSONObject();

    ReadableMapKeySetIterator iterator = readableMap.keySetIterator();

    while (iterator.hasNextKey()) {
      String key = iterator.nextKey();
      ReadableType type = readableMap.getType(key);

      switch (type) {
        case Null:
          jsonObject.put(key, null);
          break;
        case Boolean:
          jsonObject.put(key, readableMap.getBoolean(key));
          break;
        case Number:
          jsonObject.put(key, readableMap.getDouble(key));
          break;
        case String:
          jsonObject.put(key, readableMap.getString(key));
          break;
        case Map:
          jsonObject.put(key, MapUtil.toJSONObject(readableMap.getMap(key)));
          break;
        case Array:
          jsonObject.put(key, ArrayUtil.toJSONArray(readableMap.getArray(key)));
          break;
      }
    }

    return jsonObject;
  }

  public static Map<String, Object> toMap(JSONObject jsonObject) throws JSONException {
    Map<String, Object> map = new HashMap<>();
    Iterator<String> iterator = jsonObject.keys();

    while (iterator.hasNext()) {
      String key = iterator.next();
      Object value = jsonObject.get(key);

      if (value instanceof JSONObject) {
        value = MapUtil.toMap((JSONObject) value);
      }
      if (value instanceof JSONArray) {
        value = ArrayUtil.toArray((JSONArray) value);
      }

      map.put(key, value);
    }

    return map;
  }

  public static Map<String, Object> toMap(ReadableMap readableMap) {
    Map<String, Object> map = new HashMap<>();
    ReadableMapKeySetIterator iterator = readableMap.keySetIterator();

    while (iterator.hasNextKey()) {
      String key = iterator.nextKey();
      ReadableType type = readableMap.getType(key);

      switch (type) {
        case Null:
          map.put(key, null);
          break;
        case Boolean:
          map.put(key, readableMap.getBoolean(key));
          break;
        case Number:
          map.put(key, readableMap.getDouble(key));
          break;
        case String:
          map.put(key, readableMap.getString(key));
          break;
        case Map:
          map.put(key, MapUtil.toMap(readableMap.getMap(key)));
          break;
        case Array:
          map.put(key, ArrayUtil.toArray(readableMap.getArray(key)));
          break;
      }
    }

    return map;
  }

  /**
   * Converts a JSON-like map into a {@link WritableMap} for the bridge. Nested maps, {@link List}s
   * and object arrays recurse; every {@link Number} is supported (non-finite doubles become null
   * because the bridge rejects NaN / infinity); anything else is stringified. The input is left
   * untouched.
   */
  @SuppressWarnings("unchecked")
  public static WritableMap toWritableMap(Map<String, Object> map) {
    WritableMap writableMap = Arguments.createMap();

    for (Map.Entry<String, Object> entry : map.entrySet()) {
      String key = entry.getKey();
      Object value = entry.getValue();

      if (value == null) {
        writableMap.putNull(key);
      } else if (value instanceof Boolean) {
        writableMap.putBoolean(key, (Boolean) value);
      } else if (value instanceof Integer) {
        writableMap.putInt(key, (Integer) value);
      } else if (value instanceof Number) {
        double number = ((Number) value).doubleValue();
        if (Double.isNaN(number) || Double.isInfinite(number)) {
          writableMap.putNull(key);
        } else {
          writableMap.putDouble(key, number);
        }
      } else if (value instanceof String) {
        writableMap.putString(key, (String) value);
      } else if (value instanceof Map) {
        writableMap.putMap(key, MapUtil.toWritableMap((Map<String, Object>) value));
      } else if (value instanceof List) {
        writableMap.putArray(key, ArrayUtil.toWritableArray((List<Object>) value));
      } else if (value instanceof Object[]) {
        writableMap.putArray(key, ArrayUtil.toWritableArray((Object[]) value));
      } else {
        writableMap.putString(key, String.valueOf(value));
      }
    }

    return writableMap;
  }
}