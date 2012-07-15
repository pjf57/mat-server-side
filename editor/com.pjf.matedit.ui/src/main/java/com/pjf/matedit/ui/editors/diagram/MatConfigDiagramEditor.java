/**
 * Copyright (c) 2012 Laurent Mihalkovic and others.
 * All rights reserved. This program and the accompanying materials are
 * made available under the terms of the Eclipse Distribution License v1.0
 * which accompanies this distribution, and is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php 
 * 
 * Contributors: 
 * 		Laurent Mihalkovic - Initial implementation 
 */
package com.pjf.matedit.ui.editors.diagram;

import java.io.InputStream;
import java.util.Properties;

import korlis.model.DiagramModel;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.draw2d.FigureCanvas;
import org.eclipse.gef.DefaultEditDomain;
import org.eclipse.gef.EditPartViewer;
import org.eclipse.gef.GraphicalViewer;
import org.eclipse.gef.KeyHandler;
import org.eclipse.gef.KeyStroke;
import org.eclipse.gef.SnapToGrid;
import org.eclipse.gef.dnd.TemplateTransferDragSourceListener;
import org.eclipse.gef.dnd.TemplateTransferDropTargetListener;
import org.eclipse.gef.editparts.GridLayer;
import org.eclipse.gef.editparts.ScalableFreeformRootEditPart;
import org.eclipse.gef.palette.PaletteRoot;
import org.eclipse.gef.ui.actions.CopyTemplateAction;
import org.eclipse.gef.ui.actions.GEFActionConstants;
import org.eclipse.gef.ui.palette.PaletteViewer;
import org.eclipse.gef.ui.palette.PaletteViewerProvider;
import org.eclipse.gef.ui.parts.ContentOutlinePage;
import org.eclipse.gef.ui.parts.GraphicalEditorWithFlyoutPalette;
import org.eclipse.gef.ui.parts.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.BusyIndicator;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.actions.ActionFactory;
import org.eclipse.ui.forms.editor.FormEditor;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.eclipse.ui.internal.forms.widgets.FormHeading;
import org.eclipse.ui.part.IPageSite;
import org.eclipse.ui.views.contentoutline.IContentOutlinePage;

import com.pjf.mat.api.MatApi;
import com.pjf.mat.impl.MatInterface;
import com.pjf.mat.sys.UDPComms;
import com.pjf.matedit.ui.editors.MatConfigEditor;
import com.pjf.matedit.ui.editors.diagram.model.MatConfigDiagramModel;
import com.pjf.matedit.ui.editors.diagram.part.MatDiagramConfigEditPartFactory;
import com.pjf.matedit.ui.editors.diagram.part.MatTreeConfigPartFactory;
import com.pjf.matedit.ui.internal.MatEditorPlugin;

public class MatConfigDiagramEditor extends GraphicalEditorWithFlyoutPalette /*implements EditorPagePresentation*/ {

	private boolean editorSaving = false;
	
	private final MatConfigDiagramModel diagramModel;
	private FormEditor editor;
	private DiagramPaletteRoot palette;
	private OutlinePage outlinePage;
	private DisposeListener disposeListener;
	private KeyHandler sharedKeyHandler;
	
	class OutlinePage extends ContentOutlinePage implements IAdaptable {

		private Control outline;
//		private Canvas overview;

		public OutlinePage(EditPartViewer viewer) {
			super(viewer);
		}

		@Override
		public void createControl(Composite parent) {
//			pageBook = new PageBook(parent, SWT.NONE);
//			outline = getViewer().createControl(pageBook);
//			overview = new Canvas(pageBook, SWT.NONE);
			outline = getViewer().createControl(parent);
//			overview = new Canvas(pageBook, SWT.NONE);
//			pageBook.showPage(outline);
			configureOutlineViewer();
			hookOutlineViewer();
			initializeOutlineViewer();
		}

		@Override
		public void init(IPageSite pageSite) {
			super.init(pageSite);
		}

		protected void configureOutlineViewer() {
			getViewer().setEditDomain(getEditDomain());
			getViewer().setEditPartFactory(new MatTreeConfigPartFactory());
			getViewer().setKeyHandler(getCommonKeyHandler());			
		}
		
		protected void hookOutlineViewer() {
			getSelectionSynchronizer().addViewer(getViewer());
		}

