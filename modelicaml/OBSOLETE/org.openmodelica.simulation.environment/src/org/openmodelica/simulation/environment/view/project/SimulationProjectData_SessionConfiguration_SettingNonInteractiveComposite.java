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
 * THIS OSMC PUBLIC LICENSE (OSMC-PL). 
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
 *   Uwe Pohlmann, University of Paderborn 2009-2010, contribution to the Modelica code generation for state machine behavior, contribution to Papyrus GUI adoptations
 *   Parham Vasaiely, EADS Innovation Works / Hamburg University of Applied Sciences 2009-2011, implementation of simulation plugins
 */
package org.openmodelica.simulation.environment.view.project;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

// TODO: Auto-generated Javadoc
/**
 * The Class SimulationProjectData_SessionConfiguration_SettingNonInteractiveComposite.
 */
public class SimulationProjectData_SessionConfiguration_SettingNonInteractiveComposite extends org.eclipse.swt.widgets.Composite {

	/** The label used solver. */
	public Label labelUsedSolver;
	
	/** The label solver. */
	private Label labelSolver;
	
	/** The label fill empty001. */
	private Label labelFillEmpty001;
	
	/** The label fill empty003. */
	private Label labelFillEmpty003;
	
	/** The label fill empty002. */
	private Label labelFillEmpty002;
	
	/** The text stop. */
	public Text textStop;
	
	/** The label stop. */
	private Label labelStop;
	
	/** The text start. */
	public Text textStart;
	
	/** The label start. */
	private Label labelStart;
	
	/** The text tolerance. */
	public Text textTolerance;
	
	/** The label calc tolerance. */
	private Label labelCalcTolerance;
	
	/** The text interval unit. */
	public Text textIntervalUnit;
	
	/** The composite simulation settings. */
	private Composite compositeSimulationSettings;
	
	/** The text intervalls. */
	public Text textIntervalls;
	
	/** The label interval. */
	private Label labelInterval;
	
	/** The composite setting data. */
	private Composite compositeSettingData;

	/**
	* Auto-generated main method to display this 
	* org.eclipse.swt.widgets.Composite inside a new Shell.
	*/
	
	/**
	* Overriding checkSubclass allows this class to extend org.eclipse.swt.widgets.Composite
	*/	
	protected void checkSubclass() {
	}
	
	/**
	 * Auto-generated method to display this
	 * org.eclipse.swt.widgets.Composite inside a new Shell.
	 *
	 * @param parent the parent
	 * @param style the style
	 */

	public SimulationProjectData_SessionConfiguration_SettingNonInteractiveComposite(org.eclipse.swt.widgets.Composite parent, int style) {
		super(parent, style);
		initGUI();
	}

	/**
	 * Inits the gui.
	 */
	private void initGUI() {
		try {
			FormLayout thisLayout = new FormLayout();
			this.setLayout(thisLayout);
			{
				compositeSettingData = new Composite(this, SWT.NONE);
				GridLayout compositeSettingDataLayout = new GridLayout();
				compositeSettingDataLayout.makeColumnsEqualWidth = true;
				compositeSettingData.setLayout(compositeSettingDataLayout);
				{
					GridData composite4LData = new GridData();
					composite4LData.verticalAlignment = GridData.FILL;
					composite4LData.horizontalAlignment = GridData.FILL;
					composite4LData.grabExcessHorizontalSpace = true;
					compositeSimulationSettings = new Composite(compositeSettingData, SWT.BORDER);
					GridLayout composite4Layout = new GridLayout();
					composite4Layout.makeColumnsEqualWidth = true;
					composite4Layout.numColumns = 3;
					compositeSimulationSettings.setLayout(composite4Layout);
					compositeSimulationSettings.setLayoutData(composite4LData);
					{
						labelStart = new Label(compositeSimulationSettings, SWT.NONE);
						GridData labelStartLData = new GridData();
						labelStart.setLayoutData(labelStartLData);
						labelStart.setText("Start:");
					}
					{
						GridData textStartLData = new GridData();
						textStartLData.horizontalAlignment = GridData.FILL;
						textStart = new Text(compositeSimulationSettings, SWT.NONE);
						textStart.setLayoutData(textStartLData);
					}
					{
						labelFillEmpty002 = new Label(compositeSimulationSettings, SWT.NONE);
						GridData labelFillEmpty002LData = new GridData();
						labelFillEmpty002.setLayoutData(labelFillEmpty002LData);
					}
					{
						labelStop = new Label(compositeSimulationSettings, SWT.NONE);
						GridData labelStopLData = new GridData();
						labelStop.setLayoutData(labelStopLData);
						labelStop.setText("Stop:");
					}
					{
						textStop = new Text(compositeSimulationSettings, SWT.NONE);
						GridData textStopLData = new GridData();
						textStopLData.horizontalAlignment = GridData.FILL;
						textStop.setLayoutData(textStopLData);
					}
					{
						GridData labelFillEmpty003LData = new GridData();
						labelFillEmpty003 = new Label(compositeSimulationSettings, SWT.NONE);
						labelFillEmpty003.setLayoutData(labelFillEmpty003LData);
					}
					{
						labelInterval = new Label(compositeSimulationSettings, SWT.NONE);
						GridData labelIntervalLData = new GridData();
						labelInterval.setLayoutData(labelIntervalLData);
						labelInterval.setText("Interval:");
					}
					{
						textIntervalls = new Text(compositeSimulationSettings, SWT.NONE);
						GridData textIntervallsLData = new GridData();
						textIntervallsLData.horizontalAlignment = GridData.FILL;
						textIntervalls.setLayoutData(textIntervallsLData);
						textIntervalls.setText("");
					}
					{
						textIntervalUnit = new Text(compositeSimulationSettings, SWT.NONE);
						GridData textIntervalUnitLData = new GridData();
						textIntervalUnitLData.horizontalAlignment = GridData.FILL;
						textIntervalUnit.setLayoutData(textIntervalUnitLData);
						textIntervalUnit.setText("");
					}
					{
						labelCalcTolerance = new Label(compositeSimulationSettings, SWT.NONE);
						GridData labelCalcToleranceLData = new GridData();
						labelCalcTolerance.setLayoutData(labelCalcToleranceLData);
						labelCalcTolerance.setText("Tolerance:");
					}
					{
						textTolerance = new Text(compositeSimulationSettings, SWT.NONE);
						GridData textToleranceLData = new GridData();
						textToleranceLData.horizontalAlignment = GridData.FILL;
						textTolerance.setLayoutData(textToleranceLData);
						textTolerance.setText("");
					}
					{
						GridData labelFillEmpty001LData = new GridData();
						labelFillEmpty001 = new Label(compositeSimulationSettings, SWT.NONE);
						labelFillEmpty001.setLayoutData(labelFillEmpty001LData);
					}
					{
						labelSolver = new Label(compositeSimulationSettings, SWT.NONE);
						GridData labelSolverLData = new GridData();
						labelSolver.setLayoutData(labelSolverLData);
						labelSolver.setText("Solver:");
					}
					{
						labelUsedSolver = new Label(compositeSimulationSettings, SWT.NONE);
						GridData labelUsedSolverLData = new GridData();
						labelUsedSolverLData.horizontalAlignment = GridData.FILL;
						labelUsedSolver.setLayoutData(labelUsedSolverLData);
						labelUsedSolver.setText("");
					}
				}
			
			}
			this.layout();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
