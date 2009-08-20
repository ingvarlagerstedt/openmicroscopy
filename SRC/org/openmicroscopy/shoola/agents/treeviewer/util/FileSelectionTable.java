/*
 * org.openmicroscopy.shoola.util.ui.filechooser.FileSelectionTable
 *
�*------------------------------------------------------------------------------
 * �Copyright (C) 2006-2009 University of Dundee. All rights reserved.
 *
 *
 * ����This program is free software; you can redistribute it and/or modify
 * �it under the terms of the GNU General Public License as published by
 * �the Free Software Foundation; either version 2 of the License, or
 * �(at your option) any later version.
 * �This program is distributed in the hope that it will be useful,
 * �but WITHOUT ANY WARRANTY; without even the implied warranty of
 * �MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. �See the
 * �GNU General Public License for more details.
 *
 * �You should have received a copy of the GNU General Public License along
 * �with this program; if not, write to the Free Software Foundation, Inc.,
 * �51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 *------------------------------------------------------------------------------
 */
package org.openmicroscopy.shoola.agents.treeviewer.util;


//Java imports
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.DefaultCellEditor;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.filechooser.FileFilter;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;

//Third-party libraries
import layout.TableLayout;

//Application-internal dependencies
import omero.IllegalArgumentException;
import org.jdesktop.swingx.JXTable;
import org.jdesktop.swingx.decorator.Highlighter;
import org.jdesktop.swingx.decorator.HighlighterFactory;
import org.openmicroscopy.shoola.util.ui.IconManager;
import org.openmicroscopy.shoola.util.ui.UIUtilities;
import org.openmicroscopy.shoola.util.ui.treetable.renderers.BooleanCellRenderer;

/**
 * Component displaying the files to import.
 *
 * @author �Jean-Marie Burel &nbsp;&nbsp;&nbsp;&nbsp;
 * ����<a href="mailto:j.burel@dundee.ac.uk">j.burel@dundee.ac.uk</a>
 * @author ���Donald MacDonald &nbsp;&nbsp;&nbsp;&nbsp;
 * ����<a href="mailto:donald@lifesci.dundee.ac.uk">donald@lifesci.dundee.ac.uk</a>
 * @version 3.0
 * <small>
 * (<b>Internal version:</b> $Revision: $Date: $)
 * </small>
 * @since 3.0-Beta4
 */
