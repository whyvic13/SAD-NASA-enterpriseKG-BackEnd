package util;

import com.google.gson.Gson;
import play.mvc.Result;
import play.mvc.Results;

import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;

public class Common {
	public static final String DATE_PATTERN = "yyyy-MM-dd'T'HH:mm:ssz";
	public static final String DATASET_DATE_PATTERN = "yyyyMM";

	public static Result badRequestWrapper(String s)
	{
		Map<String, String> map = new HashMap<>();
		map.put("error", s);
		String error = new Gson().toJson(map);
		return Results.ok(error);
	}

}
