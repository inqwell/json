/**
 * Copyright (C) 2012 Inqwell Ltd
 *
 * You may distribute under the terms of the Artistic License, as specified in
 * the README file.
 *
 * Tests
 * 
 * JSON RFC 4627: http://www.ietf.org/rfc/rfc4627.txt
 *
 * Author: Tom Sanders
 *
 */

package com.inqwell.json;

import java.io.Reader;
import java.io.StringReader;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.junit.Assert;
import org.junit.Test;

/**
 * JSON parser tests
 * @author tom
 *
 */
public class JSONTest
{
	private static final String rootIsArray =
			"[ " +                              // Open array
	        "\"3.1415927\", " +             // fraction > 1
	        "\"0.1415927\", " +             // fraction < 1 leading zero
	        "null, " +                      // literal null
	        "\".1415927\", " +              // fraction < 1 no leading zero
	        "\"3.1415927E00\", " +          // fraction > 1 uses exponent
	        "\"6.02E+23\", " +              // fraction > 1 -ve exponent
	        "true, " +                      // literal true
	        "'2.99792e+08', " +             // fraction > 1 +ve exponent single quote
	        "'32', // comment\n" +          // integer single quote
	        "\"32\", " +                    // integer double quote
	        "false, " +                     // literal false
	        "'0', /* comment*/" +           // zero single quote
	        "\"0\", " +                     // zero double quote
		      " \"Single'Quote\", " +         // string single quote
		      " 'Double\"Quote' " +           // string double quote
			"]";                                // Close array
	
	private static String[] rootIsArrayExpected = {
      "3.1415927",
      "0.1415927",
      "null",
      ".1415927",
      "3.1415927E00",
      "6.02E+23",
      "true",
      "2.99792e+08",
      "32",
      "32",
      "false",
      "0",
      "0",
      "Single'Quote",
      "Double\"Quote" };

	private static final String rootIsObject =
			"{ " +                                  // Open object
	        "a : \"3.1415927\", " +             // fraction > 1
	        "b:  \"0.1415927\", " +             // fraction < 1 leading zero
	        "c :null, " +                       // literal null
	        "d : \".1415927\", " +              // fraction < 1 no leading zero
	        "\"e\" : \"3.1415927E00\", " +      // fraction > 1 uses exponent
	        "'f' : \"6.02E+23\", " +            // fraction > 1 -ve exponent
	        "\"g\" : true, " +                  // literal true
	        "\"h\" :'2.99792e+08', " +          // fraction > 1 +ve exponent single quote
	        "'i' : '32', " +                    // integer single quote
	        "'j':\"32\", " +                    // integer double quote
	        "\"k\" : false, " +                 // literal false
	        "\"l\" : '0', " +                   // zero single quote
	        "\"m\" : \"0\", " +                 // zero double quote
		      "\"n\" : \"Single'Quote\", " +      // string single quote
		      "\"o\" : 'Double\"Quote' " +        // string double quote
			"}";                                    // Close object
	
	private static final String rootIsArrayNestedObject =
			"[ " +                              // Open array
	        "\"3.1415927\", " +             // fraction > 1
	        "\"0.1415927\", " +             // fraction < 1 leading zero
	        "null, " +                      // literal null
	        "\".1415927\", " +              // fraction < 1 no leading zero
	        "\"3.1415927E00\", " +          // fraction > 1 uses exponent
	        "\"6.02E+23\", " +              // fraction > 1 -ve exponent
	      rootIsObject + "," +        // Leave at this position [6]
	        "true, " +                      // literal true
	        "'2.99792e+08', " +             // fraction > 1 +ve exponent single quote
	        "'32', " +                      // integer single quote
	        "\"32\", " +                    // integer double quote
	        "false, " +                     // literal false
	        "'0', " +                       // zero single quote
	        "\"0\", " +                     // zero double quote
		      " \"Single'Quote\", " +         // string single quote
		      " 'Double\"Quote' " +           // string double quote
			"]";                                // Close array

	private static final String rootIsObjectNestedArray =
			"{ " +                                  // Open object
	        "a : \"3.1415927\", " +             // fraction > 1
	        "b:  \"0.1415927\", " +             // fraction < 1 leading zero
	        "c :null, " +                       // literal null
	        "d : \".1415927\", " +              // fraction < 1 no leading zero
	        "\"e\" : \"3.1415927E00\", " +      // fraction > 1 uses exponent
	        "'f' : \"6.02E+23\", " +            // fraction > 1 -ve exponent
	        "\"g\" : true, " +                  // literal true
	        "\"h\" :'2.99792e+08', " +          // fraction > 1 +ve exponent single quote
	      "'nested' : " + rootIsArray + "," +
	        "'i' : '32', " +                    // integer single quote
	        "'j':\"32\", " +                    // integer double quote
	        "\"k\" : false, " +                 // literal false
	        "\"l\" : '0', " +                   // zero single quote
	        "\"m\" : \"0\", " +                 // zero double quote
		      "\"n\" : \"Single'Quote\", " +      // string single quote
		      "\"o\" : 'Double\"Quote' " +        // string double quote
			"}";                                    // Close object
	
