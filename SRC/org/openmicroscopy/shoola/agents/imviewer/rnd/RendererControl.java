/*
 * org.openmicroscopy.shoola.agents.imviewer.rnd.RendererControl
 *
 *------------------------------------------------------------------------------
 *  Copyright (C) 2006 University of Dundee. All rights reserved.
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

package org.openmicroscopy.shoola.agents.imviewer.rnd;



//Java imports
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import javax.swing.Action;
import javax.swing.JTabbedPane;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

//Third-party libraries

//Application-internal dependencies
import org.openmicroscopy.shoola.agents.imviewer.actions.ContrastStretchingAction;
import org.openmicroscopy.shoola.agents.imviewer.actions.HistogramAction;
import org.openmicroscopy.shoola.agents.imviewer.actions.NoiseReductionAction;
import org.openmicroscopy.shoola.agents.imviewer.actions.PlaneSlicingAction;
import org.openmicroscopy.shoola.agents.imviewer.actions.ReverseIntensityAction;
import org.openmicroscopy.shoola.agents.imviewer.actions.RndAction;
import org.openmicroscopy.shoola.agents.imviewer.util.cdm.CodomainMapContextDialog;
import org.openmicroscopy.shoola.agents.imviewer.view.ImViewer;
import org.openmicroscopy.shoola.agents.util.ui.ChannelButton;

/** 
 * The Renderer's controller.
 *
 * @author  Jean-Marie Burel &nbsp;&nbsp;&nbsp;&nbsp;
 * 				<a href="mailto:j.burel@dundee.ac.uk">j.burel@dundee.ac.uk</a>
 * @author	Andrea Falconi &nbsp;&nbsp;&nbsp;&nbsp;
 * 				<a href="mailto:a.falconi@dundee.ac.uk">a.falconi@dundee.ac.uk</a>
 * @author	Donald MacDonald &nbsp;&nbsp;&nbsp;&nbsp;
 * 				<a href="mailto:donald@lifesci.dundee.ac.uk">donald@lifesci.dundee.ac.uk</a>
 * @version 3.0
 * <small>
 * (<b>Internal version:</b> $Revision: $ $Date: $)
 * </small>
 * @since OME2.2
 */
