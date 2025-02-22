/*
 * This file is part of OpenModelica.
 *
 * Copyright (c) 1998-CurrentYear, Open Source Modelica Consortium (OSMC),
 * c/o Link�pings universitet, Department of Computer and Information Science,
 * SE-58183 Link�ping, Sweden.
 *
 * All rights reserved.
 *
 * THIS PROGRAM IS PROVIDED UNDER THE TERMS OF GPL VERSION 3 LICENSE OR 
 * THIS OSMC PUBLIC LICENSE (OSMC-PL) VERSION 1.2. 
 * ANY USE, REPRODUCTION OR DISTRIBUTION OF THIS PROGRAM CONSTITUTES RECIPIENT'S ACCEPTANCE
 * OF THE OSMC PUBLIC LICENSE OR THE GPL VERSION 3, ACCORDING TO RECIPIENTS CHOICE. 
 *
 * The OpenModelica software and the Open Source Modelica
 * Consortium (OSMC) Public License (OSMC-PL) are obtained
 * from OSMC, either from the above address,
 * from the URLs: http://www.ida.liu.se/projects/OpenModelica or  
 * http://www.openmodelica.org, and in the OpenModelica distribution. 
 * GNU version 3 is obtained from: http://www.gnu.org/copyleft/gpl.html.
 *
 * This program is distributed WITHOUT ANY WARRANTY; without
 * even the implied warranty of  MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE, EXCEPT AS EXPRESSLY SET FORTH
 * IN THE BY RECIPIENT SELECTED SUBSIDIARY LICENSE CONDITIONS OF OSMC-PL.
 *
 * See the full OSMC Public License conditions for more details.
 *
 * Main author: Wladimir Schamai, EADS Innovation Works / Link�ping University, 2009-now
 *
 * Contributors: 
 *   Uwe Pohlmann, University of Paderborn 2009-2010, contribution to the Modelica code generation for state machine behavior, contribution to Papyrus GUI adaptations
 *   Parham Vasaiely, EADS Innovation Works / Hamburg University of Applied Sciences 2009-2011, implementation of simulation plugins
 */
package org.openmodelica.modelicaml.common.services;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.transaction.TransactionalEditingDomain;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.papyrus.infra.core.services.ServiceException;
import org.eclipse.papyrus.infra.core.services.ServicesRegistry;
import org.eclipse.papyrus.infra.core.utils.ServiceUtils;
import org.eclipse.papyrus.infra.core.utils.ServiceUtilsForActionHandlers;
import org.eclipse.papyrus.views.modelexplorer.ModelExplorerPageBookView;
import org.eclipse.papyrus.views.modelexplorer.ModelExplorerView;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.navigator.CommonViewer;
import org.openmodelica.modelicaml.common.constants.Constants;
import org.openmodelica.modelicaml.common.instantiation.TreeUtls;


public class EditorServices {

	public static TransactionalEditingDomain getPapyrusEditingDomain(){
		ServicesRegistry serviceRegistry;
		TransactionalEditingDomain editingDomain = null;
		try {
			serviceRegistry = ServiceUtilsForActionHandlers.getInstance().getServiceRegistry();
			editingDomain = ServiceUtils.getInstance().getTransactionalEditingDomain(serviceRegistry);

		} catch (ServiceException e) {
			e.printStackTrace();
			
			ModelicaMLServices.notify("ModelicaML: Papyrus Editing Domain Access", 
					"Could not access the Papyrus Editing Domain." +
					"\nPlease make sure that your model is open in editor.", 
					2,
					2);
		}
		
		return editingDomain;
	}
	
	
	public static CommonViewer getModelExplorerView(){
		IViewPart view = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().findView(Constants.VIEW_MODELEXPLORER);

		ModelExplorerPageBookView modelExplorerPageBookView = null;
		if (view instanceof ModelExplorerPageBookView) {
			modelExplorerPageBookView = (ModelExplorerPageBookView)view;
		}
		CommonViewer modelExplorerView = ((ModelExplorerView) modelExplorerPageBookView.getAdapter(ModelExplorerView.class)).getCommonViewer();

		return modelExplorerView;
	}
	
	
	public static boolean isVisiblePapyrusModelExplorerView(){
		IViewPart view = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().findView(Constants.VIEW_MODELEXPLORER);

		ModelExplorerPageBookView modelExplorerPageBookView = null;
		if (view instanceof ModelExplorerPageBookView) {
			modelExplorerPageBookView = (ModelExplorerPageBookView)view;
			
			CommonViewer modelExplorerView = ((ModelExplorerView) modelExplorerPageBookView.getAdapter(ModelExplorerView.class)).getCommonViewer();
			if (modelExplorerView != null) {
				return modelExplorerView.getControl().isVisible();
			}
		}

		
		return false;
	}
	
	public static void locateInModelExplorer(Object object, boolean reselectFirst){
		if (object instanceof EObject) {
			
			IViewPart view = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().findView(Constants.VIEW_MODELEXPLORER);

			ModelExplorerPageBookView modelExplorerPageBookView = null;
			if (view instanceof ModelExplorerPageBookView) {
				modelExplorerPageBookView = (ModelExplorerPageBookView)view;
			}
			if (modelExplorerPageBookView.getAdapter(ModelExplorerView.class) != null) {
				CommonViewer modelExplorerView = ((ModelExplorerView) modelExplorerPageBookView.getAdapter(ModelExplorerView.class)).getCommonViewer();
				List<EObject> items = new ArrayList<EObject>();
				items.add((EObject) object);
				
//				ModelExplorerView.reveal(items, modelExplorerView);

				// set focus
				modelExplorerView.getControl().setFocus();
				
				if (reselectFirst) {
					// reset the selection so that the components tree can instantiate the selected class again
					ModelExplorerView.selectReveal(new StructuredSelection(items), modelExplorerView);
				}
				
				// set new selection
				ModelExplorerView.reveal(items, modelExplorerView);
			}
			else {
				MessageDialog.openError(ModelicaMLServices.getShell(), 
						"Locate in Model Explorer Error", 
						"Could not access the Model Explorer. " +
						"Please click on a diagram in order to activate the Model Explorer.");
			}
      	}
	}
	
	
	public static void locateInComponentsTreeView(Object dotPath){
		if (dotPath instanceof String) {
			
			List<Object> elements = new ArrayList<Object>();
			// find treeObject with the dotPath
			HashSet<Object> treeObjects =  TreeUtls.findTreeItems((String) dotPath, TreeUtls.componentsTreeRoot, new HashSet<Object>());
			elements.addAll(treeObjects);
			
			StructuredSelection selection = new StructuredSelection(elements);

			if (TreeUtls.componentsTreeViewer != null) {
				TreeUtls.componentsTreeViewer.setSelection(selection);	
			}
      	}
	}
}