  private static final String objectWithNumerics =
      "{ " +
          "pi : \"3.1415927\", " +
          "planck:  \"6.626e-34\", " +
          "\"lightspeed\" :'2.99792e+08', " +
          "'solarmass' :'1.98892e+30', " +
          "'string2' :'2.718epsilon', " +        // will be identified as a string
          "litnull :null, " +                    // literal null
          "\"littrue\" : true, " +               // literal true
          "\"litfalse\" : false, " +             // literal false
          "string1 : \"Hello, World\", " +       // string
          "\"one\" : '1' " +                     // integer
      "}"; 

  private static final String croaks1 =
      "{ [" +
          "pi : \"3.1415927\" " +
      "}"; 

  private static final String croaks2 =
      "{ " +
          "pi : \"3.1415927\" " +
      ""; 

	private static java.util.Map<String, Object> rootIsObjectExpected;
	
	private static java.util.Map<String, Object> orderedObjectExpected;

	private static java.util.List<Object> orderedArrayExpected;
	
	private static java.util.Map<String, Object> objectWithNumericsExpected;

	static
	{
		rootIsObjectExpected = new java.util.HashMap<String, Object>();
		for (char a = 'a', i = 0; i < rootIsArrayExpected.length; i++, a++)
		{
			rootIsObjectExpected.put(new String(String.valueOf(a)), rootIsArrayExpected[i]);
		}
		
		orderedObjectExpected = new TreeMap<String, Object>(rootIsObjectExpected);
		orderedObjectExpected.remove("n");
		
		String[] ordered = Arrays.copyOf(rootIsArrayExpected, rootIsArrayExpected.length);
		Arrays.sort(ordered);
		orderedArrayExpected = new java.util.ArrayList<Object>();
		for (int i = 0; i < ordered.length; i++)
			orderedArrayExpected.add(ordered[i]);
		
		objectWithNumericsExpected = new java.util.HashMap<String, Object>();
		objectWithNumericsExpected.put("pi", Float.valueOf("3.1415927"));
		objectWithNumericsExpected.put("planck", Double.valueOf("6.626e-34"));
		objectWithNumericsExpected.put("lightspeed", new BigDecimal("2.99792e+08"));
		objectWithNumericsExpected.put("solarmass", new BigDecimal("1.98892e+30"));
    objectWithNumericsExpected.put("string1", "Hello, World");
    objectWithNumericsExpected.put("string2", "2.718epsilon");
		objectWithNumericsExpected.put("one", new Integer(1));
		objectWithNumericsExpected.put("litnull", null);
		objectWithNumericsExpected.put("littrue", Boolean.TRUE);
		objectWithNumericsExpected.put("litfalse", Boolean.FALSE);

	}

	@Test
	public void testArray()
	{
		java.util.List<Object> array = null;
		
		try
		{
  		array = getParser(rootIsArray).parseArray(getHandler());
		}
		catch(Exception e)
		{
			Assert.fail(e.getMessage());
		}
		
		Assert.assertArrayEquals("Arrays do not match", rootIsArrayExpected, array.toArray());
	}
	
