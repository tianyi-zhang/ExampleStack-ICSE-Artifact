/* 
 * SparkBit
 *
 * Copyright 2014 Coin Sciences Ltd
 *
 * Licensed under the MIT license (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://opensource.org/licenses/mit-license.php
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.multibit.viewsystem.swing.view.components;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.io.IOException;

/**
 * Adapted from http://stackoverflow.com/questions/527719/how-to-add-hyperlink-in-jlabel
 */
public class SwingLink extends JLabel {
    private static final long serialVersionUID = 8273875024682878518L;
    private String text;
    private URI url;



    public SwingLink() {
	super();
        addMouseListener(new MouseAdapter() {
                public void mouseClicked(MouseEvent e) {
                    openLink();
                }
                public void mouseEntered(MouseEvent e) {
		    setCursor(new Cursor(Cursor.HAND_CURSOR));
                    setText(text,false);
                }
                public void mouseExited(MouseEvent e) {
		    setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
                    setText(text,true);
                }
        });
    }

        public URI getURL() {
	return url;
    }

    public void setURL(String s) {
	url = null;
	try {
	    if (s!=null) {
		url = new URI(s);
	    }
	} catch (URISyntaxException e) {
	} 
        if (url == null) {
	    setToolTipText("");
	} else {
	    setToolTipText(url.toString());
	}
    }
    
    @Override
    public void setText(String text){
        setText(text,true);
    }

    public void setText(String text, boolean ul){
        String link = ul ? "<u>"+text+"</u>" : text;
        super.setText("<html><span style=\"color: #000099;\">"+
                link+"</span></html>");
        this.text = text;
    }

    public String getRawText(){
        return text;
    }

    private void openLink() {
        if (Desktop.isDesktopSupported()) {
                Desktop desktop = Desktop.getDesktop();
                try {
		    if (url != null) {
                        desktop.browse(url);
		    }
                } catch (IOException e) {
                    JOptionPane.showMessageDialog(null,
                            "Failed to launch the link, " +
                            "your computer is likely misconfigured.",
                            "Cannot Launch Link",JOptionPane.WARNING_MESSAGE);
                }
        } else {
            JOptionPane.showMessageDialog(null,
                    "Java is not able to launch links on your computer.",
                    "Cannot Launch Link",JOptionPane.WARNING_MESSAGE);
        }
    }
}