class FileSelectionTable 
	extends JPanel
	implements ActionListener
{

	/** Bound property indicating to add files to the queue. */
	static final String ADD_PROPERTY = "add";
	
	/** Bound property indicating to remove files to the queue. */
	static final String REMOVE_PROPERTY = "remove";
	
	/** Action command ID to add a field to the result table. */
	private static final int 		ADD = 0;
	
	/** Action command ID to remove a field from the result table. */
	private static final int 		REMOVE = 1;
	
	/** Action command ID to remove all fields from the result table. */
	private static final int 		REMOVE_ALL = 2;
	
	/** The index of the file's name column. */
	private static final int		FILE_INDEX = 0;
	
	/** The index of the column indicating to import the file. */
	private static final int		SELECTED_INDEX = 1;
	
	/** The columns of the table. */
	private static final String[] COLUMNS;
	
	static {
		COLUMNS = new String[2];
		COLUMNS[FILE_INDEX] = "File";
		COLUMNS[SELECTED_INDEX] = "Import";
	}
	
	/** The button to move an item from the remaining items to current items. */
	private JButton 			addButton;
	
	/** The button to move an item from the current items to remaining items. */
	private JButton 			removeButton;
	
	/** The button to move all items to the remaining items. */
	private JButton 			removeAllButton;

	/** The table displaying the collection to files to import. */
	private JXTable				table;
	
	/** Initializes the components composing the display. */
	private void initComponents()
	{
		IconManager icons = IconManager.getInstance();
		addButton = new JButton(icons.getIcon(IconManager.RIGHT_ARROW));
		addButton.setToolTipText("Add the selected files to the queue.");
		addButton.setEnabled(false);
		removeButton = new JButton(icons.getIcon(IconManager.LEFT_ARROW));
		removeButton.setToolTipText("Remove the selected files " +
				"from the queue.");
		removeButton.setEnabled(false);
		removeAllButton = new JButton(
				icons.getIcon(IconManager.DOUBLE_LEFT_ARROW));
		removeAllButton.setToolTipText("Remove all files from the queue.");
		removeAllButton.setEnabled(false);
		addButton.setActionCommand(""+ADD);
		addButton.addActionListener(this);
		removeButton.setActionCommand(""+REMOVE);
		removeButton.addActionListener(this);
		removeAllButton.setActionCommand(""+REMOVE_ALL);
		removeAllButton.addActionListener(this);
		Object[][] data = new Object[0][0];
		table = new JXTable(new FileTableModel(data, COLUMNS));
		TableColumn tc = table.getColumnModel().getColumn(SELECTED_INDEX);
		
		final BooleanCellRenderer check = new BooleanCellRenderer();
		tc.setCellRenderer(check);
		tc.setCellEditor(new DefaultCellEditor(check));
		Highlighter h = HighlighterFactory.createAlternateStriping(
				UIUtilities.BACKGROUND_COLOUR_EVEN, 
				UIUtilities.BACKGROUND_COLOUR_ODD);
		table.addHighlighter(h);
	}
	
	/**
	 * Builds and lays out the controls.
	 * 
	 * @return See above.
	 */
	private JPanel buildControls()
	{
		JPanel p = new JPanel();
		p.setLayout(new BoxLayout(p, BoxLayout.PAGE_AXIS));
		p.add(addButton);
		p.add(Box.createVerticalStrut(5));
		p.add(removeButton);
		p.add(Box.createVerticalStrut(5));
		p.add(removeAllButton);
		return p;
	}
	
	/**
	 * Returns the component hosting the collection of files to import.
	 * 
	 * @return See above.
	 */
	private JPanel buildTablePane()
	{
		JPanel p = new JPanel();
		p.setLayout(new BoxLayout(p, BoxLayout.PAGE_AXIS));
		p.add(new JLabel("Files to import"));
		p.add(Box.createVerticalStrut(5));
		p.add(new JScrollPane(table));
		return p;
	}
	
	/** Builds and lays out the UI. */
	private void builGUI()
	{
		//setLayout(new BoxLayout(this, BoxLayout.LINE_AXIS));
		double[][] size = {{TableLayout.PREFERRED, 10, TableLayout.FILL}, 
				{TableLayout.FILL}};
		setLayout(new TableLayout(size));
		add(buildControls(), "0, 0, l, c");
		add(buildTablePane(), "2, 0");
	}

	/** Removes the selected files from the queue. */
	private void removeSelectedFiles()
	{
		int[] rows = table.getSelectedRows();
		if (rows == null || rows.length == 0) return;
		DefaultTableModel model = (DefaultTableModel) table.getModel();
		for (int i = 0; i < rows.length; i++) 
			model.removeRow(i);
		int n = table.getRowCount();
		firePropertyChange(REMOVE_PROPERTY, n-1, n);
		enabledControl(table.getRowCount() > 0);
	}
	
	/** Removes all the files from the queue. */
	private void removeAllFiles()
	{
		int n = table.getRowCount();
		if (n == 0) return;
		DefaultTableModel model = (DefaultTableModel) table.getModel();
		for (int i = 0; i < n; i++) 
			model.removeRow(i);
		firePropertyChange(REMOVE_PROPERTY, -1, 0);
		enabledControl(false);
	}
	
	/**
	 * Sets the enabled flag of the buttons.
	 * 
	 * @param value The value to set.
	 */
	private void enabledControl(boolean value)
	{
		removeButton.setEnabled(value);
		removeAllButton.setEnabled(value);
		if (value) {
			int[] rows = table.getSelectedRows();
			//removeButton.setEnabled(rows != null && rows.length > 0);
		}
	}
	
	/** Creates a new instance. */
	FileSelectionTable()
	{
		initComponents();
		builGUI();
	}

	/**
	 * Returns <code>true</code> if there are files to import,
	 * <code>false</code> otherwise.
	 * 
	 * @return See above.
	 */
	boolean hasFilesToImport() { return table.getRowCount() > 0; }
	
	/** 
	 * Returns the collection of files to import.
	 * 
	 * @return See above.
	 */
	List<File> getFilesToImport()
	{
		List<File> files = new ArrayList<File>();
		int n = table.getRowCount();
		DefaultTableModel model = (DefaultTableModel) table.getModel();
		FileElement element;
		
		for (int i = 0; i < n; i++) {
			element = (FileElement) model.getValueAt(i, FILE_INDEX);
			if ((Boolean) model.getValueAt(i, SELECTED_INDEX))
				files.add(element.getFile());
		}
		return files;
	}
	
	/**
	 * Sets the enable flag of the {@link #addButton}.
	 * 
	 * @param value The value to set.
	 */
	void allowAddition(boolean value) { addButton.setEnabled(value); }
	
	/**
	 * Adds the collection of files to the queue.
	 * 
	 * @param files The files to add.
	 */
	void addFiles(List<File> files)
	{
		if (files == null || files.size() == 0) return;
		addButton.setEnabled(false);
		enabledControl(true);
		File f;
		DefaultTableModel model = (DefaultTableModel) table.getModel();
		//Check if the file has already 
		List<String> inQueue = new ArrayList<String>();
		FileElement element;
		for (int i = 0; i < table.getRowCount(); i++) {
			element = (FileElement) model.getValueAt(i, FILE_INDEX);
			inQueue.add(element.getFile().getAbsolutePath());
		}
		Iterator<File> i = files.iterator();
		while (i.hasNext()) {
			f = i.next();
			if (!inQueue.contains(f.getAbsolutePath())) {
				model.insertRow(table.getRowCount(), 
						new Object[] {new FileElement(f), 
					Boolean.valueOf(true)});
			}
		}
		//table.repaint();
	}

	/**
	 * Adds or removes files from the import queue.
	 * @see ActionListener#actionPerformed(ActionEvent)
	 */
	public void actionPerformed(ActionEvent evt)
	{
		int index = Integer.parseInt(evt.getActionCommand());
		switch (index) {
			case ADD:
				firePropertyChange(ADD_PROPERTY, Boolean.valueOf(false),
						Boolean.valueOf(true));
				break;
			case REMOVE:
				removeSelectedFiles();
				break;
			case REMOVE_ALL:
				removeAllFiles();
		}
	}
	
	/** Helper inner class used to store the file. */
	class FileElement 
	{
		
		/** The file to host. */
		private File file;
		
		/** Creates a new instance. */
		FileElement(File file)
		{
			if (file == null)
				throw new IllegalArgumentException("No file set");
			this.file = file;
		}
		
		/** 
		 * Returns the file hosted by this component.
		 * 
		 * @return See above.
		 */
		File getFile() { return file; }
		
		/** 
		 * Overridden to return the name of the file and not the full path.
		 * @see Object#toString()
		 */
		public String toString() { return file.getName(); }
		
	}
	
	/** Inner class so that some cells cannot be edited. */
	class FileTableModel 
		extends DefaultTableModel
	{
		
		/**
		 * Creates a new instance.
		 * 
		 * @param rows		The rows to display.
		 * @param columns	The columns to display.
		 */
		FileTableModel(Object[][] rows, Object[] columns)
		{
			super(rows, columns);
		}
		
		/**
		 * Overridden so that some cells cannot be edited.
		 * @see DefaultTableModel#isCellEditable(int, int)
		 */
		public boolean isCellEditable(int row, int column)
		{
			return (column != FILE_INDEX);
		}
		
	}
	
}
