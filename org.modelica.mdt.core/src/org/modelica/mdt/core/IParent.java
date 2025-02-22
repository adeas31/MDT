/*******************************************************************************
 * Copyright (c) 2006 MDT Team, PELAB
 *******************************************************************************/

package org.modelica.mdt.core;

import java.util.Collection;

import org.eclipse.core.runtime.CoreException;
import org.modelica.mdt.core.compiler.CompilerInstantiationException;
import org.modelica.mdt.core.compiler.ConnectException;
import org.modelica.mdt.core.compiler.InvocationError;
import org.modelica.mdt.core.compiler.UnexpectedReplyException;


/**
 * Common protocol for Modelica elements that contain other Modelica elements.
 * <p>
 * This interface is not intended to be implemented by clients.
 * </p>
 */
public interface IParent 
{
	/**
	 * Returns the immediate children of this element.
	 * Unless otherwise specified by the implementing element,
	 * the children are in no particular order.
	 *
	 * @return the immediate children of this element or empty collection if
	 *    the element does not have children
	 */
	Collection<? extends IModelicaElement> getChildren() 
		throws ConnectException, UnexpectedReplyException, 
			InvocationError, CoreException, CompilerInstantiationException;
	
	/**
	 * Returns whether this element has one or more immediate children.
	 * This is a convenience method, and may be more efficient than
	 * testing whether <code>getChildren</code> is an empty array.
	 *
	 * @return true if the immediate children of this element, false otherwise
	 */
	boolean hasChildren() 
		throws CoreException, ConnectException, UnexpectedReplyException,
			InvocationError, CompilerInstantiationException;
}
