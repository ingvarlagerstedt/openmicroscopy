/*
 * org.openmicroscopy.shoola.env.data.model.ScriptObject 
 *
 *------------------------------------------------------------------------------
 *  Copyright (C) 2006-2010 University of Dundee. All rights reserved.
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
package org.openmicroscopy.shoola.env.data.model;


//Java imports
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import javax.swing.Icon;

//Third-party libraries

//Application-internal dependencies
import omero.grid.JobParams;
import omero.grid.Param;
import pojos.ExperimenterData;

/** 
 * Hosts the information about a given script.
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
public class ScriptObject 
{
	
	/** Path to the <code>Figure</code> script. */
	public static final String FIGURE_PATH = "/omero/figure_scripts/";
	
	/** Path to the <code>Export</code> script. */
	public static final String EXPORT_PATH = "/omero/export_scripts/";
	
	/** Path to the <code>Region</code> script. */
	public static final String REGION_PATH = "/omero/region_scripts/";
	
	/** Indicates that the script is a <code>Export</code> script. */
	public static final int EXPORT = 0;
	
	/** Indicates that the script is a <code>Figure</code> script. */
	public static final int FIGURE = 1;
	
	/** Indicates that the script is a <code>Region</code> script. */
	public static final int REGION = 2;
	
	/** Indicates that the script is a <code>Region</code> script. */
	public static final int OTHER = 3;
	
	/** The id of the script. */
	private long scriptID;
	
	/** The name of the script. */
	private String name;
	
	/** The full path to the script when stored in server. */
	private String path;
	
	/** The description of the script. */
	private String description;
	
	/** The description of the script. */
	private String journalRef;
	
	/** The e-mail address of the contact. */
	private String contact;
	
	/** The version of the script. */
	private String version;
	
	/** The owners of the script. */
	private List<ExperimenterData> authors;
	
	/** The parameters of the script. */
	private Map<String, Class> parameterTypes;
	
	/** The values to pass to the script. */
	private Map<String, Object> parameterValues;
	
	/** The input parameters. */
	private Map<String, ParamData> inputs;
	
	/** The output parameters. */
	private Map<String, ParamData> outputs;
	
	/** The 16x16 icon associated to the script. */
	private Icon 		icon;
	
	/** The 48x48 icon associated to the script. */
	private Icon 		iconLarge;
	
	/** Hold the parameters related to the script. */
	private JobParams  parameters;
	
	/** The MIME type of the script if set. */ 
	private String	   mimeType;
	
	/** The category of the script. */
	private int 	  category;
	
	/** Converts the parameters. */
	private void convertJobParameters()
	{
		if (parameters == null) return;
		//Convert authors if 
		String[] authors = parameters.authors;
		if (authors != null && authors.length > 0) {
			//String[][] institutions = params.authorsInstitutions;
			//
			ExperimenterData exp;
			for (int i = 0; i < authors.length; i++) {
				exp = new ExperimenterData();
				exp.setLastName(authors[i]);
			}
		}
		Map<String, Param> map = parameters.inputs;
		Entry entry;
		Iterator i;
		Param p;
		inputs = new HashMap<String, ParamData>();
		if (map != null) {
			i = map.entrySet().iterator();
			while (i.hasNext()) {
				entry = (Entry) i.next();
				p = (Param) entry.getValue();
				inputs.put((String) entry.getKey(), new ParamData(p));
			}
		}
		map = parameters.outputs;
		outputs = new HashMap<String, ParamData>();
		if (map != null) {
			i = map.entrySet().iterator();
			while (i.hasNext()) {
				entry = (Entry) i.next();
				p = (Param) entry.getValue();
				outputs.put((String) entry.getKey(), new ParamData(p));
			}
		}
	}
	
	/** Sets the category of the scripts. */
	private void setCategory()
	{
		if (FIGURE_PATH.equals(path))
			category = FIGURE;
		else if (EXPORT_PATH.equals(path))
			category = EXPORT;
		else if (REGION_PATH.equals(path))
			category = REGION;
		else category = OTHER;
	}
	
	/**
	 * Creates a new instance.
	 * 
	 * @param scriptID The id of the script if uploaded.
	 * @param path The path of the script when stored in server
	 */
	public ScriptObject(long scriptID, String path, String name)
	{
		this.scriptID = scriptID;
		this.path = path;
		this.name = name;
		setCategory();
		description = "";
		journalRef = "";
		mimeType = null;
		authors = new ArrayList<ExperimenterData>();
	}
	
	/** 
	 * Returns the label associated to the script.
	 * 
	 * @return See above.
	 */
	public String getScriptLabel() { return path+name; }
	
	/**
	 * Returns the absolute path to the script.
	 * 
	 * @return See above.
	 */
	public String getPath() { return path; }
	
	/**
	 * Returns the parameters.
	 * 
	 * @param parameters The parameters to set.
	 */
	public void setJobParams(JobParams parameters)
	{
		this.parameters = parameters;
		convertJobParameters();
	}
	
	/**
	 * Returns <code>true</code> if the script has ownership information,
	 * description etc, <code>false</code> otherwise.
	 * 
	 * @return See above.
	 */
	public boolean hasDetails()
	{
		if (authors.size() > 0) return true;
		if (description != null && description.length() > 0) return true;
		if (journalRef != null && journalRef.length() > 0) return true;
		return false;
	}
	
	/**
	 * Sets the author of the script.
	 * 
	 * @param author The author of the script.
	 */
	public void setAuthor(ExperimenterData author)
	{ 
		if (author != null) authors.add(author);
	}
	
	/**
	 * Sets the description of the script.
	 * 
	 * @param description The value to set.
	 */
	public void setDescription(String description)
	{
		this.description = description; 
	}
	
	/**
	 * Sets the reference to the journal where the script was published
	 * if it has been published.
	 * 
	 * @param journalRef The value to set.
	 */
	public void setJournalRef(String journalRef)
	{ 
		this.journalRef = journalRef; 
	}
	
	/** 
	 * Returns the authors of the script.
	 * 
	 * @return See above
	 */
	public List<ExperimenterData> getAuthors() { return authors; }
	
	/**
	 * Returns the description of the script.
	 * 
	 * @return See above.
	 */
	public String getDescription()
	{ 
		if (parameters != null) return parameters.description;
		return description;
	}
	
	/**
	 * Returns the journal where the script was published if
	 * it has been published.
	 * 
	 * @return See above.
	 */
	public String getJournalRef() { return journalRef; }
	
	/**
	 * Returns the id of the script.
	 * 
	 * @return See above.
	 */
	public long getScriptID() { return scriptID; }
	
	/**
	 * Returns the name of the script.
	 * 
	 * @return See above.
	 */
	public String getName() { return name; }

	/**
	 * Sets the icon.
	 * 
	 * @param icon The icon associated to the script.
	 */
	public void setIcon(Icon icon) { this.icon = icon; }
	
	/**
	 * Sets the 48x48 icon.
	 * 
	 * @param icon The icon associated to the script.
	 */
	public void setIconLarge(Icon icon) { this.iconLarge = icon; }
	
	/**
	 * Returns the icon associated to the script.
	 * 
	 * @return See above.
	 */
	public Icon getIcon() { return icon; }
	
	/**
	 * Returns the 48x48 icon associated to the script.
	 * 
	 * @return See above.
	 */
	public Icon getIconLarge() { return iconLarge; }
	
	/** 
	 * Returns the main contact.
	 * 
	 * @return See above.
	 */
	public String getContact()
	{ 
		if (parameters != null) return parameters.contact;
		return contact;
	}
	
	/**
	 * Sets the details of the main contact.
	 * 
	 * @param contact The details of the contact.
	 */
	public void setContact(String contact) { this.contact = contact; }
	
	/**
	 * Returns the version of the script.
	 * 
	 * @return See above.
	 */
	public String getVersion()
	{ 
		if (parameters != null) return parameters.version;
		return version; 
	}

	/**
	 * Returns the inputs.
	 * 
	 * @return See above.
	 */
	public Map<String, ParamData> getInputs() { return inputs; }
	
	/**
	 * Returns the outputs.
	 * 
	 * @return See above.
	 */
	public Map<String, ParamData> getOutputs() { return inputs; }
	
	/**
	 * Returns <code>true</code> if the parameters have been loaded,
	 * <code>false</code> otherwise.
	 * 
	 * @return See above.
	 */
	public boolean isParametersLoaded() { return parameters != null; }
	
	/**
	 * Returns the MIME type.
	 * 
	 * @return See above.
	 */
	public String getMIMEType() { return mimeType; }
	
	/**
	 * Sets the MIME type.
	 * 
	 * @param mimeType The value to set.
	 */
	public void setMIMEType(String mimeType) { this.mimeType = mimeType; }
	
	/**
	 * Returns the category of the script.
	 * 
	 * @return See above.
	 */
	public int getCategory() { return category; }
	
	
	/**
	 * Returns the parameters.
	 * 
	 * @return See above.
	 */
	public JobParams getParameters() { return parameters; }
	
	/**
	 * Overridden to return the name of the script.
	 * @see java.lang.Object#toString()
	 */
	public String toString() { return getScriptLabel(); }
	
}