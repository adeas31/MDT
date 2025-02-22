/*******************************************************************************
 * @author Adrian Pop
 * Copyright (c) 2006 Adrian Pop, adrpo@ida.liu.se
 * All rights reserved. 
 * Permission to use, copy, modify, and distribute this software and
 * its documentation for NON-COMMERCIAL purposes and without fee is hereby 
 * granted, provided that this copyright notice appear in all copies and 
 * that both the copyright notice and this permission notice and warranty
 * disclaimer appear in supporting documentation, and that the name of
 * The Author not be used in advertising or publicity pertaining to
 * distribution of the software without specific, written prior permission.
 * 
 * COMMERCIAL use, copy, modification and distribution 
 * is NOT permitted without prior agreement with Adrian Pop.
 * 
 * THE AUTHOR DISCLAIMS ALL WARRANTIES WITH REGARD TO THIS SOFTWARE,
 * INCLUDING ALL IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS.
 * IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR ANY SPECIAL, INDIRECT OR
 * CONSEQUENTIAL DAMAGES OR ANY DAMAGES WHATSOEVER RESULTING FROM LOSS
 * OF USE, DATA OR PROFITS, WHETHER IN AN ACTION OF CONTRACT, NEGLIGENCE
 * OR OTHER TORTIOUS ACTION, ARISING OUT OF OR IN CONNECTION WITH THE
 * USE OR PERFORMANCE OF THIS SOFTWARE.
*******************************************************************************/
package org.modelica.mdt.ui.actions;

import org.eclipse.swt.widgets.Shell;

import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.text.ITextSelection;
import org.eclipse.ui.IWorkbenchSite;
import org.modelica.mdt.ui.text.ModelicaTextSelection;

/**
 * Action that dispatches the <code>IAction#run()</code> and the 
 * <code>ISelectionChangedListener#selectionChanged</code> 
 * according to the type of the selection. 
 * 
 * <ul>
 * 	<li>if selection is of type <code>ITextSelection</code> then
 * 	<code>run(ITextSelection)</code> and <code>selectionChanged(ITextSelection)</code>
 * 	is called.</li> 
 * 	<li>if selection is of type <code>IStructuredSelection</code> then
 * 	<code>run(IStructuredSelection)</code> and <code>
 * 	selectionChanged(IStructuredSelection)</code> is called.</li>
 * 	<li>default is to call <code>run(ISelection)</code> and <code>
 * 	selectionChanged(ISelection)</code>.</li>
 * </ul>
 */
public abstract class SelectionDispatchAction extends Action implements ISelectionChangedListener {
	
	private IWorkbenchSite fSite;
	private ISelectionProvider fSpecialSelectionProvider;
	
	/**
	 * Creates a new action with no text and no image.
	 * <p>
	 * Configure the action later using the set methods.
	 * </p>
	 * 
	 * @param site the site this action is working on
	 */
	protected SelectionDispatchAction(IWorkbenchSite site) {
		Assert.isNotNull(site);
		fSite= site;
	}

	/**
	 * Creates a new action with no text and no image
	 * 
	 * <p>
	 * Configure the action later using the set methods.
	 * </p>
	 * 
	 * @param site the site this action is working on
	 * @param provider a special selection provider which is used
	 * instead of the site's selection provider or <code>null</code> to use the site's
	 * selection provider. Clients can for example use a {@link ConvertingSelectionProvider}
	 * to first convert a selection before passing it to the action.
	 *  
	 * @since 3.2
	 * @deprecated Use {@link #setSpecialSelectionProvider(ISelectionProvider)} instead. This constructor will be
	 * removed after 3.2 M5.
	 */
	protected SelectionDispatchAction(IWorkbenchSite site, ISelectionProvider provider) {
		this(site);
		setSpecialSelectionProvider(provider);
	}

	/**
	 * Returns the site owning this action.
	 * 
	 * @return the site owning this action
	 */
	public IWorkbenchSite getSite() {
		return fSite;
	}

	/**
	 * Returns the selection provided by the site owning this action.
	 * 
	 * @return the site's selection
	 */	
	public ISelection getSelection() {
		ISelectionProvider selectionProvider= getSelectionProvider();
		if (selectionProvider != null)
			return selectionProvider.getSelection();
		else
			return null;
	}

	/**
	 * Returns the shell provided by the site owning this action.
	 * 
	 * @return the site's shell	
	 */
	public Shell getShell() {
		return fSite.getShell();
	}
	