	@Test
	public void testObject()
	{
		java.util.Map<String, Object> object = null;
		
		try
		{
  		object = getParser(rootIsObject).parseObject(getHandler());
		}
		catch(Exception e)
		{
			Assert.fail(e.getMessage());
		}
		
		Assert.assertEquals("Objects do not match", rootIsObjectExpected, object);
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void testNestedObject()
	{
		java.util.List<Object> array = null;
		java.util.Map<String, Object> object = null;
		
		try
		{
  		array = getParser(rootIsArrayNestedObject).parseArray(getHandler());
  		object = (java.util.Map<String, Object>)array.get(6);
		}
		catch(Exception e)
		{
			Assert.fail(e.getMessage());
		}
		
		Assert.assertEquals("Objects do not match", rootIsObjectExpected, object);
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void testNestedArray()
	{
		java.util.Map<String, Object> object = null;
		java.util.List<Object> array = null;

		try
		{
  		object = getParser(rootIsObjectNestedArray).parseObject(getHandler());
  		array = (java.util.List<Object>)object.get("nested");
		}
		catch(Exception e)
		{
			Assert.fail(e.getMessage());
		}
		
		Assert.assertArrayEquals("Arrays do not match", rootIsArrayExpected, array.toArray());
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void testNestedObjectSubstitution()
	{
		java.util.List<Object> array = null;
		java.util.Map<String, Object> object = null;
		
		try
		{
  		array = getParser(rootIsArrayNestedObject).parseArray(new OrderedObjectHandler());
  		object = (java.util.Map<String, Object>)array.get(6);
		}
		catch(Exception e)
		{
			e.printStackTrace();
			Assert.fail(e.getMessage());
		}
		
		Assert.assertEquals("Objects do not match", orderedObjectExpected, object);
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void testNestedArraySubstitution()
	{
		java.util.Map<String, Object> object = null;
		java.util.List<Object> array = null;

		try
		{
  		object = getParser(rootIsObjectNestedArray).parseObject(new OrderedArrayHandler());
  		array = (java.util.List<Object>)object.get("nested");
		}
		catch(Exception e)
		{
			Assert.fail(e.getMessage());
		}
		
		Assert.assertEquals("Arrays do not match", orderedArrayExpected, array);
	}
	
	@Test
	public void testNumerics()
	{
		java.util.Map<String, Object> object = null;
		
		try
		{
  		object = getParser(objectWithNumerics).parseObject(new NumericsHandler());
		}
		catch(Exception e)
		{
			Assert.fail(e.getMessage());
		}
		
		Assert.assertEquals("Objects do not match", objectWithNumericsExpected, object);
	}

  @Test(expected=ParseException.class)
  public void croaks1() throws ParseException
  {
    getParser(croaks1).parseArray(new NothingHandler());
  }

  @Test(expected=ParseException.class)
  public void croaks2() throws ParseException
  {
    getParser(croaks2).parseArray(new NothingHandler());
  }

  private Reader getReader(String s)
	{
		return new StringReader(s);
	}
	
	private JSON<java.util.Map<String, Object>, java.util.List<Object>> getParser(String source)
	{
		return new JSON<java.util.Map<String, Object>, java.util.List<Object>>(getReader(source));
	}

	private Handler<java.util.Map<String, Object>, java.util.List<Object>> getHandler()
	{
		return new BasicHandler();
	}
	
	// Some way through object processing, skip a member and rebuild
	// the current (unordered) object into an ordered one.
	private static class OrderedObjectHandler extends BasicHandler
	{
		@Override
		public Map<String, Object> valueToObject(String name, String value, Map<String, Object> object, boolean isNumeric)
		{
			Map<String, Object> ret = object;
			
			if ("n".equals(name))
			{
				// Omit member "n" and return an ordered implementation
				// containing the current contents
				ret = new TreeMap<String, Object>(object);
			}
			else
			  ret.put(name, value);
			
			return ret;
		}

		// Change the behaviour to bottom-up. We are changing the object implementation part
		// way through so its better to add the object to its parent at the end
		@Override
		public Map<String, Object> startObject(String name, Map<String, Object> parentObject, List<Object> parentArray)
		{
			return new HashMap<String, Object>();
		}

		@Override
		public Map<String, Object> endObject(String name, Map<String, Object> object, Map<String, Object> parentObject, List<Object> parentArray)
		{
			if (parentObject != null && name != null)
				parentObject.put(name, object);
			else if (parentArray != null)
				parentArray.add(object);
			
			return object;
		}
	}
	
	// When array processing is finished sort the values placing the new
  // array in the parent container
	private static class OrderedArrayHandler extends BasicHandler
	{
		
		@Override
		public List<Object> startArray(String name, Map<String, Object> parentObject, List<Object> parentArray)
		{
			return new ArrayList<Object>();
		}
		
		@Override
		public List<Object> endArray(String name, List<Object> array, Map<String, Object> parentObject, List<Object> parentArray)
		{
			Object o[] = array.toArray();
			Arrays.sort(o);
			
			java.util.ArrayList<Object> newArray = new java.util.ArrayList<Object>();
			for (int i = 0; i < o.length; i++)
				newArray.add(o[i]);

			if (parentObject != null && name != null)
				parentObject.put(name, newArray);
			else if (parentArray != null)
				parentArray.add(newArray);

			return newArray;
		}
	}
	
	// Convert anything that is numeric to a suitable value type; handle
	// literals also.
	private static class NumericsHandler extends BasicHandler
	{
  	@Override
  	public Map<String, Object> valueToObject(String name, String value, Map<String, Object> object, boolean isNumeric)
  	{
  		if (isNumeric)
  		{
  		  // When isNumeric is true the parser has identified the
  		  // value string as being convertible as such.
  		  if ("pi".equals(name))
  		  	object.put(name, Float.valueOf(value));
  		  else if ("planck".equals(name))
  		  	object.put(name, Double.valueOf(value));
  		  else if ("lightspeed".equals(name))
  		  	object.put(name, new BigDecimal(value));
  		  else if("solarmass".equals(name))
  		  	object.put(name, new BigDecimal(value));
  		  else if("one".equals(name))
  		  	object.put(name, new Integer(value));
  		}
  		else if (DefaultHandler.isLiteral(value)) // the test for one of the three JSON literals
  		{
  			if (DefaultHandler.JSON_TRUE.equals(value))
  				object.put(name, Boolean.TRUE);
  			else if (DefaultHandler.JSON_FALSE.equals(value))
  				object.put(name, Boolean.FALSE);
  			else if (DefaultHandler.JSON_NULL.equals(value))
  				object.put(name, null);
  		}
  		else
  		  object.put(name, value);
  		  
  		return object;
  	}
	}
	
	private static class NothingHandler extends DefaultHandler<java.util.Map<String, Object>, java.util.List<Object>> {}
}
