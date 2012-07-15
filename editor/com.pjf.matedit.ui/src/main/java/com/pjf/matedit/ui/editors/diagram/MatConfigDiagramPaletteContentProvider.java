/**
 * Copyright (c) 2012 PJF and others.
 * All rights reserved. This program and the accompanying materials are
 * made available under the terms of the Eclipse Distribution License v1.0
 * which accompanies this distribution, and is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php 
 * 
 * Contributors: 
 * 		Peter Fall - MAT initial implementation 
 * 		Laurent Mihalkovic - Maven support & Editor 
 */
package com.pjf.matedit.ui.editors.diagram;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import korlis.model.DiagramNoteElement;

import org.eclipse.gef.SharedImages;
import org.eclipse.gef.palette.CombinedTemplateCreationEntry;
import org.eclipse.gef.palette.ConnectionCreationToolEntry;
import org.eclipse.gef.palette.MarqueeToolEntry;
import org.eclipse.gef.palette.PaletteContainer;
import org.eclipse.gef.palette.PaletteDrawer;
import org.eclipse.gef.palette.PaletteEntry;
import org.eclipse.gef.palette.PaletteToolbar;
import org.eclipse.gef.palette.PanningSelectionToolEntry;
import org.eclipse.gef.palette.ToolEntry;
import org.eclipse.gef.requests.CreationFactory;
import org.eclipse.gef.requests.SimpleFactory;

import com.pjf.mat.api.Element;
import com.pjf.mat.impl.element.BasicElement;
import com.pjf.matedit.ui.editors.diagram.model.MatConfigDiagramModel;
import com.pjf.matedit.ui.internal.Images;


/**
 * @author <a href="mailto:laurent.mihalkovic@gmail.com">lmihalkovic</a>
 */
public class MatConfigDiagramPaletteContentProvider implements IDiagramPaletteContentProvider {

	private ToolEntry tool;
	private MatConfigDiagramModel model;
	
	public MatConfigDiagramPaletteContentProvider(MatConfigDiagramModel model) {
		this.model = model;
	}

	@Override
	public PaletteContainer[] getContainers() {
		List<PaletteContainer> containers = new ArrayList<PaletteContainer>();
		containers.add(createModelIndependentTools());
		containers.add(createModelDependentTools());
		return containers.toArray(new PaletteContainer[containers.size()]);
	}

	@Override
	public ToolEntry getDefault() {
		return tool;
	}
	
	protected PaletteContainer createModelIndependentTools() {
		PaletteToolbar toolbar = new PaletteToolbar("Tools");
		tool = new PanningSelectionToolEntry();
		toolbar.add(tool);
		toolbar.add(new MarqueeToolEntry());
		return toolbar;
	}

	protected PaletteContainer createModelDependentTools() {
		PaletteDrawer drawer = new PaletteDrawer("MAT Elements");

		if (model != null) {

			/*
			 * Tools
			 */
			{
				//Notes
				PaletteEntry entry = new CombinedTemplateCreationEntry(
						"Note",
						"Note tool can be used to attach annotations to a design.", 
						new SimpleFactory(DiagramNoteElement.class),
						Images.desc(Images.IMG_PAL_NOTE_16), 
						Images.desc(Images.IMG_PAL_NOTE_24)
				);
//				entry.setToolClass(LogicCreationTool.class);
				drawer.add(entry);				
			}

			{
				// links
				PaletteEntry entry = new ConnectionCreationToolEntry(
						"Connection",
						"Connection tool can be used to connect the various logic blocks.", 
						null,
						Images.desc(Images.IMG_PAL_CONNECT_16), 
						Images.desc(Images.IMG_PAL_CONNECT_24)
				);
				drawer.add(entry);
			}
			
			/*
			 * Elements
			 */
			Set<String> types = model.getElementTypes();
			for(String name: types) {
				CreationFactory factory = new CreationFactory() {
					@Override
					public Object getNewObject() {
return new BasicElement(22, "dldldl");
					}

					@Override
					public Object getObjectType() {
						return Element.class;
					}
				};
				PaletteEntry entry = new CombinedTemplateCreationEntry(
						name, name, factory,
						SharedImages.DESC_MARQUEE_TOOL_NODES_16, 
						SharedImages.DESC_MARQUEE_TOOL_NODES_24);
				drawer.add(entry);
			}
			
		}
		
		return drawer;
	}
	
}
