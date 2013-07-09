/*
 *    GeoTools - The Open Source Java GIS Toolkit
 *    http://geotools.org
 *
 *    (C) 2009-2011, Open Source Geospatial Foundation (OSGeo)
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the GNU Lesser General Public
 *    License as published by the Free Software Foundation;
 *    version 2.1 of the License.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 */
package org.bisag.geotools;

import java.awt.*;
import java.awt.event.*;
import java.util.*;

import javax.swing.*;
import net.miginfocom.swing.MigLayout;

import org.bisag.popup.MapPanePopup;
import org.bisag.geotools.MapLayerTable;
import org.geotools.swing.*;
import org.geotools.map.MapContent;
import org.geotools.map.event.MapLayerListEvent;
import org.geotools.map.event.MapLayerListListener;
import org.geotools.swing.action.InfoAction;
import org.geotools.swing.action.NoToolAction;
import org.geotools.swing.action.PanAction;
import org.geotools.swing.action.ResetAction;
import org.geotools.swing.action.ZoomInAction;
import org.geotools.swing.action.ZoomOutAction;
import org.geotools.swing.control.JMapStatusBar;


/**
 * A Swing frame containing a mapContent display pane and (optionally) a toolbar,
 * status bar and mapContent layer table.
 * <p>
 * Simplest use is with the static {@link #showMap(MapContent)} method:
 * 
 * <pre>
 * @code \u0000
 * MapContent content = new MapContent();
 * content.setTitle("My beautiful mapContent");
 * 
 * // add some layers to the MapContent...
 * 
 * JMapFrame.showMap(content);
 * }
 * </pre>
 * 
 * @see MapLayerTable
 * @see StatusBar
 * 
 * @author Michael Bedward
 * @since 2.6
 * 
 * @source $URL$
 * @version $Id$
 */
@SuppressWarnings("serial")
public class JMapFrame extends JFrame {

	/*
	 * The following toolbar button names are primarily for unit testing but
	 * could also be useful for applications wanting to control appearance and
	 * behaviour at run-time.
	 */

	/** Name assigned to toolbar button for feature info queries. */
	public static final String TOOLBAR_INFO_BUTTON_NAME = "ToolbarInfoButton";
	/** Name assigned to toolbar button for mapContent panning. */
	public static final String TOOLBAR_PAN_BUTTON_NAME = "ToolbarPanButton";
	/** Name assigned to toolbar button for default pointer. */
	public static final String TOOLBAR_POINTER_BUTTON_NAME = "ToolbarPointerButton";
	/** Name assigned to toolbar button for mapContent reset. */
	public static final String TOOLBAR_RESET_BUTTON_NAME = "ToolbarResetButton";
	/** Name assigned to toolbar button for mapContent zoom in. */
	public static final String TOOLBAR_ZOOMIN_BUTTON_NAME = "ToolbarZoomInButton";
	/** Name assigned to toolbar button for mapContent zoom out. */
	public static final String TOOLBAR_ZOOMOUT_BUTTON_NAME = "ToolbarZoomOutButton";

	/**
	 * Constants for available toolbar buttons used with the {@link #enableTool}
	 * method.
	 */
	public enum Tool {
		/**
		 * Simple mouse cursor, used to unselect previous cursor tool.
		 */
		POINTER,

		/**
		 * The feature info cursor tool
		 */
		INFO,

		/**
		 * The panning cursor tool.
		 */
		PAN,

		/**
		 * The reset mapContent extent cursor tool.
		 */
		RESET,

		/**
		 * The zoom display cursor tools.
		 */
		ZOOM;
	}

	private boolean showToolBar;
	private Set<Tool> toolSet;

	/*
	 * UI elements
	 */
	private static JMapPane mapPane;
	private static MapLayerTable mapLayerTable;
	private JToolBar toolBar;
	private JPanel panel;
	public static JScrollPane lowerScrollPane;
	public static JSplitPane splitPane;
	public static JSplitPane leftSplitPane;
	private boolean showStatusBar;
	private boolean showLayerTable;
	private boolean uiSet;

	/**
	 * Creates a new mapContent frame with a toolbar, mapContent pane and status bar; sets the
	 * supplied {@code MapContent}; and displays the frame. If
	 * {@linkplain MapContent#getTitle()} returns a non-empty string, this is
	 * used as the frame's title.
	 * <p>
	 * This method can be called safely from any thread.
	 * 
	 * @param content
	 *            the mapContent content
	 */
	public static void showMap(final MapContent content) {
		if (SwingUtilities.isEventDispatchThread()) {
			doShowMap(content);
		} else {
			SwingUtilities.invokeLater(new Runnable() {

				@Override
				public void run() {
					doShowMap(content);
				}
			});
		}
	}

	private static void doShowMap(MapContent content) {
		final JMapFrame frame = new JMapFrame(content);
		frame.enableStatusBar(true);
		frame.enableToolBar(true);
		frame.initComponents();
		frame.setSize(800, 600);
		frame.setVisible(true);
	}

	/**
	 * Default constructor. Creates a {@code JMapFrame} with no mapContent content or
	 * renderer set
	 */
	public JMapFrame() {
		this(null);
	}

