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

package org.openmodelica.modelicaml.tabbedproperties.editors.sections;

import org.eclipse.emf.common.command.Command;
import org.eclipse.emf.common.command.CompoundCommand;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.transaction.RecordingCommand;
import org.eclipse.emf.transaction.TransactionalEditingDomain;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.papyrus.uml.tools.utils.OpaqueExpressionUtil;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.views.properties.tabbed.AbstractPropertySection;
import org.eclipse.ui.views.properties.tabbed.TabbedPropertySheetPage;
import org.eclipse.uml2.uml.Class;
import org.eclipse.uml2.uml.Constraint;
import org.eclipse.uml2.uml.Element;
import org.eclipse.uml2.uml.NamedElement;
import org.eclipse.uml2.uml.OpaqueExpression;
import org.eclipse.uml2.uml.Transition;
import org.eclipse.uml2.uml.UMLPackage;
import org.openmodelica.modelicaml.common.constants.Constants;
import org.openmodelica.modelicaml.common.contentassist.ModelicaMLContentAssist;
import org.openmodelica.modelicaml.common.services.ModelicaMLServices;
import org.openmodelica.modelicaml.common.validation.services.ModelicaMLMarkerSupport;
import org.openmodelica.modelicaml.editor.xtext.state.ui.internal.StatetransitionguardexpressionActivator;
import org.openmodelica.modelicaml.tabbedproperties.editors.glue.edit.part.PropertiesSectionXtextEditorHelper;

import com.google.inject.Injector;

// TODO: Auto-generated Javadoc
/**
 * The Class StateTransistionGuardPropertySectionCodeSection.
 */
public class StateTransistionGuardCodeSection extends AbstractPropertySection  {

	/** The LANGUAGE. */
	protected static String LANGUAGE = "Modelica";
	
	/** The selected uml element. */
	private Element selectedUmlElement;

	/** The parent. */
	private Composite parent;
	
	/** The editor composite. */
	private Composite editorComposite;
	
	/** The editing domain. */
	private TransactionalEditingDomain editingDomain;
	
	/** The editor. */
	private PropertiesSectionXtextEditorHelper editor;
	
	/** The injector. */
	private Injector injector;
	
	/** The file extension. */
	private String fileExtension;
	
	/** The text to edit. */
	private String textToEdit = "";
	
	/** The context element. */
	private Element contextElement;
	
	/** The owning class. */
	private Element owningClass;
	
	/** The is new selection. */
	private boolean isNewSelection;
	
	
	/* (non-Javadoc)
	 * @see org.eclipse.ui.views.properties.tabbed.AbstractPropertySection#createControls(org.eclipse.swt.widgets.Composite, org.eclipse.ui.views.properties.tabbed.TabbedPropertySheetPage)
	 */
	@Override
	public void createControls(Composite parent, TabbedPropertySheetPage tabbedPropertySheetPage) {
		this.parent = parent;
		this.parent.setLayout(new GridLayout());
		this.parent.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		
		this.editorComposite = new Composite(this.parent, SWT.BORDER);
		this.editorComposite.setLayout(new GridLayout());
		this.editorComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		
		// ################################ Adjust start
		injector = StatetransitionguardexpressionActivator.getInstance().getInjector("org.openmodelica.modelicaml.editor.xtext.state.Statetransitionguardexpression");
		fileExtension = ".modelicamlstatetransitionguardexpression";
		
		editor = new PropertiesSectionXtextEditorHelper(selectedUmlElement, injector, null, textToEdit, fileExtension);
		// ################################ Adjust end
		
		editor.showEditor(editorComposite, SWT.BORDER);

//		FocusListener was replaced by ModifyListener (see below)
//		editor.getEditorWidget().addFocusListener(new FocusListener() {
//			public void focusLost(FocusEvent e) {
//				storeText(contextElement, editor.getText());
//			}			
//			public void focusGained(FocusEvent e) {
//			}
//		}) ;

		editor.getEditorWidget().addModifyListener(new ModifyListener() {
			// TODO: observe if it has impact on performance ...
			@Override
			public void modifyText(ModifyEvent e) {
				storeText(contextElement, editor.getText());
			}
		});
		
		// Get Papyrus editing domain
//		editingDomain = EditorUtils.getTransactionalEditingDomain();
	}
	
