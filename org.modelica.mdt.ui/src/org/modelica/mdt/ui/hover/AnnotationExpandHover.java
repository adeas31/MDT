/*******************************************************************************
 * Copyright (c) 2000, 2008 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.modelica.mdt.ui.hover;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Shell;

import org.eclipse.jface.viewers.IDoubleClickListener;

import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IInformationControl;
import org.eclipse.jface.text.IInformationControlCreator;
import org.eclipse.jface.text.IInformationControlCreatorExtension;
import org.eclipse.jface.text.ITextViewerExtension5;
import org.eclipse.jface.text.Position;
import org.eclipse.jface.text.TextViewer;
import org.eclipse.jface.text.source.Annotation;
import org.eclipse.jface.text.source.CompositeRuler;
import org.eclipse.jface.text.source.IAnnotationAccess;
import org.eclipse.jface.text.source.IAnnotationAccessExtension;
import org.eclipse.jface.text.source.IAnnotationHover;
import org.eclipse.jface.text.source.IAnnotationHoverExtension;
import org.eclipse.jface.text.source.IAnnotationModel;
import org.eclipse.jface.text.source.ILineRange;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.jface.text.source.IVerticalRulerListener;
import org.eclipse.jface.text.source.LineRange;
import org.eclipse.jface.text.source.VerticalRulerEvent;
import org.modelica.mdt.ui.hover.AnnotationExpansionControl.AnnotationHoverInput;


/**
 * This class got moved here form Platform Text since it was not used there
 * and caused discouraged access warnings. It will be moved down again once
 * annotation roll-over support is provided by Platform Text.
 *
 * @since 3.2
 */
public class AnnotationExpandHover implements IAnnotationHover, IAnnotationHoverExtension {

	private class InformationControlCreator implements IInformationControlCreator, IInformationControlCreatorExtension {

		/*
		 * @see org.eclipse.jface.text.IInformationControlCreator#createInformationControl(org.eclipse.swt.widgets.Shell)
		 */
		@Override
		public IInformationControl createInformationControl(Shell parent) {
			return new AnnotationExpansionControl(parent, SWT.NONE, fAnnotationAccess);
		}

		/*
		 * @see org.eclipse.jface.text.IInformationControlCreatorExtension#canReuse(org.eclipse.jface.text.IInformationControl)
		 */
		@Override
		public boolean canReuse(IInformationControl control) {
			return control instanceof AnnotationExpansionControl;
		}

		/*
		 * @see org.eclipse.jface.text.IInformationControlCreatorExtension#canReplace(org.eclipse.jface.text.IInformationControlCreator)
		 */
		@Override
		public boolean canReplace(IInformationControlCreator creator) {
			return creator == this;
		}
	}

	private class VerticalRulerListener implements IVerticalRulerListener {

		/*
		 * @see org.eclipse.jface.text.source.IVerticalRulerListener#annotationSelected(org.eclipse.jface.text.source.VerticalRulerEvent)
		 */
		@Override
		public void annotationSelected(VerticalRulerEvent event) {
			fCompositeRuler.fireAnnotationSelected(event);
		}

		/*
		 * @see org.eclipse.jface.text.source.IVerticalRulerListener#annotationDefaultSelected(org.eclipse.jface.text.source.VerticalRulerEvent)
		 */
		@Override
		public void annotationDefaultSelected(VerticalRulerEvent event) {
			fCompositeRuler.fireAnnotationDefaultSelected(event);
		}

		/*
		 * @see org.eclipse.jface.text.source.IVerticalRulerListener#annotationContextMenuAboutToShow(org.eclipse.jface.text.source.VerticalRulerEvent, org.eclipse.swt.widgets.Menu)
		 */
		@Override
		public void annotationContextMenuAboutToShow(VerticalRulerEvent event, Menu menu) {
			fCompositeRuler.fireAnnotationContextMenuAboutToShow(event, menu);
		}
	}


	private final IInformationControlCreator fgCreator= new InformationControlCreator();
	protected final IVerticalRulerListener fgListener= new VerticalRulerListener();
	protected CompositeRuler fCompositeRuler;
	protected IDoubleClickListener fDblClickListener;
	protected IAnnotationAccess fAnnotationAccess;

	/**
	 * Creates a new hover instance.
	 *
	 * @param ruler
	 * @param access
	 * @param doubleClickListener
	 */
	public AnnotationExpandHover(CompositeRuler ruler, IAnnotationAccess access, IDoubleClickListener doubleClickListener) {
		fCompositeRuler= ruler;
		fAnnotationAccess= access;
		fDblClickListener= doubleClickListener;
	}

	/*
	 * @see org.eclipse.jface.text.source.IAnnotationHover#getHoverInfo(org.eclipse.jface.text.source.ISourceViewer, int)
	 */
	@Override
	public String getHoverInfo(ISourceViewer sourceViewer, int line) {
		// we don't have any sensible return value as text
		return null;
	}

