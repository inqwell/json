/**
 * Copyright (C) 2012 Inqwell Ltd
 *
 * You may distribute under the terms of the Artistic License, as specified in
 * the README file.
 *
 * A basic handler for the JSON parser
 * 
 * JSON RFC 4627: http://www.ietf.org/rfc/rfc4627.txt
 *
 * Author: Tom Sanders
 *
 */

package com.inqwell.json;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A Handler implementation that assumes {@link java.util.Map<String, Object>}
 * and  {@link java.util.List<Object>} for JSON objects and arrays.
 * <p/>
 * The structure is built top-down, as-is from the input stream and values
 * are always strings.
 * 
 * @author tom
 */
public class BasicHandler extends DefaultHandler<Map<String, Object>, List<Object>>
{
	@Override
	public Map<String, Object> startObject(String name, Map<String, Object> parentObject, List<Object> parentArray)
	{
		Map<String, Object> ret = new HashMap<String, Object>();
		
		if (parentObject != null && name != null)
			parentObject.put(name, ret);
		else if (parentArray != null)
			parentArray.add(ret);
		
		return ret;
	}

	@Override
	public List<Object> startArray(String name, Map<String, Object> parentObject, List<Object> parentArray)
	{
		List<Object> ret = new ArrayList<Object>();
		
		if (parentObject != null && name != null)
			parentObject.put(name, ret);
		else if (parentArray != null)
			parentArray.add(ret);
		
		return ret;
	}

	@Override
	public List<Object> valueToArray(String value, List<Object> array, int count, boolean isNumeric)
	{
		array.add(value);
		return array;
	}

	@Override
	public Map<String, Object> valueToObject(String name, String value, Map<String, Object> object, boolean isNumeric)
	{
		object.put(name, value);
		return object;
	}

	@Override
	public Map<String, Object> endObject(String name, Map<String, Object> object, Map<String, Object> parentObject, List<Object> parentArray)
	{
		return object;
	}

	@Override
	public List<Object> endArray(String name, List<Object> array, Map<String, Object> parentObject, List<Object> parentArray)
	{
		return array;
	}
}
