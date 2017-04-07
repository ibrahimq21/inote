//==============================================================================
//==============================================================================
/* This application is created By Mr.Ibrahim Qureshi Designation: Software Engineer */
//==============================================================================
//==============================================================================
/* This application is copyright protected please do not copy this code */
//==============================================================================
 package utils;
import java.awt.BorderLayout;
import java.awt.SplashScreen;
import java.awt.Graphics2D;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Frame;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.WindowEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.FileNotFoundException;

import javax.swing.BorderFactory;
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.filechooser.FileFilter;
import javax.swing.JFrame;
import javax.swing.JToolBar;
import javax.swing.JMenu;
import javax.swing.JButton;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.border.Border;
import javax.swing.event.UndoableEditEvent;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.UndoableEditListener;
import javax.swing.undo.CannotUndoException;
import javax.swing.undo.CannotRedoException;
import javax.swing.undo.UndoManager;
import javax.swing.ImageIcon;
import javax.swing.KeyStroke;
import javax.swing.text.Element;
import javax.swing.UIManager;

final class INote extends JFrame implements ActionListener{
	
	//menu
	private JMenu fileMenu,editMenu;
	private JMenuItem newFile,openFile,saveFile,saveAsFile,exit;
	private JMenuItem undoEdit, redoEdit, selectAll, copy, cut, paste;
	
	
	//buttons
	private JButton newButton, openButton, saveButton, cutButton, copyButton, pasteButton, undoBtn, redoBtn;
	
	//windows
	private JFrame editorWindow;
	
	//image icon
	private ImageIcon saveFileIcon, newFileIcon, openFileIcon, cutIcon, copyIcon, pasteIcon, undoIcon, redoIcon;
	//toolbar
	private JToolBar tool;
	
	//textArea
	private Border textBorder;
	private JScrollPane scroll;
	private JTextArea textArea,line;
	private Font textFont;
	
	//is save or open
	private boolean opened = false;
	private boolean saved = false;
	
	//record opened file for quick save
	
	private File openedFile;
	
	private BufferedWriter writer;
	
	private SplashScreen splash;
	private Graphics2D g;
	
	private Element root;
	
	// Undo manager for managing the storage of the undos
    // so that the can be redone if requested
	
	private UndoManager undo;
	
	//Constructors
	
	public INote(){
		
		// view splash screen.
		viewSplashScreen();
		
		// create Menu
		fileMenu();
		editMenu();
		
		// create text Area
		createTextArea();
		
		// Create Undo Manager for managing undo/redo commands
		undoMan();
		
		// create window
		createEditorWindow();
	}
	