	protected Object getHoverInfoForLine(ISourceViewer viewer, int line) {
		IAnnotationModel model= viewer.getAnnotationModel();
		IDocument document= viewer.getDocument();

		if (model == null)
			return null;

		List<Annotation> exact= new ArrayList<Annotation>();
		HashMap messagesAtPosition= new HashMap();

		Iterator<?> e= model.getAnnotationIterator();
		while (e.hasNext()) {
			Annotation annotation= (Annotation) e.next();
			Position position= model.getPosition(annotation);
			if (position == null)
				continue;

			if (compareRulerLine(position, document, line) == 1) {
				if (isDuplicateMessage(messagesAtPosition, position, annotation.getText()))
					continue;

				exact.add(annotation);
			}
		}

		if (exact.size() < 1)
			return null;

		sort(exact, model);

		if (exact.size() > 0)
			setLastRulerMouseLocation(viewer, line);

		AnnotationHoverInput input= new AnnotationHoverInput();
		input.fAnnotations= exact.toArray(new Annotation[0]);
		input.fViewer= viewer;
		input.fRulerInfo= fCompositeRuler;
		input.fAnnotationListener= fgListener;
		input.fDoubleClickListener= fDblClickListener;
		input.model= model;

		return input;
	}

	protected void sort(List exact, final IAnnotationModel model) {
		class AnnotationComparator implements Comparator {

			/*
			 * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
			 */
			@Override
			public int compare(Object o1, Object o2) {
				Annotation a1= (Annotation) o1;
				Annotation a2= (Annotation) o2;

				Position p1= model.getPosition(a1);
				Position p2= model.getPosition(a2);

				// annotation order:
				// primary order: by position in line
				// secondary: annotation importance
				if (p1.offset == p2.offset)
					return getOrder(a2) - getOrder(a1);
				return p1.offset - p2.offset;
			}
		}

		Collections.sort(exact, new AnnotationComparator());

	}

	protected int getOrder(Annotation annotation) {
		if (fAnnotationAccess instanceof IAnnotationAccessExtension) {
			IAnnotationAccessExtension extension= (IAnnotationAccessExtension) fAnnotationAccess;
			return extension.getLayer(annotation);
		}
		return IAnnotationAccessExtension.DEFAULT_LAYER;
	}

	protected boolean isDuplicateMessage(Map messagesAtPosition, Position position, String message) {
		if (message == null)
			return false;

		if (messagesAtPosition.containsKey(position)) {
			Object value= messagesAtPosition.get(position);
			if (message.equals(value))
				return true;

			if (value instanceof List) {
				List messages= (List)value;
				if  (messages.contains(message))
					return true;
				messages.add(message);
			} else {
				List messages= new ArrayList();
				messages.add(value);
				messages.add(message);
				messagesAtPosition.put(position, messages);
			}
		} else
			messagesAtPosition.put(position, message);
		return false;
	}

	protected void setLastRulerMouseLocation(ISourceViewer viewer, int line) {
		// set last mouse activity in order to get the correct context menu
		if (fCompositeRuler != null) {
			StyledText st= viewer.getTextWidget();
			if (st != null && !st.isDisposed()) {
				if (viewer instanceof ITextViewerExtension5) {
					int widgetLine= ((ITextViewerExtension5)viewer).modelLine2WidgetLine(line);
					Point loc= st.getLocationAtOffset(st.getOffsetAtLine(widgetLine));
					fCompositeRuler.setLocationOfLastMouseButtonActivity(0, loc.y);
				} else if (viewer instanceof TextViewer) {
					// TODO remove once TextViewer implements the extension
					int widgetLine= ((TextViewer)viewer).modelLine2WidgetLine(line);
					Point loc= st.getLocationAtOffset(st.getOffsetAtLine(widgetLine));
					fCompositeRuler.setLocationOfLastMouseButtonActivity(0, loc.y);
				}
			}
		}
	}

	/**
	 * Returns the distance to the ruler line.
	 *
	 * @param position the position
	 * @param document the document
	 * @param line the line number
	 * @return the distance to the ruler line
	 */
	protected int compareRulerLine(Position position, IDocument document, int line) {

		if (position.getOffset() > -1 && position.getLength() > -1) {
			try {
				int firstLine= document.getLineOfOffset(position.getOffset());
				if (line == firstLine)
					return 1;
				if (firstLine <= line && line <= document.getLineOfOffset(position.getOffset() + position.getLength()))
					return 2;
			} catch (BadLocationException x) {
			}
		}

		return 0;
	}

	/*
	 * @see org.eclipse.jface.text.source.IAnnotationHoverExtension#getHoverControlCreator()
	 */
	@Override
	public IInformationControlCreator getHoverControlCreator() {
		return fgCreator;
	}

	/*
	 * @see org.eclipse.jface.text.source.IAnnotationHoverExtension#getHoverInfo(org.eclipse.jface.text.source.ISourceViewer, org.eclipse.jface.text.source.ILineRange, int)
	 */
	@Override
	public Object getHoverInfo(ISourceViewer sourceViewer, ILineRange lineRange, int visibleLines) {
		return getHoverInfoForLine(sourceViewer, lineRange.getStartLine());
	}

	/*
	 * @see org.eclipse.jface.text.source.IAnnotationHoverExtension#getHoverLineRange(org.eclipse.jface.text.source.ISourceViewer, int)
	 */
	@Override
	public ILineRange getHoverLineRange(ISourceViewer viewer, int lineNumber) {
		return new LineRange(lineNumber, 1);
	}

	/*
	 * @see org.eclipse.jface.text.source.IAnnotationHoverExtension#canHandleMouseCursor()
	 */
	@Override
	public boolean canHandleMouseCursor() {
		return true;
	}
}
