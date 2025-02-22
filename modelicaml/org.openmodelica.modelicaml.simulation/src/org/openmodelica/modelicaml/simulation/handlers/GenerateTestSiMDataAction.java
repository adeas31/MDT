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
package org.openmodelica.modelicaml.simulation.handlers;

import java.io.IOException;
import java.net.URI;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.filesystem.EFS;
import org.eclipse.core.filesystem.IFileStore;
import org.eclipse.core.filesystem.IFileSystem;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.IJobChangeEvent;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.core.runtime.jobs.JobChangeAdapter;
import org.eclipse.emf.common.command.Command;
import org.eclipse.emf.common.command.CompoundCommand;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.transaction.TransactionalEditingDomain;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.papyrus.editor.PapyrusMultiDiagramEditor;
import org.eclipse.papyrus.infra.core.resource.NotFoundException;
import org.eclipse.papyrus.infra.core.resource.uml.UmlModel;
import org.eclipse.papyrus.infra.core.resource.uml.UmlUtils;
import org.eclipse.papyrus.infra.core.services.ServiceException;
import org.eclipse.papyrus.infra.core.services.ServicesRegistry;
import org.eclipse.papyrus.infra.core.utils.ServiceUtils;
import org.eclipse.papyrus.infra.core.utils.ServiceUtilsForActionHandlers;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.PlatformUI;
import org.eclipse.uml2.uml.Element;
import org.eclipse.uml2.uml.Model;
import org.eclipse.uml2.uml.NamedElement;
import org.openmodelica.modelicaml.common.constants.Constants;
import org.openmodelica.modelicaml.common.datacollection.ElementsCollector;
import org.openmodelica.modelicaml.common.helpers.VerificationServices;
import org.openmodelica.modelicaml.common.services.ModelicaMLServices;
import org.openmodelica.modelicaml.simulation.testexecution.actions.ExecuteTestsAction;
import org.openmodelica.modelicaml.simulation.testexecution.dialogs.SelectSimulationModelsToExecuteDialog;

import fr.obeo.acceleo.chain.File;
import fr.obeo.acceleo.chain.impl.spec.CChain;
import fr.obeo.acceleo.chain.impl.spec.CFolder;
import fr.obeo.acceleo.chain.impl.spec.CLog;
import fr.obeo.acceleo.chain.impl.spec.CModel;
import fr.obeo.acceleo.gen.IGenFilter;
import fr.obeo.acceleo.gen.template.eval.LaunchManager;


public class GenerateTestSiMDataAction extends AbstractHandler {

	/** The project. */
	private String projectName = null;
	
	/** The my chain. */
	private CChain myChain = null;
	
	/** The model file uri. */
	private String modelFileURI = null;
	
	/** The model name. */
	private String modelName = null;
	
	private UmlModel umlModel;
	
	private boolean generateCode = false;

	/** The filter. */
	IGenFilter filter = new IGenFilter() {
		public boolean filter(java.io.File script, IFile targetFile,
				EObject object) throws CoreException {
			return true;
		}
	};

	private String projectAbsolutePath;

	private String testSessionFolderAbsolutePath;

