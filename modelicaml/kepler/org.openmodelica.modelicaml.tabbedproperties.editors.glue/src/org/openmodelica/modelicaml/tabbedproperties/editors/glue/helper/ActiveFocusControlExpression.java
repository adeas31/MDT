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
package org.openmodelica.modelicaml.tabbedproperties.editors.glue.helper;

import org.eclipse.core.expressions.EvaluationResult;
import org.eclipse.core.expressions.Expression;
import org.eclipse.core.expressions.ExpressionInfo;
import org.eclipse.core.expressions.IEvaluationContext;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.ISources;

/**
 * An expression that evaluates to true if and only if the current focus control is the one provided. Has a very
 * high priority in order to ensure proper conflict resolution.
 */

public class ActiveFocusControlExpression extends Expression {

	private Control focusControl;

	/**
	 * ActiveFocusControlExpression
	 * 
	 * @param control
	 */
	public ActiveFocusControlExpression(Control control) {
		focusControl = control;
	}

	@Override
	public void collectExpressionInfo(ExpressionInfo info) {
		info.markDefaultVariableAccessed(); // give it a very high priority
		info.addVariableNameAccess(ISources.ACTIVE_SHELL_NAME);
		info.addVariableNameAccess(ISources.ACTIVE_WORKBENCH_WINDOW_NAME);
	}

	@Override
	public EvaluationResult evaluate(IEvaluationContext context) throws CoreException {
		if (Display.getCurrent() != null && focusControl.isFocusControl()) {
			return EvaluationResult.TRUE;
		}
		return EvaluationResult.FALSE;
	}
}