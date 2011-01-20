/*
 * org.openmicroscopy.shoola.agents.fsimporter.view.ImporterComponent 
 *
 *------------------------------------------------------------------------------
 *  Copyright (C) 2006-2008 University of Dundee. All rights reserved.
 *
 *
 * 	This program is free software; you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation; either version 2 of the License, or
 *  (at your option) any later version.
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *  
 *  You should have received a copy of the GNU General Public License along
 *  with this program; if not, write to the Free Software Foundation, Inc.,
 *  51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 *------------------------------------------------------------------------------
 */
package org.openmicroscopy.shoola.agents.fsimporter.view;


//Java imports
import java.io.File;
import java.util.Collection;


//Third-party libraries

//Application-internal dependencies
import org.openmicroscopy.shoola.agents.fsimporter.ImporterAgent;
import org.openmicroscopy.shoola.agents.fsimporter.chooser.ImportDialog;
import org.openmicroscopy.shoola.agents.fsimporter.chooser.ImportableObject;
import org.openmicroscopy.shoola.env.ui.UserNotifier;
import org.openmicroscopy.shoola.util.ui.UIUtilities;
import org.openmicroscopy.shoola.util.ui.component.AbstractComponent;

/** 
 * Implements the {@link Importer} interface to provide the functionality
 * required of the hierarchy viewer component.
 * This class is the component hub and embeds the component's MVC triad.
 * It manages the component's state machine and fires state change 
 * notifications as appropriate, but delegates actual functionality to the
 * MVC sub-components.
 *
 * @see org.openmicroscopy.shoola.agents.fsimporter.view.ImporterModel
 * @see org.openmicroscopy.shoola.agents.fsimporter.view.ImporterUI
 * @see org.openmicroscopy.shoola.agents.fsimporter.view.ImporterControl
 *
 * @author  Jean-Marie Burel &nbsp;&nbsp;&nbsp;&nbsp;
 * <a href="mailto:j.burel@dundee.ac.uk">j.burel@dundee.ac.uk</a>
 * @author Donald MacDonald &nbsp;&nbsp;&nbsp;&nbsp;
 * <a href="mailto:donald@lifesci.dundee.ac.uk">donald@lifesci.dundee.ac.uk</a>
 * @version 3.0
 * <small>
 * (<b>Internal version:</b> $Revision: $Date: $)
 * </small>
 * @since 3.0-Beta4
 */
class ImporterComponent 
	extends AbstractComponent
	implements Importer
{

	/** The Model sub-component. */
	private ImporterModel 	model;
	
	/** The Controller sub-component. */
	private ImporterControl 	controller;
	
	/** The View sub-component. */
	private ImporterUI 		view;
	
	/** Reference to the chooser used to select the files to import. */
	private ImportDialog	chooser;
	
	/** 
	 * Shows the dialog used to select the files to import. 
	 * 
	 * @param type One of the type constants.
	 */
	private void showChooser(int type)
	{
		if (chooser == null) {
			chooser = new ImportDialog(view, model.getSupportedFormats(), 
					model.getContainer(), type);
			chooser.addPropertyChangeListener(controller);
			chooser.pack();
		} else {
			
		}
		UIUtilities.centerAndShow(chooser);
	}
	
	/**
	 * Creates a new instance.
	 * The {@link #initialize() initialize} method should be called straigh 
	 * after to complete the MVC set up.
	 * 
	 * @param model The Model sub-component. Mustn't be <code>null</code>.
	 */
	ImporterComponent(ImporterModel model)
	{
		if (model == null) throw new NullPointerException("No model.");
		this.model = model;
		controller = new ImporterControl(this);
		view = new ImporterUI();
	}

	/** Links up the MVC triad. */
	void initialize()
	{
		controller.initialize(view);
		view.initialize(model, controller);
	}

	/** 
	 * Implemented as specified by the {@link Importer} interface.
	 * @see Importer#activate(int)
	 */
	public void activate(int type)
	{
		switch (model.getState()) {
			case NEW:
				showChooser(type);
				model.setState(READY);
				break;
			case DISCARDED:
				throw new IllegalStateException(
						"This method can't be invoked in the DISCARDED state.");
		} 
	}

	/** 
	 * Implemented as specified by the {@link Importer} interface.
	 * @see Importer#discard()
	 */
	public void discard()
	{
		if (model.getState() == DISCARDED)
			model.discard();
	}

	/** 
	 * Implemented as specified by the {@link Importer} interface.
	 * @see Importer#getState()
	 */
	public int getState() { return model.getState(); }

	/** 
	 * Implemented as specified by the {@link Importer} interface.
	 * @see Importer#cancel()
	 */
	public void cancel()
	{
		model.cancel();
	}

	/** 
	 * Implemented as specified by the {@link Importer} interface.
	 * @see Importer#importData(ImportableObject)
	 */
	public void importData(ImportableObject data)
	{
		if (model.getState() != READY) return;
		if (data == null || data.getFiles() == null || 
				data.getFiles().size() == 0) {
			UserNotifier un = ImporterAgent.getRegistry().getUserNotifier();
			un.notifyInfo("Import", "No Files to import.");
			return;
		}
		//
		
		//model.fireImportData(data);
		fireStateChange();
	}

	/** 
	 * Implemented as specified by the {@link Importer} interface.
	 * @see Importer#monitorDirectory(File)
	 */
	public void monitorDirectory(File dir)
	{
		if (model.getState() != READY) return;
		if (dir == null) {
			UserNotifier un = ImporterAgent.getRegistry().getUserNotifier();
			un.notifyInfo("Import", "No directory to monitor.");
			return;
		}
		model.fireMonitorDirectory(dir);
		fireStateChange();
	}

	/** 
	 * Implemented as specified by the {@link Importer} interface.
	 * @see Importer#setExistingTags(Collection)
	 */
	public void setExistingTags(Collection tags)
	{
		model.setTags(tags);
		if (chooser != null) chooser.setTags(tags);
	}
	
	/** 
	 * Implemented as specified by the {@link Importer} interface.
	 * @see Importer#loadExistingTags()
	 */
	public void loadExistingTags()
	{
		Collection tags = model.getTags();
		if (tags != null) setExistingTags(tags);
		else model.fireTagsLoading();	
	}
	
}