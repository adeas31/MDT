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
 * Main author: Wladimir Schamai, EADS Innovation Works / Link�ping University, 2009-2013
 *
 * Contributors: 
 *   Uwe Pohlmann, University of Paderborn 2009-2010, contribution to the Modelica code generation for state machine behavior, contribution to Papyrus GUI adaptations
 */
package org.openmodelica.modelicaml.modelica.importer.handlers;

import java.util.ArrayList;
import java.util.HashSet;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler;
import org.eclipse.core.commands.IHandlerListener;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.IJobChangeEvent;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.core.runtime.jobs.JobChangeAdapter;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.transaction.TransactionalEditingDomain;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.papyrus.infra.core.resource.NotFoundException;
import org.eclipse.papyrus.infra.core.services.ServicesRegistry;
import org.eclipse.papyrus.uml.tools.model.UmlModel;
import org.eclipse.papyrus.uml.tools.model.UmlUtils;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;
import org.eclipse.uml2.uml.Element;
import org.eclipse.uml2.uml.NamedElement;
import org.openmodelica.modelicaml.common.constants.Constants;
import org.openmodelica.modelicaml.common.dialogs.DialogMessage;
import org.openmodelica.modelicaml.common.services.EditorServices;
import org.openmodelica.modelicaml.common.services.ModelicaMLServices;
import org.openmodelica.modelicaml.modelica.importer.creators.ModelicaMLElementsCreator;
import org.openmodelica.modelicaml.modelica.importer.dialogs.SynchronizeOptionsDialog;
import org.openmodelica.modelicaml.modelica.importer.model.ClassItem;
import org.openmodelica.modelicaml.modelica.importer.model.TreeBuilder;
import org.openmodelica.modelicaml.modelica.importer.model.TreeObject;
import org.openmodelica.modelicaml.modelica.importer.model.TreeParent;
import org.openmodelica.modelicaml.modelica.importer.model.Utilities;

public class SynchronizeModelicaModelProxiesHandler implements IHandler {

	private TreeBuilder treeBuilder = new TreeBuilder();
	private ModelicaMLElementsCreator ec;
	private TreeParent treeRoot = null;
	
	// default values
	private boolean createProxiesAfterLoadingModelicaClasses = true;
	
	private boolean applyProxyStereotype = true;
	private boolean update = true;
	private boolean deleteNotUsedProxies = false;
	private boolean fullImport = false;
	
	private UmlModel umlModel;
	private EObject ModelicaMLRoot;
	private ServicesRegistry serviceRegistry;
	private TransactionalEditingDomain editingDomain;

	
	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		
		umlModel = UmlUtils.getUmlModel();
		
		try {
			ModelicaMLRoot = umlModel.lookupRoot();
//			serviceRegistry = ServiceUtilsForActionHandlers.getInstance().getServiceRegistry();
//			editingDomain = ServiceUtils.getInstance().getTransactionalEditingDomain(serviceRegistry);
			editingDomain = EditorServices.getPapyrusEditingDomain();
			
		} catch (NotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
//		if (serviceRegistry != null && editingDomain != null && ModelicaMLRoot != null && umlModel != null) {
		if (editingDomain != null && ModelicaMLRoot != null && umlModel != null) {
			treeRoot = new TreeParent(Constants.folderName_code_sync);
			treeBuilder.setValidateProxies(true);
			treeBuilder.setCreateOMCMarker(true);
			
			// load, synchronize and validate existing proxies.
			actionLoad(); // load action automatically starts the synch job when finished.
		}
		else {
			String title = "ModelicaML Proxies Synchronization Error";
			String message = "Could access the ModelicaML model or it its editing domain.";
			MessageDialog.openError(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), title, message);
		}
		