	public Object execute(ExecutionEvent event) throws ExecutionException {
//		TransactionalEditingDomain editingDomain = EditorUtils.getTransactionalEditingDomain();
		ServicesRegistry serviceRegistry;
		try {
			serviceRegistry = ServiceUtilsForActionHandlers.getInstance().getServiceRegistry();
			TransactionalEditingDomain editingDomain = ServiceUtils.getInstance().getTransactionalEditingDomain(serviceRegistry);
			editingDomain.getCommandStack().execute(getCommand(editingDomain));

		} catch (ServiceException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return null;
	}

	protected Command getCommand(TransactionalEditingDomain editingDomain) {
		
		umlModel = UmlUtils.getUmlModel();
		modelFileURI = umlModel.getResourceURI().toPlatformString(true);

		modelName = umlModel.getResourceURI().lastSegment();
		projectName = umlModel.getResource().getURI().segment(1);
		
		// Set a time stamp that is used for names
		VerificationServices.setDate();
		
		// Set the folder paths 
		IWorkspace workspace = ResourcesPlugin.getWorkspace();
		IWorkspaceRoot root = workspace.getRoot();
		IProject iProject = root.getProject(projectName);
		projectAbsolutePath = iProject.getLocationURI().toString().replaceFirst("file:\\/", "");
		testSessionFolderAbsolutePath = projectAbsolutePath+"/"+Constants.folderName_verification_gen+"/"+Constants.folderName_verification_session+"_"+VerificationServices.getTimeStamp("");
		
		VerificationServices.setTestSessionFolderAbsolutePath(testSessionFolderAbsolutePath);
		VerificationServices.setProjectFolderAbsolutePath(projectAbsolutePath);
		VerificationServices.setProjectName(projectName);
		
		// set the list of simulation models to be executed.
		EObject rootModel = null;
		boolean someModelsSelected = false;
		try {
			rootModel = umlModel.lookupRoot();
			if (rootModel instanceof Model) {
				someModelsSelected = setTestModels((Element) rootModel);
			}
		} catch (NotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		if (someModelsSelected && rootModel != null && VerificationServices.verificationModels != null && VerificationServices.verificationModels.size() > 0) {
			
			// prepare the chain
			org.eclipse.emf.common.util.URI chainURI = null;
			
			// reset the code generation flag from last time
			generateCode = false;
			
			// if the code should not be re-generated -> skip asking this question
			if ( ModelicaMLServices.regenerateCode(umlModel.getResource()) ) {
//				generateCode = MessageDialog.openQuestion(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), 
//						"Save Model and Generate Modelica Code?", 
//						"Save the ModelicaML model and generate Modelica code before starting the verification models execution?");
				generateCode = true;
			}
			
			// select the right cg chain based on the need to regenerate code
			if (generateCode) {
				chainURI = org.eclipse.emf.common.util.URI.createPlatformPluginURI(
						"/org.openmodelica.modelicaml.gen.modelica/bin/verification_models_data_generation_with_cg.chain",
						true);
			}
			else {
				chainURI = org.eclipse.emf.common.util.URI.createPlatformPluginURI(
						"/org.openmodelica.modelicaml.gen.modelica/bin/verification_models_data_generation.chain",
						true);
			}

			
			// create chain
			if (chainURI != null) {
				ResourceSet rs = new ResourceSetImpl();
				Resource r = (Resource) rs.createResource(chainURI);
				
				try {
					r.load(null);
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			
				myChain = (CChain) r.getContents().get(0);
			
				String modelFilePath = modelFileURI.replace("platform:/resource/", "");
				String outputFolderPath = projectName;
				String logPath = projectName + "/errors.log";
			
				// Don't create Parameter Files... simply set the correct path into them
				EList<File> files = myChain.getParametersFiles();
				for (File file : files) {
					if (file instanceof CModel) {
						file.setPath(modelFilePath);
					} else if (file instanceof CFolder) {
						file.setPath(outputFolderPath);
					} else if (file instanceof CLog) {
						file.setPath(logPath);
					}
				}
			
				// check if the model is dirty and, if it is, save it in order to make sure that code is generated from an up to date version
				IEditorPart editorPart = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().getActiveEditor();
				final PapyrusMultiDiagramEditor papyrusEditor = ((PapyrusMultiDiagramEditor)editorPart);
				
				Job job = new Job("Verification Models Data Generation") {
					protected IStatus run(IProgressMonitor monitor) {
						
						if (papyrusEditor.isDirty()) {
							papyrusEditor.doSave(monitor);
						}
						
						runchain(monitor);
						
						return Status.OK_STATUS;
					}
				};
				
				job.addJobChangeListener(jobChangeAdapter);
				job.setUser(true);
				job.schedule();	// run the chain and models execution
			}
			else {
				MessageDialog.openError(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), "Error", "Could not load the generation chain...");
			}
			CompoundCommand cc = new CompoundCommand("Verification Models Data Generation");
			return (cc.unwrap());
		}
		return null;
	}

	
	public boolean setTestModels(Element rootModel){
		ElementsCollector ec = new ElementsCollector();
		ec.setStereotypeQName(Constants.stereotypeQName_VerificationModel);
//		ec.collectElementsFromModel(rootModel, Constants.stereotypeQName_VerificationModel);
		ec.collectElementsFromModel(rootModel);
		
		SelectSimulationModelsToExecuteDialog dialog = new SelectSimulationModelsToExecuteDialog(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), ec.getElements());
		dialog.open();
		
		if (dialog.getReturnCode() == IDialogConstants.OK_ID) {
			
			VerificationServices.verificationModels.clear();
			
			for (Element element : dialog.getUserSelectedTestSimulationModels()) {
				if (element instanceof NamedElement) {
					VerificationServices.verificationModels.add((NamedElement) element);
				}
			}
			
			return true;
		}
		return false;
	}
	
	JobChangeAdapter jobChangeAdapter = new JobChangeAdapter() {
		public void done(IJobChangeEvent event) {
            if (event.getResult().isOK())
            	openDialog();
//            else
//            	openDialog();
            }
         };

	private String testSessionXMLFileToReadAbsolutePath;

	private String JSDataFileToReadAbsolutePath;

	private String JSDataFileToUpdateAbsolutePath;

	private String reportFileAbsolutePath;
	
     
//     private String reportFileAbsolutePath = TestExecutionServices.testFolderAbsolutePath + "/report-gen/report.html?enableTool=true";
         
     private void openDialog(){
 		// Use this to open a Shell in the UI thread
 		Display.getDefault().asyncExec(new Runnable() {
 			public void run() {

// 				DialogMessageWithHTMLBrowser dialog = new DialogMessageWithHTMLBrowser(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), 
// 						"Test Execution Report " + TestExecutionServices.getTime(null), reportFileAbsolutePath);
// 				dialog.open();
 				
 				ExecuteTestsAction executeAction = new ExecuteTestsAction();
 				executeAction.setXMLFilePath(testSessionXMLFileToReadAbsolutePath);
 				executeAction.run(null);
 			}
 		});
 	}
         
	public void runchain(IProgressMonitor monitor) {
		try {
			
			// stamp before generating code 
			Long timeStamp = System.currentTimeMillis();
			ModelicaMLServices.codeGenerationStamp.put(umlModel.getResource(), timeStamp);
			
			// same the model in order to make sure that the code is generated from the latest version
//			ModelicaMLServices.saveModel(umlModel);

			myChain.launch(filter, monitor, LaunchManager.create("run", true));
			
			if (generateCode) {
				// TODO: remove this when file encoding for generated code files is enforced to UTF-8
				ModelicaMLServices.generatePackageEncodingFile(projectName);
			}
			
			// TODO: Here the call for the tests execution
			testSessionXMLFileToReadAbsolutePath = testSessionFolderAbsolutePath + "/verification_session.xml";
			// TODO: read the file and execute models one after another
			// This functionality shall also be available as an Eclipse action 
			// so that the user can re-start the execution at any time by selecting the test_session.xml.
			
			// TODO: Here the call for report update
			JSDataFileToReadAbsolutePath = testSessionFolderAbsolutePath + "/report-gen/includes/report_data.js_template";
			// TODO: read the file and replace all place-holders '###...###'
			
			JSDataFileToUpdateAbsolutePath= testSessionFolderAbsolutePath + "/report-gen/includes/report_data.js";
			// TODO: save (override) the "report_data.js"
			// This functionality shall also be available as an Eclipse action 
			// so that the user can update the report_data.js at any time by selecting the report.html.
			
			// TODO: When finished -> notify user and suggest to open the report.
			reportFileAbsolutePath = testSessionFolderAbsolutePath + "/report-gen/report.html?enableTool=true";
			// TODO: open a new dialog, set the file location (reportFileAbsolutePath)
			
			// TODO: The dialog shall have a browser and a listener 
			// in order to enable the locating of the test model in Papyrus Model Explorer 
			// and then the respective component (variable) in ModelicaML Components Tree.
			// This functionality shall also be available as an Eclipse action 
			// so that the user can open this dialog by selecting the report.html.
			
			IFileSystem fileSystem = EFS.getLocalFileSystem();
			IFileStore codeGenDir = fileSystem.getStore(URI.create(VerificationServices.projectAbsolutePath + "/" + Constants.folderName_code_gen));
			IFileStore testSessionCodeGenDir = fileSystem.getStore(URI.create(VerificationServices.verificationSessionFolderAbsolutePath + "/" + Constants.folderName_code_gen));
			
//			if (codeGenDir != null ) {
//				System.err.println("code-gen directory at: " + codeGenDir);
//			}
			
			// Will recursively copy the home directory to the backup directory, 
			// but if any files are in place on the destination matching files to 
			// be copied an exception will be thrown with that given file name.
			codeGenDir.copy(testSessionCodeGenDir, EFS.NONE, null);

			/*
			 * copy also the code-sync folder if it exists
			 */
			IFileStore codeSyncDir = fileSystem.getStore(URI.create(VerificationServices.projectAbsolutePath + "/" + Constants.folderName_code_sync));
			IFileStore testSessionCodeSyncDir = fileSystem.getStore(URI.create(VerificationServices.verificationSessionFolderAbsolutePath + "/" + Constants.folderName_code_sync));
			if (codeSyncDir.fetchInfo().exists()) {
				codeSyncDir.copy(testSessionCodeSyncDir, EFS.NONE, null);
			}
			
			// refresh the project browser
			ResourcesPlugin.getWorkspace().getRoot().refreshLocal(IResource.DEPTH_INFINITE, null);
			
		} catch (CoreException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
//	private List<Object> getCurrentSelections() {
//		ISelection selection = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getSelectionService().getSelection();
//		if(selection instanceof IStructuredSelection) {
//			IStructuredSelection structuredSelection = (IStructuredSelection)selection;
//			return structuredSelection.toList();
//		}
//		return null;
//	}
	
	
	/**
	 * Adapt selected element.
	 * 
	 * @param selection
	 *            the selection
	 * @return the e object
	 */
//	protected EObject adaptSelectedElement( Object selection) {
//		EObject eObject = null;
//		if(selection != null) {
//			if(selection instanceof IAdaptable) {
//				selection = ((IAdaptable)selection).getAdapter(EObject.class);
//			}
//			Object businessObject = BusinessModelResolver.getInstance().getBusinessModel(selection);
//			if(businessObject instanceof EObject) {
//				eObject = (EObject)businessObject;
//			}
//		}
//		return eObject;
//	}
	
	
	
	/**
	 * Checks if is enabled.
	 * 
	 * @return true, if is enabled
	 * @see org.eclipse.core.commands.AbstractHandler#isEnabled()
	 */
	public boolean isEnabled() {
		return true;
	}

	/**
	 * Checks if is visible.
	 * 
	 * @return true, if is visible
	 */
	public boolean isVisible() {
		return true;
	}

}
