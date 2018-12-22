package com.jmapbundler.console;

import java.io.OutputStream;
import java.io.PrintStream;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

public class Console {

	final PrintStream printStream;

	public Console() {
		this.printStream = new PrintStream(this.initializeFrame());
	}

	private OutputStream initializeFrame() {
		final JFrame frame = new JFrame();
		frame.setTitle("Journeymap Bundler - Console");

		final JTextArea textArea = new JTextArea();
		textArea.setEditable(false);

		frame.add(new JScrollPane(textArea));
		frame.pack();
		frame.setSize(640, 480);
		frame.setVisible(true);

		return new TextAreaOutputStream(textArea);
	}

	public PrintStream getPrintStream() {
		return this.printStream;
	}

}
