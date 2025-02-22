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
package org.openmodelica.modelicaml.model.explorer.customization;
import org.eclipse.uml2.uml.Dependency;
import org.eclipse.uml2.uml.Class;
import org.eclipse.uml2.uml.Element;
import org.eclipse.uml2.uml.Enumeration;
import org.eclipse.uml2.uml.NamedElement;
import org.eclipse.uml2.uml.Property;
import org.eclipse.emf.facet.infra.query.core.exception.ModelQueryExecutionException;
import org.eclipse.emf.facet.infra.query.core.java.IJavaModelQuery;
import org.eclipse.emf.facet.infra.query.core.java.ParameterValueList;
import org.openmodelica.modelicaml.common.constants.Constants;

/** Returns the label for a dependency */
public class GetDependecyLabel implements IJavaModelQuery<Dependency, String> {
	public String evaluate(final Dependency context, final ParameterValueList parameterValues)
			throws ModelQueryExecutionException {
		
		// indicate an inconsistency
		if (context.getClients() == null || context.getSuppliers() == null) {
			return "ERROR! " + context.getName();
		}
		
		if (context.getClients() != null && context.getClients().size() > 0 ) {
			Element client = context.getClients().get(0);
//			System.err.println("client: " + client.eClass());
//			String clientName = "??";
			
			if (context.getSuppliers() != null && context.getSuppliers().size() > 0) {
				Element supplier = context.getSuppliers().get(0);
//				System.err.println("supplier: " + supplier.eClass());
//				String supplierName = "??";
				
				if (client instanceof Class) {
//					clientName = ((Class)client).getName();
					// Import dependency
					if ( (supplier instanceof Class || supplier instanceof Enumeration)
							&& context.getAppliedStereotype(Constants.stereotypeQName_ImportRelation) != null) {
						
						return "'" + ((NamedElement)client).getName() + "'" + " imports " + "'" + ((NamedElement)supplier).getName() + "'";
					}
				}
				// Value Bindings
				else if (supplier instanceof Property && client instanceof Property
							&& client.getAppliedStereotype(Constants.stereotypeQName_ValueMediator) != null ) {
					if (context.getAppliedStereotype(Constants.stereotypeQName_ObtainsValueFrom) != null ){
//						return "'" + ((NamedElement)client).getName() + "'" + " obtains value from " + "'" + ((NamedElement)supplier).getName() + "'";
						return "'" + ((NamedElement)client).getName() + "'" + " inferes binding based on provider " + "'" + ((NamedElement)supplier).getName() + "'";
						
					}
					else if (context.getAppliedStereotype(Constants.stereotypeQName_ProvidesValueFor) != null) {
//						return "'" + ((NamedElement)client).getName() + "'" + " provides value for " + "'" + ((NamedElement)supplier).getName() + "'";
						return "'" + ((NamedElement)client).getName() + "'" + " inferes binding for client " + "'" + ((NamedElement)supplier).getName() + "'";
					}
				}
				
				// TODO: Redeclare, extends relation, etc.
			}
		}
		return context.getName();
	}
}
