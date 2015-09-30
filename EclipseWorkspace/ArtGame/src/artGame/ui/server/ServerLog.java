<<<<<<< HEAD
package artGame.ui.server;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.SwingUtilities;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.Position.Bias;
import javax.swing.text.View;


public class ServerLog extends JPanel {

	private JScrollPane pane;
	private JTextPane text;
	
	private static int MAX = 20;
	
	public ServerLog(int width, int height){
		setPreferredSize(new Dimension(width, height));
		
		//pane = new JScrollPane();
		//pane.setPreferredSize(new Dimension(width, height));
		
		text = new JTextPane();
		text.setPreferredSize(new Dimension(width, height));
		 text.getDocument().addDocumentListener( new DocumentListener() {
	            public void removeUpdate( DocumentEvent e ) {
	            }
	            public void insertUpdate( DocumentEvent e ) {
	                SwingUtilities.invokeLater( new Runnable() {
	                    public void run() {
	                        try {
	                            View baseView = text.getUI().getRootView(text);
	                            View root = baseView.getView(0);
	                            for( int i = 0; i < root.getViewCount()-MAX; i++ ) {
	                                int line = root.getViewIndex( i, Bias.Forward );
	                                View lineview = root.getView(line);
	                                text.getDocument().remove( lineview.getStartOffset(), lineview.getEndOffset() );
	                            }
	                        } catch( BadLocationException e1 ) {
	                            e1.printStackTrace();
	                        }
	                    }
	                } );
	            }
	            public void changedUpdate( DocumentEvent e ) {
	            }
	        });
		
		//pane.add(text);
		log("GUI initiated - ready to start server");
		add(text);
	}
	
	public void log(String string){
		try {
			text.getDocument().insertString(text.getDocument().getEndPosition().getOffset(), 
					new SimpleDateFormat("H:m:s").format( new Date() )+": " + string + "\n", null);
		} catch (BadLocationException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}
	
	@Override
	public void paintComponent(Graphics g){
		g.setColor(Color.WHITE);
		g.fillRect(0, 0, getWidth(), getHeight());
	}
	
}
=======
package artGame.ui.server;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.SwingUtilities;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.Position.Bias;
import javax.swing.text.View;


public class ServerLog extends JPanel {

	private JScrollPane pane;
	private JTextPane text;
	
	private static int MAX = 20;
	
	public ServerLog(int width, int height){
		setPreferredSize(new Dimension(width, height));
		
		//pane = new JScrollPane();
		//pane.setPreferredSize(new Dimension(width, height));
		
		text = new JTextPane();
		text.setPreferredSize(new Dimension(width, height));
		 text.getDocument().addDocumentListener( new DocumentListener() {
	            public void removeUpdate( DocumentEvent e ) {
	            }
	            public void insertUpdate( DocumentEvent e ) {
	                SwingUtilities.invokeLater( new Runnable() {
	                    public void run() {
	                        try {
	                            View baseView = text.getUI().getRootView(text);
	                            View root = baseView.getView(0);
	                            for( int i = 0; i < root.getViewCount()-MAX; i++ ) {
	                                int line = root.getViewIndex( i, Bias.Forward );
	                                View lineview = root.getView(line);
	                                text.getDocument().remove( lineview.getStartOffset(), lineview.getEndOffset() );
	                            }
	                        } catch( BadLocationException e1 ) {
	                            e1.printStackTrace();
	                        }
	                    }
	                } );
	            }
	            public void changedUpdate( DocumentEvent e ) {
	            }
	        });
		
		//pane.add(text);
		log("GUI initiated - ready to start server");
		add(text);
	}
	
	public void log(String string){
		try {
			text.getDocument().insertString(text.getDocument().getEndPosition().getOffset(), 
					new SimpleDateFormat("H:m:s").format( new Date() )+": " + string + "\n", null);
		} catch (BadLocationException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}
	
	@Override
	public void paintComponent(Graphics g){
		g.setColor(Color.WHITE);
		g.fillRect(0, 0, getWidth(), getHeight());
	}
	
}
>>>>>>> 342b35d931854225238b59e40ad31c79ee42260b