		protected void unhookOutlineViewer() {
			getSelectionSynchronizer().removeViewer(getViewer());
//			if (disposeListener != null && getEditor() != null
//					&& !getEditor().isDisposed())
//				getEditor().removeDisposeListener(disposeListener);
		}
		
		protected void initializeOutlineViewer() {
			setContents(getDiagramModel());
		}

		public void setContents(Object contents) {
			getViewer().setContents(contents);
		}

		@Override
		public Object getAdapter(Class adapter) {
			return null;
		}
		
		public void dispose() {
			unhookOutlineViewer();
//			if (thumbnail != null) {
//				thumbnail.deactivate();
//				thumbnail = null;
//			}
			super.dispose();
//			LogicEditor.this.outlinePage = null;
//			outlinePage = null;
		}

	}

    
	public MatConfigDiagramEditor(FormEditor editor) {
		this.editor = editor;
		diagramModel = new MatConfigDiagramModel();
		setEditDomain(new DefaultEditDomain(this));
		setPartName("Diagram");
	}
	
	@Override
	public void createPartControl(Composite parent) 
	{
		final ScrolledForm form = editor.getToolkit().createScrolledForm(parent);
		form.setText("Wiring Diagram");
		MatConfigEditor.decorateHeading((FormHeading)form.getForm().getHead(), MatEditorPlugin.getDefault().getFormColors(parent.getDisplay()));

		BusyIndicator.showWhile(parent.getDisplay(), new Runnable() {
			public void run() {
				FillLayout layout = new FillLayout();
				layout.marginHeight = 0;
				layout.marginWidth = 0;
				form.getBody().setLayout(layout);
				MatConfigDiagramEditor.super.createPartControl(form.getBody());
			}
		});
		
	}
		
	/* (non-Javadoc)
	 * @see org.eclipse.ui.part.EditorPart#setInput(org.eclipse.ui.IEditorInput)
	 */
	@Override
	protected void setInput(IEditorInput input) {
		super.setInput(input);

		if (input instanceof IFileEditorInput) {
		
			final IFile file= ((IFileEditorInput)input).getFile();
			try {
				// read file
				InputStream config = file.getContents();
				Properties properties = new Properties();
				properties.load(config);
				UDPComms comms = new UDPComms("192.168.0.9",2000);
				MatApi mat = new MatInterface(properties,comms);
//				mat = new MatInterface(props,comms);
				
// ----------------
// HACK
Properties p = Loader.loadMacd16(mat, properties);				
// ----------------
				
				diagramModel.reloadModel(mat, p);				
				palette.updatePaletteEntries();
			} catch (Exception e) {
				// TODO route to normal error handling
e.printStackTrace();
			}
		
			if (!editorSaving) {
				if (getGraphicalViewer() != null) {
					getGraphicalViewer().setContents(getDiagramModel());
//					loadProperties();
				}
				if (outlinePage != null) {
					outlinePage.setContents(getDiagramModel());
				}
			}
		}
	}

	@Override
	public void doSave(IProgressMonitor monitor) {
		// TODO save new model
		
		// tell save is done
		getCommandStack().markSaveLocation();
		firePropertyChange(PROP_DIRTY);
	}
	
	@Override
	protected void initializeGraphicalViewer() {
		super.initializeGraphicalViewer();
		GraphicalViewer viewer = getGraphicalViewer();		
		viewer.setContents(getDiagramModel());
		viewer.addDropTargetListener(new TemplateTransferDropTargetListener(viewer));
		
		disposeListener = new DisposeListener() {
			public void widgetDisposed(DisposeEvent e) {
				diagramModel.dispose();
			}
		};
		getEditor().addDisposeListener(disposeListener);
	}
	
	protected FigureCanvas getEditor() {
		return (FigureCanvas) getGraphicalViewer().getControl();
	}

	@Override
	protected void configureGraphicalViewer() {
		super.configureGraphicalViewer();

		// configure diagram
		GraphicalViewer viewer = getGraphicalViewer();		

		viewer.setEditPartFactory(new MatDiagramConfigEditPartFactory(diagramModel));
		viewer.setRootEditPart(new ScalableFreeformRootEditPart() {
			@Override
			protected GridLayer createGridLayer() {
				return new DiagramGridLayer(diagramModel);
			}			
		});
		
		viewer.setProperty(SnapToGrid.PROPERTY_GRID_VISIBLE, Boolean.TRUE);
//        this.diagramEditorPagePartListener = new Listener()
//        {
//            @Override
//            public void handle( final Event event )
//            {
//                if( event instanceof ImageChangedEvent )
//                {
//                    refreshFormHeaderImage();
//                }
//                else if( event instanceof ZoomLevelEvent )
//                {
//                    refreshZoomLevel();
//                }
//            }
//        };
	}	
	
