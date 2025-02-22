/*******************************************************************************
 * Copyright (c) 2000, 2006 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.modelica.mdt.debug.core;

 
import org.eclipse.core.runtime.CoreException;

/**
 * A breakpoint that suspends execution when a corresponding exception
 * is thrown in a target VM. An exception breakpoint can be configured
 * to suspend execution when the corresponding exception is thrown in
 * a caught or uncaught location. As well, the location can be filtered
 * inclusively or exclusively by type name patterns.
 * <p>
 * Clients are not intended to implement this interface.
 * </p>
 * @since 2.0
 */
public interface IMDTFailureBreakpoint extends IMDTBreakpoint {
	/**
	 * Sets the inclusion filters that will define the scope for the associated exception.
	 * Filters are a collection of strings of type name prefixes.
	 * Default packages should be specified as the empty string.
	 * 
	 * @param filters the array of filters to apply
	 * @exception CoreException if unable to set the property on 
	 *  this breakpoint's underlying marker
	 * @since 2.1
	 */
	public void setInclusionFilters(String[] filters) throws CoreException;

	/**
	 * Returns the inclusive filters that define the scope for the associated exception.
	 * Filters are a collection of strings of type name prefixes.
	 * 
	 * @return the array of defined inclusive filters
	 * @exception CoreException if unable to access the property on
	 *  this breakpoint's underlying marker
	 * @since 2.1
	 */
	public String[] getInclusionFilters() throws CoreException;

	/**
	 * Returns whether this breakpoint suspends execution when the
	 * associated exception is thrown in a caught location (in
	 * a try/catch statement).
	 * 
	 * @return <code>true</code> if this is a caught exception
	 *  breakpoint
	 * @exception CoreException if unable to access the property from
	 * 	this breakpoint's underlying marker
	 */
	public boolean isCaught() throws CoreException;
	/**
	 * Returns whether this breakpoint suspends execution when the
	 * associated exception is thrown in an uncaught location (not
	 * caught by a try/catch statement).
	 * 
	 * @return <code>true</code> if this is an uncaught exception
	 *  breakpoint.
	 * @exception CoreException if unable to access the property from
	 * 	this breakpoint's underlying marker
	 */
	public boolean isUncaught() throws CoreException;		
	/**
	 * Sets whether this breakpoint suspends execution when the associated
	 * exception is thrown in a caught location (in a try/catch
	 * statement).
	 *
	 * @param caught whether or not this breakpoint suspends execution when the
	 *  associated exception is thrown in a caught location
	 * @exception CoreException if unable to set the property on
	 * 	this breakpoint's underlying marker
	 */
	public void setCaught(boolean caught) throws CoreException;
	/**
	 * Sets whether this breakpoint suspends execution when the associated
	 * exception is thrown in an uncaught location.
	 * 
	 * @param uncaught whether or not this breakpoint suspends execution when the
	 *  associated exception is thrown in an uncaught location
	 * @exception CoreException if unable to set the property
	 * 	on this breakpoint's underlying marker
	 */	
	public void setUncaught(boolean uncaught) throws CoreException;
	/**
	 * Returns whether the exception associated with this breakpoint is a
	 * checked exception (compiler detected).
	 * 
	 * @return <code>true</code> if the exception associated with this breakpoint
	 *  is a checked exception
	 * @exception CoreException if unable to access the property from
	 * 	this breakpoint's underlying marker
	 */
	public boolean isChecked() throws CoreException;
	
	/**
	 * Returns the fully qualified type name of the exception that
	 * last caused this breakpoint to suspend, of <code>null</code>
	 * if this breakpoint has not caused a thread to suspend. Note
	 * that this name may be a sub type of the exception that this
	 * breakpoint is associated with.
	 * 
	 * @return fully qualified exception name or <code>null</code>
	 */
	public String getExceptionTypeName();
	
	/**
	 * Sets the filters that will define the scope for the associated exception.
	 * Filters are a collection of strings of type name prefixes.
	 * Default packages should be specified as the empty string.
	 * 
	 * @param filters the array of filters to apply
	 * @param inclusive whether or not to apply the filters as inclusive or exclusive
	 * @exception CoreException if unable to set the property on 
	 *  this breakpoint's underlying marker
	 * @deprecated Exception breakpoints can have a mixed set of filters. Use setInclusiveFilters(String[] filters) or setExclusiveFilters(String[] filters)
	 */
	public void setFilters(String[] filters, boolean inclusive) throws CoreException;
	
	/**
	 * Sets the exclusion filters that will define the scope for the associated exception.
	 * Filters are a collection of strings of type name prefixes.
	 * Default packages should be specified as the empty string.
	 * 
	 * @param filters the array of filters to apply
	 * @exception CoreException if unable to set the property on 
	 *  this breakpoint's underlying marker
	 * @since 2.1
	 */
	public void setExclusionFilters(String[] filters) throws CoreException;
	
	/**
	 * Returns the filters that define the scope for the associated exception.
	 * Filters are a collection of strings of type name prefixes.
	 * 
	 * @return the array of defined filters
	 * @exception CoreException if unable to access the property on
	 *  this breakpoint's underlying marker
	 * @deprecated Use getExclusionFilters() or getInclusionFilters()
	 */
	public String[] getFilters() throws CoreException;
	
	/**
	 * Returns the exclusive filters that define the scope for the associated exception.
	 * Filters are a collection of strings of type name prefixes.
	 * 
	 * @return the array of defined inclusive filters
	 * @exception CoreException if unable to access the property on
	 *  this breakpoint's underlying marker
	 * @since 2.1
	 */
	public String[] getExclusionFilters() throws CoreException;
	
	/**
	 * Returns whether any inclusive filters have been applied.
	 * @return <code>true</code> if the inclusive filters have been applied
	 * @exception CoreException if unable to access the property on 
	 *  this breakpoint's underlying marker
	 * @deprecated Exception breakpoints can have a mixed set of filters
	 * and this method is maintained strictly for API backwards compatibility
	 */
	public boolean isInclusiveFiltered() throws CoreException;
}

