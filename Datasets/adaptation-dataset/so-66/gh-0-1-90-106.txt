/*
 * Copyright (C) 2016
 * 
 * 
 * 
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 */

package edu.wright.cs.sp16.ceg3120.gui.other;

import java.awt.AWTEvent;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLayer;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.SwingUtilities;
import javax.swing.plaf.LayerUI;


/**
 * A component to give tab pane close buttons. 
 * http://stackoverflow.com/questions/24634047/closeable-jtabbedpane-alignment-of-the-close-button
 *
 */
public class CloseableTabbedPaneLayer extends LayerUI<JTabbedPane> {

	private static final long serialVersionUID = 1L;
	private final JPanel p0 = new JPanel();
	private final Point pt = new Point(-100, -100);
	
	private final JButton button = new XButton("x");
	
	/**
	 * Exit button. 
	 *
	 */
	static class XButton extends JButton {
		private static final long serialVersionUID = 1L;

		/**
		 * Super cons.
		 * 
		 * @param string button text
		 */
		public XButton(String string) {
			super(string);
		}

		@Override
		public Dimension getPreferredSize() {
			return new Dimension(16, 16);
		}
	}

	/**
	 * Constructor.
	 */
	public CloseableTabbedPaneLayer() {
		super();
		button.setBorder(BorderFactory.createEmptyBorder());
		button.setFocusPainted(false);
		button.setBorderPainted(false);
		button.setContentAreaFilled(false);
		button.setRolloverEnabled(false);
	}

	
	@Override
	public void paint(Graphics gr, JComponent jc) {
		super.paint(gr, jc);
		if (jc instanceof JLayer<?>) {
			JLayer<?> jlayer = (JLayer<?>) jc;
			JTabbedPane tabPane =  (JTabbedPane) jlayer.getView();
			for (int i = 0; i < tabPane.getTabCount(); i++) {
				Rectangle rect = tabPane.getBoundsAt(i);
				Dimension dim = button.getPreferredSize();
				int x0 = rect.x + rect.width - dim.width - 2;
				int y0 = rect.y + (rect.height - dim.height) / 2;
				Rectangle r2 = new Rectangle(x0, y0, dim.width, dim.height);
				button.setForeground(r2.contains(pt) ? Color.RED : Color.BLACK);
				SwingUtilities.paintComponent(gr, button, p0, r2);
			}
		}
	}

	
	@Override
	public void installUI(JComponent jc) {
		super.installUI(jc);
		if (jc instanceof JLayer<?>) {
			((JLayer<?>) jc).setLayerEventMask(AWTEvent.MOUSE_EVENT_MASK 
					| AWTEvent.MOUSE_MOTION_EVENT_MASK);
		}
	}

	
	@Override
	public void uninstallUI(JComponent comp) {
		if (comp instanceof JLayer<?>) {
			((JLayer<?>) comp).setLayerEventMask(0);
			super.uninstallUI(comp);
		}
	}

	@Override
	protected void processMouseEvent(MouseEvent event, JLayer<? extends JTabbedPane> jl) {
		if (event.getID() == MouseEvent.MOUSE_CLICKED) {
			pt.setLocation(event.getPoint());
			JTabbedPane tabbedPane = (JTabbedPane) jl.getView();
			int index = tabbedPane.indexAtLocation(pt.x, pt.y);
			if (index >= 0) {
				Rectangle rect = tabbedPane.getBoundsAt(index);
				Dimension d0 = button.getPreferredSize();
				int x0 = rect.x + rect.width - d0.width - 2;
				int y0 = rect.y + (rect.height - d0.height) / 2;
				Rectangle r0 = new Rectangle(x0, y0, d0.width, d0.height);
				if (r0.contains(pt)) {
					tabbedPane.removeTabAt(index);
				}
			}
			jl.getView().repaint();
		}
	}

	@Override
	protected void processMouseMotionEvent(MouseEvent e0, JLayer<? extends JTabbedPane> jl) {
		pt.setLocation(e0.getPoint());
		JTabbedPane tabbedPane = (JTabbedPane) jl.getView();
		int index = tabbedPane.indexAtLocation(pt.x, pt.y);
		if (index >= 0) {
			tabbedPane.repaint(tabbedPane.getBoundsAt(index));
		} else {
			tabbedPane.repaint();
		}
	}
}