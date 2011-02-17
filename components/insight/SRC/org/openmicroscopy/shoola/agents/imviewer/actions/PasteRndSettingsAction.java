/*
 * org.openmicroscopy.shoola.agents.imviewer.actions.PasteRndSettingsAction 
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
package org.openmicroscopy.shoola.agents.imviewer.actions;



//Java imports
import java.awt.event.ActionEvent;
import javax.swing.Action;
import javax.swing.event.ChangeEvent;

//Third-party libraries

//Application-internal dependencies
import org.openmicroscopy.shoola.agents.imviewer.IconManager;
import org.openmicroscopy.shoola.agents.imviewer.view.ImViewer;
import org.openmicroscopy.shoola.util.ui.UIUtilities;

/** 
 * Pastes the rendering settings. 
 *
 * @author  Jean-Marie Burel &nbsp;&nbsp;&nbsp;&nbsp;
 * <a href="mailto:j.burel@dundee.ac.uk">j.burel@dundee.ac.uk</a>
 * @author Donald MacDonald &nbsp;&nbsp;&nbsp;&nbsp;
 * <a href="mailto:donald@lifesci.dundee.ac.uk">donald@lifesci.dundee.ac.uk</a>
 * @version 3.0
 * <small>
 * (<b>Internal version:</b> $Revision: $Date: $)
 * </small>
 * @since OME3.0
 */
public class PasteRndSettingsAction 	
	extends ViewerAction
{
	
	/** The description of the action. */
    private static final String NAME = "Paste Settings";
    
	/** The description of the action. */
    private static final String DESCRIPTION = 
    	"Apply Saved Rendering Settings to the Current Image.";
    
    /** 
     * Sets the enabled flag depending on the tab selected.
     * @see ViewerAction#onTabSelection()
     */
    protected void onTabSelection()
    {
    	if (model.getSelectedIndex() == ImViewer.PROJECTION_INDEX)
    		setEnabled(false);
    	else setEnabled(model.hasSettingsToPaste());
    }
    
    /**
     * Disposes and closes the movie player when the {@link ImViewer} is
     * discarded.
     * @see ViewerAction#onStateChange(ChangeEvent)
     */
    protected void onStateChange(ChangeEvent e)
    {
    	if (model.getState() == ImViewer.READY)
    		onTabSelection();
    }
    
    /**
     * Creates a new instance.
     * 
     * @param model     Reference to the model. Mustn't be <code>null</code>.
     */
	public PasteRndSettingsAction(ImViewer model)
	{
		super(model);
		name = NAME;
    	IconManager icons = IconManager.getInstance();
    	putValue(Action.SMALL_ICON, icons.getIcon(IconManager.PASTE));
    	putValue(Action.SHORT_DESCRIPTION, 
    			UIUtilities.formatToolTipText(DESCRIPTION));
	}

	/** 
     * Pastes the rendering settings.
     * @see java.awt.event.ActionListener#actionPerformed(ActionEvent)
     */
    public void actionPerformed(ActionEvent e)
    {
    	model.pasteRenderingSettings();
    }
    
}