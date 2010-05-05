/*
 * org.openmicroscopy.shoola.env.ui.MovieCreator
 *
 *------------------------------------------------------------------------------
 * Copyright (C) 2006-2009 University of Dundee. All rights reserved.
 *
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along
 * with this program; if not, write to the Free Software Foundation, Inc.,
 * 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 *------------------------------------------------------------------------------
 */
package org.openmicroscopy.shoola.env.ui;



//Java imports
import java.util.ArrayList;
import java.util.List;

//Third-party libraries

//Application-internal dependencies
import org.openmicroscopy.shoola.env.config.Registry;
import org.openmicroscopy.shoola.env.data.ScriptCallback;
import org.openmicroscopy.shoola.env.data.events.DSCallFeedbackEvent;
import org.openmicroscopy.shoola.env.data.model.MovieExportParam;
import org.openmicroscopy.shoola.env.data.views.CallHandle;
import pojos.FileAnnotationData;
import pojos.ImageData;

/**
 * Creates a movie of the passed image.
 *
 * @author Jean-Marie Burel &nbsp;&nbsp;&nbsp;&nbsp;
 *         <a href="mailto:j.burel@dundee.ac.uk">j.burel@dundee.ac.uk</a>
 * @author Donald MacDonald &nbsp;&nbsp;&nbsp;&nbsp;
 *         <a href="mailto:donald@lifesci.dundee.ac.uk">donald@lifesci.dundee.ac.uk</a>
 * @version 3.0
 * <small>
 * (<b>Internal version:</b> $Revision: $Date: $)
 * </small>
 * @since 3.0-Beta4
 */
public class MovieCreator 
	extends UserNotifierLoader
{

    /** Handle to the asynchronous call so that we can cancel it. */
    private CallHandle  			handle;
    
    /** The image to create a movie from. */
    private ImageData 				image;
    
    /** The parameters to use.*/
    private MovieExportParam 		param;
    
    /** The select channels. */
    private List<Integer>			channels;
    
    /** The result. */
    private FileAnnotationData		data;

    /** The call-back returned by the server. */
    private ScriptCallback 			callBack;
    
    /** Notifies the user that an error occurred. */
    protected void onException() { handleNullResult(); }
    
    /**
     * Creates a new instance.
     * 
     * @param viewer	The viewer this data loader is for.
     *               	Mustn't be <code>null</code>.
     * @param registry	Convenience reference for subclasses.
     * @param param  	The parameters used to create the movie.
     * @param channels	The selected channels.
     * @param image		The image.
     * @param activity 	The activity associated to this loader.
     */
	public MovieCreator(UserNotifier viewer,  Registry registry,
			MovieExportParam param, List<Integer> channels, 
			ImageData image, ActivityComponent activity)
	{
		super(viewer, registry, activity);
		if (image == null)
			throw new IllegalArgumentException("Image not valid.");
		if (param == null)
			throw new IllegalArgumentException("Parameters cannot be null.");
		this.param = param;
		if (channels == null) 
			channels = new ArrayList<Integer>();
		this.channels = channels;
		this.image = image;
	}
	
	/**
     * Creates a movie of the selected image.
     * @see UserNotifierLoader#load()
     */
    public void load()
    {
    	long pixelsID = image.getDefaultPixels().getId();
        handle = ivView.createMovie(image.getId(), pixelsID, channels, param, 
        		this);
    }
    
    /**
     * Cancels the ongoing data retrieval.
     * @see UserNotifierLoader#cancel()
     */
    public void cancel()
    { 
    	try {
    		if (callBack != null) {
    			callBack.cancel();
        		activity.onActivityCancelled();
    		}
		} catch (Exception e) {
			handleException(e);
		}
    	handle.cancel();
    }
    
    /** 
     * Stores the call-back.
     * @see UserNotifierLoader#update(DSCallFeedbackEvent)
     */
    public void update(DSCallFeedbackEvent fe) 
    {
        //if (viewer.getState() == DataBrowser.DISCARDED) return;  //Async cancel.
        String status = fe.getStatus();
        int percDone = fe.getPercentDone();
        Object o = fe.getPartialResult();
        if (o != null) {
        	callBack = (ScriptCallback) o;
        	activity.onCallBackSet();
        }
    }
    
    /**
     * Notifies the user that it wasn't possible to create the movie.
     * @see UserNotifierLoader#handleNullResult()
     */
    /*
    public void handleNullResult()
    { 
    	activity.notifyError("Unable to create movie for ");
    }
    */
	
}