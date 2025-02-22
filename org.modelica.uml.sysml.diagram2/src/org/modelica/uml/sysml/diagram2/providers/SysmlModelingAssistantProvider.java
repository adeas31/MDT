package org.modelica.uml.sysml.diagram2.providers;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.gmf.runtime.diagram.ui.editparts.IGraphicalEditPart;
import org.eclipse.gmf.runtime.emf.type.core.ElementTypeRegistry;
import org.eclipse.gmf.runtime.emf.type.core.IElementType;
import org.eclipse.gmf.runtime.emf.ui.services.modelingassistant.ModelingAssistantProvider;
import org.eclipse.gmf.runtime.notation.Diagram;
import org.eclipse.gmf.runtime.notation.View;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.dialogs.ElementListSelectionDialog;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.edit.ui.provider.AdapterFactoryLabelProvider;
import org.modelica.uml.sysml.ModelicaClass;
import org.modelica.uml.sysml.ModelicaConnector;
import org.modelica.uml.sysml.ModelicaRecord;
import org.modelica.uml.sysml.diagram2.edit.parts.ModelEditPart;
import org.modelica.uml.sysml.diagram2.edit.parts.ModelicaClass2EditPart;
import org.modelica.uml.sysml.diagram2.edit.parts.ModelicaClassConnections2EditPart;
import org.modelica.uml.sysml.diagram2.edit.parts.ModelicaClassConnectionsEditPart;
import org.modelica.uml.sysml.diagram2.edit.parts.ModelicaClassEditPart;

import org.modelica.uml.sysml.diagram2.edit.parts.ModelicaClassNested2EditPart;
import org.modelica.uml.sysml.diagram2.edit.parts.ModelicaClassNestedEditPart;

import org.modelica.uml.sysml.diagram2.edit.parts.ModelicaTypeEditPart;

import org.modelica.uml.sysml.diagram2.part.SysmlDiagramEditorPlugin;

/**
 * @generated
 */
public class SysmlModelingAssistantProvider extends ModelingAssistantProvider {

	/**
	 * @generated NOT
	 */
	public List getTypesForPopupBar(IAdaptable host) {
		IGraphicalEditPart editPart = (IGraphicalEditPart) host
				.getAdapter(IGraphicalEditPart.class);
		if (editPart instanceof ModelicaClassEditPart) {
			List types = new ArrayList();
			types.add(SysmlElementTypes.ModelicaProperty_2001);
			types.add(SysmlElementTypes.ModelicaProperty_2003);
			types.add(SysmlElementTypes.ModelicaEquationProperty_2006);
			return types;
		}
		if (editPart instanceof ModelicaClass2EditPart) {
			List types = new ArrayList();
			types.add(SysmlElementTypes.ModelicaProperty_2001);
			types.add(SysmlElementTypes.ModelicaProperty_2002);
			types.add(SysmlElementTypes.ModelicaProperty_2003);

			ModelicaClass modelicaClass = (ModelicaClass) ((View) editPart
					.getModel()).getElement();
			if (!(modelicaClass instanceof ModelicaConnector)
					&& !(modelicaClass instanceof ModelicaRecord)) {

				types.add(SysmlElementTypes.ModelicaEquationProperty_2006);
			}
			return types;
		}
		if (editPart instanceof ModelicaClassNestedEditPart) {
			List types = new ArrayList();
			types.add(SysmlElementTypes.ModelicaClass_2005);
			// contributed code start
			types.add(SysmlElementTypes.ModelicaModel_2005);
			types.add(SysmlElementTypes.ModelicaBlock_2005);
			types.add(SysmlElementTypes.ModelicaRecord_2005);
			types.add(SysmlElementTypes.ModelicaConnector_2005);
			types.add(SysmlElementTypes.ModelicaFunction_2005);
			// contributed code end
			return types;
		}
		if (editPart instanceof ModelicaClassNested2EditPart) {
			List types = new ArrayList();
			types.add(SysmlElementTypes.ModelicaClass_2005);
			// contributed code start
			types.add(SysmlElementTypes.ModelicaModel_2005);
			types.add(SysmlElementTypes.ModelicaBlock_2005);
			types.add(SysmlElementTypes.ModelicaRecord_2005);
			types.add(SysmlElementTypes.ModelicaConnector_2005);
			types.add(SysmlElementTypes.ModelicaFunction_2005);
			// contributed code end
			return types;
		}
		if (editPart instanceof ModelEditPart) {
			List types = new ArrayList();
			types.add(SysmlElementTypes.ModelicaClass_1001);
			types.add(SysmlElementTypes.ModelicaType_1002);

			// contributed code start
			types.add(SysmlElementTypes.ModelicaModel_1001);
			types.add(SysmlElementTypes.ModelicaBlock_1001);
			types.add(SysmlElementTypes.ModelicaRecord_1001);
			types.add(SysmlElementTypes.ModelicaConnector_1001);
			types.add(SysmlElementTypes.ModelicaFunction_1001);
			// contrubuted code end

			return types;
		}
		/*if (editPart instanceof ModelicaTypeEditPart) {
		 List types = new ArrayList();
		 types.add(SysmlElementTypes.PrimitiveType_2007);
		 return types;
		 }

		 //contributed code start
		 if (editPart instanceof PrimitiveTypeEditPart) {
		 List types = new ArrayList();
		 types.add(SysmlElementTypes.PrimitiveType_2007);
		 return types;
		 }

		 if (editPart instanceof ModelicaTypeTypeEditPart) {
		 List types = new ArrayList();
		 types.add(SysmlElementTypes.PrimitiveType_2007);
		 return types;
		 }*/
		//contrubuted code end
		return Collections.EMPTY_LIST;
	}

