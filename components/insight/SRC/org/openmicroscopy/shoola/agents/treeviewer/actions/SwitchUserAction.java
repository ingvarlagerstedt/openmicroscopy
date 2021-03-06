/*
 * org.openmicroscopy.shoola.agents.treeviewer.actions.SwitchUserAction 
 *
 *------------------------------------------------------------------------------
 *  Copyright (C) 2006-2007 University of Dundee. All rights reserved.
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
package org.openmicroscopy.shoola.agents.treeviewer.actions;


//Java imports
import java.awt.Component;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.Action;
import javax.swing.SwingUtilities;

//Third-party libraries

//Application-internal dependencies
import org.openmicroscopy.shoola.agents.treeviewer.IconManager;
import org.openmicroscopy.shoola.agents.treeviewer.browser.Browser;
import org.openmicroscopy.shoola.agents.treeviewer.view.TreeViewer;
import org.openmicroscopy.shoola.env.data.model.AdminObject;
import org.openmicroscopy.shoola.util.ui.UIUtilities;
import pojos.GroupData;

/** 
 * Action to bring up the Switch user dialog.
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
public class SwitchUserAction 
	extends TreeViewerAction
	implements MouseListener
{

	/** The name of the action. */
	private static final String NAME = "Switch User...";
	
	/** The description of the action. */
	private static final String DESCRIPTION = "Select another " +
			"user and view his/her data.";
	
    /** The location of the mouse pressed. */
    private Point point;
    
    /** 
     * Enables the action if the browser is not ready.
     * @see TreeViewerAction#onBrowserStateChange(Browser)
     */
    protected void onBrowserStateChange(Browser browser)
    {
    	if (browser == null) return;
    	if (browser.getBrowserType() == Browser.ADMIN_EXPLORER) {
    		setEnabled(false);
    		return;
    	}
    	if (browser.getState() == Browser.READY) {
    		boolean enabled = false;
    		GroupData group = model.getSelectedGroup();
    		if (group == null) {
    			setEnabled(false);
    			return;
    		}
    		int level = model.getSelectedGroupPermissions();
    		if (level == AdminObject.PERMISSIONS_PRIVATE) {
    			if (model.isLeaderOfSelectedGroup()) {
					enabled = group.getExperimenters().size() > 1;
				}
    		} else {
    			enabled = group.getExperimenters().size() > 1;
    		}
    		setEnabled(enabled);
    	} else setEnabled(false);
    }
    
    /** 
     * Enables the action if the browser is not ready.
     * @see TreeViewerAction#onBrowserSelection(Browser)
     */
    protected void onBrowserSelection(Browser browser)
    {
    	onBrowserStateChange(browser);
    }
    
    /**
     * Creates a new instance.
     * 
     * @param model Reference to the Model. Mustn't be <code>null</code>.
     */
	public SwitchUserAction(TreeViewer model)
	{
		super(model);
		name = NAME;
		putValue(Action.SHORT_DESCRIPTION, 
                UIUtilities.formatToolTipText(DESCRIPTION));
        IconManager im = IconManager.getInstance();
        putValue(Action.SMALL_ICON, im.getIcon(IconManager.OWNER_GROUP));
	}
	
    /**
     * Brings up the switch user dialog.
     * @see java.awt.event.ActionListener#actionPerformed(ActionEvent)
     */
    public void actionPerformed(ActionEvent e)
    { 
    	SwingUtilities.convertPointToScreen(point, 
			(Component) e.getSource());
    	model.retrieveUserGroups(point);
    }
    
    /** 
     * Sets the location of the point where the <code>mousePressed</code>
     * event occurred. 
     * @see MouseListener#mousePressed(MouseEvent)
     */
    public void mousePressed(MouseEvent me) { point = me.getPoint(); }
    
    /** 
     * Required by {@link MouseListener} I/F but not actually needed in our
     * case, no-operation implementation.
     * @see MouseListener#mouseReleased(MouseEvent)
     */
    public void mouseReleased(MouseEvent me) {}
    
    /** 
     * Required by {@link MouseListener} I/F but not actually needed in our
     * case, no-operation implementation.
     * @see MouseListener#mouseEntered(MouseEvent)
     */   
    public void mouseEntered(MouseEvent e) {}

    /** 
     * Required by {@link MouseListener} I/F but not actually needed in our
     * case, no-operation implementation.
     * @see MouseListener#mouseExited(MouseEvent)
     */   
    public void mouseExited(MouseEvent e) {}
    
    /** 
     * Required by {@link MouseListener} I/F but not actually needed in our
     * case, no-operation implementation.
     * @see MouseListener#mouseClicked(MouseEvent)
     */   
    public void mouseClicked(MouseEvent e) {}
    
}