		return null;
	}
	
	
	// Load job listener
	final JobChangeAdapter loadJobChangeAdapter = new JobChangeAdapter() {
		public void done(IJobChangeEvent event) {
            if (event.getResult().isOK()) {
            	Display.getDefault().asyncExec(new Runnable() {
        			public void run() {
        				
        				if (createProxiesAfterLoadingModelicaClasses) {
							// run the synch action after loading
        					actionSynchronize();
						}
        				else {
        					// report what was created/updated
	        				String message = "";
	        				int loadedClassesNumber = treeBuilder.getLoadedClasses().size();
	        				int loadedComponentsNumber = treeBuilder.getLoadedComponents().size();
	        				int loadedExtendsRelationsNumber = treeBuilder.getLoadedExtendsRelations().size();
	        				
	        				message = message + "Number of loaded classes: " + loadedClassesNumber + "\n";
	        				message = message + "Number of loaded components: " + loadedComponentsNumber + "\n";
	        				message = message + "Number of loaded extends relations: " + loadedExtendsRelationsNumber + "\n";
	        				
	        				boolean isError = loadedClassesNumber == 0;
	        				DialogMessage dialog = new DialogMessage(new Shell(), "Modelica Models Synchronization Report", null, message, isError);
	        				dialog.open();
        				}
        			}
        		});
            }
            else {
            	System.err.println("Could not complete the loading of Modelica Models through OMC.");
            	}
			}
         };
	
         
         
         
	private void actionLoad(){
		final UmlModel umlModel = UmlUtils.getUmlModel();
		if (umlModel == null) {
			MessageDialog.openError(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), 
					"ModelicaML Model Editing Domain Access Error", 
					"Cannot acceess the ModelicaML model or its editing domain. " +
					"Please make sure that the ModelicaML model is open in the active editor.");
		}
		else {
			try {
				final EObject ModelicaMLRoot = umlModel.lookupRoot();
//					final ServicesRegistry serviceRegistry = ServiceUtilsForActionHandlers.getInstance().getServiceRegistry();
//					final TransactionalEditingDomain editingDomain = ServiceUtils.getInstance().getTransactionalEditingDomain(serviceRegistry);
					final TransactionalEditingDomain editingDomain = EditorServices.getPapyrusEditingDomain();
					
//					if (serviceRegistry != null && editingDomain != null && ModelicaMLRoot != null && umlModel != null) {
					if (editingDomain != null && ModelicaMLRoot != null && umlModel != null) {
						
						// ask to set options
						SynchronizeOptionsDialog dialog = new SynchronizeOptionsDialog(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell());
						dialog.open();
						int result = dialog.getReturnCode();
						if (result == IDialogConstants.OK_ID) {

							// ask user for options
							/*
							 * Dialog for selecting the options: 
							 * 	- apply proxy stereotype
							 * 		A proxy stereotype indicates that the a ModelicaML element is not complete. 
							 *  	It is a proxy that reflects only information about a Modelica model that is needed to its usage.
							 *  	No code is generated from a proxy.
							 * 	- update existing proxies
							 * 	- delete proxies that do not exist in the loaded Modelica models (NOT RECOMMENDED). 
							 * 		If proxies are referenced by other elements then the references should 
							 * 		be redirected to new proxies before deleting the old proxies.
							 */
							applyProxyStereotype = dialog.isApplyProxyStereotype();
							update = dialog.isUpdate();
							deleteNotUsedProxies = dialog.isDeleteNotUsedProxies();
							
							fullImport = dialog.isFullImport();
							treeBuilder.setFullImport(fullImport);
							
							Job job = new Job("Loading Modelica Models from '"+Constants.folderName_code_sync+"' folder..."){

								@Override
								protected IStatus run(IProgressMonitor monitor) {
									
									TreeObject[] children = treeRoot.getChildren();
									for (int i = 0; i < children.length; i++) {
										treeRoot.removeChild(children[i]);
									}
									
									// set the uml model to encapsulate the job required data
									treeBuilder.setModelicaMLModel(umlModel);
									treeBuilder.setModelicaMLRoot(ModelicaMLRoot);
//									treeBuilder.setModelicamlProfile();
									
									// delete OMC markers
									String projectName = umlModel.getResource().getURI().segment(1);
									IWorkspace workspace = ResourcesPlugin.getWorkspace();
									IWorkspaceRoot root = workspace.getRoot();
									IProject iProject = root.getProject(projectName);

//									treeBuilder.deleteOMCLoadingMarkers(iProject);
									Utilities.deleteOMCLoadingMarkers(iProject);

									// build tree
									// TODO: libraries to be excluded should be selected by the user
									ArrayList<String> modelsToBeExcluded = new ArrayList<String>();
									modelsToBeExcluded.add("Modelica.UsersGuide");
									modelsToBeExcluded.add("Modelica.Fluid");

									treeBuilder.setMonitor(monitor);
									
									treeBuilder.buildTree(treeRoot, modelsToBeExcluded);
									return Status.OK_STATUS;
								}
							};
							job.addJobChangeListener(loadJobChangeAdapter);
							job.setUser(true);
							job.schedule();
						}
					}
					else {
						MessageDialog.openError(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), "Loading error", 
								"Could not access the Papyrus editing domain and the uml model.");
					}
			} catch (NotFoundException e) {
				MessageDialog.openError(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), 
						"ModelicaML Model Access Error", 
					"Cannot acceess the ModelicaML model. " +
					"Please make sure that the ModelicaML model is open in the active editor.");
				
				e.printStackTrace();
			}
		}
	}
	
	
	
	// Synch job listener
	final JobChangeAdapter synchJobChangeAdapter = new JobChangeAdapter() {
		public void done(IJobChangeEvent event) {
            if (event.getResult().isOK()) {
            	Display.getDefault().asyncExec(new Runnable() {
            		
        			public void run() {
        				
        				String message = "";
        				
        				// LOADED
        				int loadedClassesNumber = treeBuilder.getLoadedClasses().size();
        				int loadedComponentsNumber = treeBuilder.getLoadedComponents().size();
        				int loadedExtendsRelationsNumber = treeBuilder.getLoadedExtendsRelations().size();
        				
        				message = message + "Number of loaded classes: " + loadedClassesNumber + "\n";
        				message = message + "Number of loaded components: " + loadedComponentsNumber + "\n";
        				message = message + "Number of loaded extends relations: " + loadedExtendsRelationsNumber + "\n";
        				
        				message = message + "\n";
        				
        				// CREATED OR UPDATED 
        				int createdClassesNumber = ec.getCreatedClasses().size();
        				String createdClassesNames = "";
        				for (Element element : ec.getCreatedClasses()) {
							if (element instanceof NamedElement) {
								createdClassesNames = createdClassesNames + "        - " + ((NamedElement)element).getQualifiedName() + "\n";
							}
						}
        				
        				int updatedClassesNumber = ec.getUpdatedClasses().size();
        				String updatedClassesNames = "";
        				for (Element element : ec.getUpdatedClasses()) {
							if (element instanceof NamedElement) {
								updatedClassesNames = updatedClassesNames + "        - " + ((NamedElement)element).getQualifiedName() + "\n";
							}
						}
        				
        				int createdPropertiesNumber = ec.getCreatedProperties().size();
        				String createdPropertiesNames = "";
        				for (Element element : ec.getCreatedProperties()) {
							if (element instanceof NamedElement) {
								createdPropertiesNames = createdPropertiesNames + "        - " + ((NamedElement)element).getQualifiedName() + "\n";
							}
						}
        				
        				int updatedPropertiesNumber = ec.getUpdatedProperties().size();
        				String updatedPropertiesNames = "";
        				for (Element element : ec.getUpdatedProperties()) {
							if (element instanceof NamedElement) {
								updatedPropertiesNames = updatedPropertiesNames + "        - " + ((NamedElement)element).getQualifiedName() + "\n";
							}
						}
        				
        				int createdGeneralizationNumber = ec.getCreatedGeneralizations().size();
        				int updateGeneralizationNumber = ec.getUpdatedGeneralizations().size();

        				// classes
        				if (createdClassesNumber > 0) {
	        				message = message + "Number of created classes: " + createdClassesNumber + "\n";
        					message = message + createdClassesNames+ "\n";	
						}
        				if (updatedClassesNumber > 0) {
	        				message = message + "Number of updated classes: " + updatedClassesNumber + "\n";
        					message = message + updatedClassesNames+ "\n";	
						}

        				// properties
        				if (createdPropertiesNumber > 0) {
	        				message = message + "Number of created components: " + createdPropertiesNumber + "\n";
        					message = message + createdPropertiesNames+ "\n";	
						}
        				if (updatedPropertiesNumber > 0) {
	        				message = message + "Number of updated components: " + updatedPropertiesNumber+ "\n";
        					message = message + updatedPropertiesNames+ "\n";	
						}
        				
        				// extends relations
        				if (createdGeneralizationNumber > 0) {
	        				message = message + "Number of created extends relations: " + createdGeneralizationNumber + "\n";
						}
        				if (updateGeneralizationNumber > 0) {
	        				message = message + "Number of updated extends relations: " + updateGeneralizationNumber + "\n";
						}
        				
        				// add log
        				message = message + "\n\n********************   LOG ********************\n";
        				for (String logEntry : ec.getLog()) {
        					message = message + logEntry + "\n"; 
        				}
        				
        				DialogMessage dialog = new DialogMessage(new Shell(), "Modelica Model Proxies Synchronization Report", null, message, false);
        				dialog.open();
        				
        				
        				// start the proxies validation job.
        				actionValidateProxies();
        			}
        		});
            }
            else {
            	System.err.println("Could not complete the synchronization of Modelica Models Proxies.");
            	}
			}
         };
	
	
         
	
      public Job createSynchJob(final ServicesRegistry serviceRegistry, 
			final TransactionalEditingDomain editingDomain, 
			final UmlModel umlModel,
			final EObject ModelicaMLRoot,
			final boolean applyProxyStereotype,
			final boolean update,
			final boolean deleteNotUsedProxies
			){
				
		Job job = new Job("Creating ModelicaML Proxies for Modelica Models ..."){

			@Override
			protected IStatus run(IProgressMonitor monitor) {
				
				ec = new ModelicaMLElementsCreator(serviceRegistry, 
						editingDomain, 
						umlModel, 
						ModelicaMLRoot,
						treeBuilder,
						monitor);
				
				TreeObject[] modelicaRoots = treeRoot.getChildren();
				for (int i = 0; i < modelicaRoots.length; i++) {
					
					TreeObject treeObject = modelicaRoots[i];
					
					// only create if it is a package
					if (treeObject instanceof ClassItem && 
							( ((ClassItem)treeObject).getClassRestriction().equals("package") 
									|| ((ClassItem)treeObject).getClassRestriction().contains("operator")) ) {
						
						EObject modelicaRoot = ec.createProxyRoot(treeObject.getName(), true);
						treeObject.setModelicaMLProxy((Element) modelicaRoot);
						treeBuilder.addProxyToMaps((NamedElement) modelicaRoot);
						
						ec.createElements((Element)modelicaRoot, (TreeParent)treeObject, update, applyProxyStereotype, true);
						
						if (deleteNotUsedProxies) {
							ec.deleteInvalidProxyElements("");
						}
					}
				}
				
				return Status.OK_STATUS;
			}
		};
		
		return job;
	}
	
	  
      private void actionSynchronize(){
    	  
			/*
			 * TODO: if the element that is being updated is selected in GUI (i.e. in Papyrus modeling tool)
			 * then there will be a "SWT invalid thread exception because this job will modify it.
			 * WORAROUND: before creating elements set Papyrus Model Explorer Selection to 
			 * an element that will not be modified, e.g. the ModelicaML root. 
			 */
//			PapyrusServices.locateWithReselection(treeBuilder.getModelicaMLRoot());
			EditorServices.locateInModelExplorer(treeBuilder.getModelicaMLRoot(), true);
			
    	  // As a job
			TreeObject[] children = treeRoot.getChildren();
			for (int i = 0; i < children.length; i++) {
				TreeObject treeObject = children[i];
				HashSet<TreeObject> invalidFirstLevelClasses = new HashSet<TreeObject>();
				
				if (treeObject instanceof ClassItem && ((ClassItem)treeObject).getClassRestriction().equals("package")) {
					// ok, do nothing
				}
				else {
					invalidFirstLevelClasses.add(treeObject);
				}
					
				if (invalidFirstLevelClasses.size() > 0) {
					String title = "ModelicaML Proxies Synchronization Error";
					String message = "To use the ModelicaML Proxies Synchronization feature all first level Modelica classes" +
							"must be packages. Moreover, these packages must not have extends relations or imports" +
							" of other classes and not be encapsulated, partial, final or replaceable. " +
							"The following first level classes cannot be synchronized: \n\n";
					
					String invalideClassesString = "";
					for (TreeObject invalideClass : invalidFirstLevelClasses) {
						invalideClassesString = invalideClassesString  + "             -" + invalideClass.getQName() + "\n";
					}
					message = message + invalideClassesString;
					MessageDialog.openError(ModelicaMLServices.getShell(), title, message);
				}
			}
			
//			if (serviceRegistry != null && editingDomain != null && ModelicaMLRoot != null && umlModel != null) {
			if (editingDomain != null && ModelicaMLRoot != null && umlModel != null) {
				Job job = createSynchJob(serviceRegistry, 
						editingDomain, 
						umlModel, 
						ModelicaMLRoot, 
						applyProxyStereotype, 
						update, 
						deleteNotUsedProxies);

				job.addJobChangeListener(synchJobChangeAdapter);
//				job.setUser(true);
				job.schedule();
			}
			else {
				String title = "ModelicaML Proxies Synchronization Error";
				String message = "Could access the ModelicaML model or its editing domain.";
				MessageDialog.openError(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), title, message);
			}
		}
      
	  
      
      private Job createValidateProxiesJob( final UmlModel umlModel ){
			
    		Job job = new Job("Validating ModelicaML Modelica Models Proxies ..."){

    			@Override
    			protected IStatus run(IProgressMonitor monitor) {

    				// set up marker data
    				if (umlModel != null) {
    				
    					String projectName = umlModel.getResource().getURI().segment(1);
    					IWorkspace workspace = ResourcesPlugin.getWorkspace();
    					IWorkspaceRoot root = workspace.getRoot();
    					IProject iProject = root.getProject(projectName);

    					/*
    					 * Note: There should be an option to enable/disable this validation feature.
    					 * This may be needed for large models where the validation 
    					 * and the number of markers are too large ...
    					 */
    					treeBuilder.validateProxies(iProject);
    				}
    				else {
    					MessageDialog.openError(ModelicaMLServices.getShell(), 
    						"ModelicaML Model Access Error", 
    						"Cannot acceess the ModelicaML model. " +
    						"Please make sure that the ModelicaML model is open in editor.");
    				}

    				return Status.OK_STATUS;
    			}
    		};
    		
    		return job;
    	}
        
        
      private void actionValidateProxies(){
      	UmlModel umlModel = UmlUtils.getUmlModel();
      	Job job = createValidateProxiesJob(umlModel);
//  	job.setUser(true);
  		job.schedule();
      }
      
	
	

     
	@Override
	public boolean isEnabled() {
		/* check if the model is open in editor,
		 * if its root element is accessible 
		 * and if its editing domain is available
		 */
		
		/*
		 * When synchronizing proxies the Papyrus model explorer view must be visible 
		 * so that the viewer selection can be reset in order to avoid parallel access to 
		 * proxies that are displayed in Papyrus Properties View and are modified by 
		 * the synchronization job at the same time. 
		 */
		if (!EditorServices.isVisiblePapyrusModelExplorerView()) {
			return false;
		}
		
		UmlModel umlModel = UmlUtils.getUmlModel();
		if (umlModel != null) {
			try {
				EObject ModelicaMLRoot = umlModel.lookupRoot();
				if (ModelicaMLRoot != null) {
					
//					ServicesRegistry serviceRegistry = ServiceUtilsForActionHandlers.getInstance().getServiceRegistry();
//					TransactionalEditingDomain editingDomain = ServiceUtils.getInstance().getTransactionalEditingDomain(serviceRegistry);
					TransactionalEditingDomain editingDomain = EditorServices.getPapyrusEditingDomain();
					
//					if (serviceRegistry != null  && editingDomain != null && ModelicaMLRoot != null && umlModel != null) {
					if (editingDomain != null && ModelicaMLRoot != null && umlModel != null) {
						return true;
					}
				}
			} catch (NotFoundException e) {
				e.printStackTrace();
			}
		}
		return false;
	}

	
  	@Override
	public void addHandlerListener(IHandlerListener handlerListener) {
		// TODO Auto-generated method stub

	}

	@Override
	public void dispose() {
		// TODO Auto-generated method stub

	}
	
	@Override
	public boolean isHandled() {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public void removeHandlerListener(IHandlerListener handlerListener) {
		// TODO Auto-generated method stub

	}

}