	/**
	 * @generated
	 */
	public List getRelTypesOnSource(IAdaptable source) {
		return Collections.EMPTY_LIST;
	}

	/**
	 * @generated
	 */
	public List getRelTypesOnTarget(IAdaptable target) {
		return Collections.EMPTY_LIST;
	}

	/**
	 * @generated
	 */
	public List getRelTypesOnSourceAndTarget(IAdaptable source,
			IAdaptable target) {
		return Collections.EMPTY_LIST;
	}

	/**
	 * @generated
	 */
	public List getTypesForSource(IAdaptable target,
			IElementType relationshipType) {
		return Collections.EMPTY_LIST;
	}

	/**
	 * @generated
	 */
	public List getTypesForTarget(IAdaptable source,
			IElementType relationshipType) {
		return Collections.EMPTY_LIST;
	}

	/**
	 * @generated
	 */
	public EObject selectExistingElementForSource(IAdaptable target,
			IElementType relationshipType) {
		return selectExistingElement(target, getTypesForSource(target,
				relationshipType));
	}

	/**
	 * @generated
	 */
	public EObject selectExistingElementForTarget(IAdaptable source,
			IElementType relationshipType) {
		return selectExistingElement(source, getTypesForTarget(source,
				relationshipType));
	}

	/**
	 * @generated
	 */
	protected EObject selectExistingElement(IAdaptable host, Collection types) {
		if (types.isEmpty()) {
			return null;
		}
		IGraphicalEditPart editPart = (IGraphicalEditPart) host
				.getAdapter(IGraphicalEditPart.class);
		if (editPart == null) {
			return null;
		}
		Diagram diagram = (Diagram) editPart.getRoot().getContents().getModel();
		Collection elements = new HashSet();
		for (Iterator it = diagram.getElement().eAllContents(); it.hasNext();) {
			EObject element = (EObject) it.next();
			if (isApplicableElement(element, types)) {
				elements.add(element);
			}
		}
		if (elements.isEmpty()) {
			return null;
		}
		return selectElement((EObject[]) elements.toArray(new EObject[elements
				.size()]));
	}

	/**
	 * @generated
	 */
	protected boolean isApplicableElement(EObject element, Collection types) {
		IElementType type = ElementTypeRegistry.getInstance().getElementType(
				element);
		return types.contains(type);
	}

	/**
	 * @generated
	 */
	protected EObject selectElement(EObject[] elements) {
		Shell shell = Display.getCurrent().getActiveShell();
		ILabelProvider labelProvider = new AdapterFactoryLabelProvider(
				SysmlDiagramEditorPlugin.getInstance()
						.getItemProvidersAdapterFactory());
		ElementListSelectionDialog dialog = new ElementListSelectionDialog(
				shell, labelProvider);
		dialog.setMessage("Available domain model elements:");
		dialog.setTitle("Select domain model element");
		dialog.setMultipleSelection(false);
		dialog.setElements(elements);
		EObject selected = null;
		if (dialog.open() == Window.OK) {
			selected = (EObject) dialog.getFirstResult();
		}
		return selected;
	}
}