	/**
	 * Checks if is valid element.
	 * 
	 * @return the boolean
	 */
	private Boolean isValidElement(){
		// ################################ Adjust start
		if ( this.selectedUmlElement instanceof Transition ) {
			return true;
		}
		// ################################ Adjust end
		return false;
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.ui.views.properties.tabbed.AbstractPropertySection#refresh()
	 */
	@Override
	public void refresh() {
//		if (isNewSelection && isValidElement() ) { // Only react if a different (new) element was selected
		if ( isValidElement() ) { // Only react if a different (new) element was selected
			// ################################ Adjust start
			if (selectedUmlElement instanceof Transition ) {
				contextElement = ((Transition)selectedUmlElement);
				textToEdit = "";
				//String exisingGuardString = OpaqueExpressionUtil.getBodyForLanguage((OpaqueExpression)((Transition)selectedUmlElement).getGuard().getSpecification(), LANGUAGE);
				
				if ( ((Transition)selectedUmlElement).getGuard() != null && ((Transition)selectedUmlElement).getGuard().getSpecification() instanceof OpaqueExpression ) {
					textToEdit = (String) "" + OpaqueExpressionUtil.getBodyForLanguage((OpaqueExpression)((Transition)selectedUmlElement).getGuard().getSpecification(), LANGUAGE);
				}
				
//				if (exisingGuardString != null) {
//					textToEdit = textToEdit + exisingGuardString;
//				}
				
				
				// build the content assistance proposals list.
				ModelicaMLContentAssist.setSelectedSourceElement(selectedUmlElement);
				//owningClass = ((Transition)selectedUmlElement).getOwner().getOwner().getOwner();
				owningClass = Utils.getContextClass(selectedUmlElement);
				if (owningClass instanceof Class) {
					ModelicaMLContentAssist.createComponentReferencelist((Class)owningClass);
				}
			}
			// ################################ Adjust end
			
			editor.setTextToEdit(textToEdit);
			editor.setContextElement(contextElement);
			
			// marker support
			if (selectedUmlElement instanceof NamedElement) {
				generateUMLModelMarker(); 	// Generate UML model marker
			}
		}
	}
	
	
	/**
	 * Store text.
	 * 
	 * @param element
	 *            the element
	 * @param bodyText
	 *            the body text
	 */
	private void storeText(final EObject element, final String bodyText) {
		CompoundCommand cc = new CompoundCommand();
		
		// Record command
		// ################################## Adjust start
		if (element instanceof Transition) {
			
			Command command = new RecordingCommand(editingDomain) {
				@Override
				protected void doExecute() {
					if ( !bodyText.trim().equals("") ) {
						
						// delete the language expression 
						if ( ((Transition)element).getGuard() != null && ((Transition)element).getGuard().getSpecification() instanceof OpaqueExpression) {
							OpaqueExpression opaqueExpression = (OpaqueExpression) ((Transition)element).getGuard().getSpecification();
							int index = opaqueExpression.getLanguages().indexOf(LANGUAGE);
							if (index > -1 ) {
								opaqueExpression.getLanguages().remove(index);
								opaqueExpression.getBodies().remove(index);
								
								((Transition)element).getGuard().destroy(); // reset the guard
								//((Transition)element).setGuard(null); // reset the guard 
							}
						}	
						
						// create a new guard
						Constraint c = ((Transition)element).createGuard("ModelicaGuardCode");
						OpaqueExpressionUtil.setBodyForLanguage((OpaqueExpression)c.createSpecification("ModelicaGuardCode", null, UMLPackage.Literals.OPAQUE_EXPRESSION), LANGUAGE, bodyText);
					}
					else {
						// delete the language expression 
						if ( ((Transition)element).getGuard() != null && ((Transition)element).getGuard().getSpecification() instanceof OpaqueExpression) {
							OpaqueExpression opaqueExpression = (OpaqueExpression) ((Transition)element).getGuard().getSpecification();
							int index = opaqueExpression.getLanguages().indexOf(LANGUAGE);
							if (index > -1 ) {
								opaqueExpression.getLanguages().remove(index);
								opaqueExpression.getBodies().remove(index);

								((Transition)element).getGuard().destroy(); // reset the guard
//								((Transition)element).setGuard(null); // reset the guard 
							}
						}	
					}
				}
			};
			cc.append(command);
		}
		// ################################## Adjust end
		
		// Execute command
		editingDomain.getCommandStack().execute(cc);
		
		isNewSelection = false; 	// Resets the indicator for a new selection, 
									// i.e., if the new text is put into the EObject 
									// then there was no new selection until the setInput() method determines a new selection.
		
		if (element instanceof NamedElement) {
			generateUMLModelMarker(); 	// Generate UML model marker
		}
	}
	
	

	
	
	// Marker support #########################################################
	/**
	 * Generate uml model marker.
	 */
	private void generateUMLModelMarker(){
		if (selectedUmlElement != null && selectedUmlElement instanceof NamedElement) {
			// create a marker for the uml model element
			String message = "The " + ((NamedElement)selectedUmlElement).eClass().getName() 
								+ " '" + ((NamedElement)selectedUmlElement).getName() + "' has errors in its guard Modelica code.";
			
			if (editor.isDocumentHasErrors()) {
				ModelicaMLMarkerSupport.generateMarker(message, "error", (NamedElement)selectedUmlElement, Constants.MARKERTYPE_ACTION_CODE);
			}
			else {
				ModelicaMLMarkerSupport.deleteMarker( message, (NamedElement)selectedUmlElement, Constants.MARKERTYPE_ACTION_CODE);
			}
		}
	}
	
	// Utl #########################################################
//	public static int getBodyIndex(OpaqueAction behavior, String language) {
//		int index = 0;
//		boolean isFound = false;
//
//		// test if the language exists
//		Iterator<String> it = behavior.getLanguages().iterator();
//		while (it.hasNext() && !isFound) {
//			String lang = it.next();
//			if (lang.equalsIgnoreCase(language)) {
//				isFound = true;
//			} else {
//				index++;
//			}
//		}
//		// returns -1 if not found
//		if (!isFound) {
//			index = -1;
//		}
//		return index;
//	}
	
//	private String retrieveBody(OpaqueExpression exp, String languageName) {
//		String body = "";
//		if(exp == null)
//			return body;
//		int index = 0;
//		for(String _languageName : exp.getLanguages()) {
//			if(_languageName.equals(languageName)) {
//				if(index < exp.getBodies().size())
//					return exp.getBodies().get(index);
//				else
//					return "";
//			}
//			index++;
//		}
//		return body;
//	}
//	
	
	
	
	// Same for all sections #########################################################
	/* (non-Javadoc)
	 * @see org.eclipse.ui.views.properties.tabbed.AbstractPropertySection#setInput(org.eclipse.ui.IWorkbenchPart, org.eclipse.jface.viewers.ISelection)
	 */
	@Override
	public void setInput(IWorkbenchPart part, ISelection selection) {

		// get the selectedUmlElement
		Object input = ((IStructuredSelection) selection).getFirstElement();
		
		// treat the object selected on a diagram and in the model explorer separately.
		// Get the selected element
        EObject selectedElement = ModelicaMLServices.adaptSelectedElement(input);
        if (selectedElement instanceof Element) {
        	this.selectedUmlElement = (Element)selectedElement;
        	isNewSelection = true;
		}
		
		// Get Papyrus editing domain.
		editingDomain = (TransactionalEditingDomain) Utils.getEditingDomain(part);
	}

//	@Override
//	was used in combination with focusLost listener
//	public void aboutToBeHidden() { 
//		// This is necessary in order to enforce saving when user changes Eclipse views 
//		// (e.g. from Properties view to Console View, etc.). 
//		storeText(contextElement, editor.getText());
//	}
	
}

	