	/**
	 * Constructs a new {@code JMapFrame} object with specified mapContent content.
	 * 
	 * @param content
	 *            the mapContent content
	 */
	public JMapFrame(MapContent content) {
		super(content == null ? "" : content.getTitle());
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		showLayerTable = false;
		showStatusBar = false;
		showToolBar = false;
		toolSet = EnumSet.noneOf(Tool.class);

		// the mapContent pane is the one element that is always displayed
		mapPane = new JMapPane(content);
		mapPane.setBackground(new Color(200,200,200));

		// give keyboard focus to the mapContent pane
		addWindowFocusListener(new WindowAdapter() {
			@Override
			public void windowGainedFocus(WindowEvent e) {
				mapPane.requestFocusInWindow();
			}
		});

		mapPane.addMouseListener(new MouseAdapter() {

			@Override
			public void mousePressed(MouseEvent e) {
				mapPane.requestFocusInWindow();
			}
			public void mouseReleased(MouseEvent e) {
				if(e.isPopupTrigger()) {
					new MapPanePopup(mapPane).show(mapPane, e.getX(), e.getY());
				}
			}
		});
	}

	/**
	 * Sets whether to display the default toolbar (default is false). Calling
	 * this with state == true is equivalent to calling {@link #enableTool} with
	 * all {@link JMapFrame.Tool} constants.
	 * 
	 * @param enabled
	 *            whether the toolbar is required
	 */
	public void enableToolBar(boolean enabled) {
		if (enabled) {
			toolSet = EnumSet.allOf(Tool.class);
		} else {
			toolSet.clear();
		}
		showToolBar = enabled;
	}

	/**
	 * This method is an alternative to {@link #enableToolBar(boolean)}. It
	 * requests that a tool bar be created with specific tools, identified by
	 * {@link JMapFrame.Tool} constants.
	 * 
	 * <code><pre>
	 * myMapFrame.enableTool(Tool.PAN, Tool.ZOOM);
	 * </pre></code>
	 * 
	 * Calling this method with no arguments or {@code null} is equivalent to
	 * {@code enableToolBar(false)}.
	 * 
	 * @param tool
	 *            tools to display on the toolbar
	 */
	public void enableTool(Tool... tool) {
		if (tool == null || tool.length == 0) {
			enableToolBar(false);
		} else {
			toolSet = EnumSet.copyOf(Arrays.asList(tool));
			showToolBar = true;
		}
	}

	/**
	 * Set whether a status bar will be displayed to display cursor positionOfTable and
	 * mapContent bounds.
	 * 
	 * @param enabled
	 *            whether the status bar is required.
	 */
	public void enableStatusBar(boolean enabled) {
		showStatusBar = enabled;
	}

	/**
	 * Set whether a mapContent layer table will be displayed to show the list of
	 * layers in the mapContent content and set their order, visibility and selected
	 * status.
	 * 
	 * @param enabled
	 *            whether the mapContent layer table is required.
	 */
	public void enableLayerTable(boolean enabled) {
		showLayerTable = enabled;
	}

	/**
	 * Calls {@link #initComponents()} if it has not already been called
	 * explicitly to construct the frame's components before showing the frame.
	 * 
	 * @param state
	 *            true to show the frame; false to hide.
	 */
	@Override
	public void setVisible(boolean state) {
		if (state && !uiSet) {
			initComponents();
		}

		super.setVisible(state);
	}