	@Override
	protected PaletteViewerProvider createPaletteViewerProvider() {
		return new PaletteViewerProvider(getEditDomain()) {
			protected void configurePaletteViewer(PaletteViewer viewer) {
				super.configurePaletteViewer(viewer);
				viewer.addDragSourceListener(new TemplateTransferDragSourceListener(viewer));
			}
			
			protected void hookPaletteViewer(PaletteViewer viewer) {
				super.hookPaletteViewer(viewer);
				final CopyTemplateAction copy = 
						(CopyTemplateAction) getActionRegistry().getAction(ActionFactory.COPY.getId());
				viewer.addSelectionChangedListener(copy);
//				if (menuListener == null)
//					menuListener = new IMenuListener() {
//						public void menuAboutToShow(IMenuManager manager) {
//							manager.appendToGroup(
//									GEFActionConstants.GROUP_COPY, copy);
//						}
//					};
//				viewer.getContextMenu().addMenuListener(menuListener);
			}
		};
	}

	@Override
	protected PaletteRoot getPaletteRoot() {
		if (palette == null) {
			palette = new DiagramPaletteRoot(new MatConfigDiagramPaletteContentProvider(diagramModel));
		}
		return palette;
	}

	protected DiagramModel getDiagramModel() {
		return diagramModel;
	}
	
	@Override
	public Object getAdapter(Class type) {
		if (type == IContentOutlinePage.class) {
			outlinePage = new OutlinePage(new TreeViewer());
			return outlinePage;
		}
		return super.getAdapter(type);
	}

	
//	protected boolean performSaveAs() {
//		SaveAsDialog dialog = new SaveAsDialog(getSite().getWorkbenchWindow()
//				.getShell());
//		dialog.setOriginalFile(((IFileEditorInput) getEditorInput()).getFile());
//		dialog.open();
//		IPath path = dialog.getResult();
//
//		if (path == null)
//			return false;
//
//		IWorkspace workspace = ResourcesPlugin.getWorkspace();
//		final IFile file = workspace.getRoot().getFile(path);
//
//		if (!file.exists()) {
//			WorkspaceModifyOperation op = new WorkspaceModifyOperation() {
//				public void execute(final IProgressMonitor monitor) {
//					saveProperties();
//					try {
//						ByteArrayOutputStream out = new ByteArrayOutputStream();
//						writeToOutputStream(out);
//						file.create(
//								new ByteArrayInputStream(out.toByteArray()),
//								true, monitor);
//						out.close();
//					} catch (Exception e) {
//						e.printStackTrace();
//					}
//				}
//			};
//			try {
//				new ProgressMonitorDialog(getSite().getWorkbenchWindow()
//						.getShell()).run(false, true, op);
//			} catch (Exception e) {
//				e.printStackTrace();
//			}
//		}
//
//		try {
//			superSetInput(new FileEditorInput(file));
//			getCommandStack().markSaveLocation();
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//		return true;
//	}
	
//	protected void saveProperties() {
//		getLogicDiagram().setRulerVisibility(
//				((Boolean) getGraphicalViewer().getProperty(
//						RulerProvider.PROPERTY_RULER_VISIBILITY))
//						.booleanValue());
//		getLogicDiagram().setGridEnabled(
//				((Boolean) getGraphicalViewer().getProperty(
//						SnapToGrid.PROPERTY_GRID_ENABLED)).booleanValue());
//		getLogicDiagram().setSnapToGeometry(
//				((Boolean) getGraphicalViewer().getProperty(
//						SnapToGeometry.PROPERTY_SNAP_ENABLED)).booleanValue());
//		ZoomManager manager = (ZoomManager) getGraphicalViewer().getProperty(
//				ZoomManager.class.toString());
//		if (manager != null)
//			getLogicDiagram().setZoom(manager.getZoom());
//	}

	protected KeyHandler getCommonKeyHandler() {
		if (sharedKeyHandler == null) {
			sharedKeyHandler = new KeyHandler();
			sharedKeyHandler.put(
					KeyStroke.getPressed(SWT.F2, 0),
					getActionRegistry().getAction(
							GEFActionConstants.DIRECT_EDIT));
		}
		return sharedKeyHandler;
	}


}