class RendererControl
    implements ChangeListener, PropertyChangeListener
{

    /** Identifies the action to select the bit resolution. */
    static final Integer    BIT_RESOLUTION = Integer.valueOf(0);
    
    /** Identifies the action to select the family. */
    static final Integer    FAMILY = Integer.valueOf(1);
    
    /** Identifies the action to select the coefficient. */
    static final Integer    COEFFICIENT = Integer.valueOf(2);
    
    /** Identifies the action to select the noise reduction algorithm. */
    static final Integer    NOISE_REDUCTION = Integer.valueOf(3);
    
    /** Identifies the action to select the reverse intensity transformation. */
    static final Integer    REVERSE_INTENSITY = Integer.valueOf(4);
    
    /** Identifies the action to select the plane slicing transformation. */
    static final Integer    PLANE_SLICING = Integer.valueOf(5);
    
    /**
     * Identifies the action to select the contrast stretching transformation.
     */
    static final Integer    CONTRAST_STRETCHING = Integer.valueOf(6);
    
    /** Identifies the action to bring up the histogram widget. */
    static final Integer    HISTOGRAM = Integer.valueOf(7);
    
    /**
     * Reference to the {@link Renderer} component, which, in this context,
     * is regarded as the Model.
     */
    private Renderer    			model;
    
    /** Reference to the View. */
    private RendererUI  			view;
    
    /** Maps actions ids onto actual <code>Action</code> object. */
    private Map<Integer, RndAction>	actionsMap;
    
    /** Helper method to create all the UI actions. */
    private void createActions()
    {
        actionsMap.put(REVERSE_INTENSITY, new ReverseIntensityAction(model));
        actionsMap.put(PLANE_SLICING, new PlaneSlicingAction(model));
        actionsMap.put(CONTRAST_STRETCHING, 
                        new ContrastStretchingAction(model));
        actionsMap.put(NOISE_REDUCTION, new NoiseReductionAction(model));
        actionsMap.put(HISTOGRAM, new HistogramAction(model));
    }
    
    /** 
     * Attaches a window listener to the view and a property listener to the 
     * model.
     */
    private void attachListeners()
    {
        model.addPropertyChangeListener(this);
        model.getParentModel().addPropertyChangeListener(this);
        view.addChangeListener(this);
        /*
        view.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
        view.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e)
            {
            	visibility = false;
                view.setVisible(false);
            }
        });
        */
    }
    
    /** 
     * Creates a new instance.
     * The {@link #initialize(Renderer, RendererUI) initialize} method should 
     * be called straight after to link this Controller to the other MVC
     * components.
     */
    RendererControl()
    {
        actionsMap = new HashMap<Integer, RndAction>();
    }
    
    /**
     * Links this Controller to its Model and View.
     * 
     * @param model Reference to the {@link Renderer} component, which, in this
     *              context, is regarded as the Model. Mustn't be
     *              <code>null</code>.
     * @param view  Reference to the View. Mustn't be <code>null</code>.
     */
    void initialize(Renderer model, RendererUI view)
    {
        if (model == null) throw new NullPointerException("No model.");
        if (view == null) throw new NullPointerException("No view.");
        this.model = model;
        this.view = view;
        createActions();
        attachListeners();
    }
    
    /**
     * Returns the action corresponding to the specified id.
     * 
     * @param id One of the flags defined by this class.
     * @return The specified action.
     */
    Action getAction(Integer id) { return actionsMap.get(id); }
    
    /**
     * Registers the specified observer.
     * 
     * @param observer  The observer to register.
     */
    void addPropertyListener(PropertyChangeListener observer)
    {
        model.addPropertyChangeListener(observer);
    }

    /** 
     * Sets the the pixels intensity interval for the
     * currently selected channel.
     * 
     * @param s         The lower bound of the interval.
     * @param e         The upper bound of the interval.
     * @param released  If <code>true</code>, we fire a property change event
     *                  to render a new plane.
     */
    void setInputInterval(double s, double e, boolean released)
    {
        model.setInputInterval(s, e, released);
    }
    
    /** 
     * Sets the sub-interval of the device space. 
     * 
     * @param s         The lower bound of the interval.
     * @param e         The upper bound of the interval.
     * @param released  If <code>true</code>, we fire a property change event
     *                  to render a new plane.
     */
    void setCodomainInterval(int s, int e, boolean released)
    {
        model.setCodomainInterval(s, e, released);
    }

    /**
     * Reacts to property change events in the {@link ImViewer} and 
     * the {@link CodomainMapContextDialog}
     * @see PropertyChangeListener#propertyChange(PropertyChangeEvent)
     */
    public void propertyChange(PropertyChangeEvent evt)
    {
        String name = evt.getPropertyName();
        /*
        } else if (name.equals(
            CodomainMapContextDialog.UPDATE_MAP_CONTEXT_PROPERTY)) {
            CodomainMapContext ctx = (CodomainMapContext)  evt.getNewValue();
            model.updateCodomainMap(ctx);
        */
        if (ControlPane.FAMILY_PROPERTY.equals(name)) {
            String oldValue = (String) evt.getOldValue();
            String newValue = (String) evt.getNewValue();
            if (newValue.equals(oldValue)) return;
            model.setFamily(newValue);
            view.onCurveChange();
        } else if (ControlPane.GAMMA_PROPERTY.equals(name)) {
            Double oldValue = (Double) evt.getOldValue();
            Double newValue = (Double) evt.getNewValue();
            if (newValue.equals(oldValue)) return;
            model.setCurveCoefficient(newValue.doubleValue());
            view.onCurveChange();
        } else if (ControlPane.BIT_RESOLUTION_PROPERTY.equals(name)) {
            Integer oldValue = (Integer) evt.getOldValue();
            Integer newValue = (Integer) evt.getNewValue();
            if (newValue.equals(oldValue)) return;
            model.setBitResolution(newValue.intValue());
        } if (ChannelButton.CHANNEL_SELECTED_PROPERTY.equals(name)) {
            Map map = (Map) evt.getNewValue();
			if (map == null) return;
			if (map.size() != 1) return;
			Set set = map.entrySet();
			Entry entry;
			Iterator i = set.iterator();
			Integer index;
			while (i.hasNext()) {
				entry = (Entry) i.next();
				index = (Integer) entry.getKey();
				model.setChannelSelection(index.intValue(), 
						(Boolean) entry.getValue());
			}
        } else if (ChannelButton.CHANNEL_COLOR_PROPERTY.equals(name)) {
        	view.showColorPicker(((Integer) evt.getNewValue()).intValue());
        } else if (ImViewer.CHANNEL_ACTIVE_PROPERTY.equals(name)) {
            int v = ((Integer) evt.getNewValue()).intValue();
            model.setSelectedChannel(v);
        } else if (Renderer.INPUT_INTERVAL_PROPERTY.equals(name)) {
            view.setInputInterval();
        } else if (ImViewer.CHANNEL_COLOR_CHANGED_PROPERTY.equals(name)) {
        	
            // DO SOME UPDATES ON THE CHANNEL TOGGLE BUTTON MODEL.
        	int index = (Integer) evt.getNewValue();
        	model.setChannelColor(index);
        } else if (ImViewer.COLOR_MODEL_CHANGED_PROPERTY.equals(name)) {
            // DO SOME UPDATES ON THE CHANNEL TOGGLE BUTTON MODEL.
        	model.setColorModelChanged();
        } 
    }

    /**
     * Loads the metadata depending on the tabbed selected.
     * @see ChangeListener#stateChanged(ChangeEvent)
     */
	public void stateChanged(ChangeEvent e)
	{
		Object src = e.getSource();
		if (src instanceof JTabbedPane) {
			JTabbedPane pane = (JTabbedPane) src; 
			if (pane.getSelectedIndex() == ControlPane.METADATA_PANE_INDEX) 
				view.loadMetadata();
			return;
		}
	}
    
}