	/**
	 * Creates and lays out the frame's components that have been specified with
	 * the enable methods (e.g. {@link #enableToolBar(boolean)} ). If not called
	 * explicitly by the client this method will be invoked by
	 * {@link #setVisible(boolean) } when the frame is first shown.
	 */
	public void initComponents() {
		if (uiSet) {
			// @todo log a warning ?
			return;
		}

		/*
		 * We use the MigLayout manager to make it easy to manually code our UI
		 * design
		 */
		StringBuilder sb = new StringBuilder();
		if (!toolSet.isEmpty()) {
			sb.append("[]"); // fixed size
		}
		sb.append("[grow]"); // mapContent pane and optionally layer table fill space
		if (showStatusBar) {
			sb.append("[min!]"); // status bar height
		}

		panel = new JPanel(new MigLayout("wrap 1, insets 0", // layout
																// constrains: 1
																// component per
																// row, no
																// insets

				"[grow]", // column constraints: col grows when frame is resized

				sb.toString()));

		/*
		 * A toolbar with buttons for zooming in, zooming out, panning, and
		 * resetting the mapContent to its full extent. The cursor tool buttons
		 * (zooming and panning) are put in a ButtonGroup.
		 * 
		 * Note the use of the XXXAction objects which makes constructing the
		 * tool bar buttons very simple.
		 */
		if (showToolBar) {
			toolBar = new JToolBar();
			toolBar.setOrientation(JToolBar.HORIZONTAL);
			toolBar.setFloatable(false);

			JButton btn;
			ButtonGroup cursorToolGrp = new ButtonGroup();

			if (toolSet.contains(Tool.POINTER)) {
				btn = new JButton(new NoToolAction(mapPane));
				btn.setName(TOOLBAR_POINTER_BUTTON_NAME);
				toolBar.add(btn);
				cursorToolGrp.add(btn);
			}

			if (toolSet.contains(Tool.ZOOM)) {
				btn = new JButton(new ZoomInAction(mapPane));
				btn.setName(TOOLBAR_ZOOMIN_BUTTON_NAME);
				toolBar.add(btn);
				cursorToolGrp.add(btn);

				btn = new JButton(new ZoomOutAction(mapPane));
				btn.setName(TOOLBAR_ZOOMOUT_BUTTON_NAME);
				toolBar.add(btn);
				cursorToolGrp.add(btn);

				toolBar.addSeparator();
			}

			if (toolSet.contains(Tool.PAN)) {
				btn = new JButton(new PanAction(mapPane));
				btn.setName(TOOLBAR_PAN_BUTTON_NAME);
				toolBar.add(btn);
				cursorToolGrp.add(btn);

				toolBar.addSeparator();
			}

			if (toolSet.contains(Tool.INFO)) {
				btn = new JButton(new InfoAction(mapPane));
				btn.setName(TOOLBAR_INFO_BUTTON_NAME);
				toolBar.add(btn);

				toolBar.addSeparator();
			}

			if (toolSet.contains(Tool.RESET)) {
				btn = new JButton(new ResetAction(mapPane));
				btn.setName(TOOLBAR_RESET_BUTTON_NAME);
				toolBar.add(btn);
			}

			panel.add(toolBar, "grow");
		}

		if (showLayerTable) {
			mapLayerTable = new MapLayerTable(mapPane);

			/*
			 * We put the mapContent layer panel and the mapContent pane into a JSplitPane so
			 * that the user can adjust their relative sizes as needed during a
			 * session. The call to setPreferredSize for the layer panel has the
			 * effect of setting the initial positionOfTable of the JSplitPane divider
			 */

			leftSplitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, true,
					mapLayerTable, lowerScrollPane);
			leftSplitPane.setOneTouchExpandable(true);

			leftSplitPane.setDividerLocation(400);
			leftSplitPane.setResizeWeight(0.5);

			splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, true,
					leftSplitPane, mapPane);
			splitPane.setOneTouchExpandable(true);

			panel.add(splitPane, "grow");

		} else {
			panel.add(mapPane, "grow");
		}

		if (showStatusBar) {
			panel.add(JMapStatusBar.createDefaultStatusBar(mapPane), "grow");
		}

		this.getContentPane().add(panel);
		uiSet = true;
	}

	public static void setLegendColorTable(JPanel legendTable,int width,int height,boolean isSidebar) {
			mapPane.removeAll();
			if(isSidebar){
				mapPane.removeAll();
				leftSplitPane.setBottomComponent(new JScrollPane(legendTable));
				leftSplitPane.setDividerLocation(0);
				splitPane.resetToPreferredSizes();
			}
			else
			{
				splitPane.setDividerLocation(0);
				mapPane.repaint();
				mapPane.updateUI();
				mapPane.setLayout(null);
				legendTable.setBounds(mapPane.getWidth()-width, mapPane.getHeight()-height,width,height);
				mapPane.add(legendTable);
			}
	}
	

	/**
	 * Get the mapContent content associated with this frame. Returns {@code null} if
	 * no mapContent content has been set explicitly with the constructor or
	 * {@link #setMapContent}.
	 * 
	 * @return the current {@code MapContent} object
	 */
	public MapContent getMapContent() {
		return mapPane.getMapContent();
	}

	/**
	 * Set the MapContent object used by this frame.
	 * 
	 * @param content
	 *            the mapContent content
	 * @throws IllegalArgumentException
	 *             if content is null
	 */
	public void setMapContent(MapContent content) {
		if (content == null) {
			throw new IllegalArgumentException("mapContent content must not be null");
		}

		mapPane.setMapContent(content);
		
		content.addMapLayerListListener(new MapLayerListListener() {

			@Override
			public void layerRemoved(MapLayerListEvent arg0) {
				
			}

			@Override
			public void layerPreDispose(MapLayerListEvent arg0) {

			}

			@Override
			public void layerMoved(MapLayerListEvent arg0) {
				

			}

			@Override
			public void layerChanged(MapLayerListEvent arg0) {
				
			}

			@Override
			public void layerAdded(MapLayerListEvent arg0) {
				if(mapPane.getBackground().equals(new Color(200,200,200)))
					mapPane.setBackground(Color.WHITE);
			}
		});
	}

	/**
	 * Provides access to the instance of {@code JMapPane} being used by this
	 * frame.
	 * 
	 * @return the {@code JMapPane} object
	 */
	public static JMapPane getMapPane() {
		return mapPane;
	}

	/**
	 * Provides access to the toolbar being used by this frame. If
	 * {@link #initComponents} has not been called yet this method will invoke
	 * it.
	 * 
	 * @return the toolbar or null if the toolbar was not enabled
	 */
	public JToolBar getToolBar() {
		if (!uiSet)
			initComponents();
		return toolBar;
	}

}
