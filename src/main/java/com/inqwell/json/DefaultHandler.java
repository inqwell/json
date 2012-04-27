/**
 * Copyright (C) 2012 Inqwell Ltd
 *
 * You may distribute under the terms of the Artistic License, as specified in
 * the README file.
 *
 * Default handler for JSON parser
 * 
 * JSON RFC 4627: http://www.ietf.org/rfc/rfc4627.txt
 *
 * Author: Tom Sanders
 *
 */

/**
 * 
 */
package com.inqwell.json;

/**
 * A degenerate Handler implementation.
 * <p/>
 * While the parser always scans the entire input, this implementation
 * does nothing and causes the parser to return <code>null</code> for the
 * root object.
 *  
 * @author tom
 *
 * @param <O>
 * @param <A>
 */
public class DefaultHandler<O, A> implements Handler<O, A>
{

	/**
	 * Whether the given value is one of the JSON literals <code>true</code>,
	 * <code>false</code> or <code>null</code>
	 * @param value the value
	 * @return <code>true<code> if a literal, <code>false</code> otherwise.
	 */
	public static boolean isLiteral(String value)
	{
		return value.equals(JSON_TRUE) || value.equals(JSON_FALSE) || value.equals(JSON_NULL);
	}

	@Override
	public O startObject(String name, O parentObject, A parentArray)
	{
		return null;
	}

	@Override
	public A startArray(String name, O parentObject, A parentArray)
	{
		return null;
	}

	@Override
	public String name(String name, O thisObject)
	{
		return name;
	}

	@Override
	public A valueToArray(String value, A array, int count, boolean isNumeric)
	{
		return array;
	}

	@Override
	public O valueToObject(String name, String value, O object, boolean isNumeric)
	{
		return object;
	}

	@Override
	public O endObject(String name, O object, O parentObject, A parentArray)
	{
		return object;
	}

	@Override
	public A endArray(String name, A array, O parentObject, A parentArray)
	{
		return array;
	}
}