	private SplashScreen viewSplashScreen(){
		splash = SplashScreen.getSplashScreen();
		if(splash == null){
			System.out.print("getSplashScreen returned null...");
			return null;
		}
		Graphics2D g = splash.createGraphics();
		for(int i = 0; i<100; i++){
			splash.update();
			try{
				Thread.sleep(90);
			}catch(InterruptedException e){
			}
		}
		return splash;
	}
	
	
	private JToolBar createToolBar(){
		
		tool = new JToolBar();
		
		newFileIcon = new ImageIcon("icons/default_document2.png");
		openFileIcon = new ImageIcon("icons/document_add.png");
		saveFileIcon = new ImageIcon("icons/save.png");
		cutIcon = new ImageIcon("icons/cut-icon.png");
		copyIcon = new ImageIcon("icons/copy-icon.png");
		pasteIcon = new ImageIcon("icons/Paste-icon.png");
		undoIcon = new ImageIcon("icons/Undo-icon.png");
		redoIcon = new ImageIcon("icons/Redo-icon.png");
		
		undoBtn = new JButton(undoIcon);
		undoBtn.addActionListener(this);
		undoBtn.setToolTipText("Undo text");
		undoBtn.setEnabled(true);
		redoBtn = new JButton(redoIcon);
		redoBtn.addActionListener(this);
		redoBtn.setToolTipText("Redo text");
		redoBtn.setEnabled(true);
		cutButton = new JButton(cutIcon);
		cutButton.addActionListener(this);
		cutButton.setToolTipText("Cut text");
		cutButton.setEnabled(true);
		copyButton = new JButton(copyIcon);
		copyButton.addActionListener(this);
		copyButton.setToolTipText("Copy text");
		copyButton.setEnabled(true);
		pasteButton = new JButton(pasteIcon);
		pasteButton.addActionListener(this);
		pasteButton.setToolTipText("Paste text");
		pasteButton.setEnabled(true);
		newButton = new JButton(newFileIcon);
		newButton.addActionListener(this);
		newButton.setToolTipText("New File");
		newButton.setEnabled(true);
		openButton = new JButton(openFileIcon);
		openButton.addActionListener(this);
		openButton.setToolTipText("Open File");
		openButton.setEnabled(true);
		saveButton = new JButton(saveFileIcon);
		saveButton.addActionListener(this);
		saveButton.setToolTipText("Save File");
		saveButton.setEnabled(true);
		
		tool.add(newButton);
		tool.add(openButton);
		tool.add(saveButton);
		add(tool, BorderLayout.NORTH);
		tool.addSeparator();
		tool.add(cutButton);
		tool.add(copyButton);
		tool.add(pasteButton);
		tool.addSeparator();
		tool.add(undoBtn);
		tool.add(redoBtn);
		tool.setFloatable(false);
		return tool;
	}
	
	
	private JFrame createEditorWindow(){
		
		editorWindow = new JFrame("INote");
		editorWindow.setVisible(true);
		editorWindow.setExtendedState(Frame.MAXIMIZED_BOTH);
		editorWindow.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		// create menu bar
		editorWindow.setJMenuBar(createMenuBar());
		editorWindow.add(scroll, BorderLayout.CENTER);
		editorWindow.add(createToolBar(), BorderLayout.NORTH);
		editorWindow.pack();
		//center application on screen
		editorWindow.setLocationRelativeTo(null);
		
		return editorWindow;
	}
	