	/**
	 * Returns the selection provider managed by the site owning this action or the selection
	 * provider explicitly set in {@link #setSpecialSelectionProvider(ISelectionProvider)}.
	 * 
	 * @return the site's selection provider	
	 */
	public ISelectionProvider getSelectionProvider() {
		if (fSpecialSelectionProvider != null) {
			return fSpecialSelectionProvider;
		}
		return fSite.getSelectionProvider();
	}
	
	/**
	 * Sets a special selection provider which will be used instead of the site's selection provider.
	 * This method should be used directly after constructing the action and before the action is
	 * registered as a selection listener. The invocation will not a perform a selection change notification. 
	 * 
	 * @param provider a special selection provider which is used
	 * instead of the site's selection provider or <code>null</code> to use the site's
	 * selection provider. Clients can for example use a {@link ConvertingSelectionProvider}
	 * to first convert a selection before passing it to the action.
	 * 
	 * @since 3.2
	 */
	public void setSpecialSelectionProvider(ISelectionProvider provider) {
		fSpecialSelectionProvider= provider;
	}

	/**
	 * Updates the action's enablement state according to the given selection. This
	 * default implementation calls one of the <code>selectionChanged</code>
	 * methods depending on the type of the passed selection.
	 * 
	 * @param selection the selection this action is working on
	 */
	public void update(ISelection selection) {
		dispatchSelectionChanged(selection);
	}

	/**
	 * Notifies this action that the given structured selection has changed. This default
	 * implementation calls <code>selectionChanged(ISelection selection)</code>.
	 * 
	 * @param selection the new selection
 	 */
	public void selectionChanged(IStructuredSelection selection) {
		selectionChanged((ISelection)selection);
	}

	/**
	 * Executes this actions with the given structured selection. This default implementation
	 * calls <code>run(ISelection selection)</code>.
	 * 
	 * @param selection the selection
	 */
	public void run(IStructuredSelection selection) {
		run((ISelection)selection);
	}
	
	/**
	 * Note: This method is for internal use only. Clients should not call this method.
	 * 
	 * @param selection the selection
	 */
	public void selectionChanged(ModelicaTextSelection selection) {
		selectionChanged((ITextSelection)selection);
	}
	
	/**
	 * Note: This method is for internal use only. Clients should not call this method.
	 * 
	 * @param selection the selection
	 */
	public void run(ModelicaTextSelection selection) {
		run((ITextSelection)selection);
	}
	
	/**
	 * Notifies this action that the given text selection has changed.  This default
	 * implementation calls <code>selectionChanged(ISelection selection)</code>.
	 * 
	 * @param selection the new selection
 	 */
	public void selectionChanged(ITextSelection selection) {
		selectionChanged((ISelection)selection);
	}
	
	/**
	 * Executes this actions with the given text selection. This default implementation
	 * calls <code>run(ISelection selection)</code>.
	 * 
	 * @param selection the selection
	 */
	public void run(ITextSelection selection) {
		run((ISelection)selection);
	}
	
	/**
	 * Notifies this action that the given selection has changed.  This default
	 * implementation sets the action's enablement state to <code>false</code>.
	 * 
	 * @param selection the new selection
 	 */
	public void selectionChanged(ISelection selection) {
		setEnabled(false);
	}
	
	/**
	 * Executes this actions with the given selection. This default implementation
	 * does nothing.
	 * 
	 * @param selection the selection
	 */
	public void run(ISelection selection) {
	}

	/* (non-Modelicadoc)
	 * Method declared on IAction.
	 */
	public void run() {
		dispatchRun(getSelection());
	}
	
	/* (non-Modelicadoc)
	 * Method declared on ISelectionChangedListener.
	 */
	public void selectionChanged(SelectionChangedEvent event) {
		dispatchSelectionChanged(event.getSelection());
	}

	private void dispatchSelectionChanged(ISelection selection) {
		if (selection instanceof IStructuredSelection) {
			selectionChanged((IStructuredSelection)selection);
		} else if (selection instanceof ModelicaTextSelection) {
			selectionChanged((ModelicaTextSelection)selection);
		} else if (selection instanceof ITextSelection) {
			selectionChanged((ITextSelection)selection);
		} else {
			selectionChanged(selection);
		}
	}

	private void dispatchRun(ISelection selection) {
		if (selection instanceof IStructuredSelection) {
			run((IStructuredSelection)selection);
		} else if (selection instanceof ModelicaTextSelection) {
			run((ModelicaTextSelection)selection);
	    } else if (selection instanceof ITextSelection) {
			run((ITextSelection)selection);
		} else {
			run(selection);
		}
	}
}
