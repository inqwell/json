/**
 * Copyright (C) 2012 Inqwell Ltd
 *
 * You may distribute under the terms of the Artistic License, as specified in
 * the README file.
 *
 * Callback handler for JSON parser
 * 
 * JSON RFC 4627: http://www.ietf.org/rfc/rfc4627.txt
 *
 * Author: Tom Sanders
 *
 */

package com.inqwell.json;

/**
 * The strategy for handling JSON parse events. The parser
 * will build a heterogeneous structure of JSON objects,
 * arrays and values from a JSON input stream
 * according to the implementation.
 * <p/>
 * The methods of this interface are called as various events
 * occur during JSON parsing. Implementations are responsible
 * for creating their chosen representation of objects, arrays
 * values and the JSON literals of <code>true</code>, <code>false</code>
 * and <code>null</code>, as well as adding them to the current container.
 * </p>
 * Individual method documentation should be consulted, but broadly
 * speaking the following applies:
 * <ol>
 * <li>Parsing always processes the JSON stream completely unless
 * an exception is thrown.
 * </li>
 * <li>The structure can be built "top-down" or "bottom-up" depending
 * on whether the implementation manipulates the containment
 * hierarchy in the startXXX or endXXX methods
 * </li>
 * <li>The current container is returned by the valueToXXX and
 * endXXX methods. At either of these points the container can
 * be arbitrarily mutated or even completely rebuilt, as long
 * as in endXXX it is (re)placed in its parent. It is then
 * possible, for example, to change the container implementation
 * at some point in the processing, should that be a requirement. 
 * </li>
 * </ol> 
 * 
 * @author tom
 * @see JSON.jj
 *
 * @param <O> The type for JSON objects
 * @param <A> The type for JSON arrays
 */
public interface Handler<O, A>
{
	/**
	 * The value when literal <code>true</code> is encountered
	 */
	public static final String JSON_TRUE  = "true";

	/**
	 * The value when literal <code>false</code> is encountered
	 */
	public static final String JSON_FALSE = "false";

	/**
	 * The value when literal <code>null</code> is encountered
	 */
	public static final String JSON_NULL  = "null";

	/**
	 * A new JSON object is being opened. The implementation must return
	 * an instance of <code>&lt;O&gt;</code>. It may choose to add this
	 * object to any specified parent or this can instead happen
   * when {@link #endObject(String, Object, Object, Object)} is called.
   * 
	 * @param name the name of this object in its parent
   * when <code>parentObject</code> is not null.
	 * @param parentObject the parent object or <code>null</code> when
	 * processing the JSON root or the parent is an array.
	 * @param parentArray the parent array or null when processing the root
	 * or the parent is an object
	 * @return the new object.
	 */
  public O startObject(String name, O parentObject, A parentArray);
  
	/**
	 * A new JSON array is being opened. The implementation must return
	 * an instance of <code>&lt;A&gt;</code>. It may choose to add this
	 * array to any specified parent or this can instead happen
   * when {@link #endArray(String, Object, Object, Object)} is called.
   * 
	 * @param name the name of this array in its parent
   * when <code>parentObject</code> is not null.
	 * @param parentObject the parent object or <code>null</code> when
	 * processing the JSON root or the parent is an array.
	 * @param parentArray the parent array or null when processing the root
	 * or the parent is an object
	 * @return the new object.
	 */
  public A startArray(String name, O parentObject, A parentArray);
  
	/**
	 * A JSON name has been encountered. The implementation may
	 * return a modified name. The return value is passed
	 * to {@link #valueToObject(String, String, Object, boolean)
	 * or {@link #endObject(String, Object, Object, Object)}, whichever
	 * is appropriate.
   * 
	 * @param name the parsed name.
	 * @param thisObject the current object
	 * @return the name.
	 */
  public String name(String name, O thisObject);
  
	/**
	 * A request to place the current value into the
	 * current array.
	 * <p/>
	 * This method will typically add the value (or whatever
	 * representation it chooses for the value) to the array
	 * and return the array argument. However it may
	 * return a new array altogether, possibly reconstructing
	 * the the current contents.
	 * <p/>
	 * Any new return value is passed to subsequent calls at the
	 * the current level and to {@link #endArray(String, Object, Object, Object)}.
	 * </p>
	 * If the given array is substituted at some point the
	 * implementation must ensure the eventual array is placed
	 * in its parent when {@link #endArray(String, Object, Object, Object)}
   * is called.
   * 
	 * @param value the parsed value. If one of the JSON literals the
	 * value will be {@link #JSON_TRUE}, {@link #JSON_FALSE} or {@link #JSON_NULL}
	 * @param array the current array
	 * @param count TODO
	 * @param isNumeric <code>true</code> if the value is a JSON
   * number, <code>false</code> otherwise.
	 * @return the current array.
	 */
  public A valueToArray(String value, A array, int count, boolean isNumeric);
  
	/**
	 * A request to place the current value as a named member into the
	 * current object.
	 * <p/>
	 * This method will typically add the value (or whatever
	 * representation it chooses for the value) to the object
	 * using the given name and return the object argument. However
   * it may return a new object altogether, possibly reconstructing
	 * the current contents.
	 * <p/>
	 * Any new return value is passed to subsequent calls at the
	 * the current level and to {@link #endObject(String, Object, Object, Object)}.
	 * </p>
	 * If the given object is substituted at some point the
	 * implementation must ensure the eventual object is placed
	 * in its parent when {@link #endObject(String, Object, Object, Object)}
   * is called.
   * 
	 * @param name the name of the current member
	 * @param value the parsed value. If one of the JSON literals the
	 * value will be {@link #JSON_TRUE}, {@link #JSON_FALSE} or {@link #JSON_NULL}
	 * @param object the current object
	 * @param isNumeric <code>true</code> if the value is a JSON
   * number, <code>false</code> otherwise.
	 * @return the current object.
	 */
  public O valueToObject(String name, String value, O object, boolean isNumeric);
  
  /**
   * Finalise the construction of the current object.
   * <p/>
   * This method is called after all the current object's values
   * have been processed. Furthermore, the structure beneath the
   * current object has also been processed by this method.
   * <p/>
   * If the containment hierarchy has already been established
   * in {@link #startObject(String, Object, Object)} then implementations
   * may be degenerate and return <code>object</code>. Otherwise
   * they may complete the containment hierarchy, possibly reconstructing
   * and returning a new current object.
	 * @param name the name of this object in its parent
   * when <code>parentObject</code> is not null.
   * @param object the current object
	 * @param parentObject the parent object or <code>null</code> when
	 * processing the JSON root or the parent is an array.
	 * @param parentArray the parent array or null when processing the root
	 * or the parent is an object
   * @return the current object
   */
  public O endObject(String name, O object, O parentObject, A parentArray);

  /**
   * Finalise the construction of the current array.
   * <p/>
   * This method is called after all the current arrays's values
   * have been processed. Furthermore, the structure beneath the
   * current array has also been processed by this method.
   * <p/>
   * If the containment hierarchy has already been established
   * in {@link #startArray(String, Object, Object) then implementations
   * may be degenerate and return <code>array</code>. Otherwise
   * they may complete the containment hierarchy, possibly reconstructing
   * and returning a new current array.
	 * @param name the name of this object in its parent
   * when <code>parentObject</code> is not null.
   * @param array the current array
	 * @param parentObject the parent object or <code>null</code> when
	 * processing the JSON root or the parent is an array.
	 * @param parentArray the parent array or null when processing the root
	 * or the parent is an object
   * @return the current array
   */
  public A endArray(String name, A array, O parentObject, A parentArray);
}