	private JTextArea createTextArea(){
		scroll = new JScrollPane(textArea, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
		textArea = new JTextArea(30, 50);
		line = new JTextArea("1");
		line.setBackground(Color.LIGHT_GRAY);
		getLine();// Read Lines
		scroll.getViewport().add(textArea);
		scroll.setRowHeaderView(line);
		line.setEditable(false);
		
		textArea.setEditable(true);
		textFont = new Font("Courier New", 0, 16);
		textArea.setFont(textFont);
		line.setFont(textFont);
		textArea.setBorder(BorderFactory.createCompoundBorder(textBorder, BorderFactory.createEmptyBorder(2, 5, 0, 0)));
		return textArea;
	}
	
	
	
	private void getLine(){
		textArea.getDocument().addDocumentListener(new DocumentListener(){
			public String getText(){
				int caretPosition = textArea.getDocument().getLength();
				root = textArea.getDocument().getDefaultRootElement();
				String text = "1"+System.getProperty("line.separator");
				for(int i = 2; i < root.getElementIndex(caretPosition)+2; i++){
					text += i+System.getProperty("line.separator");
				}
				return text;
			}
			public void changedUpdate(DocumentEvent de){
				line.setText(getText());
			}
			public void insertUpdate(DocumentEvent de){
				line.setText(getText());
			}
			public void removeUpdate(DocumentEvent de){
				line.setText(getText());
			}
		});
	}
	
	private JMenuBar createMenuBar(){
		JMenuBar menuBar = new JMenuBar();
		setJMenuBar(menuBar);
		menuBar.add(fileMenu);
		menuBar.add(editMenu);
		return menuBar;
	}
	
	private UndoManager undoMan(){
		
		undo = new UndoManager();
		textArea.getDocument().addUndoableEditListener(new UndoableEditListener(){
			public void undoableEditHappened(UndoableEditEvent e){
				undo.addEdit(e.getEdit());
			}
		});
		return undo;
	}
	
	private void fileMenu(){
		
		fileMenu = new JMenu("File");
		fileMenu.setPreferredSize(new Dimension(40, 30));
		
		newFile = new JMenuItem("New");
		newFile.setToolTipText("New File");
		newFile.setAccelerator(KeyStroke.getKeyStroke("control N"));//that's perfect
		newFile.addActionListener(this);
		newFile.setPreferredSize(new Dimension(120, 20));
		newFile.setEnabled(true);
		
		openFile = new JMenuItem("Open...");
		openFile.setToolTipText("Open file");
		openFile.setAccelerator(KeyStroke.getKeyStroke("control O"));
		openFile.addActionListener(this);
		openFile.setPreferredSize(new Dimension(120, 20));
		openFile.setEnabled(true);
		
		saveFile = new JMenuItem("Save");
		saveFile.setToolTipText("Save File");
		saveFile.setAccelerator(KeyStroke.getKeyStroke("control S"));
		saveFile.addActionListener(this);
		saveFile.setPreferredSize(new Dimension(120, 20));
		saveFile.setEnabled(true);
		
		saveAsFile = new JMenuItem("Save As...");
		saveAsFile.setToolTipText("Save As");
		saveAsFile.setAccelerator(KeyStroke.getKeyStroke("control alt S"));
		saveAsFile.addActionListener(this);
		saveAsFile.setPreferredSize(new Dimension(160, 20));
		saveAsFile.setEnabled(true);
		
		exit = new JMenuItem("Exit");
		exit.setToolTipText("Exit");
		exit.setAccelerator(KeyStroke.getKeyStroke("control Q"));
		exit.addActionListener(this);
		exit.setPreferredSize(new Dimension(40, 20));
		exit.setEnabled(true);
		
		fileMenu.add(newFile);
		fileMenu.add(openFile);
		fileMenu.add(saveFile);
		fileMenu.add(saveAsFile);
		fileMenu.add(exit);
		
	}
	
	private void editMenu(){
		
		editMenu = new JMenu("Edit");
		editMenu.setPreferredSize(new Dimension(40,20));
		
		undoEdit = new JMenuItem("Undo");
		undoEdit.setToolTipText("Undo Text");
		undoEdit.addActionListener(this);
		undoEdit.setAccelerator(KeyStroke.getKeyStroke("control Z"));
		undoEdit.setPreferredSize(new Dimension(100, 20));
		undoEdit.setEnabled(true);
		
		redoEdit = new JMenuItem("Redo");
		redoEdit.setToolTipText("Redo Text");
		redoEdit.addActionListener(this);
		redoEdit.setAccelerator(KeyStroke.getKeyStroke("control Y"));
		redoEdit.setPreferredSize(new Dimension(150, 20));
		redoEdit.setEnabled(true);
		
		selectAll = new JMenuItem("Select All");
		selectAll.setToolTipText("Select All");
		selectAll.addActionListener(this);
		selectAll.setAccelerator(KeyStroke.getKeyStroke("control A"));
		selectAll.setPreferredSize(new Dimension(100, 20));
		selectAll.setEnabled(true);
		
		copy = new JMenuItem("Copy");
		copy.setToolTipText("Copy text");
		copy.addActionListener(this);
		copy.setPreferredSize(new Dimension(100, 20));
		copy.setEnabled(true);
		
		paste = new JMenuItem("Paste");
		paste.setToolTipText("Paste text");
		paste.addActionListener(this);
		paste.setPreferredSize(new Dimension(100, 20));
		paste.setEnabled(true);
		
		cut = new JMenuItem("Cut");
		cut.setToolTipText("Cut text");
		cut.addActionListener(this);
		cut.setPreferredSize(new Dimension(100, 20));
		cut.setEnabled(true);
		
		editMenu.add(undoEdit);
		editMenu.add(redoEdit);
		editMenu.add(selectAll);
		editMenu.add(copy);
		editMenu.add(paste);
		editMenu.add(cut);
		
	}
	
	private void saveFile(File fileName)throws IOException{
		try{
			writer = new BufferedWriter(new FileWriter(fileName));
			writer.write(textArea.getText());
			saved = true;
			editorWindow.setTitle("INote -"+fileName.getName()+" Path:"+fileName.getPath());
			
		}catch(IOException e){
			e.printStackTrace();
		}finally{
			writer.close();
		}
	}
	
	private void quickSave(File fileName)throws IOException{
		try{
			writer = new BufferedWriter(new FileWriter(fileName));
			writer.write(textArea.getText());
			saved = true;
			editorWindow.setTitle("INote -"+fileName.getName()+" Path:"+fileName.getPath());
		}catch(IOException e){
			e.printStackTrace();
		}finally{
			writer.close();
		}
	}
	
	private void openingFiles(File fileName){
		try{
			openedFile = fileName;
			FileReader fr = new FileReader(fileName);
			BufferedReader reader = new BufferedReader(fr);
			String lines = reader.readLine();
			while(lines != null){
				textArea.append(lines+"\n");
				lines = reader.readLine();
			}
			opened = true;
			editorWindow.setTitle("INote :-"+fileName.getName()+" Path:"+fileName.getPath());
			
		}catch(FileNotFoundException f){
			JOptionPane.showMessageDialog(null, "File not found.....!");
		}catch(IOException e){
			e.printStackTrace();
		}
	}
	
	public void actionPerformed(ActionEvent event){
		if(event.getSource() == newFile || event.getSource() == newButton){
			new INote();
		}else if(event.getSource() == openFile ^ event.getSource() == openButton){
			JFileChooser open = new JFileChooser();
			FileFilter filter = new FileNameExtensionFilter("txt file","txt");
			open.addChoosableFileFilter(filter);
			open.addChoosableFileFilter(new FileNameExtensionFilter("java file","java"));
			open.addChoosableFileFilter(new FileNameExtensionFilter("c file","c"));
			open.addChoosableFileFilter(new FileNameExtensionFilter("c# file","cs"));
			open.addChoosableFileFilter(new FileNameExtensionFilter("cpp file","cpp"));
			open.addChoosableFileFilter(new FileNameExtensionFilter("html file","html"));
			open.addChoosableFileFilter(new FileNameExtensionFilter("xml file","xml"));
			open.setDialogTitle("Open File...!");
			open.showOpenDialog(null);
			File file = open.getSelectedFile();
			openingFiles(file);
		}else if(event.getSource() == saveFile ^ event.getSource() == saveButton){
			JFileChooser save = new JFileChooser();
			FileFilter filter = new FileNameExtensionFilter("txt file","txt");
			save.addChoosableFileFilter(filter);
			save.addChoosableFileFilter(new FileNameExtensionFilter("java file","java"));
			save.addChoosableFileFilter(new FileNameExtensionFilter("c file","c"));
			save.addChoosableFileFilter(new FileNameExtensionFilter("c# file","cs"));
			save.addChoosableFileFilter(new FileNameExtensionFilter("cpp file","cpp"));
			save.addChoosableFileFilter(new FileNameExtensionFilter("html file","html"));
			save.addChoosableFileFilter(new FileNameExtensionFilter("xml file","xml"));
			save.showSaveDialog(null);
			save.setDialogTitle("Save....!");
			File fileName = save.getSelectedFile();
			int confirmationResult;
			if(opened == false){
				if(saved == false){
					if(fileName.exists()){
						confirmationResult = JOptionPane.showConfirmDialog(saveFile,"Replace Existing file?");
						
						if(confirmationResult == JOptionPane.YES_OPTION){
							try{
								saveFile(fileName);
							}catch(IOException e){
								JOptionPane.showMessageDialog(null,"file error:\n"+e);
								e.printStackTrace();
							}
						}
					}
					try{
						quickSave(fileName);
					}catch(IOException e){
						e.printStackTrace();
					}
				}else{
					try{
						quickSave(fileName);
					}catch(IOException e){
						JOptionPane.showMessageDialog(null,"file error:\n"+e);
						e.printStackTrace();
					}
				}
			}else{
				try{
					quickSave(fileName);
				}catch(IOException e){
					e.printStackTrace();
				}
			}
			
		}else if(event.getSource() == saveAsFile){
			JFileChooser saveAs = new JFileChooser();
			FileFilter filter = new FileNameExtensionFilter("txt file","txt");
			saveAs.addChoosableFileFilter(filter);
			saveAs.addChoosableFileFilter(new FileNameExtensionFilter("java file","java"));
			saveAs.addChoosableFileFilter(new FileNameExtensionFilter("c file","c"));
			saveAs.addChoosableFileFilter(new FileNameExtensionFilter("c# file","cs"));
			saveAs.addChoosableFileFilter(new FileNameExtensionFilter("cpp file","cpp"));
			saveAs.addChoosableFileFilter(new FileNameExtensionFilter("html file","html"));
			saveAs.addChoosableFileFilter(new FileNameExtensionFilter("xml file","xml"));
			saveAs.setDialogTitle("Save As...");
			saveAs.showSaveDialog(null);
			File fileName = saveAs.getSelectedFile();
			int confirmationResult;
			if(fileName.exists()){
				confirmationResult = JOptionPane.showConfirmDialog(saveAsFile, "Replace existing file?");
				if(confirmationResult == JOptionPane.YES_OPTION){
					try{
						saveFile(fileName);
					}catch(IOException e){
						JOptionPane.showMessageDialog(null,"file error:\n"+e);
						e.printStackTrace();
					}
				}
			}else{
				try{
					saveFile(fileName);
				}catch(IOException e){
					JOptionPane.showMessageDialog(null,"file error:\n"+e);
					e.printStackTrace();
				}
			}
		}else if(event.getSource() == exit){
			int confirm;
			if(saved == false){
				confirm = JOptionPane.showConfirmDialog(exit,"Save file before exit?");
				if(confirm == JOptionPane.YES_OPTION){
					JFileChooser fileChooser = new JFileChooser();
					FileFilter filter = new FileNameExtensionFilter("txt file","txt");
					fileChooser.addChoosableFileFilter(filter);
					fileChooser.addChoosableFileFilter(new FileNameExtensionFilter("java file","java"));
					fileChooser.addChoosableFileFilter(new FileNameExtensionFilter("c file","c"));
					fileChooser.addChoosableFileFilter(new FileNameExtensionFilter("c# file","cs"));
					fileChooser.addChoosableFileFilter(new FileNameExtensionFilter("cpp file","cpp"));
					fileChooser.addChoosableFileFilter(new FileNameExtensionFilter("html file","html"));
					fileChooser.addChoosableFileFilter(new FileNameExtensionFilter("xml file","xml"));
					fileChooser.setDialogTitle("Save Before Exit");
					fileChooser.showSaveDialog(null);
					File fileName = fileChooser.getSelectedFile();
					try{
						saveFile(fileName);
					}catch(IOException e){
						e.printStackTrace();
					}
					editorWindow.dispose();
					System.exit(0);
				}if(confirm == JOptionPane.NO_OPTION){
					editorWindow.dispose();
				}else{
					//do nothing
				}
			}else{
				// editorWindow.dispose();
			}
		}else if(event.getSource() == undoEdit ^ event.getSource() == undoBtn){
			try{
				undo.undo();
			}catch(CannotUndoException c){
				c.printStackTrace();
			}
		}else if(event.getSource() == redoEdit ^ event.getSource() == redoBtn){
			try{
				undo.redo();
			}catch(CannotRedoException r){
				r.printStackTrace();
			}
		}else if(event.getSource() == selectAll){
			getTextArea().selectAll();
		}else if(event.getSource() == copy ^ event.getSource() == copyButton){
			getTextArea().copy();
		}else if(event.getSource() == paste ^ event.getSource() == pasteButton){
			getTextArea().paste();
		}else if(event.getSource() == cut ^ event.getSource() == cutButton){
			getTextArea().cut();
		}
	}
	
	public JTextArea getTextArea(){
		return textArea;
	}
	
	public void setTextArea(JTextArea text){
		textArea = text;
	}
	
	public static void main(String[] args){
		try{
			UIManager.setLookAndFeel(
				UIManager.getSystemLookAndFeelClassName());
		}catch(Exception e){
			System.out.println("Error :"+e.getStackTrace());
		}
				new INote();
		// IsetVisible(true);
	}
	
}