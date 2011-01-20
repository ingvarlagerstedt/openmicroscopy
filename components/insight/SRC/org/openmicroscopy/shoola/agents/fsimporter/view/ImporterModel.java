/*
 * org.openmicroscopy.shoola.agents.fsimporter.view.ImporterModel 
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
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import javax.swing.filechooser.FileFilter;

//Third-party libraries

//Application-internal dependencies
import org.openmicroscopy.shoola.agents.fsimporter.DataImporterLoader;
import org.openmicroscopy.shoola.agents.fsimporter.DirectoryMonitor;
import org.openmicroscopy.shoola.agents.fsimporter.ImagesImporter;
import org.openmicroscopy.shoola.agents.fsimporter.TagsLoader;
import org.openmicroscopy.shoola.agents.fsimporter.chooser.ImportableObject;
import org.openmicroscopy.shoola.env.data.model.ImportContext;

import pojos.DataObject;

/** 
 * The Model component in the <code>Importer</code> MVC triad.
 * This class tracks the <code>Importer</code>'s state and knows how to
 * initiate data retrievals. It also knows how to store and manipulate
 * the results. This class  provide  a suitable data loader. 
 * The {@link ImporterComponent} intercepts the 
 * results of data loadings, feeds them back to this class and fires state
 * transitions as appropriate.
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
class ImporterModel
{

	/** Holds one of the state flags defined by {@link Importer}. */
	private int					state;

	/** Reference to the component that embeds this model. */
	protected Importer			component;

	/** The object where to import the images. */
	private DataObject			container;
	
	/** 
	 * Will either be a data loader or
	 * <code>null</code> depending on the current state. 
	 */
	private DataImporterLoader 	currentLoader;
	
	/** The collection of exiting tags. */
	private Collection			tags;
	
	/** Creates a new instance. */
	ImporterModel()
	{
		state = Importer.NEW;
	}
	
	/**
	 * Called by the <code>FSImporter</code> after creation to allow this
	 * object to store a back reference to the embedding component.
	 * 
	 * @param component The embedding component.
	 */
	void initialize(Importer component)
	{
		this.component = component;
	}
	
	/**
	 * Returns the current state.
	 * 
	 * @return One of the flags defined by the {@link Importer} interface.  
	 */
	int getState() { return state; }    

	/**
	 * Sets the state of the component.
	 * 
	 * @param state The value to set.
	 */
	void setState(int state) { this.state = state; }
	
	/**
	 * Sets the object in the {@link Importer#DISCARDED} state.
	 * Any ongoing data loading will be cancelled.
	 */
	void discard()
	{
		cancel();
		state = Importer.DISCARDED;
	}

	/**
	 * Sets the object in the {@link Importer#READY} state.
	 * Any ongoing data loading will be cancelled.
	 */
	void cancel()
	{
		if (currentLoader != null) {
			currentLoader.cancel();
			currentLoader = null;
		}
		state = Importer.READY;
	}
	
	/**
	 * Sets the container. 
	 * 
	 * @param container
	 */
	void setContainer(DataObject container) { this.container = container; }
	
	/**
	 * Returns the container.
	 * 
	 * @return See above.
	 */
	DataObject getContainer() { return container; }

	/**
	 * Returns the list of the supported file formats.
	 * 
	 * @return See above.
	 */
	List<FileFilter> getSupportedFormats()
	{
		return new ArrayList<FileFilter>();
		
		//OmeroImageService svc = ImporterAgent.getRegistry().getImageService();
		//return svc.getSupportedFileFilters();
	}
	
	/**
	 * Fires an asynchronous call to import the files in the object.
	 * 
	 * @param data The file to import.
	 */
	void fireImportData(ImportableObject data)
	{
		//ImportContext metadata = new ImportContext();
		//metadata.setTags(data.getTags());
		//metadata.setPixelsSize(data.getPixelsSize());
		
		
		/*
		List<Object> files = new ArrayList<Object>(data.length);
		for (int i = 0; i < data.length; i++)
			files.add(data[i]);
		currentLoader = new ImagesImporter(component, container, files);
		currentLoader.load();
		state = Importer.IMPORTING;
		*/
	}
	
	/**
	 * Fires an asynchronous call to monitor the specified directory.
	 * 
	 * @param directory The directory to monitor.
	 */
	void fireMonitorDirectory(File directory)
	{
		if (currentLoader != null) cancel();
		currentLoader = new DirectoryMonitor(component, directory, container);
		currentLoader.load();
		state = Importer.IMPORTING;
	}
	
	/**
	 * Returns the collection of the existing tags.
	 * 
	 * @return See above.
	 */
	Collection getTags() { return tags; }
	
	/**
	 * Sets the collection of the existing tags.
	 * 
	 * @param The value to set.
	 */
	void setTags(Collection tags)
	{ 
		this.tags = tags; 
	}
	
	/** Starts an asynchronous call to load the tags. */
	void fireTagsLoading()
	{
		if (tags != null) return; //already loading tags
		TagsLoader loader = new TagsLoader(component);
		loader.load();
	}
	
}